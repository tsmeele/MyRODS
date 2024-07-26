package nl.tsmeele.myrods.plumbing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.tsmeele.log.Log;
import nl.tsmeele.myrods.irodsDataTypes.Data;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataPIStr;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;
import nl.tsmeele.myrods.irodsDataTypes.IrodsType;

/**
 * Class ParsedInstruction provides for a parsed single packing instruction.
 * It can be used to lookup the name, type, direct/pointer and size/dimension 
 * of an element of a data structure.
 * Using the provided context of already-parsed data, it dereferences variables that specify 
 * a literal value, and it resolves dependent type references (specified using the "?" symbol). 
 * @author Ton Smeele
 *
 */
public class ParsedInstruction {
	private static PackMapConstants packMapConstants = new PackMapConstants();
	
	private Pattern regexType  = Pattern.compile("^\\s*([?]?)(\\w+)\\s+([*]?)(\\w+)\\s*(.*)\\s*$");
	private Pattern regexArray = Pattern.compile("\\s*\\[\\s*(\\w+)\\s*\\]");
	private Pattern regexDim   = Pattern.compile("\\s*\\(\\s*(\\w+)\\s*\\)");

	private IrodsType type = null;
	private boolean pointer = false;
	private String nameDependPI = "";
	private String name = "";
	private int cardinality = 1;
	private ArrayList<Integer> dimensions = new ArrayList<Integer>();
	
	public ParsedInstruction(DataStruct context, String instruction) throws MyRodsException {
		if (context == null) {
			throw new MyRodsException("Internal PI parse error: context missing");
		}
		// parse into (dependent)type, pointer, name, and optional details
		Matcher m = regexType.matcher(instruction);
		if (!m.find()) {
			throw new MyRodsException("PI parse error (" + instruction + ")");
		}
		boolean dependent = !m.group(1).equals("");
		if (dependent) {
			type = IrodsType.DEPENDS;
			nameDependPI = parseToPI(context, m.group(2));
		} else {
			type = IrodsType.lookup(m.group(2));
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
	
	public IrodsType getType() {
		if (type == IrodsType.DEPENDS) {
			// return the resolved reference as a struct
			return IrodsType.STRUCT;
		}
		return type;
	}

	public boolean isPointer() {
		return pointer;
	}
	
	public int getCardinality() {
		return cardinality;
	}
	
	public String getName() {
		if (type == IrodsType.DEPENDS) {
			// return the resolved reference to a packing instruction string
			return nameDependPI;
		}
		return name;
	}
	
	public List<Integer> getDimensions() {
		return dimensions;
	}
	
	private DataStruct getRoot(DataStruct element) {
		DataStruct root = element;
		while (root.getParent() != null) {
			root = root.getParent();
		}
		return root;
	}
	
	private String parseToPI(DataStruct context, String s) throws MyRodsException {
		Log.debug("IN parseToPI lookup: " + s);
		// first look in parsed data belonging to current PackInstruction context
		Data d = context.lookupName(s);
		if (d == null) {
			// broaden context to al parsed data
			d = getRoot(context).lookupName(s);
		}
		if (d == null || d.getType() != IrodsType.PISTR) {
			throw new MyRodsException("PI reference must be a PISTR (" + s + ")");
		}
		Log.debug("parseToPI found: " + ((DataPIStr)d).get() );
		return ((DataPIStr)d).get();		
	}
	
	private int parseToInt (DataStruct context, String s) throws MyRodsException {
		// string is either an integer value...
		try {
			return Integer.parseInt(s);
		} 
		catch (Exception e) {}
		// ...or string is a reference to an earlier defined int variable, we look up its value
		Log.debug("IN parseToInt lookup: " + s);
		Log.debug("context is " + context.getName());
		Data d = context.lookupName(s);
		if (d == null) {
			d = getRoot(context).lookupName(s);
		}
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
		if (d.getType() == IrodsType.INT) {
			return ((DataInt)d).get();
		}
		throw new MyRodsException("PI referenced name must be integer type (" + s + ")");
	}

	public String toString() {
		return "Instruction{type=" + type + " dependPI=" + nameDependPI + 
				" ptr=" + pointer + " name=" + name + " cardinality=" + cardinality  + 
				" dimensions=" + dimensions + "}" ;
	}
	

}
