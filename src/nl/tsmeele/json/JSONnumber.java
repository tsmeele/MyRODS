package nl.tsmeele.json;

/** Class to model the behavior of a JSON number.
 * @author Ton Smeele
 *
 */
public class JSONnumber implements JSON {
	private Object obj = null;
	
	public JSONnumber(long i) {
		obj = new Long(i);
	}
	
	public JSONnumber(double f) {
		obj = new Double(f);
	}
	
	public Object get() {
		return obj;
	}
	
	public String toString() {
		return obj.toString();
	}

	static public int toInt(JSON json) {
		Long l = (Long) ((JSONnumber)json) .get();
		return l.intValue();
	}

	@Override
	public String toPrettyString() {
		return toString();
	}
}
