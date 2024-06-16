package plumbing;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import irodsType.Data;
import irodsType.DataInt;
import irodsType.DataPIStr;
import irodsType.DataStruct;
import irodsType.IrodsBaseType;

/**
 * Class ParsedInstruction provides for a parsed single packing instruction.
 * It can be used to lookup the name, type and size/dimension of an element of a data structure
 * @author Ton Smeele
 *
 */
public class ParsedInstruction {
	private static PackMapConstants packMapConstants = new PackMapConstants();
	
	private Pattern regexType  = Pattern.compile("^\\s*([?]?)(\\w+)\\s+([*]?)(\\w+)\\s*(.*)\\s*$");
	private Pattern regexArray = Pattern.compile("\\s*\\[\\s*(\\w+)\\s*\\]");
	private Pattern regexDim   = Pattern.compile("\\s*\\(\\s*(\\w+)\\s*\\)");

	private IrodsBaseType type = null;
	private boolean pointer = false;
	private String nameDependPI = "";
	private String name = "";
	private int cardinality = 1;
	private ArrayList<Integer> dimensions = new ArrayList<Integer>();
	
	public ParsedInstruction(DataStruct context, String instruction) throws MyRodsException {
		// parse into (dependent)type, pointer, name, and optional details
		Matcher m = regexType.matcher(instruction);
		if (!m.find()) {
			throw new MyRodsException("PI parse error (" + instruction + ")");
		}
		boolean dependent = !m.group(1).equals("");
		if (dependent) {
			type = IrodsBaseType.DEPENDS;
			nameDependPI = parseToPI(context, m.group(2));
		} else {
			type = IrodsBaseType.get(m.group(2));
		}
		pointer = m.group(3).equals("*");
		name = m.group(4);
		String details = m.group(5);
		
		// extract array dimension if present
		m = regexArray.matcher(details);
		if (m.find()) {
			cardinality = parseToInt(context, m.group(1));
		}
		// extract hints for per-element size allocation
		m = regexDim.matcher(details);
		while (m.find()) {
			int i = parseToInt(context, m.group(1));
			dimensions.add(i);
		}		
	}
	
	public IrodsBaseType getType() {
		if (type == IrodsBaseType.DEPENDS) {
			// return the resolved reference to a packing instruction string
			return IrodsBaseType.PISTR;
		}
		return type;
	}
	
	public String getName() {
		if (type == IrodsBaseType.DEPENDS) {
			// return the resolved reference to a packing instruction string
			return nameDependPI;
		}
		return name;
	}
	
	/**
	 * suggests if and how arrays should be created upon unpack
	 * @return a list of array size per dimension.  
	 * an empty list means no array needs to be created
	 */
	public ArrayList<Integer> getArrayDimensions() {
		ArrayList<Integer> dim = new ArrayList<Integer>();
		int len = dimensions.size();
		if (pointer && type.isArrayType()) {
			// in case of pointers, the last dimension is used to indicate an element size
			// this only applies to types char/bin/str/piStr
			len = len - 1;
		}
		for (int i=0; i < len; i++) {
			dim.add(dimensions.get(i));
		}
		return dim;
	}
	
	/** 
	 * suggests the allocation size of the element. 
	 * @return allocation size (value can be 0 or greater)
	 * The allocation size pertains only to char/bin array or size of str/piStr
	 *  0 = use actual string length as allocation size
	 *  other = allocate the returned number of bytes
	 */
	public int getAllocationSize() {
		if (!type.isArrayType()) {
			// non-string types have a fixed size as determined by their type. 
			return 1;
		}
		if (!pointer || dimensions.isEmpty()) {
			// we will use cardinality as a hint to allocation size
			if (type == IrodsBaseType.CHAR ||
					type == IrodsBaseType.BIN) {
				return cardinality;
			}
			// type is string or piStr
			if (cardinality > 1) {
				return cardinality;
			} 
			// string with no explicitly defined size, signal allocation should be based on length of string
			return 0;
		}
		// this is a pointer for a char/bin/str/piStr type and dimensions have been specified 
		// the last dimension is used to specify an allocation size
		return dimensions.get(dimensions.size() - 1);
	}
	
	public String toString() {
		return "Instruction{type=" + type + " dependPI=" + nameDependPI + 
				" ptr=" + pointer + " name=" + name + " cardinality=" + cardinality  + 
				" dimensions=" + dimensions + "}" ;
	}
	
	
	private String parseToPI(DataStruct context, String s) throws MyRodsException {
		Data d = context.lookupName(s);
		if (d == null || d.getType() != IrodsBaseType.PISTR) {
			throw new MyRodsException("PI reference must be a PISTR (" + s + ")");
		}
		return ((DataPIStr)d).get();		
	}
	
	private int parseToInt (DataStruct context, String s) throws MyRodsException {
		// string is either an integer value...
		try {
			return Integer.parseInt(s);
		} 
		catch (Exception e) {}
		// ...or string is a reference to an earlier defined int variable, we look up its value
		Data d = context.lookupName(s);
		if (d == null) {
			// ...or string is an iRODS predefined alias for an integer value
			Integer predefinedInt = packMapConstants.get(s);
			if (predefinedInt != null) {
				return predefinedInt;
			}
		}
		if (d == null) {
			throw new MyRodsException("PI reference to undefined name (" + s + ")");
		}
		if (d.getType() == IrodsBaseType.INT) {
			return ((DataInt)d).get();
		}
		throw new MyRodsException("PI referenced name must be integer type (" + s + ")");
	}

	
	

}
