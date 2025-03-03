package nl.tsmeele.myrods.irodsDataTypes;

import java.io.IOException;

import nl.tsmeele.myrods.api.RodsCall;
import nl.tsmeele.myrods.apiDataStructures.IrodsCsNegResult;
import nl.tsmeele.myrods.apiDataStructures.IrodsCsNegType;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.apiDataStructures.MessageType;
import nl.tsmeele.myrods.plumbing.DataEncryptConfig;
import nl.tsmeele.myrods.plumbing.IrodsProtocolType;
import nl.tsmeele.myrods.plumbing.ServerConnection;
import nl.tsmeele.myrods.plumbing.MyRodsException;
import nl.tsmeele.myrods.plumbing.MessageSerializer;
import nl.tsmeele.myrods.plumbing.SessionDetails;

/**
 * API call to establish a connection with an iRODS server.
 * @author Ton Smeele
 *
 */
public class RcConnect extends RodsCall {
	private static final int NEGO_SUCCESS = 1;
	
	private static final String RELVERSION = "rods4.3.1";
	private static final String APIVERSION = "d";
	private static final String OPTION = ";request_server_negotiation";

	private IrodsProtocolType requestedProtocol = IrodsProtocolType.NATIVE_PROT;
	private IrodsCsNegType clientPolicy = IrodsCsNegType.CS_NEG_DONT_CARE;
	private String applicationName = "MyRods";
		
	
	/**
	 * Connect with default application name and client policy
	 * @param reconnFlag
	 * @param connectCnt
	 * @param proxyUser
	 * @param proxyZone
	 * @param clientUser
	 * @param clientZone
	 */
	public RcConnect(int reconnFlag, int connectCnt,
			String proxyUser, String proxyZone, String clientUser, String clientZone) {
		
		connect(reconnFlag, connectCnt, proxyUser, proxyZone, clientUser, clientZone, 
				applicationName, clientPolicy);
	}
	
	/**
	 * Connect using specified application name and client policy
	 * @param reconnFlag
	 * @param connectCnt
	 * @param proxyUser
	 * @param proxyZone
	 * @param clientUser
	 * @param clientZone
	 * @param applicationName
	 * @param clientPolicy
	 */
	public RcConnect(int reconnFlag, int connectCnt,
			String proxyUser, String proxyZone, String clientUser, String clientZone,
			String applicationName, IrodsCsNegType clientPolicy) {
		
		connect(reconnFlag, connectCnt, proxyUser, proxyZone, clientUser, clientZone, 
				applicationName, clientPolicy);
	}
	
	/**
	 * Connect with full flexibility
	 * @param irodsProt
	 * @param reconnFlag
	 * @param connectCnt
	 * @param proxyUser
	 * @param proxyZone
	 * @param clientUser
	 * @param clientZone
	 * @param applicationNAme
	 */
	public RcConnect(IrodsProtocolType irodsProt, int reconnFlag, int connectCnt,
			String proxyUser, String proxyZone, String clientUser, String clientZone, 
			String applicationName, IrodsCsNegType clientPolicy) {
		requestedProtocol = irodsProt;
		connect(reconnFlag, connectCnt, proxyUser, proxyZone, clientUser, clientZone, 
				applicationName, clientPolicy);
	}
	

	
//	public void setRequestedProtocol(IrodsProtocolType protocol) {
//		if (protocol != null) {
//			requestedProtocol = protocol;
//		}
//	}
//	
//	public void setClientPolicy(IrodsCsNegType policy) {
//		if (policy != null) {
//			clientPolicy = policy;
//		}
//	}
//	
//	public void setApplicationName(String name) {
//		if (name != null) {
//			applicationName = name;
//		}
//	}

