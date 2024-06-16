package api;

import java.io.IOException;

import plumbing.IrodsMessage;
import plumbing.IrodsPackedMessage;
import plumbing.IrodsSession;
import plumbing.MyRodsException;

/**
 * Superclass for all API calls to iRODS server.
 * @author Ton Smeele
 *
 */
public abstract class RcApi {
	protected IrodsMessage msg = null;

	/**
	 * Sends a request to the server and receives a reply in return.
	 * Subclasses can override this behavior
	 * @param session
	 * @return reply from server
	 * @throws IOException
	 * @throws MyRodsException
	 */
	public IrodsMessage sendTo(IrodsSession session) throws IOException, MyRodsException {
		session.getOutputStream().writeMessage(msg);
		IrodsPackedMessage reply = session.getInputStream().readMessage();
		return reply.unpack(unpackInstruction());
	}
	
	public IrodsMessage getIrodsMessage() {
		return msg;
	}

	
	public abstract String unpackInstruction();
}
