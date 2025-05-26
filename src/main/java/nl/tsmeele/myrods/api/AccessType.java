package nl.tsmeele.myrods.api;

public enum AccessType {
	NULL("null", 1000),
	EXECUTE("execute", 1010),
	READ_ANNOTATION("read_annotation",1020),
	READ_SYSTEM_METADATA("read_system_metadata",1030),
	READ_METADATA("read_metadata",1040),
	READ_OBJECT("read_object",1050),
	READ("read", 1050), // alias for read_object
	WRITE_ANNOTATION("write_annotation",1060),
	CREATE_METADATA("create_metadata",1070),
	MODIFY_METADATA("modify_metadata",1080),
	DELETE_METADATA("delete_metadata",1090),
	ADMINISTER_OBJECT("administer_object",1100),
	CREATE_OBJECT("create_object",1110),
	MODIFY_OBJECT("modify_object",1120),
	WRITE("write", 1120),	// alias for modify_object
	DELETE_OBJECT("delete_object",1130),
	CREATE_TOKEN("create_token",1140),
	DELETE_TOKEN("delete_token",1150),
	CURATE("curate",1160),
	OWN("own",1200);

	
	private String label;
	private int id;
	
	private AccessType(String label, int id) {
		this.label = label;
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public int getId() {
		return id;
	}
	
	public static AccessType lookup(String s) {
		s = s.toUpperCase();
		for (AccessType type : AccessType.values()) {
			if (s.equals(type.label)) {
				return type;
			}
		}
		return null;
	}
}
