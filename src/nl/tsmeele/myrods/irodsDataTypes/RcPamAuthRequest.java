package nl.tsmeele.myrods.irodsDataTypes;

import nl.tsmeele.myrods.api.RodsApiCall;
import nl.tsmeele.myrods.apiDataStructures.Api;

/**
 * API call to authenticate using a PAM (plaintext) password.
 * NB: For security reasons, *only* use this API request when communication protocol has SSL on.
 * After succesful authentication, the reply will return an iRODS native-auth password that expires after ttl.
 * The native-auth password can be stored and reused in future connections using native authentication. 
 * @author Ton Smeele
 *
 */
public class RcPamAuthRequest extends RodsApiCall {

	/**
	 * API call PAM_AUTH_REQUEST_AN.
	 * note that ony *local zone* iRODS users can be authenticated via this method
	 * @param pamUser name of an iRODS user, specified without zone suffix
	 * @param pamPassword password that will be checked via PAM callout
	 * @param ttl time-to-live in hours as requested for returned iRODS (native) password.
	 * A ttl value of 0 means ttl is unspecified.
	 * 
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
	 * 
	 * 
	 */
	public RcPamAuthRequest(String pamUser, String pamPassword, int ttl) {
		super(Api.PAM_AUTH_REQUEST_AN);
		DataStruct message = new DataStruct("pamAuthRequestInp_PI");
		message.add(new DataString("pamUser", pamUser));
		message.add(new DataString("pamPassword", pamPassword));
		message.add(new DataInt("timeToLive", ttl));
		msg.setMessage(message);
	}
	
	@Override
	public String unpackInstruction() {
		return "pamAuthRequestOut_PI";
	}

}
