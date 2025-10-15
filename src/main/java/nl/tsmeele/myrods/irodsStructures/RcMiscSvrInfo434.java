package nl.tsmeele.myrods.irodsStructures;


import nl.tsmeele.myrods.api.Api;


/**
 * API call to request information on attributes of the iRODS server.
 * This variant supports the extended reply message, introduced per iRODS 4.3.4
 * @author Ton Smeele
 *
 */
public class RcMiscSvrInfo434 extends RodsApiCall {
	
	public RcMiscSvrInfo434() {
		super(Api.GET_MISC_SVR_INFO_AN);
	}


	@Override
	public String unpackInstruction() {
		return "MiscSvrInfo434_PI";	// miscSvrInfo_PI was updated per iRODS 4.3.4
	}

	
}
