package nl.tsmeele.myrods.plumbing;

import java.util.List;

import nl.tsmeele.log.Log;
import nl.tsmeele.myrods.irodsStructures.Data;
import nl.tsmeele.myrods.irodsStructures.DataArray;
import nl.tsmeele.myrods.irodsStructures.DataPtr;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

/** Class Unpacker unpacks data that was serialized in accordance with a predefined protocol. 
 * The data is unpacked using a pack instruction that specifies the expected sequence of data elements, 
 * their name, type and cardinality.
 * @author Ton Smeele
 *
 */
public class Unpacker {
	private static PackInstructions packMap = new PackInstructions();
	
	
	public static DataStruct unpack(IrodsProtocolType protocol, String packInstruction, byte[] packedData) throws MyRodsException {
		InputBuffer buf = null;
		switch (protocol) {
		case NATIVE_PROT:
			Log.debug("Unpack: using native protocol");
			buf = new InputBufferNative(packedData);
			break;
		case XML_PROT:
			Log.debug("Unpack: using legacy xml protocol");
			buf = new InputBufferXml(packedData);
			break;
		case XML_PROT429:
			Log.debug("Unpack: using 4.2.9+ xml protocol");
			buf = new InputBufferXml429(packedData);
		default:
			break;
			
		}
		DataStruct result = unpackStruct(null, packInstruction, buf);
		buf.endUnpack();
		return result;
	}
	
	private static DataStruct unpackStruct(DataStruct parent, String packInstruction, InputBuffer buf) throws MyRodsException {
		buf.startUnpackStruct(packInstruction);
		DataStruct dataStruct = new DataStruct(packInstruction); 
		Log.debug("IN unpackStruct: " + packInstruction);
		if (parent != null) {
			// make sure members are added to our scope as soon as they get evaluated
			// the pack instruction might reference the value of a variable defined in an earlier instruction
			dataStruct.setParent(parent);
		} else {
			parent = dataStruct;
		}
		List<String> piList = packMap.get(packInstruction);
		if (piList == null || piList.isEmpty()) {
			throw new MyRodsException("Unsupported Pack-Instruction (" + packInstruction + ")");
		}
		// use each instruction of the PI sequentially to unpack the elements of the the DataStruct
		for (String instruction : piList) {
			// parse instruction
			Log.debug("UnpackStruct, instruction =" + instruction);
			ParsedInstruction parsed = new ParsedInstruction((DataStruct) dataStruct, instruction);	
			// we need to cater for a possible mixture of array and pointer constructs
			if (parsed.getCardinality() > 1 ) {
				// case:  "type var[n]"  or  "type *var[n]"
				if (parsed.isPointer()) {
					// subcase is "type *var[n]"
					// we build an array of pointers
					dataStruct.add(unpackArray(dataStruct, parsed, buf));
					continue;		
				}
				// subcase is "type var[n]"
				if (parsed.getType().isArrayType()) {
					// arraytypes use cardinality as an allocation size
					// we really need to unpack just one, not an array
					dataStruct.add(unpackVariable(dataStruct, parsed, buf));
					continue;
				}
				// not an arraytype, hence cardinality requires to build an array
				dataStruct.add(unpackArray(dataStruct, parsed, buf));
				continue;
			}
			if (parsed.getCardinality() == 0) {
				// case:  "type var[0]"  (hence array is not present)
				continue;
			}
			if (parsed.isPointer()) {
				// case:  "type *var" 
				dataStruct.add(unpackPointer(dataStruct, parsed, buf));
				continue;
			}
			// case:  "type var"
			dataStruct.add(unpackVariable(dataStruct, parsed, buf));
		}
		buf.endUnpackStruct(packInstruction);
		return dataStruct;
	}
	
	private static DataArray unpackArray(DataStruct parent, ParsedInstruction parsed, InputBuffer buf) throws MyRodsException {
		DataArray dataArray = new DataArray(parsed.getName());
		Log.debug("IN unpackArray: " + parsed.getName() + " cardinality=" + parsed.getCardinality());
		dataArray.setParent(parent);
		for (int i = 0; i < parsed.getCardinality(); i++) {
			// case: "type *var[n]"
			if (parsed.isPointer()) {
				dataArray.add(unpackPointer(dataArray,parsed, buf));
				continue;
			}
			// case: "type var[n]"
			dataArray.add(unpackVariable(dataArray, parsed, buf));
		}
		return dataArray;
	}
	
	private static Data unpackPointer(DataStruct parent, ParsedInstruction parsed, InputBuffer buf) throws MyRodsException {
		String name = parsed.getName();
		DataPtr ptr = new DataPtr(name);
		Log.debug("IN unpackPointer: " + name);
		Log.debug(parsed.toString());
		ptr.setParent(parent);
		// read the buf to check if the pointer is a nullpointer
		if (!buf.unpackNullPointer(name)) {
			ptr.add(unpackVariable(ptr, parsed, buf));
		}
		Log.debug("UNPACKED PTR: " + ptr);
		return ptr;
	}
	
	
	private static Data unpackVariable(DataStruct parent, ParsedInstruction parsed, InputBuffer buf) throws MyRodsException {
		int allocSize = 1;
		int cardinality = parsed.getCardinality();
		List<Integer> dimensions = parsed.getDimensions();
		if (parsed.getType().isArrayType()) {
			if (parsed.isPointer()) {
				// the last dimension, if present, indicates the allocation size for each occurrence of the type
				if (!dimensions.isEmpty()) {
					allocSize = dimensions.remove(dimensions.size() - 1);
				} 
			} else {
				// cardinality is used as an allocation size
				allocSize = cardinality;
				cardinality = 1;
			}
		} 
		Log.debug("IN unpackVariable " + parsed.getName() + " allocSize=" + allocSize);
		if (!dimensions.isEmpty()) {
			// we need to unpack multiple instances of the same variable
			return unpackOccurrences(parent, parsed, 
					dimensions, allocSize, buf);
		}
		// else we unpack just one occurrence
		return unpackOne(parent, parsed, allocSize, buf);
	}
	
	private static Data unpackOccurrences(DataStruct parent, ParsedInstruction parsed, List<Integer> dimensions, 
			int allocSize, InputBuffer buf) throws MyRodsException {
		// we create an array per dimension 
		DataArray array = new DataArray(parsed.getName());
		array.setParent(parent);
		Log.debug("IN unpackOccurrences " + parsed.getName());
		int thisDimension = dimensions.remove(0);
		for (int i = 0; i < thisDimension; i++) {
			if (!dimensions.isEmpty()) {
				array.add(unpackOccurrences(array, parsed, dimensions, allocSize, buf));
			} else {
				array.add(unpackOne(array, parsed, allocSize, buf));
			}
		}
		return array;
	}
			
	private static Data unpackOne(DataStruct parent, ParsedInstruction parsed, int allocSize, InputBuffer buf) throws MyRodsException {
		String name = parsed.getName();
		Log.debug("In unpackOne " + name + " allocSize " + allocSize);
			switch (parsed.getType()) {
		case BIN:
			return buf.unpackBinArray(name, allocSize);
		case CHAR:
			return buf.unpackCharArray(name, allocSize);
		case INT: 
			return buf.unpackInt(name);
		case INT16:
			return buf.unpackInt16(name);
		case INT64:
			return buf.unpackInt64(name);
		case PISTR:
			return buf.unpackPIStr(name, allocSize);
		case STR:
			return buf.unpackString(name, allocSize);
		case STRUCT:
			return unpackStruct(parent, name, buf);
		default:
		}
		throw new MyRodsException("Unpack internal error, unhandled type " + parsed.getType().name());
	}	

}
