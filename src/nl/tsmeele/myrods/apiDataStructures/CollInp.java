package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

public class CollInp extends DataStruct {

	public CollInp(String collName, int flags, int oprType, KeyValPair condInput) {
		super("CollInpNew_PI");
		add(new DataString("collName", collName));
		add(new DataInt("flags", flags));
		add(new DataInt("oprType", oprType));
		add(condInput);
	}

}
