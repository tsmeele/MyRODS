package plumbing;

import irodsType.DataInt;

public class SessionDetails {
	private DataInt cookie = null;
	private DataEncryptConfig dataEncryptConfig = null;

	public DataInt getCookie() {
		return cookie;
	}
	
	public void setCookie(DataInt cookie) {
		this.cookie = cookie;
	}
	
	public DataEncryptConfig getDataEncryptConfig() {
		return dataEncryptConfig;
	}
	
	public void setDataEncryptConfig(DataEncryptConfig encryptConfig) {
		this.dataEncryptConfig = encryptConfig;
	}
}
