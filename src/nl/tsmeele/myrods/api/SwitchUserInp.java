package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class SwitchUserInp extends DataStruct {

	// SwitchUserInp_PI "str username[64]; str zone[64]; struct KeyValPair_PI;"
	/*
	 * options in KV:
	 *  KW_SWITCH_PROXY_USER = "switch_proxy_user"
	 *  KW_CLOSE_OPEN_REPLICAS = "close_open_replicas"
	 *  KW_KEEP_SVR_TO_SVR_CONNECTIONS = "keep_svr_to_svr_connections"
	 * 
	 */
	public SwitchUserInp(String username, String userzone, KeyValPair keyValPair) {
		super("SwitchUserInp_PI");
		add(new DataString("username", username));
		add(new DataString("userzone", userzone));
		keyValPair = keyValPair == null ? new KeyValPair() : keyValPair;
		add(keyValPair);
	}

}
