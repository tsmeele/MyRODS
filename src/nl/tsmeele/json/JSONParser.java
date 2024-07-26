package nl.tsmeele.json;

/** Class to parse json-serialized data into a JSON data structure.
 * @author Ton Smeele
 *
 */
public class JSONParser {
	private static final String QUOTES = "\"";
	private static final String COLON = ":";
	private static final String CURLYOPEN = "{";
	private static final String CURLYCLOSE = "}";
	private static final String BLOCKOPEN = "[";
	private static final String BLOCKCLOSE = "]";
	private static final String COMMA = ",";
	private static final char ESCAPECHAR = '\\';

	public static JSON parse(String s) {
		
		if (s == null) {
			return (JSON)new JSONnull();
		}
		s = trimWhite(s);
		if (s.startsWith(CURLYOPEN) && s.endsWith(CURLYCLOSE)) {
			// JSON object
			return parseObject(s);
		}
		if (s.startsWith(BLOCKOPEN) && s.endsWith(BLOCKCLOSE)) {
			// JSON array
			return parseArray(s);		
		}
		if (s.startsWith(QUOTES) && s.endsWith(QUOTES)) {
			// JSONstring
			s = trimOuter(s);
			return (JSON)new JSONstring(s);
		}
		if (s.toLowerCase().equals("true")) {
			return (JSON)new JSONboolean(true);
		}
		if (s.toLowerCase().equals("false")) {
			return (JSON)new JSONboolean(false);
		}
		if (s.toLowerCase().equals("null")) {
			return (JSON)new JSONnull();
		}
		// should be numeric then...
		try {  
			long l = Long.parseLong(s);  
			return (JSON)new JSONnumber(l);
		} 
		catch(NumberFormatException e){ } 
		try {  
			double d = Double.parseDouble(s);  
			return (JSON)new JSONnumber(d);
		} 
		catch(NumberFormatException e){ } 
		// give up, must be parse error
		return null;
	}
	
	private static JSON parseObject(String s) {
		JSONobject obj = new JSONobject();
		s = consumeChar(s); // consume CURLYOPEN
		s = trimWhite(s);
		while (s.length() > 0 && !nextToken(s).equals(CURLYCLOSE)) {
			// parse a map element
			if (!s.startsWith(QUOTES)) {
				// not a string: unable to find key
				return (JSON) obj;  // parse error
			}
			String key = nextToken(s);   // read quoted string as key
			s = trimWhite(s.substring(key.length()));
			key = trimOuter(key);  // and remove the quotes
			if (!s.startsWith(COLON)) {		
				// key and value should be delimited by a colon
				return (JSON) obj;
			}
			s = trimWhite(s.substring(1));
			// 
			String value = s.substring(0,elementLength(s));
			s = s.substring(value.length()); // consume element
			s = trimWhite(s);
			if (s.startsWith(COMMA)) {
				s = consumeChar(s); // consume COMMA
				s = trimWhite(s);
			}
			obj.put(key, parse(value));
		}
		return (JSON)obj;
	}
	
	// parseArray parameter should be a trimmed string
	private static JSON parseArray(String s) {
		JSONarray array = new JSONarray();
		s = consumeChar(s);	// consume BLOCKOPEN
		s = trimWhite(s);
		while (s.length() > 0 && !nextToken(s).equals(BLOCKCLOSE)) {
			String element = s.substring(0,elementLength(s));
			s = s.substring(element.length());
			array.add(parse(element));
			s = trimWhite(s);
			if (s.startsWith(COMMA)) {
				s = consumeChar(s); // consume COMMA
				s = trimWhite(s);
			}
		}	
		return (JSON)array;
	}
	
	private static int elementLength(String s) {
		if (s.startsWith(CURLYOPEN)) {
			return endOfObject(s);	
		}
		if (s.startsWith(BLOCKOPEN)) {
			return endOfArray(s);
		}
		return nextToken(s).length();
	}
	
