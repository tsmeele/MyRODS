package nl.tsmeele.myrods.api;

import java.io.IOException;

import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.plumbing.IrodsSession;
import nl.tsmeele.myrods.plumbing.MyRodsException;
import nl.tsmeele.myrods.plumbing.PackedMessage;

/**
 * Superclass for all API calls to iRODS server.
 * @author Ton Smeele
 *
 */
public abstract class RodsCall {
	protected Message msg = null;

	/**
	 * Sends a request to the server and receives a reply in return.
	 * Subclasses can override this behavior 
	 * @param session
	 * @return reply from server
	 * @throws IOException
	 * @throws MyRodsException
	 */
	public Message sendTo(IrodsSession session) throws IOException, MyRodsException {
		session.getOutputStream().writeMessage(msg);
		PackedMessage reply = session.getInputStream().readMessage();
		return reply.unpack(unpackInstruction());
	}
	
	public Message getIrodsMessage() {
		return msg;
	}

	public String toString() {
		return msg.toString();
	}
	
	public abstract String unpackInstruction();
}
