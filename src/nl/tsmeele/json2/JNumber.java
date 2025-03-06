package nl.tsmeele.json2;

import java.util.ArrayList;

public class JNumber implements Json {
	public int i;
	public double d;
	public boolean isInt;
	
	public JNumber(int i) {
		this.i = i;
		isInt = true;
	}
	
	public JNumber(double d) {
		this.d = d;
		isInt = false;
	}
	
	public String toString() {
		if (isInt) {
			return String.valueOf(i);
		} else {
			return String.valueOf(d);
		}
	}

	@Override
	public ArrayList<String> toPrettyLines() {
		ArrayList<String> out = new ArrayList<String>();
		out.add(toString());
		return out;
	}
	

}
