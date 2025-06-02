package nl.tsmeele.json;

import java.util.ArrayList;
import java.util.HashMap;

public class JObject extends HashMap<String,Json> implements Json{
	private static final long serialVersionUID = 1L;
	
	public JObject() {
	}
	
	public JObject(String s) {
		parseObject(this, s, 0);
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean isNext = false;
		for (String key : this.keySet()) {
			if (isNext) {
				sb.append(',');
			}
			else {
				isNext = true;
			}
			sb.append(JString.serialize(key) + ":");
			sb.append(get(key).toString());
		}
		sb.append("}");
		return sb.toString();
	}
	
	
	public String toPrettyString() {
		ArrayList<String> lines = toPrettyLines();
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String s : lines) {
			if (first) {
				first = false;
			}
			else {
				sb.append("\n");
			}
			sb.append(s);
		}
		return sb.toString();
	}
	
	
	public ArrayList<String> toPrettyLines() {
		ArrayList<String> out = new ArrayList<String>();
		out.add("{");
		boolean firstItem = true;
		for (String key : this.keySet()) {
			// if appropriate, add item separator
			if (firstItem) {
				firstItem = false;
			} else {
				int last = out.size() - 1;
				out.set(last, out.get(last) + ",");
			}
			// process the item itself
			String sKey = JString.serialize(key);
			Json jValue = get(key);
			if (jValue == null) {
				out.add(INDENT + sKey + ": null");
				continue;
			}
			ArrayList<String> valueLines = jValue.toPrettyLines();
			String firstValueLine = valueLines.remove(0);
			// output the key along with the first value line
			out.add(INDENT + sKey + ": " + firstValueLine);
			// output remaining value lines, if any
			for (String line : valueLines) {
				out.add(INDENT + line);
			}
		}
		if (out.size() == 1) {
			// condense empty object to a single line
			out.set(0, "{ }");
		} else {
			out.add("}");
		}
		return out;
	}
	
	
	//  methods for parsing, for specification see https://www.json.org/json-en.html

	
	private static int parseObject(JObject jObj, String s, int startIndex) {
		int i = readWhiteSpace(s, startIndex);
		// read opening curly
		i = readChar(s, i, '{');
		i = readWhiteSpace(s, i);
		boolean first = true;
		while (i < s.length() && s.charAt(i) != '}') {
			if (first) {
				first = false;
			} else {
				// read item separator
				i = readLiteral(s, i, ",");
				i = readWhiteSpace(s, i);
			}
			// process a key:value pair
			int start = i;
			i = readString(s, i);	// read item key
			String key = s.substring(start + 1, i - 1);	// omit surrounding quotes
			i = readWhiteSpace(s, i);
			i = readChar(s, i, ':');	// read key-value separator
			i = readWhiteSpace(s, i);
			i = parseValue(jObj, key, s, i);	// read item value 
			i = readWhiteSpace(s, i);
		}
		// read closing curly
		return readChar(s, i, '}');
	}
	
		
	private static int parseValue(JObject jObj, String key, String s, int startIndex) {
		int i = startIndex;
		// determine type of value
		switch (s.charAt(i)) {
			case '"': {
				// json string
				i = readString(s, i);
				String value = s.substring(startIndex + 1, i - 1);	// omit surrounding quotes
				jObj.put(key, new JString(value));
				break;
			}
			case 'n': 
			case 'N': {
				// json null 
				i = readLiteral(s, i, "null");
				jObj.put(key, new JNull());
				break;
			}
			case 't':
			case 'T': {
				// json boolean True
				i = readLiteral(s, i, "true");
				jObj.put(key, new JBool(true));
				break;
			}
			case 'f':
			case 'F': {
				// json boolean False
				i = readLiteral(s, i, "false");
				jObj.put(key, new JBool(false));
				break;
			}				
			case '{': {
				// json object
				JObject subObj = new JObject();
				i = parseObject(subObj, s, i);
				jObj.put(key, subObj);
				break;
			}
			case '[': {
				// json array
				JArray subArray = new JArray();
				i = parseArray(subArray, s, i);
				jObj.put(key, subArray);
				break;
			}
			default: 
				// json number
				startIndex = i;
				// read sign symbol, if present
				if (s.charAt(i) == '-') {
					i++;
				}
				// read integer digits
				i = readDigits(s, i);
				long intValue = Long.parseLong(s.substring(startIndex, i));
				// it is an integer if neither a fraction nor an exponent is present
				if (i >= s.length() || ( 
						s.charAt(i) != '.' && 
						s.charAt(i) != 'e' && s.charAt(i) != 'E')
						) {
					jObj.put(key, new JNumber(intValue));
					break;
				}
				// not an integer, must be a double then with at least fraction or exponent present
				// read fraction, if present
				if (s.charAt(i) == '.') {
					i++;
					i = readDigits(s, i);
				}
				// read exponent, if present
				if (s.charAt(i) == 'E' || s.charAt(i) == 'e') {
					i++;
					// exponent can optionally have a sign
					if (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
						i++;
					}
					i = readDigits(s, i);
				}
				double dValue = Double.parseDouble(s.substring(startIndex, i));
				jObj.put(key,  new JNumber(dValue));
		}
		return i;
	}
	
	
	private static int readDigits(String s, int i) {
		int startIndex = i;
		while (i < s.length() && s.charAt(i) >= '0' && s.charAt(i) <= '9') {
			i++;
		}
		if (i == startIndex) {
			// must be at least 1 digit present
			throw new RuntimeException("Missing numeric character at' at column " + i + " of string " + s);
		}
		return i;
	}
	
	
	private static int parseArray(JArray array, String s, int startIndex) {
		int i = readChar(s, startIndex, '[');
		i = readWhiteSpace(s, i);
		boolean first = true;
		while (i < s.length() && s.charAt(i) != ']') {
			if (first) {
				first = false;
			}
			else {
				// read item-separator
				i = readLiteral(s, i, ",");
				i = readWhiteSpace(s, i);
			}
			// read one array-item 
			JObject valueObj = new JObject();
			i = parseValue(valueObj, "dummyKey", s, i);
			array.add(valueObj.get("dummyKey"));
			// read whitespace upto item-separator or array-end
			i = readWhiteSpace(s, i);
		}
		i = readLiteral(s, i, "]");
		return i;
	}
	
	
	private static int readString(String s, int startIndex) {
		// read starting quotes
		int i = readChar(s, startIndex, '"');
		// read string content
		while (i < s.length() && s.charAt(i) != '"') {
			if (s.charAt(i) == '\\') {
				// escaped char, read a extra char
				i++;
			}
			if (i < s.length()) {
				i++;
			}
		}
		// read ending quotes
		return readChar(s, i, '"');
	}
	
	
	private static int readLiteral(String s, int startIndex, String literal) {
		if (!s.toLowerCase().startsWith(literal.toLowerCase(), startIndex)) {
			throw new RuntimeException("Missing literal '" + literal + "' at column " + startIndex + " of string " + s);
		}
		return startIndex + literal.length();
	}
	
	
	private static int readChar(String s, int startIndex, char c) {
		if (startIndex >= s.length() || s.charAt(startIndex) != c) {
			throw new RuntimeException("Missing '" + c + "' at column " + startIndex + " of string " + s);
		}
		return startIndex + 1;
	}
	
	
	private static int readWhiteSpace(String s, int i) {
		while (i < s.length() && isWhiteSpace(s.charAt(i))) {
			i++;
		}
		return i;
	}
	
	
	private static boolean isWhiteSpace(char c) {
		// json whitespace is more restricted than Java whitespace
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}
	

}
