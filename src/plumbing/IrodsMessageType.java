package plumbing;

/**
 * List of supported message types.
 * @author Ton Smeele
 *
 */
public enum IrodsMessageType {
	RODS_CONNECT("RODS_CONNECT"),
	RODS_DISCONNECT("RODS_DISCONNECT"),
	RODS_API_REQ("RODS_API_REQ"),
	RODS_API_REPLY("RODS_API_REPLY"),
	RODS_REAUTH("RODS_REAUTH"),
	RODS_VERSION("RODS_VERSION"),
	RODS_CS_NEG_T("RODS_CS_NEG_T");

	private String label;
	
	private IrodsMessageType(String label) {
		this.label = label;
	}
	
	String getLabel() {
		return label;
	}
    
	public static IrodsMessageType get(String s) {
	s = s.toUpperCase();
	for (IrodsMessageType type : IrodsMessageType.values()) {
		if (s.equals(type.label)) {
			return type;
		}
	}
	return null;
}
}
