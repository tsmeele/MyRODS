package nl.tsmeele.myrods.high;


import nl.tsmeele.myrods.api.ObjType;

public abstract class IrodsObject implements Comparable<IrodsObject> {
	public IrodsUser owner;
	
	public IrodsObject(String ownerName, String ownerZone) {
		owner = new IrodsUser(ownerName, ownerZone);
	}
	
	public boolean isCollection() {
		return getType() == ObjType.COLLECTION;
	}
	
	public boolean isDataObject() {
		return getType() == ObjType.DATAOBJECT;
	}
	public abstract ObjType getType();

	public abstract String getPath();
	
	public abstract String getParentPath();
	
	@Override
	public int compareTo(IrodsObject other) {
		return getPath().compareTo(other.getPath());
	}

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
