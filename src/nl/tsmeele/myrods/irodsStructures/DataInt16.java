package nl.tsmeele.myrods.irodsStructures;


/**
 * Class DataInt16 implements the 16 bit integer data type.
 * @author Ton Smeele
 *
 */
public class DataInt16 extends Data {
	private short i;

	public DataInt16(String variableName, Short i) {
		super(variableName, IrodsType.INT16);
		set(i);
	}
	
	public short get() {
		return i;
	}
	
	public void set(Short i) {
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
