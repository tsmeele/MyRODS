package nl.tsmeele.myrods.irodsDataTypes;


/**
 * List of all foundational data structures supported by the iRODS protocol.
 * All other data structures are compositions of these data structures.
 * @author Ton Smeele
 *
 */
public enum IrodsType {
	CHAR("char"), BIN("bin"), STR("str"), PISTR("pistr"), INT("int"), 
	INT64("double"), // note that in practice the iRODS data type "double" is used as an integer of 8 bytes
	STRUCT("struct"), DEPENDS("?"), 
	INT16("int16"),		// not listed in specs, yet in use in iRODS source code
	// we add array and pointer for efficiency reasons, they are not listed in the iRODS protocol specification
	ARRAY("array"),
	POINTER("ptr");
	
	private String label;
	
	private IrodsType(String label) {
		this.label = label;
	}
	
	public static IrodsType lookup(String s) {
		s = s.toLowerCase();
		if (s.equals("array")) return null;	// array type not available to user data 
		for (IrodsType type : IrodsType.values()) {
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
