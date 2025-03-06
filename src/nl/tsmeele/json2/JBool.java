package nl.tsmeele.json2;

import java.util.ArrayList;

public class JBool implements Json {
	public boolean b;
	
	public JBool(boolean b) {
		this.b = b;
	}

	public String toString() {
		return b ? "true" : "false";
	}
	
	@Override
	public ArrayList<String> toPrettyLines() {
		ArrayList<String> out = new ArrayList<String>();
		out.add(toString());
		return out;
	}
	

}
