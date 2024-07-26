package nl.tsmeele.myrods.high;

import java.io.IOException;
import java.time.Instant;

import nl.tsmeele.myrods.api.RcAuthRequest;
import nl.tsmeele.myrods.api.RcAuthResponse;
import nl.tsmeele.myrods.api.RcConnect;
import nl.tsmeele.myrods.api.RcDisconnect;
import nl.tsmeele.myrods.api.RcPamAuthRequest;
import nl.tsmeele.myrods.api.RcSslEnd;
import nl.tsmeele.myrods.api.RcSslStart;
import nl.tsmeele.myrods.api.RodsCall;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;
import nl.tsmeele.myrods.plumbing.IrodsSession;
import nl.tsmeele.myrods.plumbing.MyRodsException;
import nl.tsmeele.myrods.plumbing.SessionDetails;

/**
 * The class manages an authenticated user connection to an iRODS server.  
 * @author Ton Smeele
 *
 */
public class Session implements Cloneable {
	private IrodsSession irodsSession = null;
	private SessionPool pool = null; 
	private Long connectTime = null;
	private Long markedTime = null;
	
	
	public SessionPool getSessionPool() {
		return pool;
	}
	
	public IrodsSession getIrodsSession() {
		return irodsSession;
	}
	
	/**
	 * Returns the time that user has succesfully authenticated. 
	 * @return	epoch time in seconds, null if not yet authenticated
	 */
	public Long getConnectTime() {
		if (irodsSession == null) {
			return null;
		}
		return connectTime;
	}
	

	/**
	 * Mark the current Session time.
	 * This mark can be used by applications as a starting point for elapsed time measurements.
	 */
	public void setMarkTimeStamp() {
		markedTime = Instant.now().getEpochSecond();
	}
	
	/**
	 * Remove a marked time, if any was set previously.
	 */
	public void resetMarkTimeStamp() {
		markedTime = null;
	}
	
	/**
	 * Measure the elapsed time since a mark was set.
	 * This call does not update the mark.
	 * @return 	elapsed time in seconds, or null if no mark has been found
	 */
	public Long secondsSinceMarkTimeStamp() {
		if (markedTime == null) {
			return null;
		}
		return Instant.now().getEpochSecond() - markedTime;
	}
	

	
	/**
	 * Disconnect the connection with the iRODS server.
	 * If the current session is the primary connection with the server, then any related (secondary)
	 * sessions obtained via a session pool will also be disconnected. 
	 * 
	 * NB: Disconnecting secondary sessions can potentially interfere with ongoing communications if still active.
	 * To prevent accidental disruptions, first check the pool's activeConnectionsCount().
	 */
	public void disconnect() {
		try {
			new RcDisconnect().sendTo(irodsSession);
		} catch (IOException e) {}
		if (pool != null && this == pool.getMainSession()) {
			pool.stopPool();
		}
		irodsSession = null;
		connectTime = null;
	}
	
	/**
	 * Connect with server and authenticate using native authentication scheme.
	 * @param host		iRODS server hostname or ip address
	 * @param port		server port
	 * @param userName	name of user 
	 * @param userZone	zone of origin of user
	 * @param password	native password of user
	 * @return			true if succesfully connected and authenticated
	 */
	public boolean nativeLogin(String host, int port, String userName, String userZone, String password) {
		try {
			irodsSession = connect(host, port, userName, userZone);
			if (authNative(userName, userZone, password)) {

				System.out.println("NEW SESSION LOGGED IN");
				return true;
			}
		} catch (Exception e) { }
		try {
			irodsSession.disconnect();
		} catch (Exception e2) {}
		irodsSession = null;
		return false;
	}
	
