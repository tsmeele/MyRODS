package nl.tsmeele.myrods.high;

public class IrodsUser {
	public String name;
	public String zone;
	
	public IrodsUser(String name, String zone) {
		this.name = name;
		this.zone = zone;
	}
	
	public String nameAndZone() {
		return name + "#" + zone;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.getClass() != obj.getClass()) return false;
		IrodsUser user = (IrodsUser) obj;
		return this.name.equals(user.name) && this.zone.equals(user.zone);
	}
	
	
	
	public String toString() {
		return nameAndZone();
	}

}
