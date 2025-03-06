package nl.tsmeele.json2;

import java.util.ArrayList;

public class JBool implements Json {
	public boolean data;
	
	public JBool(boolean data) {
		this.data = data;
	}

	public String toString() {
		return data ? "true" : "false";
	}
	
	@Override
	public ArrayList<String> toPrettyLines() {
		ArrayList<String> out = new ArrayList<String>();
		out.add(toString());
		return out;
	}
	

}
