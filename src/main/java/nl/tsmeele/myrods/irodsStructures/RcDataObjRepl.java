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
	 * ADMIN_KW - Admin mode (override access restrictions) (requires rodsadmin privs)
	 * 
	 * undocumented?:
	 * VERIFY_CHKSUM_KW - compute and verify checksum
	 *                    NB: checksum must be present on source replica
	 * REG_CHKSUM_KW  - compute, do NOT verify, checksum
	 *                    NB: checksum (only) added to the new replica
	 * NO_COMPUTE_KW  - do NOT compute a checksum
	 * SU_CLIENT_USER_KW  - act as agent for client user (requires rodsadmin privs)
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
