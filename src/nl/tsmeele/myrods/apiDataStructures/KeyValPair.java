package nl.tsmeele.myrods.apiDataStructures;

import java.util.ArrayList;
import java.util.List;

import nl.tsmeele.myrods.irodsDataTypes.Data;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

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
