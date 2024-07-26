package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataInt64;
import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

public class DataObjInfo extends DataStruct {

	// "DataObjInfo_PI"   "str objPath[MAX_NAME_LEN]; str rescName[NAME_LEN]; str rescHier[MAX_NAME_LEN]; str dataType[NAME_LEN]; 
	//       double dataSize; str chksum[NAME_LEN]; str version[NAME_LEN]; str filePath[MAX_NAME_LEN]; str dataOwnerName[NAME_LEN]; 
	//       str dataOwnerZone[NAME_LEN]; int  replNum; int  replStatus; str statusString[NAME_LEN]; double  dataId; double collId; 
	//       int  dataMapId; int flags; str dataComments[LONG_NAME_LEN]; str dataMode[SHORT_STR_LEN]; str dataExpiry[TIME_LEN]; 
	//       str dataCreate[TIME_LEN]; str dataModify[TIME_LEN]; str dataAccess[NAME_LEN]; int  dataAccessInx; int writeFlag; 
	//       str destRescName[NAME_LEN]; str backupRescName[NAME_LEN]; str subPath[MAX_NAME_LEN]; int *specColl;  int regUid; 
	//       int otherFlags; struct KeyValPair_PI; str in_pdmo[MAX_NAME_LEN]; int *next; double rescId;",
	
	// supported keyValPair keywords:
	//       ALL_KW
	//       ADMIN_KW
	//		 IN_PDMO_KW
	//       OPEN_TYPE_KW
	//       SYNC_OBJ_KW
	//       REPL_STATUS_KW
	//       SELECTED_HIERARCHY_KW
	
	
	public DataObjInfo(String objPath, String rescName, String rescHier, String dataType,
			long dataSize, String chksum, String version, String filePath, String dataOwnerName,
			String dataOwnerZone, int replNum, int replStatus, String statusString, long dataId, long collId,
			int dataMapId, int flags, String dataComments, String dataMode, String dataExpiry,
			String dataCreate, String dataModify, String dataAccess, int dataAccessInx, int writeFlag,
			String destRescName, String backupRescName, String subPath, Integer specColl, int regUid,
			int otherFlags, KeyValPair keyValPair_PI, String in_pdmo, Integer next, long rescId) {
		super("DataObjInfo_PI");
		init(objPath, rescName, rescHier, dataType, dataSize, chksum, version, filePath, dataOwnerName,
				dataOwnerZone, replNum, replStatus, statusString, dataId, collId, dataMapId, flags, dataComments, dataMode, dataExpiry,
				dataCreate, dataModify, dataAccess, dataAccessInx, writeFlag, destRescName, backupRescName, subPath, specColl, regUid,
				otherFlags, keyValPair_PI, in_pdmo, next, rescId);
	}
	
	
	private void init(String objPath, String rescName, String rescHier, String dataType,
			long dataSize, String chksum, String version, String filePath, String dataOwnerName,
			String dataOwnerZone, int replNum, int replStatus, String statusString, long dataId, long collId,
			int dataMapId, int flags, String dataComments, String dataMode, String dataExpiry,
			String dataCreate, String dataModify, String dataAccess, int dataAccessInx, int writeFlag,
			String destRescName, String backupRescName, String subPath, Integer specColl, int regUid,
			int otherFlags, KeyValPair keyValPair_PI, String in_pdmo, Integer next, long rescId) {
		addStr("objPath", objPath);
		addStr("rescName", rescName);
		addStr("rescHier", rescHier);
		addStr("dataType", dataType);
		addLong("dataSize", dataSize);
		addStr("chksum", chksum);
		addStr("version", version);
		addStr("filePath", filePath);
		addStr("dataOwnerName", dataOwnerName);
		addStr("dataOwnerZone", dataOwnerZone);
		addInt("replNum", replNum);
		addInt("replStatus", replStatus);
		addStr("statusString", statusString);
		addLong("dataId", dataId);
		addLong("collId", collId);
		addInt("dataMapId", dataMapId);
		addInt("flags", flags);
		addStr("dataComments", dataComments);
		addStr("dataMode", dataMode);
		addStr("dataExpiry", dataExpiry);
		addStr("dataCreate", dataCreate);
		addStr("dataModify", dataModify);
		addStr("dataAccess", dataAccess);
		addInt("dataAccessInx", dataAccessInx);
		addInt("writeFlag", writeFlag);
		addStr("destRescName", destRescName);
		addStr("backupRescName", backupRescName);
		addStr("subPath", subPath);
		addIntPtr("specColl", specColl);
		addInt("regUid", regUid);
		addInt("otherFlags", otherFlags);
		add(keyValPair_PI);
		addStr("in_pdmo", in_pdmo);
		addIntPtr("next", next);
		addLong("rescId", rescId);
		
	}

	private void addStr(String name, String value) {
		add(new DataString(name, value));
	}
	
	private void addInt(String name, int value) {
		add(new DataInt(name, value));
	}
	
	private void addLong(String name, long value) {
		add(new DataInt64(name, value));
	}
	
	private void addIntPtr(String name, Integer value) {
		if (value == null) {
			add(new DataPtr(name, null));
		} else {
			add(new DataPtr(name, new DataInt(name, value)));
		}
	}
	
}
