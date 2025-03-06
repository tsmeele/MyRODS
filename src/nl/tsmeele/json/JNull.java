package nl.tsmeele.json2;

import java.util.ArrayList;

public class JNull implements Json {

	public String toString() {
		return "null";
	}
	
	@Override
	public ArrayList<String> toPrettyLines() {
		ArrayList<String> out = new ArrayList<String>();
		out.add(toString());
		return out;
	}

}
