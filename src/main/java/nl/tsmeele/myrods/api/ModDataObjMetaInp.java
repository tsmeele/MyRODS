package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.irodsStructures.DataPtr;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class ModDataObjMetaInp extends DataStruct {
	
	// "ModDataObjMeta_PI"   "struct *DataObjInfo_PI; struct *KeyValPair_PI;"
	
	public ModDataObjMetaInp(DataObjInfo dataObjInfo, KeyValPair keyValPair) {
		super("ModDataObjMeta_PI");
		add(new DataPtr("DataObjInfo_PI", dataObjInfo));
		add(new DataPtr("KeyValPair_PI", keyValPair));
	}

}
