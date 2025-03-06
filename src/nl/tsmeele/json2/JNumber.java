package nl.tsmeele.json2;

import java.util.ArrayList;

public class JNumber implements Json {
	public long dataLong;
	public double dataDouble;
	public boolean isInt;
	
	public JNumber(long i) {
		this.dataLong = i;
		isInt = true;
	}
	
	public JNumber(double d) {
		this.dataDouble = d;
		isInt = false;
	}
	
	public String toString() {
		if (isInt) {
			return String.valueOf(dataLong);
		} else {
			return String.valueOf(dataDouble);
		}
	}

	@Override
	public ArrayList<String> toPrettyLines() {
		ArrayList<String> out = new ArrayList<String>();
		out.add(toString());
		return out;
	}
	

}
