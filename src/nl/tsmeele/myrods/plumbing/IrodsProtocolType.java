package nl.tsmeele.myrods.plumbing;

/**
 * List of supported protocols for (un)packing messages that are exchanged
 * betwen data grid participants.
 * @author Ton Smeele
 *
 */
public enum IrodsProtocolType {
	NATIVE_PROT(0),
	XML_PROT(1),
	XML_PROT429(1);	// variant, replaces XML_PROT per iRODS 4.2.9+
	
	private int protocolId;
	
	private IrodsProtocolType(int protocolId) {
		this.protocolId = protocolId;
	}
	
	public int getId() {
		return protocolId;
	}
}
