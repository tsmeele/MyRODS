package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class CollOprStat  extends DataStruct {

	//	"CollOprStat_PI", "int filesCnt; int totalFileCnt; double bytesWritten; str lastObjPath[MAX_NAME_LEN];",

	public Integer filesCnt;
	public Integer totalFileCnt;
	public Long bytesWritten;
	public String lastObjPath;
	
	public CollOprStat(DataStruct dataStruct) {
		super("CollOprStat_PI");
		addFrom(dataStruct);
		filesCnt = lookupInt("filesCnt");
		totalFileCnt = lookupInt("totalFileCnt");
		bytesWritten = lookupLong("bytesWritten");
		lastObjPath = lookupString("lastObjPath");
	}

	public String toString() {
		return "CollOprStat: " +
				"filesCnt(" + filesCnt + "), totalFileCnt(" + totalFileCnt + "), bytesWritten(" + bytesWritten + 
				"), lastObjPath='" + lastObjPath + "'";
	}
}