	public void connect(int reconnFlag, int connectCnt, String proxyUser, String proxyZone, 
			String clientUser, String clientZone, String applicationName, 
			IrodsCsNegType clientPolicy) {
		DataStruct message = new DataStruct("StartupPack_PI");
		message.add(new DataInt("irodsProt", requestedProtocol.getId()));
		message.add(new DataInt("reconnFlag", reconnFlag));
		message.add(new DataInt("connectCnt", connectCnt));
		message.add(new DataString("proxyUser",proxyUser));
		message.add(new DataString("proxyRcatZone", proxyZone));
		message.add(new DataString("clientUser", clientUser));
		message.add(new DataString("clientRcatZone", clientZone));
		message.add(new DataString("relVersion", RELVERSION));
		message.add(new DataString("apiVersion", APIVERSION));
		message.add(new DataString("option", applicationName + OPTION));
		msg = new Message(MessageType.RODS_CONNECT);
		msg.setMessage(message);
		this.clientPolicy = clientPolicy;
	}
	

	@Override
	public Message sendTo(ServerConnection session) throws IOException, MyRodsException { 
		// try to reuse details from a prior connection


		// By convention, RODS_CONNECT type messages always use XML protocol for request/reply exchange
		session.updateProtocol(IrodsProtocolType.XML_PROT);  
	    session.getOutputStream().writeMessage(msg);
		MessageSerializer pMsg = session.getInputStream().readMessage();
		Message reply = null;
		if (pMsg.getType() == MessageType.RODS_CS_NEG_T) {
			// server is iRODS v4+ and ready to negotiate
			reply = pMsg.unpack("CS_NEG_PI");
			// we respond with a negotiation message, and receive a RODS_VERSION message in return
			reply = makeNegotiationRoundtrip(session, reply);
		} else {
			// server is Consumer type, or server is iRODS v3 or older, does not support negotiation
			// the reply will be a RODS_VERSION type msg
			reply = pMsg.unpack(unpackInstruction());
		}
		// process the version reply message
		DataStruct m = reply.getMessage();
		DataString relVersion = (DataString) m.lookupName("relVersion");
		DataString apiVersion = (DataString) m.lookupName("apiVersion");
		SessionDetails sd = session.getSessionDetails();
		sd.relVersion = relVersion;
		sd.apiVersion = apiVersion;
		sd.reconnPort = (DataInt) m.lookupName("reconnPort");
		sd.reconnAddr = (DataString) m.lookupName("reconnAddr");
		sd.cookie = (DataInt) m.lookupName("cookie");
		sd.connectMsg = msg.getMessage();
		
		// subsequent message exchanges will use the agreed-upon protocol
		requestedProtocol = negotiateXmlProtocol(requestedProtocol, relVersion);
		session.updateProtocol(requestedProtocol);
		return reply;
	}

	@Override
	public String unpackInstruction() {
		return "Version_PI";
	}
	
