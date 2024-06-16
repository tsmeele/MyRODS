package api;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import irodsType.DataBinArray;
import irodsType.DataString;
import irodsType.DataStruct;
import plumbing.IrodsMessage;
import plumbing.IrodsMessageType;
import plumbing.PackMapConstants;

/**
 * API call to provide credentials for native authentication.
 * @author Ton Smeele
 *
 */
public class RcAuthResponse extends RcApi {
	
	public RcAuthResponse(DataString username, DataString password, DataBinArray challenge)  {
		msg = new IrodsMessage(IrodsMessageType.RODS_API_REQ);
		msg.setIntInfo(Api.AUTH_RESPONSE_AN);
		DataStruct message = new DataStruct(unpackInstruction());
		DataBinArray response = null;
		try {
			response = new DataBinArray("response", calcResponse(challenge, password) );
		} catch (NoSuchAlgorithmException e) {
			// huh no MD5 available?? send null response, authentication will fail
			response = new DataBinArray("response", null);
		}
		message.add(response);
		message.add(new DataString("username", username.get()));
		msg.setMessage(message);
	}

	@Override
	public String unpackInstruction() {
		return "authResponseInp_PI";
	}

	
	public static byte[] calcResponse(DataBinArray challengeBin, DataString passwordStr) throws NoSuchAlgorithmException {
		byte[] challenge = challengeBin.get();
		PackMapConstants constants = new PackMapConstants();
		byte[] password = passwordStr.get().getBytes();
		
		// create a byte array from the challenge + password, 
		// pad with '0' upto maxlength
		int maxPwdLen  = constants.get("MAX_PASSWORD_LEN");
		int pwdLen  = Math.min(maxPwdLen, password.length);
		int maxChalLen = constants.get("CHALLENGE_LEN");
		int chalLen = Math.min(challenge.length, maxChalLen);
		byte[] combined = new byte[maxChalLen + maxPwdLen];
		for (int i = 0; i < chalLen; i++) {
			combined[i] = challenge[i];
		}
		for (int i = 0; i < pwdLen; i++) {
			combined[chalLen + i] = password[i];
		}
		// pad with '\0' bytes
		for (int i = chalLen + pwdLen; i < maxChalLen + maxPwdLen; i++) {
				combined[i] = 0;
		}
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte[] hashed = digest.digest(combined);
		// change all '0' to '1' to avoid string terminator confusion
		for (int i = 0; i< hashed.length; i++) {
			if (hashed[i] == 0) {
				hashed[i] = 1;
			}
		}
		return hashed;	
	}
	

	
	
}
