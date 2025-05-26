package nl.tsmeele.myrods.irodsStructures;


/**
 * Class DataInt64 implements the long integer datatype (32 bits).
 * @author Ton Smeele
 *
 */
public class DataInt64 extends Data {
	private long i;

	public DataInt64(String variableName, Long i) {
		super(variableName,IrodsType.INT64);
		set(i);
	}
	
	public long get() {
		return i;
	}
	
	public void set(Long i) {
		if (i == null) {
			i = 0L;
		}
		this.i = i;
	}
	
	@Override
	public String toString() {
		return getName() + "(" + Long.toString(i) + ")";
	}

}
