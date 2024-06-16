package irodsType;


/**
 * List of all foundational data structures supported by the iRODS protocol.
 * All other data structures are compositions of these data structures.
 * @author Ton Smeele
 *
 */
public enum IrodsBaseType {
	CHAR("char"), BIN("bin"), STR("str"), PISTR("pistr"), INT("int"), DOUBLE("double"), 
	STRUCT("struct"), DEPENDS("?"), 
	INT16("int16"),		// not listed in specs, yet in use in iRODS source code
	// we add array for efficiency reasons, it is not listed in the iRODS protocol specification
	ARRAY("array");
	
	private String label;
	
	private IrodsBaseType(String label) {
		this.label = label;
	}
	
	public static IrodsBaseType get(String s) {
		s = s.toLowerCase();
		if (s.equals("array")) return null;	// array type not available to user data 
		for (IrodsBaseType type : IrodsBaseType.values()) {
			if (s.equals(type.label)) {
				return type;
			}
		}
		return null;
	}
	
	public boolean isArrayType() {
		return this == CHAR || this == BIN || this == STR || this == PISTR;
	}
	
}
