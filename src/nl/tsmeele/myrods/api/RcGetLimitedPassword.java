package nl.tsmeele.myrods.api;

import java.security.NoSuchAlgorithmException;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

/**
 * API call to receive a hash for building a short-lived password for the user stated at rcConnect.
 * The received hash must be combined with an existing native-auth password known to the
 * user, to generate a new native-auth password that can be used temporarily to authenticate
 * @author Ton Smeele
 *
 */
public class RcGetLimitedPassword extends RodsApiCall {


	/**
	 * Request a time-limited password
	 * @param timeToLive	duration in hours (!)
	 * Note that internally iRODS will multiply the ttl value by 3600 and
	 * check against password_min_time/password_max_time boundaries specified in seconds
	 * 
	 * Pre-iRODS4.3.1:  if unspecified, ttl will default to:
	 *    in case no_extent=false :  1209600 seconds (2 weeks)
	 *    in case no_extent=true  :  28800 seconds (8 hours)
	 * iRODS4.3.1+ : 
	 *   rcGetLimitedPassword with timeToLive=0 as argument will usually fail as the 
	 *   value is below the password_min_time. Here the value 0 is *not* interpreted as unspecified.
	 *   rcPamAuthRequest with ttl=0 as argument means unspecified, hence use the default value which
	 *   will be password_min_time (this config parameter has a default of 121 seconds)
	 */
	public RcGetLimitedPassword(int timeToLive) {
		super(Api.GET_LIMITED_PASSWORD_AN);
		DataStruct message = new DataStruct("getLimitedPasswordInp_PI");
		message.add(new DataInt("ttl", timeToLive) );
		message.add(new DataPtr("unused",new DataString("unused", "")) );
		msg.setMessage(message);
	}
	
	@Override
	public String unpackInstruction() {
		return "getLimitedPasswordOut_PI";
	}

	public static String createLimitedPassword(String stringToHashWith, String password) throws NoSuchAlgorithmException  {
		return RcGetTempPassword.createTempPassword(stringToHashWith, password)	;
	}
	
}
