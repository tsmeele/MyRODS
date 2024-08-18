package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

/**
 * The class RodsObjStat contains an output datastructure as returned by RcObjStat.
 * For convenience, its elements are (also) accessible as instance attributes.
 * @author Ton Smeele
 *
 */
public class RodsObjStat extends DataStruct {
//	"RodsObjStat_PI", "double objSize; int objType; int dataMode; str dataId[NAME_LEN]; 
//     str chksum[NAME_LEN]; str ownerName[NAME_LEN]; str ownerZone[NAME_LEN]; 
//     str createTime[TIME_LEN]; str modifyTime[TIME_LEN]; struct *SpecColl_PI;",

	public int intInfo = 0;
	public DataStruct errorMessage = null;
	public Long objSize = null;
	public Integer objType = null;
	public Integer dataMode = null;
	public String dataId = null;
	public String chksum = null;
	public String ownerName = null;
	public String ownerZone = null;
	public TimeStamp createTime = null;
	public TimeStamp modifyTime = null;
	public DataStruct specColl = null;
	
	public RodsObjStat(DataStruct dataStruct) {
		super("RodsObjStat_PI");
		addFrom(dataStruct);
		objSize = lookupLong("objSize");
		objType = lookupInt("objType");
		dataMode = lookupInt("dataMode");
		dataId = lookupString("dataId");
		chksum = lookupString("chksum");
		ownerName = lookupString("ownerName");
		ownerZone = lookupString("ownerZone");
		createTime = new TimeStamp( (DataString)lookupName("createTime") );
		modifyTime = new TimeStamp( (DataString)lookupName("modifyTime") );
		specColl = (DataStruct) lookupName("specColl");
	}
	
	public String toString() {
		return "RodsObjStat: " +
				"objSize(" + objSize + "), objType(" + objType + "), dataMode(" + dataMode + 
				"), dataId='" + dataId + "', chksum='" + chksum + 
				"'\nowner='" + ownerName + "#" + ownerZone + "', createTime='" + 
				createTime.localTime() + "', modifyTime='" + modifyTime.localTime() + "'";
	}
	

}
