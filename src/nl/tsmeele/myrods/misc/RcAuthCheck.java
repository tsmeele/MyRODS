package nl.tsmeele.myrods.misc;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.irodsStructures.DataPtr;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;
import nl.tsmeele.myrods.irodsStructures.RodsApiCall;

/**
 * Server to server request.
 * @author Ton Smeele
 *
 */
public class RcAuthCheck extends RodsApiCall {
	public RcAuthCheck(String challenge, String response, String username) {
		super(Api.AUTH_CHECK_AN);
		DataStruct message = new DataStruct("authCheckInp_PI");
		message.add(new DataPtr("challenge", new DataString("challenge", challenge)));
		message.add(new DataPtr("response", new DataString("response", response)));
		message.add(new DataPtr("username", new DataString("username", username)));
		msg.setMessage(message);
	}
	

	@Override
	public String unpackInstruction() {
		return "authCheckOut_PI";
	}

}
