package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class CollInp extends DataStruct {
	
	/*
	 * collName 				- name of the collection
	 * flags  					- only used by rcOpenCollection
	 * oprType 					- currently not used 
	 * condInput 				- option, supports the following keywords:
	 *  	RECURSIVE_OPR__KW	- create parent collections as needed. This keyword has no value. 
	 */

	public CollInp(String collName, int flags, int oprType, KeyValPair condInput) {
		super("CollInpNew_PI");
		add(new DataString("collName", collName));
		add(new DataInt("flags", flags));
		add(new DataInt("oprType", oprType));
		add(condInput);
	}
	
	public CollInp(String collName) {
		super("CollInpNew_PI");
		add(new DataString("collName", collName));
		add(new DataInt("flags", 0));
		add(new DataInt("oprType", 0));
		add(new KeyValPair());
	}

}
