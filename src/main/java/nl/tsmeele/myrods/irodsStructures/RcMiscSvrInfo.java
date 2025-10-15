package nl.tsmeele.myrods.irodsStructures;


import nl.tsmeele.myrods.api.Api;


/**
 * API call to request information on attributes of the iRODS server.
 * Supports iRODS servers prior to iRODS 4.3.4.
 * Please use class RcMiscSvrinfo434 for connections with iRODS servers 4.3.4+
 * @author Ton Smeele
 *
 */
public class RcMiscSvrInfo extends RodsApiCall {
	
	public RcMiscSvrInfo() {
		super(Api.GET_MISC_SVR_INFO_AN);
	}


	@Override
	public String unpackInstruction() {
		return "MiscSvrInfo_PI";
	}

	
}
