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
 * API call to request upgrade of current regular TCP connection to SSL.
 * @author Ton Smeele
 *
 */
public class RcSslStart extends RcApi {

	public RcSslStart(DataString arg0) {
		msg = new IrodsMessage(IrodsMessageType.RODS_API_REQ);
		msg.setIntInfo(Api.SSL_START_AN);
		DataStruct message = new DataStruct("sslStartInp_PI");
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

	@Override
	public IrodsMessage sendTo(IrodsSession session) throws IOException, MyRodsException {
		session.getOutputStream().writeMessage(msg);
		IrodsPackedMessage reply = session.getInputStream().readMessage();
		IrodsMessage unpackedMessage = reply.unpack(unpackInstruction());
		if (unpackedMessage.getIntInfo() == 0) {
			// server agrees to switching to SSL, make the change
			session.startSSL();
		}
		return unpackedMessage;
	}
	
}
