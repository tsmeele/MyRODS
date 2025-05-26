package nl.tsmeele.myrods.irodsStructures;


/**
 * Class DataInt implements the 32 bit integer data type.
 * @author Ton Smeele
 *
 */
public class DataInt extends Data {
	private int i;

	public DataInt(String variableName, Integer i) {
		super(variableName, IrodsType.INT);
		set(i);
	}
	
	public int get() {
		return i;
	}
	
	public void set(Integer i) {
		if (i == null) {
			i = 0;
		}
		this.i = i;
	}

	@Override
	public String toString() {
		return getName() + "(" + Integer.toString(i) + ")";
	}
}
