package api;

import java.io.IOException;

import irodsType.DataInt;
import irodsType.DataString;
import irodsType.DataStruct;
import plumbing.DataEncryptConfig;
import plumbing.IrodsCsNegResult;
import plumbing.IrodsCsNegType;
import plumbing.IrodsMessage;
import plumbing.IrodsPackedMessage;
import plumbing.IrodsMessageType;
import plumbing.IrodsProtocolType;
import plumbing.IrodsSession;
import plumbing.MyRodsException;
import plumbing.SessionDetails;

/**
 * API call to establish a connection with an iRODS server.
 * @author Ton Smeele
 *
 */
public class RcConnect extends RcApi {
	private static final int NEGO_SUCCESS = 1;
	
	private static final String RELVERSION = "4.3.2";
	private static final String APIVERSION = "d";
	private static final String OPTION = ";request_server_negotiation";

	private IrodsProtocolType requestedProtocol = IrodsProtocolType.XML_PROT;
	private IrodsCsNegType clientPolicy = IrodsCsNegType.CS_NEG_DONT_CARE;
	private String applicationName = "MyRods";
	
	private SessionDetails sessionDetails = null;
	
	
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
	
	public void connect(int reconnFlag, int connectCnt, String proxyUser, String proxyZone, 
			String clientUser, String clientZone, String applicationName, 
			IrodsCsNegType clientPolicy) {
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
		message.add(new DataString("option", applicationName + OPTION));
		msg = new IrodsMessage(IrodsMessageType.RODS_CONNECT);
		msg.setMessage(message);
		this.clientPolicy = clientPolicy;
	}
	
	@Override
	public IrodsMessage getIrodsMessage() {
		return msg;
	}

	@Override
	public IrodsMessage sendTo(IrodsSession session) throws IOException, MyRodsException { 
		// try to reuse details from a prior connection
		sessionDetails = session.getSessionDetails();
		if (sessionDetails == null) {
			sessionDetails = new SessionDetails();
		}
		// By convention, RODS_CONNECT type messages always use XML protocol for request/reply exchange
		session.updateProtocol(IrodsProtocolType.XML_PROT);  
	    session.getOutputStream().writeMessage(msg);
		IrodsPackedMessage pMsg = session.getInputStream().readMessage();
		IrodsMessage reply = null;
		if (pMsg.getType() == IrodsMessageType.RODS_CS_NEG_T) {
			// server is iRODS v4+ and ready to negotiate
			reply = pMsg.unpack("CS_NEG_PI");
			session.updateProtocol(requestedProtocol);
			// we respond with a negotiation message, and receive a RODS_VERSION message in return
			reply = makeNegotiationRoundtrip(session, reply);
		} else {
			// server is iRODS v3 or older, does not support negotiation
			// the reply will be a RODS_VERSION type msg
			reply = pMsg.unpack(unpackInstruction());
		}
		// process the version reply message
		sessionDetails.setCookie( (DataInt)reply.getMessage().lookupName("cookie"));
		// register session details with the session
		session.setSessionDetails(sessionDetails);
		
		// subsequent message exchanges will use the agreed-upon protocol 
		session.updateProtocol(requestedProtocol);
		return reply;
	}

	@Override
	public String unpackInstruction() {
		return "Version_PI";
	}
	
	private IrodsMessage makeNegotiationRoundtrip(IrodsSession session, IrodsMessage reply) throws IOException {
		DataInt status = (DataInt) reply.getMessage().lookupName("status");
		DataString result = (DataString) reply.getMessage().lookupName("result");
		if (status == null || status.get() != NEGO_SUCCESS) {
			throw new MyRodsException("Error during connect: Server refuses to negotiate");
		}
		IrodsCsNegType serverPolicy = IrodsCsNegType.get(result.get());
		if (serverPolicy == null) {
			throw new MyRodsException("Error during connect: Unrecognized server policy");
		}
		// perform policy negotiation
		IrodsCsNegResult negResult = calcNegoResult(clientPolicy, serverPolicy);
		
		// send negotiation result to server
		DataStruct message = new DataStruct("CS_NEG_PI");
		message.add( new DataInt("status", NEGO_SUCCESS) );
		message.add( new DataString("result", "cs_neg_result_kw=" + negResult.getLabel() + ";")  );
		IrodsMessage negoMsg = new IrodsMessage(IrodsMessageType.RODS_CS_NEG_T);
		negoMsg.setMessage(message);
	    session.getOutputStream().writeMessage(negoMsg);
	    
	    // first receive the reply from server, before applying the negotiation result
	    // the reply will be a RODS_VERSION msg
		IrodsPackedMessage pMsg = session.getInputStream().readMessage();
		IrodsMessage versionMsg = pMsg.unpack(unpackInstruction());
		// now take action based on the negotiation result
		System.out.println("NEGO RESULT = " + negResult.getLabel());
		switch (negResult) {
		case CS_NEG_FAILURE:
			// send a RODS_DISCONNECT to the server and close the connection
			IrodsMessage disconnect = new IrodsMessage(IrodsMessageType.RODS_DISCONNECT);
			session.getOutputStream().writeMessage(disconnect);
			session.disconnect();
			throw new MyRodsException("Error during connect: Policies Client/Server are incompatible");
		case CS_NEG_USE_TCP:
			break;
		case CS_NEG_USE_SSL: {
			session.startSSL();
			// we will need to encrypt data transfers, set encryption config
			DataEncryptConfig encrypt = sessionDetails.getDataEncryptConfig();
			if (encrypt == null) {
				// not yet established, create new using default settings
				encrypt = new DataEncryptConfig();
				sessionDetails.setDataEncryptConfig(encrypt);
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

	


	
	

}
