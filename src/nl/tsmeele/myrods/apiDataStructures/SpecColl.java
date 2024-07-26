package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

public class SpecColl extends DataStruct {
	// "SpecColl_PI", "int collClass; int type; str collection[MAX_NAME_LEN]; 
	//  str objPath[MAX_NAME_LEN]; str resource[NAME_LEN]; str rescHier[MAX_NAME_LEN]; 
	//  str phyPath[MAX_NAME_LEN]; str cacheDir[MAX_NAME_LEN]; int cacheDirty; int replNum;",

	public SpecColl() {
		super("SpecColl_PI");
		init(null, null, "", "", "", "", "", "", null, null);
	}
	
	public SpecColl(Integer collClass, Integer type, String collection,
			String objPath, String resource, String rescHier,
			String phyPath, String cacheDir, Integer cacheDirty, Integer replNum) {
		
		super("SpecColl_PI");
		init(collClass, type, collection, objPath, resource, rescHier, phyPath, cacheDir, cacheDirty, replNum);
	}
	
	public void init(Integer collClass, Integer type, String collection,
			String objPath, String resource, String rescHier,
			String phyPath, String cacheDir, Integer cacheDirty, Integer replNum) {
		add(new DataInt("collClass", collClass));
		add(new DataInt("type", type));
		add(new DataString("collection", collection));
		add(new DataString("objPath", objPath));
		add(new DataString("resource", resource));
		add(new DataString("reschier", rescHier));
		add(new DataString("phyPath", phyPath));
		add(new DataString("cacheDir", cacheDir));
		add(new DataInt("cacheDirty", cacheDirty));
		add(new DataInt("replNum", replNum));
	}

}
