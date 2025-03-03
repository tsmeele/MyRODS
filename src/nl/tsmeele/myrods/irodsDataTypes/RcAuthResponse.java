package nl.tsmeele.myrods.irodsDataTypes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import nl.tsmeele.myrods.api.RodsApiCall;
import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.plumbing.PackInstructionsConstants;

/**
 * API call to provide proof of credentials for native authentication.
 * This call uses the challenge obtained through a prior RcAuthRequest call
 * @author Ton Smeele
 *
 */
public class RcAuthResponse extends RodsApiCall {
	
	public RcAuthResponse(String userNameAndZone, String password, byte[] challenge)  {
		super(Api.AUTH_RESPONSE_AN);
		DataStruct message = new DataStruct("authResponseInp_PI");
		DataBinArray response = null;
		try {
			response = new DataBinArray("response", calcResponse(challenge, password) );
		} catch (NoSuchAlgorithmException e) {
			// huh no MD5 available?? send null response, authentication will fail
			response = new DataBinArray("response", null);
		}
		message.add(response);
		message.add(new DataString("username", userNameAndZone));
		msg.setMessage(message);
	}

	@Override
	public String unpackInstruction() {
		return null;	// call has no output message
	}

	
	public static byte[] calcResponse(byte[] challenge, String passwordStr) throws NoSuchAlgorithmException {
		PackInstructionsConstants constants = new PackInstructionsConstants();
		byte[] password = passwordStr.getBytes();
		
		// create a byte array from the challenge + password, 
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
		// pad byte array with '\0' bytes upto maxlength
		for (int i = chalLen + pwdLen; i < maxChalLen + maxPwdLen; i++) {
				combined[i] = 0;
		}
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte[] hashed = digest.digest(combined);
		// convention to change all '0' to '1' to avoid confusion with string terminator
		for (int i = 0; i< hashed.length; i++) {
			if (hashed[i] == 0) {
				hashed[i] = 1;
			}
		}
		return hashed;	
	}
	

	
	
}