	/**
	 * Connect with server and authenticate using PAM authentication scheme.
	 * @param host			iRODS server hostname or ip address
	 * @param port			server port
	 * @param userName		name of user
	 * @param userZone		zone of origin of user
	 * @param pamPassword	pam password of user
	 * @return				true if succesfully connected and authenticated
	 */
	public boolean pamLogin(String host, int port, String userName, String userZone, 
			String pamPassword) {
			return pamLogin(host, port, userName, userZone, pamPassword, 0);
	}
	
	// vaiant method that also specifies a time-to-live	for the (irodsPamPassword) access token
	public boolean pamLogin(String host, int port, String userName, String userZone, 
			String pamPassword, int ttl) {
		try {
			irodsSession = connect(host, port, userName, userZone);
			Message reply = null;
			boolean ssl = irodsSession.isSsl();
			if (!ssl) {
				// need to upgrade to ssl before exchanging password
				RcSslStart rcSslStart = new RcSslStart(null);
				reply = rcSslStart.sendTo(irodsSession);
				if (reply.getIntInfo() < 0 || !irodsSession.isSsl()) {
					return false;
				}
			}
			RcPamAuthRequest rcPamAuthRequest = new RcPamAuthRequest(userName, pamPassword, ttl);
			reply = rcPamAuthRequest.sendTo(irodsSession);
			String irodsPamPassword = reply.getMessage().lookupString("irodsPamPassword");
			if (!ssl) {
				// need to downgrade to previous non-ssl state
				RcSslEnd rcSslEnd = new RcSslEnd(null);
				rcSslEnd.sendTo(irodsSession);
			}
			// use obtained password to login
			if (irodsPamPassword != null && authNative(userName, userZone, irodsPamPassword)) {
				irodsSession.getSessionDetails().nativePassword = irodsPamPassword;
				return true;
			}
		}
		catch (Exception e) { }
		return false;
	}

	@Override
	public Session clone() {
		if (irodsSession == null) {
			return null;
		}
		SessionDetails sd = irodsSession.getSessionDetails();
		// new session, connect with credentials from current session
		Session cSession = new Session();
		cSession.pool = pool;
		DataStruct connectMsg = sd.connectMsg;
		String userName = connectMsg.lookupString("clientUser");
		String userZone = connectMsg.lookupString("clientRcatZone");
		if (cSession.nativeLogin(sd.host, sd.port, userName, userZone, sd.nativePassword)) {
			return cSession;
		}
		try {
			cSession.disconnect();
		} catch (Exception e) {}
		return null;
	}
	
	
	private IrodsSession connect(String host, int port, String userName, String userZone) throws IOException {
		IrodsSession server = new IrodsSession();
		server.connect(host,  port);
		RcConnect rcConnect = new RcConnect(0, 1, userName, userZone, userName, userZone);
		rcConnect.sendTo(server);
		return server;
	}
	
	private boolean authNative(String userName, String userZone, String irodsPassword) throws MyRodsException, IOException {
		RcAuthRequest rcAuthRequest = new RcAuthRequest();
		Message reply = rcAuthRequest.sendTo(irodsSession);
		byte[] challenge = reply.getMessage().lookupByte("challenge");
		RcAuthResponse rcAuthResponse = new RcAuthResponse(userName + "#" + userZone, irodsPassword, challenge);
		reply = rcAuthResponse.sendTo(irodsSession);
		int intInfo = reply.getIntInfo();
		if (intInfo == 0) {
			irodsSession.getSessionDetails().nativePassword = irodsPassword;
			setConnectTimeStamp();
			if (pool == null) {
				pool = new SessionPool(this);
			}
		}
		return intInfo == 0;
	}
	
	private void setConnectTimeStamp() {
		connectTime = Instant.now().getEpochSecond();
	}
	
	
	// available for debug purposes, prints exchanged messages
	public  Message request(RodsCall apiCall) {
		System.out.println("SEND: " + apiCall.getIrodsMessage());
		Message reply = null;
		try {
			reply = apiCall.sendTo(irodsSession);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("RECEIVED: " + reply);
		return reply;
	}
	
	
}
