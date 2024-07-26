package nl.tsmeele.json;

public interface JSON {
	public static final String INDENT = "  ";
	
	// toString must encode the object as a JSON formatted string
	public String toString();
	
	public String toPrettyString();
	



}
