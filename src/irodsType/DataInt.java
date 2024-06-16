package irodsType;

import java.nio.ByteBuffer;

/**
 * Class DataInt implements the 32 bit integer data type.
 * @author Ton Smeele
 *
 */
public class DataInt extends Data {
	private static final int INTSIZE = 4;
	private int i;

	public DataInt(String variableName, Integer i) {
		super(variableName);
		if (i == null) {
			nullValue = true;
			i = 0;
		}
		this.i = i;
	}
	
	public DataInt(String variableName, ByteBuffer b) {
		super(variableName);
		this.i = b.getInt();
	}
	
	public int get() {
		return i;
	}

	@Override
	public String toXmlString() {
		return Integer.toString(i);
	}

	@Override
	public IrodsBaseType getType() {
		return IrodsBaseType.INT;
	}

	@Override
	public byte[] packNative() {
		return ByteBuffer.allocate(INTSIZE).putInt(i).array();
	}

	@Override
	public int packNativeSize() {
		return INTSIZE;
	}

	@Override
	public String toString() {
		return "INT(" + Integer.toString(i) + ")";
	}
}
