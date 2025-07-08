package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.DataObjInp;

public class RcDataObjTrim extends RodsApiCall {

	/*
	 * Elements of dataObjInp used: 
	 * 		objPath - the path of the data object. 
	 * 		condInput - conditional Input 
	 * 			COPIES_KW - The number of copies to retain. Default is 2.
	 * 			REPL_NUM_KW - "value" = The replica number to trim. 
	 * 			RESC_NAME_KW - "value" = The Resource to trim. 
	 * 			ADMIN_KW - Admin trim other users' files. 
	 * 
	 */

	public RcDataObjTrim(DataObjInp dataObjInp) {
		super(Api.DATA_OBJ_TRIM_AN);
		msg.setMessage(dataObjInp);
	}

	@Override
	public String unpackInstruction() {
		return null; // returns intInfo = 0 on success
	}

}