	private Message makeNegotiationRoundtrip(ServerConnection session, Message reply) throws IOException {
		DataInt status = (DataInt) reply.getMessage().lookupName("status");
		DataString result = (DataString) reply.getMessage().lookupName("result");
		if (status == null || status.get() != NEGO_SUCCESS) {
			throw new MyRodsException("Error during connect: Server refuses to negotiate");
		}
		IrodsCsNegType serverPolicy = IrodsCsNegType.lookup(result.get());
		if (serverPolicy == null) {
			throw new MyRodsException("Error during connect: Unrecognized server policy");
		}
		SessionDetails sessionDetails = session.getSessionDetails();
		sessionDetails.serverPolicy = serverPolicy;
		
		// perform policy negotiation
		IrodsCsNegResult negResult = calcNegoResult(clientPolicy, serverPolicy);
		
		// send negotiation result to server
		DataStruct message = new DataStruct("CS_NEG_PI");
		message.add( new DataInt("status", NEGO_SUCCESS) );
		message.add( new DataString("result", "cs_neg_result_kw=" + negResult.getLabel() + ";")  );
		Message negoMsg = new Message(MessageType.RODS_CS_NEG_T);
		negoMsg.setMessage(message);
	    session.getOutputStream().writeMessage(negoMsg);
	    
	    // first receive the reply from server, before applying the negotiation result
	    // the reply will be a RODS_VERSION msg
		MessageSerializer pMsg = session.getInputStream().readMessage();
		Message versionMsg = pMsg.unpack(unpackInstruction());
		// now take action based on the negotiation result
		switch (negResult) {
		case CS_NEG_FAILURE:
			// send a RODS_DISCONNECT to the server and close the connection
			Message disconnect = new Message(MessageType.RODS_DISCONNECT);
			session.getOutputStream().writeMessage(disconnect);
			session.disconnect();
			throw new MyRodsException("Error during connect: Policies Client/Server are incompatible");
		case CS_NEG_USE_TCP:
			break;
		case CS_NEG_USE_SSL: {
			session.startSSL();
			// we will need to encrypt data transfers, set encryption config
			DataEncryptConfig encrypt = sessionDetails.dataEncryptConfig;
			if (encrypt == null) {
				// not yet established, create new using default settings
				encrypt = new DataEncryptConfig();
				sessionDetails.dataEncryptConfig = encrypt;
			}
			// send desired data transfer encryption config to server
			encrypt.sendConfigTo(session.getOutputStream());
			break;
		}
		}		
		// return the expected RODS_VERSION message
		return versionMsg;
	}

	
	private IrodsCsNegResult calcNegoResult(IrodsCsNegType client, IrodsCsNegType server) {
		// see: lib/core/src/irods_client_negotiation.cpp
		switch (client) {
		case CS_NEG_REQUIRE: {
			switch (server) {
			case CS_NEG_REQUIRE:
			case CS_NEG_DONT_CARE:
				return IrodsCsNegResult.CS_NEG_USE_SSL;
			case CS_NEG_REFUSE:
				return IrodsCsNegResult.CS_NEG_FAILURE;
			}
		}
		case CS_NEG_DONT_CARE: {
			switch (server) {
			case CS_NEG_DONT_CARE:
			case CS_NEG_REQUIRE:
				return IrodsCsNegResult.CS_NEG_USE_SSL;
			case CS_NEG_REFUSE:
				return IrodsCsNegResult.CS_NEG_USE_TCP;
			}
		}
		case CS_NEG_REFUSE: {
			switch (server) {
			case CS_NEG_REQUIRE:
				return IrodsCsNegResult.CS_NEG_FAILURE;
			case CS_NEG_DONT_CARE:
			case CS_NEG_REFUSE:
				return IrodsCsNegResult.CS_NEG_USE_TCP;
			}
		}
		
		} // end switch(client)
		return IrodsCsNegResult.CS_NEG_FAILURE;
	}

	// if we use XML protocol, then:
	//     if the server version is < 4.2.9,  always specify requested protocol as XML_PROT
	//     if the server version is >= 4.2.9, server will expect us to pack according to
	//         our client version.  (as specified in variable RELVERSION)
	//            - Use XML_PROT if our client version < rods4.2.9   
	//            - Use XML_PROT429 if our client version >= rods4.2.9
	
	private IrodsProtocolType negotiateXmlProtocol(IrodsProtocolType requestedProtocol,
			DataString relVersion) {
		if (requestedProtocol == IrodsProtocolType.NATIVE_PROT) {
			return requestedProtocol;
		}
		String strRelVersion = relVersion.get() == null ? "" : relVersion.get(); 
		if ( strRelVersion.compareToIgnoreCase("rods4.2.9") < 0) {
			// peer is older server version, needs XML_PROT rather than XML_PROT429
			return IrodsProtocolType.XML_PROT;
		}
		if ( RELVERSION.compareToIgnoreCase("rods4.2.9") < 0) {
			// we pretend to be older client version, server will use XML_PROT rather than XML_PROT429
			return IrodsProtocolType.XML_PROT;
		}
		// both client and server use newer XMl protocol 
		return IrodsProtocolType.XML_PROT429;
	}

	
	

}
