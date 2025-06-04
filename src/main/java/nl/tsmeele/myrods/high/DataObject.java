package nl.tsmeele.myrods.high;

import nl.tsmeele.myrods.api.ObjType;

public class DataObject extends IrodsObject {
	public String collName;
	public String dataName;
	public long dataSize;
	
	
	public DataObject(String collName, String dataName, long dataSize, String ownerName, String ownerZone) {
		super(ownerName, ownerZone);
		this.collName = collName;
		this.dataName = dataName;
		this.dataSize = dataSize;
	}
	
	@Override
	public String getPath() {
		return collName + "/" + dataName;
	}
	
	@Override
	public String getParentPath() {
		return collName;
	}
	
	@Override
	public ObjType getType() {
		return ObjType.DATAOBJECT;
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






	
}
