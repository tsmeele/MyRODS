package nl.tsmeele.myrods.high;

import nl.tsmeele.myrods.api.ObjType;

public class Collection extends IrodsObject {
	public String collName;
	
	public Collection(String collName, String ownerName, String ownerZone) {
		super(ownerName, ownerZone);
		this.collName = collName;
	}
	
	public String getParentPath() {
		return super.parent(collName);
	}
	
	@Override
	public String getPath() {
		return collName;
	}
	
	@Override
	public ObjType getType() {
		return ObjType.COLLECTION;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		return ((Collection) obj).collName.equals(collName);
	}
	
	public String toString() {
		return collName;
	}
}
