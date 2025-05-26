package nl.tsmeele.myrods.api;

import java.util.ArrayList;
import java.util.List;

import nl.tsmeele.myrods.irodsStructures.Data;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class KeyValPair extends IrodsMap {
	// "KeyValPair_PI", "int ssLen; str *keyWord[ssLen]; str *svalue[ssLen];",
	
	public KeyValPair() {
		super("KeyValPair_PI","ssLen", "keyWord", "svalue");		
	}

	public List<String> getKeys() {
		ArrayList<String> keys = new ArrayList<String>();
		DataStruct array = lookupArray("keyWord");
		for (Data data : array) {
			String key = ((DataString)data).get();
			keys.add(key);
		}
		return keys;
	}
	
}
