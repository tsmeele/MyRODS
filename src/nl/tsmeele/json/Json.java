package nl.tsmeele.json;

import java.util.ArrayList;

public interface Json {
	
	public static final String INDENT = "  ";
	
	public ArrayList<String> toPrettyLines();
	
}
