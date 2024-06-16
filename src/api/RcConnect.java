package api;

import java.io.IOException;

import irodsType.DataInt;
import irodsType.DataString;
import irodsType.DataStruct;
import plumbing.IrodsMessage;
import plumbing.IrodsPackedMessage;
import plumbing.IrodsMessageType;
import plumbing.IrodsProtocolType;
import plumbing.IrodsSession;
import plumbing.MyRodsException;

/**
 * API call to establish a connection with an iRODS server.
 * @author Ton Smeele
 *
 */
public class RcConnect extends RcApi {
	private static final String RELVERSION = "4.3.2";
	private static final String APIVERSION = "d";

	private IrodsProtocolType requestedProtocol = IrodsProtocolType.NATIVE_PROT;
	
	public RcConnect(int reconnFlag, int connectCnt,
			String proxyUser, String proxyZone, String clientUser, String clientZone) {
		DataStruct message = new DataStruct("StartupPack_PI");
		message.add(new DataInt("irodsProt", requestedProtocol.getid()));
		message.add(new DataInt("reconnFlag", reconnFlag));
		message.add(new DataInt("connectCnt", connectCnt));
		message.add(new DataString("proxyUser",proxyUser));
		message.add(new DataString("proxyRcatZone", proxyZone));
		message.add(new DataString("clientUser", clientUser));
		message.add(new DataString("clientRcatZone", clientZone));
		message.add(new DataString("relVersion", RELVERSION));
		message.add(new DataString("apiVersion", APIVERSION));
		message.add(new DataString("option", ""));
		msg = new IrodsMessage(IrodsMessageType.RODS_CONNECT);
		msg.setMessage(message);
	}
	
	@Override
	public IrodsMessage getIrodsMessage() {
		return msg;
	}

	@Override
	public IrodsMessage sendTo(IrodsSession session) throws IOException, MyRodsException {
		// By convention, RODS_CONNECT type messages always use XML protocol for request/reply exchange
		session.updateProtocol(IrodsProtocolType.XML_PROT);  
	    session.getOutputStream().writeMessage(msg);
		IrodsPackedMessage pMsg = session.getInputStream().readMessage();
		IrodsMessage reply = pMsg.unpack(unpackInstruction());
		// subsequent message exchanges will use the agreed-upon protocol 
		session.updateProtocol(requestedProtocol);
		return reply;
	}

	@Override
	public String unpackInstruction() {
		return "Version_PI";
	}




	
	

}
