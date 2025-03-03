package nl.tsmeele.myrods.irodsDataTypes;

import java.io.IOException;

import nl.tsmeele.myrods.api.RodsApiCall;
import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.plumbing.ServerConnection;
import nl.tsmeele.myrods.plumbing.MyRodsException;
import nl.tsmeele.myrods.plumbing.MessageSerializer;

/**
 * API call to request upgrade of current regular TCP connection to SSL.
 * @author Ton Smeele
 *
 */
public class RcSslStart extends RodsApiCall {

	public RcSslStart(String arg0) {
		super(Api.SSL_START_AN);
		DataStruct message = new DataStruct("sslStartInp_PI");
		message.add(new DataString("arg0", arg0) );
		msg.setMessage(message);
	}
	
	@Override
	public String unpackInstruction() {
		return "";	
	}

	@Override
	public Message sendTo(ServerConnection session) throws IOException, MyRodsException {
		session.getOutputStream().writeMessage(msg);
		MessageSerializer reply = session.getInputStream().readMessage();
		Message unpackedMessage = reply.unpack(unpackInstruction());
		if (unpackedMessage.getIntInfo() == 0) {
			// server agrees to switching to SSL, make the change
			session.startSSL();
		}
		return unpackedMessage;
	}
	
}
