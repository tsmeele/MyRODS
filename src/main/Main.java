package main;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import api.RcApi;
import api.RcAuthRequest;
import api.RcAuthResponse;
import api.RcConnect;
import api.RcDisconnect;
import api.RcGetLimitedPassword;
import api.RcGetTempPassword;
import api.RcMiscSvrInfo;
import api.RcPamAuthRequest;
import api.RcSslEnd;
import api.RcSslStart;
import irodsType.Data;
import irodsType.DataBinArray;
import irodsType.DataInt;
import irodsType.DataPIStr;
import irodsType.DataString;
import irodsType.DataStruct;
import plumbing.IrodsMessage;
import plumbing.IrodsPackedMessage;
import plumbing.IrodsOutputStream;
import plumbing.IrodsSession;
import plumbing.MyRodsException;
import plumbing.PackMapConstants;
import plumbing.ParsedInstruction;
import plumbing.TagParser;

public class Main {

	public static void main(String[] args) throws MyRodsException{

		
		// connect to irods VM:  172.28.128.3  port 1247
		IrodsSession server = new IrodsSession();
		server.connect("172.28.128.3", 1247);
		System.out.println("is connected = " + server.isConnected());
	 	// bob is an example user, must have been created			
		RcConnect rcConnect = new RcConnect(0, 1, "bob","tempZone","bob","tempZone");

		IrodsMessage reply = request(server, rcConnect);
		System.out.println("is connected = " + server.isConnected());
	
		// feel free to leave out below SSLstart part when you plan to do a simple test
		// just be aware that PAM passwords are communicated across the network in plaintext	
		System.out.println("TEST: upgrade communication to SSL");
		RcSslStart rcSslStart = new RcSslStart(null);
		reply = request(server, rcSslStart);
		if (server.isSsl()) {
			System.out.println("SSL started");
		}
		
		System.out.println("TEST: authenticate using a pam password");
		DataString pamUser = new DataString("pamUser", "bob");
		DataString pamPassword = new DataString("pamPassword", "bob");
		DataInt ttl = new DataInt("ttl", 10);
		RcPamAuthRequest rcPamAuthRequest = new RcPamAuthRequest(pamUser, pamPassword, ttl);
		reply = request(server, rcPamAuthRequest);
		if (reply.getIntInfo() == 0) {
			System.out.println("PAM Auth was successful");
		}
		DataString irodsPamPassword = (DataString) reply.getMessage().lookupName("irodsPamPassword");
		System.out.println("irodsPamPassword =" + irodsPamPassword.get() + "=");
		
		

		System.out.println("TEST: receive server info");
		RcMiscSvrInfo rcMiscSvrInfo = new RcMiscSvrInfo();
		reply = request(server, rcMiscSvrInfo);

		System.out.println("TEST: perform native authentication");
		RcAuthRequest rcAuthRequest = new RcAuthRequest();
		reply = request(server, rcAuthRequest);
		DataBinArray challenge = (DataBinArray) reply.getMessage().lookupName("challenge");
		DataString username = new DataString("username", "bob#tempZone");
		DataString password = new DataString("password", irodsPamPassword.get());
		System.out.println("using irodsPamPassword to authenticate");
		RcAuthResponse rcAuthResponse = new RcAuthResponse(username, password, challenge);
		reply = request(server, rcAuthResponse);
		if (reply.getIntInfo() == 0) {
			System.out.println("Native authentication successful");
		}

		
		System.out.println("TEST: downgrade communications from SLL to regular");
		RcSslEnd rcSslEnd = new RcSslEnd(null);
		reply = request(server, rcSslEnd);
		if (server.isSsl()) {
			System.out.println("downgrade to regular comm failed");
		} else {
			System.out.println("Success: we're no longer on SSL");
		}

		System.out.println("TEST: Disconnect");
		RcDisconnect rcDisconnect = new RcDisconnect();
		reply = request(server, rcDisconnect);
		System.out.println("is connected = " + server.isConnected());
		System.exit(0);	
		
	}
	
	private static IrodsMessage request(IrodsSession server, RcApi apiCall) {
		System.out.println("SEND: " + apiCall.getIrodsMessage());
		IrodsMessage reply = null;
		try {
			reply = apiCall.sendTo(server);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("RECEIVED: " + reply);
		return reply;
	}
	
	
	
	

}
