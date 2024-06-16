package irodsType;

import java.nio.ByteBuffer;

/**
 * Class DataInt16 implements the 16 bit integer data type.
 * @author Ton Smeele
 *
 */
public class DataInt16 extends Data {
	private static final int INT16SIZE = 2;
	private short i;

	public DataInt16(String variableName, Short i) {
		super(variableName);
		if (i == null) {
			nullValue = true;
			i = 0;
		}
		this.i = i;
	}
	
	public DataInt16(String variableName, ByteBuffer b) {
		super(variableName);
		this.i = b.getShort();
	}
	
	public short get() {
		return i;
	}

	@Override
	public String toXmlString() {
		return Integer.toString(i);
	}

	@Override
	public IrodsBaseType getType() {
		return IrodsBaseType.INT16;
	}

	@Override
	public byte[] packNative() {
		return ByteBuffer.allocate(INT16SIZE).putShort(i).array();
	}

	@Override
	public int packNativeSize() {
		return INT16SIZE;
	}

	@Override
	public String toString() {
		return "INT16(" + Integer.toString(i) + ")";
	}
}
