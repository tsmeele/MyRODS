package nl.tsmeele.myrods.api;

/**
 * List of supported message types.
 * @author Ton Smeele
 *
 */
public enum MessageType {
	RODS_CONNECT("RODS_CONNECT"),
	RODS_DISCONNECT("RODS_DISCONNECT"),
	RODS_API_REQ("RODS_API_REQ"),
	RODS_API_REPLY("RODS_API_REPLY"),
	RODS_REAUTH("RODS_REAUTH"),
	RODS_VERSION("RODS_VERSION"),
	RODS_CS_NEG_T("RODS_CS_NEG_T");

	private String label;
	
	private MessageType(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
    
	public static MessageType lookup(String s) {
	s = s.toUpperCase();
	for (MessageType type : MessageType.values()) {
		if (s.equals(type.label)) {
			return type;
		}
	}
	return null;
}
}
