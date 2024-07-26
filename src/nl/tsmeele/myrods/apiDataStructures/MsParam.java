package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsDataTypes.Data;
import nl.tsmeele.myrods.irodsDataTypes.DataBinArray;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataPIStr;
import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

public class MsParam extends DataStruct {

	// "MsParam_PI", "str *label; piStr *type; ?type *inOutStruct; struct *BinBytesBuf_PI;",

	public MsParam(String label, String type, DataStruct inOutStruct, BinBytesBuf binBytesBuf) {
		super("MsParam_PI");
		add(new DataPtr("label", new DataString("label", label)));
		add(new DataPtr("type", new DataPIStr("type", type)));
		add(new DataPtr(type, inOutStruct));
		add(new DataPtr("binBytesBuf", binBytesBuf));
	}
	
	public MsParam(String label, String stringValue) {
		super("MsParam_PI");
		add(new DataPtr("label", new DataString("label", label)));
		add(new DataPtr("type", new DataPIStr("type", "STR_PI")));
		DataStruct inOutStruct = new DataStruct("STR_PI");
		inOutStruct.add(new DataString("myStr", stringValue));
		add(new DataPtr("STR_PI", inOutStruct));
		add(new DataPtr("binBytesBuf", null));
	}
	
	public MsParam(String label, int intValue) {
		super("MsParam_PI");
		add(new DataPtr("label", new DataString("label", label)));
		add(new DataPtr("type", new DataPIStr("type", "INT_PI")));
		DataStruct inOutStruct = new DataStruct("INT_PI");
		inOutStruct.add(new DataInt("myInt", intValue));
		add(new DataPtr("INT_PI", inOutStruct));
		add(new DataPtr("binBytesBuf", null));
	}
	
	/**
	 * Construct MsParam from an existing DataStruct (that must be likely formatted!)
	 * @param msParam existing DataStruct object
	 */
	public MsParam(DataStruct msParam) {
		super("MsParam_PI");
		for (Data d : msParam) {
			add(d);
		}
	}
	
	
	public String getParamName() {
		return lookupString("label");
	}
	
	public String getParamType() {
		return lookupString("type");
	}
	
	public DataStruct getParamContent() {
		DataPtr content = (DataPtr) get(2); // regular struct
		if (content.get() != null) {
			return (DataStruct) content.get();
		}
		return null;
	}
	
	public byte[] getParamByteContent() {
		DataPtr content = (DataPtr) get(3); // binBytesbuf
		if (content == null) {
			return null;
		}
		DataStruct binBytesBuf = (DataStruct) content.get();
		if (binBytesBuf == null || binBytesBuf.size() != 2) {
			return null;
		}
		DataPtr bufPtr = (DataPtr) binBytesBuf.get(1);
		if (bufPtr.get() == null) {
			return null;
		}
		return ((DataBinArray) bufPtr.get()).get();
	}

}
