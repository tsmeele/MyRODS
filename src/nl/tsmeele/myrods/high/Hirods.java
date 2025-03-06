package nl.tsmeele.myrods.high;


import java.io.IOException;

import nl.tsmeele.myrods.apiDataStructures.Irods;
import nl.tsmeele.myrods.plumbing.MyRodsException;

public class Hirods extends Irods {
	public IrodsPool irodsPool = null;
	
	
	public Hirods(String host, int port) {
		super(host, port);
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
		irodsPool = new IrodsPool(this);
		return true;
	}

	

}
