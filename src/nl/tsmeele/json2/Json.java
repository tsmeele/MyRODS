package nl.tsmeele.json2;

import java.util.ArrayList;

public interface Json {
	
	static final String INDENT = "  ";
	
	public ArrayList<String> toPrettyLines();
	
}
