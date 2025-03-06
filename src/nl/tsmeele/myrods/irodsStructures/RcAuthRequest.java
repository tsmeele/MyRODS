package nl.tsmeele.myrods.irodsStructures;


import nl.tsmeele.myrods.apiDataStructures.Api;


/**
 * API call to request/initiate native authentication.
 * Returns a challenge for use by the client to encode its native password
 * @author Ton Smeele
 *
 */
public class RcAuthRequest extends RodsApiCall {

	public RcAuthRequest() {
		super(Api.AUTH_REQUEST_AN);
	}
	

	@Override
	public String unpackInstruction() {
		return "authRequestOut_PI";
	}

}
