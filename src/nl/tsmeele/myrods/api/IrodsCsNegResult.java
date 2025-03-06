package nl.tsmeele.myrods.api;

public enum IrodsCsNegResult {
	CS_NEG_FAILURE("CS_NEG_FAILURE"),
	CS_NEG_USE_TCP("CS_NEG_USE_TCP"),
	CS_NEG_USE_SSL("CS_NEG_USE_SSL");
	
	private String label = null;
	
	private IrodsCsNegResult(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
