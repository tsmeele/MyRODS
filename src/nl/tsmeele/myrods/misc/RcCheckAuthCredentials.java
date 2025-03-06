package nl.tsmeele.myrods.misc;


import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;
import nl.tsmeele.myrods.irodsStructures.RodsApiCall;

/**
 * Verifies the native authentication credentials passed match the credentials stored in the ICAT.
 * requires rodsadmin priv
 * @author Ton Smeele
 *
 */
public class RcCheckAuthCredentials extends RodsApiCall {
	
	public RcCheckAuthCredentials(String username, String zone, String password)  {
		super(Api.CHECK_AUTH_CREDENTIALS_AN);
		DataStruct message = new DataStruct("CheckAuthCredentialsInput_PI");
		message.add(new DataString("username", username));
		message.add(new DataString("zone", zone));
		message.add(new DataString("password", password)); 
		msg.setMessage(message);
	}

	@Override
	public String unpackInstruction() {
		// value  1 = correct,   other values = not correct
		return "INT_PI";	// TODO:  check output
	}

	
}
