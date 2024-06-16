package plumbing;

/**
 * List of supported protocols for (un)packing messages that are exchanged
 * betwen data grid participants.
 * @author Ton Smeele
 *
 */
public enum IrodsProtocolType {
	NATIVE_PROT(0),
	XML_PROT(1);
	
	private int protocolId;
	
	private IrodsProtocolType(int protocolId) {
		this.protocolId = protocolId;
	}
	
	public int getid() {
		return protocolId;
	}
}
