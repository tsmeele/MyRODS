package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.Message;
import nl.tsmeele.myrods.api.MessageType;

/**
 * Superclass for all RODS_API type API calls to iRODS server.
 * Presets the api number in the RodsMessage
 * @author Ton Smeele
 *
 */
public abstract class RodsApiCall extends RodsCall {
	
	public RodsApiCall(Api api) {
		msg = new Message(MessageType.RODS_API_REQ);
		msg.setIntInfo(api);
	}

}
