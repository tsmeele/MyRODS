package nl.tsmeele.myrods.high;

public class AVU {
	public String name = "";
	public String value = "";
	public String units;
	
	public AVU(String name, String value, String units) {
		this.name = name;
		this.value = value;
		this.units = units;
	}
	
	public String toString() {
		return "(A:'" + name + "', V:'" + value + "', U:'" + units + "')"; 
	}

}
