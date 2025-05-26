package nl.tsmeele.json;

import java.util.ArrayList;

public class JArray extends ArrayList<Json> implements Json {
	private static final long serialVersionUID = 1L;
	
	
	@Override
	public ArrayList<String> toPrettyLines() {
		ArrayList<String> out = new ArrayList<String>();
		out.add("[");
		boolean firstItem = true;
		for (Json item : this) {
			if (firstItem) {
				firstItem = false;
			} else {
				int last = out.size() - 1;
				out.set(last, out.get(last) + ",");
			}
			ArrayList<String> itemLines = item.toPrettyLines();
			for (String line : itemLines) {
				out.add(INDENT + line);
			}
		}
		out.add("]");
		return out;
	}

}
