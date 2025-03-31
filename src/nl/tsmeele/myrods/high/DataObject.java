package nl.tsmeele.myrods.high;

public class DataObject {
	public String collName;
	public String dataName;
	public String ownerName;
	public String ownerZone;
	public long dataSize;
	
	
	public DataObject(String collName, String dataName, long dataSize, String ownerName, String ownerZone) {
		this.collName = collName;
		this.dataName = dataName;
		this.dataSize = dataSize;
		this.ownerName = ownerName;
		this.ownerZone = ownerZone;
	}
	
	public String getOwner() {
		return ownerName + "#" + ownerZone;
	}
	
	public String getPath() {
		return collName + "/" + dataName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		DataObject data = (DataObject) obj;
		return data.collName.equals(collName) && data.dataName.equals(dataName);
	}
	
	public String toString() {
		return collName + "/" + dataName;
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
