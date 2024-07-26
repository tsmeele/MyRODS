package nl.tsmeele.myrods.apiDataStructures;

public enum ObjType {
	DATAOBJECT("dataObj", 1),
	COLLECTION("collection", 2);

	
	private String label;
	private int id;
	
	private ObjType(String label, int id) {
		this.label = label;
		this.id = id;
		
	}

	public String getLabel() {
		return label;
	}
	
	public int getId() {
		return id;
	}
	
	public ObjType lookup(int id) {
		for (ObjType t : values()) {
			if (t.id == id) {
				return t;
			}
		}
		return null;
	}
}
