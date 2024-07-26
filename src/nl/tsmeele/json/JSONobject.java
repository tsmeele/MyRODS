package nl.tsmeele.json;

import java.util.HashMap;

/** Class to model the behavior of a JSON object. 
 * @author Ton Smeele
 *
 */
public class JSONobject extends HashMap<String, JSON> implements JSON {
	private static final long serialVersionUID = 6L; 
	
	@Override
	public String toString() {
		// we want to write key:value instead of HashMap's key=value
		return serialize(false);
	}
	
	@Override
	public String toPrettyString() {
		return serialize(true);
	}
	
	
	private String serialize(boolean pretty) {
		StringBuilder s = new StringBuilder();
		s.append("{");
		boolean additional = false;
		for (String key : this.keySet()) {
			if (additional) {
				s.append(",");
				if (pretty) s.append("\n" + JSON.INDENT);
			} else {
				additional = true;
				if (pretty) s.append(JSON.INDENT.substring(1));
			}
			s.append(quote(key));
			if (pretty) {
				s.append(" : ");
			} else {
				s.append(":");
			}
			String value = get(key).toString();
			// value might be multi-line, add extra indent to each line
			s.append( value.replace("\n", "\n" + JSON.INDENT) );
			if (pretty) s.append("\n");
		}
		return s.append("}").toString();
	}
	
	private String quote(String s) {
		return "\"" + s + "\"";
	}





}
