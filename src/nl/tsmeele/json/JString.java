package nl.tsmeele.json;

import java.util.ArrayList;

public class JString implements Json{
	public String data = "";
	
	public JString(String data) {
		this.data = data;
	}
	
	public String toString() {
		return serialize(data);
	}
	
	public ArrayList<String> toPrettyLines() {
		ArrayList<String> str = new ArrayList<String>();
		str.add(serialize(data));
		return str;
	}
	
	public static String serialize(String s) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		sb.append("\"");
		while (i < s.length()) {
			char c = s.charAt(i);
			i++;
			// escape special characters
			if (c == '/' || c == '\\' || c == '"') {
				sb.append("\\" + c);
				continue;
			}
			// all other except control characters
			if (c >= 32) {
				sb.append(c);
				continue;
			}
			// escape all control characters
			sb.append("\\");
			switch (c) {
				case '\t': {sb.append('t'); break;}
				case '\b': {sb.append('b'); break;}
				case '\f': {sb.append('f'); break;}
				case '\n': {sb.append('n'); break;}
				case '\r': {sb.append('r'); break;}
				default: {
					sb.append('u' + String.format("%04x", (int) c));
				}
			}		
		}
		sb.append("\"");
		return sb.toString();
	}
		
}
