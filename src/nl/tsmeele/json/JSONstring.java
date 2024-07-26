package nl.tsmeele.json;

/** Class to model the behavior of a JSON string.
 * @author Ton Smeele
 *
 */
public class JSONstring implements JSON {
	private String s = null;
	
	public JSONstring(String s) {
		this.s = s;
	}
	
	public String get() {
		return s;
	}
	
	static public String convertToString(JSON j) {
		return ((JSONstring)j).get();
	}
	
	@Override
	public String toString() {
		return quote(s);
	}

	@Override
	public String toPrettyString() {
		return toString();
	}
	
	private String quote(String s) {
		return "\"" + s + "\"";
	}
	
}
