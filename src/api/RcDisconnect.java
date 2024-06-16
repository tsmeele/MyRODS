package api;

import plumbing.IrodsSession;

import java.io.IOException;

import plumbing.IrodsMessage;
import plumbing.IrodsMessageType;

/**
 * Api call to end a connection to an iRODS server.
 * @author Ton Smeele
 *
 */
public class RcDisconnect extends RcApi {

	public RcDisconnect() {
		msg = new IrodsMessage(IrodsMessageType.RODS_DISCONNECT);
	}
	

	@Override
	public IrodsMessage sendTo(IrodsSession session) throws IOException {
		// construct and send a trivial message, only the message type is specified
		session.getOutputStream().writeMessage(msg);
		
		// give the other side a little time to receive and digest the disconnect request message
		try {
			Thread.sleep(50L);
		} catch (InterruptedException e) { /* ignore */	}
		
		session.disconnect();
		return null;
	}


	@Override
	public String unpackInstruction() {
		return null; // not used upon disconnect
	}


}
