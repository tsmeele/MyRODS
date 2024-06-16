package irodsType;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import plumbing.IrodsProtocolType;
import plumbing.MyRodsException;
import plumbing.PackMap;
import plumbing.ParsedInstruction;
import plumbing.TagParser;

/**
 * Abstract class to represent arbitrary data structure compositions.
 * (Composite design pattern)
 * @author Ton Smeele
 *
 */
public abstract class Data {
	// PackMap has all concrete packing instructions for data 
	private static PackMap piMap = new PackMap();
	private String label;
	protected boolean nullValue = false;
	
	public Data(String variableName) {
		this.label = variableName;
	}
	
	public String getName() {
		return label;
	}
	
	public boolean isNull() {
		return nullValue;
	}
	
	protected abstract String toXmlString();
	
	public abstract IrodsBaseType getType();
	

	public abstract byte[] packNative();
	
	public abstract int packNativeSize();
	
	public String packXml() {
		if (nullValue) {
			// in XMl pack, omission of an element indicates that the element has a null value
			// the packing instruction can be used to find out about missing elements
			return "";
		}
		return "<" + escapeXml(label) + ">" + toXmlString() + "</" + escapeXml(label) + ">";
	}
	
	protected static String escapeXml(String s) {
		s = s.replace("&", "&amp");
		s = s.replace("<", "&lt");
		s = s.replace(">", "&gt");
		s = s.replace("'", "&apos");
		s = s.replace("\"", "&quot");
		return s;
	}
	
	protected static String unescapeXml(String s) {
		s = s.replace("&quot","\"");
		s = s.replace("&apos", "'");
		s = s.replace("&gt", ">");
		s = s.replace("&lt", "<");
		s = s.replace("&amp", "&");
		return s;
	}
	
	public static DataStruct unpack(IrodsProtocolType protocol, String packInstruction, ByteBuffer b) throws MyRodsException {
		switch (protocol) {
		case NATIVE_PROT:
			return unpackNative(packInstruction, b);
		case XML_PROT:
			return unpackXml(packInstruction, b);
		default:
		}
		return null;
	}
	
	public static DataStruct unpackXml(String packInstruction, ByteBuffer b) throws MyRodsException {
		TagParser tags = new TagParser(new String(b.array()));
		if (!tags.hasTag()) {
			throw new MyRodsException("Input exhausted at start unpack of instruction " + packInstruction);
		}
		String tagLabel = unescapeXml(tags.getTagLabel());
		boolean endTag = tags.isEndTag();
		// a packing instruction does not have tagcontent, just get the label and tagtype
		// String tagContent = unescapeXml(tags.getTagContent());
		tags.setTagRead(true);
		if (!tagLabel.equals(packInstruction)) {
			throw new MyRodsException("Found request for an unsupported packing Instruction" + packInstruction);
		}
		DataStruct result = xmlUnpackTags(packInstruction, tags);
		if (tags.isRead()) {
			tags.advanceOneTag();
		}
		tagLabel = unescapeXml(tags.getTagLabel());
		endTag = tags.isEndTag();
		//String tagContent = unescapeXml(tags.getTagContent());
		tags.setTagRead(true);
		if (!(tagLabel.equals(packInstruction) && endTag)) {
			throw new MyRodsException("Unexpected XML end while parsing " + packInstruction);
		}
		return result;
	}
	
	public static DataStruct xmlUnpackTags(String packInstruction, TagParser tags) throws MyRodsException {
		DataStruct data = new DataStruct(packInstruction);
		ArrayList<String> piList = piMap.get(packInstruction);
		if (piList == null || piList.isEmpty()) throw new MyRodsException("Unsupported Pack-Instruction (" + packInstruction + ")");
		for (String strInstruction : piList) {
			// parse instruction
			ParsedInstruction instruction = new ParsedInstruction(data, strInstruction);
			int numberOfDim = instruction.getArrayDimensions().size();
			if (numberOfDim > 0) {
				data.add(xmlUnpackArray(0, instruction, tags));
			} else {
				data.add(xmlUnpackOne(instruction, tags));
			}
		}
		return data;
	}
	
	public static DataArray xmlUnpackArray(int dim, ParsedInstruction instruction, TagParser tags) throws MyRodsException {
		DataArray array = new DataArray(instruction.getName());
		for (int i = 0; i < instruction.getArrayDimensions().get(dim); i++) {
			if (instruction.getArrayDimensions().size() - dim > 1) {
				// need to drill down another level
				array.add(xmlUnpackArray(dim + 1, instruction, tags));
			} else {
				array.add(xmlUnpackOne(instruction, tags));
			}
		}
		return null;
	}
	
