package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

public class RodsVersion extends DataStruct {
	public String relVersion = "";
	public String apiVersion = "";

	public RodsVersion(DataStruct data) {
		super("Version_PI");
		addFrom(data);
		relVersion = lookupString("relVersion");
		apiVersion = lookupString("apiVersion");
	}
	
	public String toString() {
		return "RodsVersion: relVersion='" + relVersion + "' apiVersion='" + apiVersion + "'";
	}
}
