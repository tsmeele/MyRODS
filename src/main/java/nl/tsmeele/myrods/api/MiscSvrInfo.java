package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.irodsStructures.DataStruct;

// "MiscSvrInfo_PI":  "int serverType; int serverBootTime; str relVersion[NAME_LEN]; 
//                    str apiVersion[NAME_LEN]; str rodsZone[NAME_LEN];",


public class MiscSvrInfo extends DataStruct {
	public int serverType; 
	public TimeStamp serverBootTime;
	public String relVersion, apiVersion, rodsZone;
	
	public MiscSvrInfo(DataStruct dataStruct) {
		super("MiscSvrInfo_PI");
		addFrom(dataStruct);
		serverType = lookupInt("serverType");
		serverBootTime = new TimeStamp(lookupInt("serverBootTime"));
		relVersion = lookupString("relVersion");
		apiVersion = lookupString("apiVersion");
		rodsZone = lookupString("rodsZone");
	}
	
	public static String getServerType(int type) {
		switch (type) {
		case 0: return "iRODS consumer";
		case 1: return "iRODS provider";
		default: return "iRODS server type " + type;
		}
	}
	
	public String toString() {
		return "MiscSvrInfo: serverType(" + serverType + "), serverBootTime='" + serverBootTime + 
				"', relVersion='" + relVersion + "', apiVersion='" + apiVersion + "', rodsZone='" + rodsZone + "'"; 
	}
	
}
