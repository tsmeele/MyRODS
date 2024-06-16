package api;


import plumbing.IrodsMessage;
import plumbing.IrodsMessageType;

/**
 * API call to request information on attributes of the iRODS server.
 * @author Ton Smeele
 *
 */
public class RcMiscSvrInfo extends RcApi {
	
	public RcMiscSvrInfo() {
		msg = new IrodsMessage(IrodsMessageType.RODS_API_REQ);
		msg.setIntInfo(Api.GET_MISC_SVR_INFO_AN);
	}


	@Override
	public String unpackInstruction() {
		return "MiscSvrInfo_PI";
	}


}
