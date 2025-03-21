package nl.tsmeele.myrods.irodsStructures;

import java.io.IOException;

import nl.tsmeele.myrods.plumbing.ServerConnection;
import nl.tsmeele.myrods.plumbing.MyRodsException;
import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.Message;
import nl.tsmeele.myrods.plumbing.MessageSerializer;

/**
 * API call to request downgrading of current SSL connection to regular TCP connection.
 * @author Ton Smeele
 *
 */
public class RcSslEnd extends RodsApiCall {

	public RcSslEnd(String arg0) {
		super(Api.SSL_END_AN);
		DataStruct message = new DataStruct("sslEndInp_PI");
		message.add(new DataString("arg0", arg0) );
		msg.setMessage(message);
	}
	
	@Override
	public String unpackInstruction() {
		return "";	
	}

	public Message sendTo(ServerConnection session) throws IOException, MyRodsException {
		session.getOutputStream().writeMessage(msg);
		MessageSerializer reply = session.getInputStream().readMessage();
		Message unpackedMessage = reply.unpack(unpackInstruction());
		if (unpackedMessage.getIntInfo() == 0) {
			// server agrees to switch back to regular TCP, make the change
			session.stopSSL();
		}
		return unpackedMessage;
	}
}
