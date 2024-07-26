package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

public class ModDataObjMetaInp extends DataStruct {
	
	// "ModDataObjMeta_PI"   "struct *DataObjInfo_PI; struct *KeyValPair_PI;"
	
	public ModDataObjMetaInp(DataObjInfo dataObjInfo, KeyValPair keyValPair) {
		super("ModDataObjMeta_PI");
		add(new DataPtr("DataObjInfo_PI", dataObjInfo));
		add(new DataPtr("KeyValPair_PI", keyValPair));
	}

}
