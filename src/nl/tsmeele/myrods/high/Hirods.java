package nl.tsmeele.myrods.high;


import java.io.IOException;

import nl.tsmeele.myrods.api.Irods;
import nl.tsmeele.myrods.irodsStructures.RcConnect;
import nl.tsmeele.myrods.plumbing.MyRodsException;
import nl.tsmeele.myrods.plumbing.ServerConnectionDetails;

public class Hirods extends Irods {
	public IrodsPool irodsPool = null;
	
	
	public Hirods(String host, int port) {
		super(host, port);
	}
	
	public Hirods cloneConnection() throws IOException, MyRodsException {
		if (!serverConnection.isConnected() || authenticatedPassword == null || irodsPool == null) {
			throw new MyRodsException("Unable to clone: Missing authenticated iRODS connection");
		}
		Hirods irods2 = new Hirods(host, port);
		// clone will reuse current pool
		irods2.irodsPool = irodsPool;
		ServerConnectionDetails sd = serverConnection.getSessionDetails();
		
		RcConnect rcConnect = new RcConnect(sd.startupPack, sd.clientPolicy);
		// clone connects to the server
		irods2.rcConnect(rcConnect);
		if (irods2.error) {
			throw new MyRodsException("Unable to clone: cannot connect to server");
		}
		// clone authenticates
		byte[] challenge = irods2.rcAuthRequest();
		if (irods2.error) {
			irods2.rcDisconnect();
			throw new MyRodsException("Unable to clone: authentication request failed");
		}
		String proxyUser = sd.startupPack.lookupString("proxyUser");
		String proxyZone = sd.startupPack.lookupString("proxyRcatZone");
		irods2.rcAuthResponse(proxyUser + "#" + proxyZone, authenticatedPassword, challenge);
		if (irods2.error) {
			irods2.rcDisconnect();
			throw new MyRodsException("Unable to clone: authentication failed");
		}
		return irods2;
	}

	
	public boolean pamLogin(String proxyUser, String proxyZone, String proxyPamPassword,
			String clientUser, String clientZone) throws MyRodsException, IOException {
		return pamLogin(proxyUser, proxyZone, proxyPamPassword,	clientUser, clientZone, 0);
	}
	
	public boolean pamLogin(String proxyUser, String proxyZone, String proxyPamPassword,
			String clientUser, String clientZone, int ttl) throws MyRodsException, IOException {
		rcConnect(proxyUser, proxyZone, clientUser, clientZone);
		if (error) return false;
		boolean originalSslState = serverConnection.isSsl();
		if (!originalSslState) {
			// upgrade session to ssl for exchange of proxyPamPassword
			rcSslStart();
			if (error || !serverConnection.isSsl()) return false;
		}
		// authenticate pam user in local zone and obtain time-limited password
		String proxyTtlPassword = rcPamAuthRequest(proxyUser, proxyPamPassword, ttl);
		boolean authError = error;
		if (!originalSslState) {
			// downgrade session back to original state
			rcSslEnd();
		}
		if (authError) return false;
		return nativeLogin(proxyUser, proxyZone, proxyTtlPassword, clientUser, clientZone);
	}
	
	
	public boolean nativeLogin(String proxyUser, String proxyZone, String proxyNativePassword,
			String clientUser, String clientZone) throws MyRodsException, IOException {
		if (!serverConnection.isConnected()) {
			rcConnect(proxyUser, proxyZone, clientUser, clientZone);
			if (error) return false;
		}
		byte[] challenge = rcAuthRequest();
		if (error) return false;
		rcAuthResponse(proxyUser + "#" + proxyZone, proxyNativePassword, challenge);
		if (error) return false;
		if (irodsPool == null) {
			irodsPool = new IrodsPool(this);
		}
		return true;
	}

	

}
