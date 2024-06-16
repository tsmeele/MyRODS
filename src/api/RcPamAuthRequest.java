package api;

import irodsType.DataInt;
import irodsType.DataString;
import irodsType.DataStruct;
import plumbing.IrodsMessage;
import plumbing.IrodsMessageType;

/**
 * API call to authenticate using a PAM (plaintext) password.
 * NB: For security reasons, *only* use this API request when communication protocol has SSL on.
 * After succesful authentication, the reply will return an iRODS native-auth password that expires after ttl.
 * The native-auth password can be stored and reused in future connections using native authentication. 
 * @author Ton Smeele
 *
 */
public class RcPamAuthRequest extends RcApi {

	/**
	 * API call PAM_AUTH_REQUEST_AN.
	 * note that ony *local zone* iRODS users can be authenticated via this method
	 * @param pamUser name of an iRODS user, specified without zone suffix
	 * @param pamPassword password that will be checked via PAM callout
	 * @param ttl time-to-live as requested for returned iRODS (native) password
	 */
	public RcPamAuthRequest(DataString pamUser, DataString pamPassword, DataInt ttl) {
		msg = new IrodsMessage(IrodsMessageType.RODS_API_REQ);
		msg.setIntInfo(Api.PAM_AUTH_REQUEST_AN);
		DataStruct message = new DataStruct("pamAuthRequestInp_PI");
		message.add(new DataString("pamUser", pamUser.get()));
		message.add(new DataString("pamPassword", pamPassword.get()));
		message.add(new DataInt("timeToLive", ttl.get()));
		msg.setMessage(message);
	}
	
	@Override
	public String unpackInstruction() {
		return "pamAuthRequestOut_PI";
	}

}
