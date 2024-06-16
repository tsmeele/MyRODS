package api;

import java.io.IOException;

import irodsType.DataString;
import irodsType.DataStruct;
import plumbing.IrodsMessage;
import plumbing.IrodsMessageType;
import plumbing.IrodsPackedMessage;
import plumbing.IrodsSession;
import plumbing.MyRodsException;

/**
 * API call to request downgrading of current SSL connection to regular TCP connection.
 * @author Ton Smeele
 *
 */
public class RcSslEnd extends RcApi {

	public RcSslEnd(DataString arg0) {
		msg = new IrodsMessage(IrodsMessageType.RODS_API_REQ);
		msg.setIntInfo(Api.SSL_END_AN);
		DataStruct message = new DataStruct("sslEndInp_PI");
		if (arg0 == null) {
			arg0 = new DataString("arg0",null);
		}
		message.add(new DataString("arg0", arg0.get()) );
		msg.setMessage(message);
	}
	
	@Override
	public String unpackInstruction() {
		return "";	
	}

	public IrodsMessage sendTo(IrodsSession session) throws IOException, MyRodsException {
		session.getOutputStream().writeMessage(msg);
		IrodsPackedMessage reply = session.getInputStream().readMessage();
		IrodsMessage unpackedMessage = reply.unpack(unpackInstruction());
		if (unpackedMessage.getIntInfo() == 0) {
			// server agrees to switch back to regular TCP, make the change
			session.stopSSL();
		}
		return unpackedMessage;
	}
}
