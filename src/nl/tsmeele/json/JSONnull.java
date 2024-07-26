package nl.tsmeele.json;

/** Class to model the behavior of a JSON null value.
 * @author Ton Smeele
 *
 */
public class JSONnull implements JSON {
	
	@Override
	public String toString() {
		return "null";
	}

	@Override
	public String toPrettyString() {
		return toString();
	}

}
