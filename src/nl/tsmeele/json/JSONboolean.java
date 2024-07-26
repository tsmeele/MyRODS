package nl.tsmeele.json;

/** Class to model the behavior of a JSON boolean.
 * @author Ton Smeele
 *
 */
public class JSONboolean implements JSON {
	
	private boolean b;
	
	public JSONboolean(boolean b) {
		this.b = b;
	}
	
	@Override
	public String toString() {
		if (b) {
			return "true";
		}
		return "false";
	}

	@Override
	public String toPrettyString() {
		return toString();
	}

}
