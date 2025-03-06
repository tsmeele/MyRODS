package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class RHostAddr extends DataStruct {

	// "RHostAddr_PI", "str hostAddr[LONG_NAME_LEN]; str rodsZone[NAME_LEN]; int port; int dummyInt;",

	public RHostAddr(String hostAddr, String rodsZone, int port, int dummyInt) {
		super("RHostAddr_PI");
		add(new DataString("hostAddr", hostAddr));
		add(new DataString("rodsZone", rodsZone));
		add(new DataInt("port", port));
		add(new DataInt("dummyInt", dummyInt));
	}
	

}