	public static Data xmlUnpackOne(ParsedInstruction instruction, TagParser tags) throws MyRodsException {
		String name = instruction.getName();
		if (tags.isRead()) {
			tags.advanceOneTag();
		}
		if (!tags.hasTag()) {
			throw new MyRodsException("Input exhausted during unpack of instruction " + name);
		}
		// get the tag details
		String tagLabel = unescapeXml(tags.getTagLabel());
		boolean endTag = tags.isEndTag();
		String tagContent = unescapeXml(tags.getTagContent());
		tags.setTagRead(true);
		//System.out.println("TAG label=" + tagLabel + "| endTag=" + endTag + "| content =" + tagContent + "|");
		boolean nullTag = !name.equals(tagLabel);
		if (nullTag) {
			// the tag we are looking for is missing out in the data stream
			// by convention, this signals a null value for this data element
			// we need to save the tag just read for a next element
			tags.setTagRead(false);
		}
		if (!nullTag && endTag) {
			throw new MyRodsException("Unexpected endtag found during unpack of instruction " + name);
		}
		int alloc = instruction.getAllocationSize();
		Data result = null;
		switch (instruction.getType()) {
		case INT16: {
			try {
				if (nullTag) throw new RuntimeException();
				result = new DataInt16(name, Short.parseShort(tagContent));
			} catch (Exception e) {
				result = new DataInt16(name, (Short) null);
			}
			break;
		}
		case INT: {
			try {
				if (nullTag) throw new RuntimeException();
				result = new DataInt(name, Integer.parseInt(tagContent));
			} catch (Exception e) {
				result = new DataInt(name, (Integer) null);
			}
			break;
		}
		case DOUBLE: {
			try {
				if (nullTag) throw new RuntimeException();
				result = new DataDouble(name, Double.parseDouble(tagContent));
			} catch (Exception e) {
				result = new DataDouble(name, (Double) null);
			}
			break;
		}
		case BIN: {
			if (nullTag) {
				result = new DataBinArray(name, null);
			} else {
				result = new DataBinArray(name, tagContent, alloc);
			}
			break;
		}
		case CHAR: {
			byte[] buf;
			if (nullTag) {
				result = new DataCharArray(name, null);
				break;
			} else {
				buf = tagContent.getBytes();
			}
			byte[] bytes = new byte[alloc];
			for (int i = 0; i < alloc; i++) {
				if (i < buf.length) {
					bytes[i] = buf[i];
				} else {
					bytes[i] = 0;
				}
			}
			result = new DataCharArray(name, bytes);
			break;
		}
		case STR: {
			if (nullTag) {
				result = new DataString(name, null);
			} else {
			result = new DataString(name, tagContent, alloc);
			}
			break;
		}
		case PISTR: {
			if (nullTag) {
				result = new DataPIStr(name, null);
			} else {
				result = new DataPIStr(name, tagContent, alloc);
			}
			break;
		}
		case STRUCT: {
			if (nullTag) {
				result = new DataStruct(name, null);
			} else {
				result = xmlUnpackTags(name, tags);				
			}
		}
		default: 
			// the types DEPENDS and ARRAY should never be returned by instruction.getType()
		}
		if (nullTag) {
			return result;
		}
		// read away the corresponding endTag
		if (tags.isRead()) {
			tags.advanceOneTag();
		}
		if (!tags.hasTag() ) {
			throw new MyRodsException("Input exhausted during unpack of instruction " + name);
		}
		if (!(tags.isEndTag() && tags.getTagLabel().equals(name))) {
			throw new MyRodsException("Input endTag missing during unpack of instruction " + name);
		}
		tags.setTagRead(true);
		return result;
	}

	
	// UNPACK NATIVE:
	
	public static DataStruct unpackNative(String packInstruction, ByteBuffer b) throws MyRodsException {
		
		DataStruct data = new DataStruct(packInstruction);
		ArrayList<String> piList = piMap.get(packInstruction);
		if (piList == null || piList.isEmpty()) throw new MyRodsException("Unsupported Pack-Instruction (" + packInstruction + ")");
		for (String strInstruction : piList) {
			// parse instruction
			ParsedInstruction instruction = new ParsedInstruction(data, strInstruction);
			int numberOfDim = instruction.getArrayDimensions().size();
			// TODO: check if we could leave out the array structure if number of elements is one
			//       it appears that we still need to keep the array structure, hence below code is commented out
//			boolean useOne = numberOfDim < 1 || 
//					(numberOfDim == 1 && instruction.getArrayDimensions().get(0) < 2);
//			if (useOne) {
//				data.add(unpackOne(instruction, b));
//			} else {
//				data.add(unpackArray(0, instruction, b));
//			}
			if (numberOfDim > 0) {
				data.add(unpackArray(0, instruction, b));
			} else {
				data.add(unpackOne(instruction, b));
			}
		}
		return data;
	}

	private static DataArray unpackArray(int dim, ParsedInstruction instruction, ByteBuffer b) throws MyRodsException {
		DataArray array = new DataArray(instruction.getName());
		for (int i = 0; i < instruction.getArrayDimensions().get(dim); i++) {
			if (instruction.getArrayDimensions().size() - dim > 1) {
				// need to drill down another level
				array.add(unpackArray(dim + 1, instruction, b));
			} else {
				array.add(unpackOne(instruction, b));
			}
		}
		return array;
	}

	private static Data unpackOne(ParsedInstruction instruction, ByteBuffer b) throws MyRodsException {
		String name = instruction.getName();
		int alloc = instruction.getAllocationSize();
		switch (instruction.getType()) {
		case INT16: return new DataInt16(name, b); 
		case INT: return new DataInt(name, b);
		case DOUBLE: return new DataDouble(name, b);
		case BIN: return new DataBinArray(name, b, alloc);
		case CHAR: return new DataCharArray(name, b, alloc);
		case STR: return new DataString(name, b, alloc);
		case PISTR: return new DataPIStr(name, b, alloc);
		case STRUCT: return unpackNative(name, b);
		default: 
		}
		// the types DEPENDS and ARRAY should never by returned by instruction.getType()
		return null;
	}
	
	

}
