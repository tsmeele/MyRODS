package nl.tsmeele.myrods.api;

import nl.tsmeele.json.JObject;
import nl.tsmeele.myrods.irodsStructures.DataBinArray;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

// "MiscSvrInfo_PI":  "int serverType; int serverBootTime; str relVersion[NAME_LEN]; 
//                    str apiVersion[NAME_LEN]; str rodsZone[NAME_LEN];",


public class MiscSvrInfo extends DataStruct {
	public int serverType; 
	public TimeStamp serverBootTime;
	public String relVersion, apiVersion, rodsZone;
	public JObject certinfo = null;
	
	public MiscSvrInfo(DataStruct dataStruct) {
		super("MiscSvrInfo_PI");
		addFrom(dataStruct);
		serverType = lookupInt("serverType");
		serverBootTime = new TimeStamp(lookupInt("serverBootTime"));
		relVersion = lookupString("relVersion");
		apiVersion = lookupString("apiVersion");
		rodsZone = lookupString("rodsZone");
		// as of iRODS 4.3.4+ additional content is conveyed
		try {
			DataBinArray bin = (DataBinArray) lookupName("buf");
			String s = bin.getAsString();
			certinfo = new JObject(s);
		} catch (NullPointerException e) {
			certinfo = null;
		}		
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
				"', relVersion='" + relVersion + "', apiVersion='" + apiVersion + "', rodsZone='" + rodsZone + "', certinfo='" + certinfo + "'"; 
	}
	
}
