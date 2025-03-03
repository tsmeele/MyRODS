package nl.tsmeele.myrods.api;

import java.io.IOException;

import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.apiDataStructures.MessageType;
import nl.tsmeele.myrods.plumbing.ServerConnection;

/**
 * Api call to end a connection to an iRODS server.
 * @author Ton Smeele
 *
 */
public class RcDisconnect extends RodsCall {

	public RcDisconnect() {
		msg = new Message(MessageType.RODS_DISCONNECT);
	}
	

	@Override
	public Message sendTo(ServerConnection session) throws IOException {
		// construct and send a trivial message, only the message type is specified
		session.getOutputStream().writeMessage(msg);
		session.disconnect();
		return null;
	}


	@Override
	public String unpackInstruction() {
		return null; // not used upon disconnect
	}


}
