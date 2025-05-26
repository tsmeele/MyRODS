package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.irodsStructures.Data;
import nl.tsmeele.myrods.irodsStructures.DataArray;
import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataPtr;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

/**
 * IrodsMap manages Data structures that are key-value based. 
 * Supported maps are the following key->value combinations: 
 * 		String->String
 * 		Integer->Integer
 * 		Integer->String
 * where in the packinstruction the key and value are pointer array
 * e.g. : "KeyValPair_PI", "int ssLen; str *keyWord[ssLen]; str *svalue[ssLen];" 
 * @author Ton Smeele
 *
 */
public abstract class IrodsMap extends DataStruct {
	
	private String lengthName, keyName, valueName;
	
	public IrodsMap(String packInstruction, String lengthName, String keyName, String valueName) {
		super(packInstruction);		
		this.lengthName = lengthName;
		this.keyName = keyName;
		this.valueName = valueName;
		add(new DataInt(lengthName, 0));
		add(new DataPtr(keyName, new DataArray(keyName)));
		add(new DataPtr(valueName, new DataArray(valueName)));
	}
	
	
	public void put(String keyword, String value) {
		DataInt l = (DataInt) lookupName(lengthName);
		DataArray k = lookupArray(keyName);
		DataArray v = lookupArray(valueName);
		k.add(new DataString(keyName, keyword));
		v.add(new DataString(valueName, value));
		l.set(l.get() + 1);
	}

	public void put(int keyword, int value) {
		DataInt l = (DataInt) lookupName(lengthName);
		DataArray k = lookupArray(keyName);
		DataArray v = lookupArray(valueName);
		k.add(new DataInt(keyName, keyword));
		v.add(new DataInt(valueName, value));
		l.set(l.get() + 1);
	}
	
	public void put(int keyword, String value) {
		DataInt l = (DataInt) lookupName(lengthName);
		DataArray k = lookupArray(keyName);
		DataArray v = lookupArray(valueName);
		k.add(new DataInt(keyName, keyword));
		v.add(new DataString(valueName, value));
		l.set(l.get() + 1);
	}
	
	protected DataArray lookupArray(String name) {
		for (Data element : list) {
			if (element.getName().equals(name)) {
				return (DataArray) ((DataPtr)element).get();
			}
		}
		return null;
	}
	
}
