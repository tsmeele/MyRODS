package nl.tsmeele.json;

import java.util.ArrayList;


/** Class to model the behavior of a JSON array.
 * @author Ton Smeele
 *
 */
public class JSONarray extends ArrayList<JSON> implements JSON {
	private static final long serialVersionUID = 5L;

	@Override
	public String toPrettyString() {
		StringBuilder s = new StringBuilder();
		s.append("[");
		boolean additional = false;
		for (JSON element : this) {
			if (additional) {
				s.append(",\n");
			} else {
				additional = true;
				s.append(JSON.INDENT.substring(1));
			}
			s.append(JSON.INDENT + element.toPrettyString());		
		}
		if (additional) {
			s.append("\n");
		}
		return s.append("]").toString();
	}
	
}
