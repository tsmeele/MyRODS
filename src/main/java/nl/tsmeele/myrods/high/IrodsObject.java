package nl.tsmeele.myrods.high;

import nl.tsmeele.myrods.api.ObjType;

public abstract class IrodsObject {
	public String ownerName;
	public String ownerZone;
	
	public IrodsObject(String ownerName, String ownerZone) {
		this.ownerName = ownerName;
		this.ownerZone = ownerZone;
	}
	
	public String getOwner() {
		return ownerName + "#" + ownerZone;
	}
	
	public boolean isCollection() {
		return getType() == ObjType.COLLECTION;
	}
	
	public boolean isDataObject() {
		return getType() == ObjType.DATAOBJECT;
	}
	public abstract ObjType getType();

	public abstract String getPath();

	public static String parent(String path) {
		int i = path.lastIndexOf('/');
		if (i < 0) return "";
		return path.substring(0, i);
	}
	
	public static String basename(String path) {
		int i = path.lastIndexOf('/');
		if (i < 0 || i == path.length() - 1) return path;
		return path.substring(i + 1);
	}
	
}
