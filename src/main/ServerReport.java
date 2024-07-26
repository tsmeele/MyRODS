package main;

import java.io.IOException;

import nl.tsmeele.myrods.api.RcConnect;
import nl.tsmeele.myrods.api.RcDisconnect;
import nl.tsmeele.myrods.api.RcMiscSvrInfo;
import nl.tsmeele.myrods.apiDataStructures.IrodsCsNegType;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;
import nl.tsmeele.myrods.plumbing.IrodsSession;

/** Demonstrator class that retrieves and reports public attributes of an iRODS server.
 *  Precondition: none
 * @author Ton Smeele
 *
 */
public class ServerReport {
	private String relVersion = null;
	private String apiVersion = null;
	private String rodsZone = null;
	private int serverType;
	private String serverPolicy = null;
	
	public String execute(String host, int port) throws IOException {
		IrodsSession server = new IrodsSession();
		server.connect(host, port);
		
		// exchange startup message, obtain version info
		RcConnect rcConnect = new RcConnect(0, 1, "","", "", "");
		rcConnect.setClientPolicy(IrodsCsNegType.CS_NEG_DONT_CARE);
		rcConnect.setApplicationName("Ping");
		DataStruct replyMsg = rcConnect.sendTo(server).getMessage();
		relVersion = replyMsg.lookupString("relVersion");
		apiVersion = replyMsg.lookupString("apiVersion");	
		IrodsCsNegType policy = server.getSessionDetails().serverPolicy;
		if (policy == null) {
			// server did not negotiate, only returned with Version reply message
			serverPolicy = "<unknown>";
		} else {
			serverPolicy = policy.getLabel();
		}
		
		// depending on server mood, our connection may have been reset by the server
		// the next api request therefore is somewhat opportunistic, and enclosed in try/catch 
		RcMiscSvrInfo rcMiscSvrInfo = new RcMiscSvrInfo();
		try {
			replyMsg = rcMiscSvrInfo.sendTo(server).getMessage();
			rodsZone = replyMsg.lookupString("rodsZone");
			serverType = replyMsg.lookupInt("serverType");
			RcDisconnect rcDisconnect = new RcDisconnect();
			rcDisconnect.sendTo(server);
		}
		catch (Exception e) {
			rodsZone = "<could not retrieve>";
			serverType = 0;
		}
		
		// build and return report
		return "Server information retrieved:\n" +
				"host:port      : " + host + ":" + port + "\n" +
		        "server type    : " + RcMiscSvrInfo.getServerType(serverType) + "\n" +
		        "server version : " + "release '" + relVersion + "' , api version '" + apiVersion + "'\n" +
		        "server policy  : " + serverPolicy + "\n" +
				"iRODS zone     : " + rodsZone;
	}


}
