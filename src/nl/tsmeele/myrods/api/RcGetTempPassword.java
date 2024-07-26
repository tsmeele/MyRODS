package nl.tsmeele.myrods.api;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import nl.tsmeele.myrods.apiDataStructures.Api;

/**
 * API call to receive a hash for building a one-time password for the user stated at rcConnect.
 * The received hash must be combined with an existing native-auth password known to the
 * user, to generate a new native-auth password that can be used only *once* to authenticate.
 * 
 * The server configuration parameter "default_temporary_password_lifetime_in_seconds" can be set
 * to require that the user consumes the token within a specific amount of time (default is 120 seconds).
 * 
 * @author Ton Smeele
 *
 */
public class RcGetTempPassword extends RodsApiCall {
	
	public RcGetTempPassword() {
		super(Api.GET_TEMP_PASSWORD_AN);
	}

	@Override
	public String unpackInstruction() {
		return "getTempPasswordOut_PI";
	}
	

	/**
	 * Creates a (short-lived) password suitable for one-time-use-only native authentication.
	 * @param stringToHashWith  as returned by server upon API-Request GET_TEMP_PASSWORD_AN
	 * @param password an existing password suitable for native authentication
	 * @return short-lived password suitable for native authentication
	 * @throws NoSuchAlgorithmException
	 */
	public static String createTempPassword(String stringToHashWith, String password) throws NoSuchAlgorithmException  {
		String totalStr = stringToHashWith + password;
		// padd with '\0' to exactly 100 bytes
		byte[] total = totalStr.getBytes();
		byte[] b100 = new byte[100];
		for (int i = 0; i < 100; i++) {
			if (i < total.length) {
				b100[i] = total[i];
			} else {
				b100[i] = 0;
			}
		}
		// hash the result
		MessageDigest digest = null;
		digest = MessageDigest.getInstance("MD5");
		byte[] hashed = digest.digest(b100);
		// return as hex-string representation
		StringBuilder sb = new StringBuilder(hashed.length * 2);
		for(byte b: hashed) {
		   sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
	
	
	
}
