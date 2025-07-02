package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.DataObjInp;

public class RcDataObjRepl extends RodsApiCall {

	/*
	 * Uses the following parts of DataObjInp structure:
	 * 
	 * objPath - the path of the data object.
	 * 
	 * condInput KeyValPair - valid keywords are: 
	 * ALL_KW - update all copies. 
	 * DATA_TYPE_KW - "value" = the data type of the file.
	 * REPL_NUM_KW - "value" = The replica number to use as source copy. (optional)
	 * RESC_NAME_KW - "value" = The source Resource (optional). 
	 * DEST_RESC_NAME_KW - "value" = The destination Resource. 
	 * ADMIN_KW - Admin removing other users' files. Only files in trash can be removed.
	 * 
	 */
	
	
	public RcDataObjRepl(DataObjInp dataObjInp) {
		super(Api.DATA_OBJ_REPL_AN);
		msg.setMessage(dataObjInp);
	}

	@Override
	public String unpackInstruction() {
		return null;
	}

}
