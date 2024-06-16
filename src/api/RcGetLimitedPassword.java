package api;

import java.security.NoSuchAlgorithmException;

import irodsType.DataInt;
import irodsType.DataString;
import irodsType.DataStruct;
import plumbing.IrodsMessage;
import plumbing.IrodsMessageType;

/**
 * API call to receive a hash for building a short-lived password for the user stated at rcConnect.
 * The received hash must be combined with an existing native-auth password known to the
 * user, to generate a new native-auth password that can be used temporarily to authenticate
 * @author Ton Smeele
 *
 */
public class RcGetLimitedPassword extends RcApi {

	// TODO: check timeToLive specified in hours?
	public RcGetLimitedPassword(DataInt timeToLive) {
		msg = new IrodsMessage(IrodsMessageType.RODS_API_REQ);
		msg.setIntInfo(Api.GET_LIMITED_PASSWORD_AN);
		DataStruct message = new DataStruct("getLimitedPasswordInp_PI");
		message.add(new DataInt("ttl", timeToLive.get()) );
		message.add(new DataString("unused", "") );
		msg.setMessage(message);
	}
	
	@Override
	public String unpackInstruction() {
		return "getLimitedPasswordOut_PI";
	}

	public static DataString createLimitedPassword(DataString stringToHashWith, DataString password) throws NoSuchAlgorithmException  {
		return RcGetTempPassword.createTempPassword(stringToHashWith, password)	;
	}
	
}
