package nl.tsmeele.myrods.api;

import java.io.IOException;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;
import nl.tsmeele.myrods.plumbing.IrodsSession;
import nl.tsmeele.myrods.plumbing.MyRodsException;
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

	public Message sendTo(IrodsSession session) throws IOException, MyRodsException {
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
