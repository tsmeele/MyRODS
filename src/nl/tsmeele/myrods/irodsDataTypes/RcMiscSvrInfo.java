package nl.tsmeele.myrods.irodsDataTypes;


import nl.tsmeele.myrods.api.RodsApiCall;
import nl.tsmeele.myrods.apiDataStructures.Api;


/**
 * API call to request information on attributes of the iRODS server.
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