	private static int endOfObject(String s) {
		String original = s;
		s = consumeChar(s); // consume CURLYOPEN
		s = trimWhite(s);
		while (s.length() > 0 && !nextToken(s).equals(CURLYCLOSE)) {
			String key = nextToken(s);
			s = s.substring(key.length());
			s = trimWhite(s);
			s = consumeChar(s);	// consume COLON
			s = trimWhite(s);
			// consume "value" related to the key
			s = s.substring(elementLength(s));
			s = trimWhite(s);
			// find out if there are more elements
			if (nextToken(s).equals(COMMA)) {
				s = consumeChar(s);
				s = trimWhite(s);
			}
		}
		s = consumeChar(s);	// consume CURLYCLOSE
		return original.length() - s.length();
	}
	
	private static int endOfArray(String s) {
		String original = s;
		s = consumeChar(s); // consume BLOCKOPEN
		s = trimWhite(s);
		while (s.length() > 0 && !nextToken(s).equals(BLOCKCLOSE)) {
			s = s.substring(elementLength(s));
			s = trimWhite(s);
			if (nextToken(s).equals(COMMA)) {
				s = consumeChar(s);
				s = trimWhite(s);
			}
		}
		s = consumeChar(s);	// consume BLOCKCLOSE
		return original.length() - s.length();
	}

	private static String consumeChar(String s) {
		if (s.length() > 0) {
			return s.substring(1);
		}
		return s;
	}
	
	// reads either a quoted string or upto whitespace or upto end-of-string
	private static String nextToken(String s) {
		if (s.length() > 0 && isDelim(s.charAt(0))) {
			// delimiters are a token
			return s.substring(0,1);
		}
		int i = 0;
		boolean inString = false;
		boolean tokenIsString = false;
		if (s.startsWith(QUOTES)) {
			// we will read upto and including the ending quotes of the string
			tokenIsString = true;
			inString = true;
			i++;
		}
		while (i < s.length() && 
			    ( !isWhitespaceOrDelim(s.charAt(i)) || inString) &&	// upto whitespace outside quoted string
			    ( inString || !tokenIsString)	// upto-and-including closing-quotes of quoted string 
			  ){
			char c = s.charAt(i);
			if (c == QUOTES.charAt(0)) {
				// start or end reading a quoted string
				inString = !inString;
			}
			if (c == ESCAPECHAR && 	i + 1 < s.length() && isEscaped(s.charAt(i + 1))
				) {
				// consume extra character(s) when escape char encountered
				// do not interprete the extra character
				if (s.charAt(i + 1) == 'u' && i + 5 < s.length()) {
					i = i + 4;
				} 
				i++; 
			} 
			i++;
		}
		return s.substring(0, i);
	}
	
	// strip away the outermost characters at begin and end of string)
	private static String trimOuter(String s) {
		if (s.length() >= 2) {
			return s.substring(1, s.length() - 1);
		}
		return "";
	}
	
	// strip away all whitespace characters at begin and end of string
	private static String trimWhite(String s) {
		while (s.length() > 0 && isWhitespace(s.charAt(s.length() - 1)) )  {
			s = s.substring(0, s.length() - 1);
		}
		while (s.length() > 0 && isWhitespace(s.charAt(0)) ) {
			s = s.substring(1);
		}
		return s;
	}
	
	private static boolean isWhitespaceOrDelim(char c) {
		return isWhitespace(c) || isDelim(c);
	}
	
	private static boolean isWhitespace(char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}
	
	private static boolean isDelim(char c) {
		return 	c == COMMA.charAt(0) ||
				c == BLOCKCLOSE.charAt(0) ||
				c == CURLYCLOSE.charAt(0) ||
				c == BLOCKOPEN.charAt(0) ||
				c == CURLYOPEN.charAt(0);
	}
	
	private static boolean isEscaped(char c) {
		return c == '\\' || c == '"' || c == '/' || c == 'b' || 
			c == 'f' || c == 'n' || c == 'r' || c == 't' || c == 'u';
	}
	

	

}
	
