package main;

import java.io.IOException;

import nl.tsmeele.myrods.api.Irods;
import nl.tsmeele.myrods.api.IrodsCsNegType;
import nl.tsmeele.myrods.api.MiscSvrInfo;
import nl.tsmeele.myrods.api.RodsVersion;
import nl.tsmeele.myrods.plumbing.IrodsProtocolType;

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
	private String serverTypeString = "";
	private String serverPolicy = null;
	
	public String execute(String host, int port) throws IOException {
		Irods irods = new Irods(host, port);
		
		// exchange startup message, obtain version info
		RodsVersion version = irods.rcConnect(IrodsProtocolType.NATIVE_PROT, 0, 0, "","", "", "", "Ping", IrodsCsNegType.CS_NEG_DONT_CARE);
		relVersion = version.relVersion;
		apiVersion = version.apiVersion;	
		IrodsCsNegType policy = irods.getServerPolicy();
		if (policy == null) {
			// server did not negotiate, only returned with Version reply message
			serverPolicy = "<unknown>";
		} else {
			serverPolicy = policy.getLabel();
		}
		
		// depending on server mood, our connection may have been reset by the server
		// the next api request therefore is somewhat opportunistic, and enclosed in try/catch 
		try {
			MiscSvrInfo miscSvrInfo = irods.rcMiscSvrInfo();
			rodsZone = miscSvrInfo.rodsZone;
			serverType = miscSvrInfo.serverType;
			serverTypeString = MiscSvrInfo.getServerType(serverType);
			irods.rcDisconnect();
		}
		catch (Exception e) {
			rodsZone = "<could not retrieve>";
			serverType = 0;
			serverTypeString = MiscSvrInfo.getServerType(0);
		}
		
		// build and return report
		return "Server information retrieved:\n" +
				"host:port      : " + host + ":" + port + "\n" +
		        "server type    : " + serverTypeString + "\n" +
		        "server version : " + "release '" + relVersion + "' , api version '" + apiVersion + "'\n" +
		        "server policy  : " + serverPolicy + "\n" +
				"iRODS zone     : " + rodsZone;
	}


}
