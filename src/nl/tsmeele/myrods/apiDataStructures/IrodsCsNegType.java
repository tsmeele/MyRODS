package nl.tsmeele.myrods.apiDataStructures;

public enum IrodsCsNegType {
	CS_NEG_REQUIRE("CS_NEG_REQUIRE", 0),
	CS_NEG_DONT_CARE("CS_NEG_DONT_CARE", 1),
	CS_NEG_REFUSE("CS_NEG_REFUSE", 2);
	
	private String label;
	private int id;
	
	private IrodsCsNegType(String label, int id) {
		this.label =label;
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}
	public int getId() {
		return id;
	}

	public static IrodsCsNegType lookup(String s) {
	s = s.toUpperCase();
	for (IrodsCsNegType type : IrodsCsNegType.values()) {
		if (s.equals(type.label)) {
			return type;
		}
	}
	return null;
}
}
