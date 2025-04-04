package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class RErrMsg extends DataStruct {
	
	public int status;
	public String msg;
	
	// "RErrMsg_PI", "int status; str msg[ERR_MSG_LEN];",

	public RErrMsg(DataStruct dataStruct) {
		super("RErrMsg_PI");
		addFrom(dataStruct);
		status = lookupInt("status");
		msg = lookupString("msg");
	}
	

}
