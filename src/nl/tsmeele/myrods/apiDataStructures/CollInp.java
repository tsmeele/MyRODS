package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class CollInp extends DataStruct {

	public CollInp(String collName, int flags, int oprType, KeyValPair condInput) {
		super("CollInpNew_PI");
		add(new DataString("collName", collName));
		add(new DataInt("flags", flags));
		add(new DataInt("oprType", oprType));
		add(condInput);
	}

}
