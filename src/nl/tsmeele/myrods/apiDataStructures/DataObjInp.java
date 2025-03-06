package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataInt64;
import nl.tsmeele.myrods.irodsStructures.DataPtr;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class DataObjInp extends DataStruct {
	// 	"DataObjInp_PI", "str objPath[MAX_NAME_LEN]; int createMode; int openFlags; 
	//   double offset; double dataSize; int numThreads; int oprType; struct *SpecColl_PI; struct KeyValPair_PI;",

	public DataObjInp(String path, Integer createMode, Integer openFlags, Long offset,
			Long dataSize, Integer numThreads, OprType oprType, SpecColl specColl, KeyValPair keyValPair) {
		super("DataObjInp_PI");
		init(path, createMode, openFlags, offset, dataSize, numThreads, oprType, specColl, keyValPair);
	}
		
	public DataObjInp(String path, KeyValPair keyValPair) {
		super("DataObjInp_PI");
		init(path, null, null, null, null, null, null, null, keyValPair);
	}
	
	public void init(String path, Integer createMode, Integer openFlags, Long offset,
			Long dataSize, Integer numThreads, OprType oprType, SpecColl specColl, KeyValPair keyValPair) {
		add(new DataString("objPath", path));
		add(new DataInt("createMode", createMode));
		add(new DataInt("openFlags", openFlags));
		add(new DataInt64("offset", offset));
		add(new DataInt64("dataSize", dataSize));
		add(new DataInt("numThreads", numThreads));
		int opr = oprType == null ? 0 : oprType.getId();
		add(new DataInt("oprType", opr));
		add(new DataPtr("specColl", specColl));
		keyValPair = keyValPair == null ? new KeyValPair() : keyValPair;
		add(keyValPair);	
	}

}
