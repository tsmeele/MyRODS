package api;


import plumbing.IrodsMessage;
import plumbing.IrodsMessageType;

/**
 * API call to request native authentication.
 * @author Ton Smeele
 *
 */
public class RcAuthRequest extends RcApi {

	public RcAuthRequest() {
		msg = new IrodsMessage(IrodsMessageType.RODS_API_REQ);
		msg.setIntInfo(Api.AUTH_REQUEST_AN);
	}
	

	@Override
	public String unpackInstruction() {
		return "authRequestOut_PI";
	}

}
