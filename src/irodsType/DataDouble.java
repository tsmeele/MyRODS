package irodsType;

import java.nio.ByteBuffer;

/**
 * Class DataDouble implements the floating point data type.
 * @author Ton Smeele
 *
 */
public class DataDouble extends Data {
	private static final int DOUBLESIZE = 8;
	private double data;

	public DataDouble(String variableName, Double data) {
		super(variableName);
		if (data == null) {
			nullValue = true;
			data = 0.0;
		}
		this.data = data;
	}
	
	public DataDouble(String variableName, ByteBuffer b) {
		super(variableName);
		this.data = b.getDouble();
	}

	@Override
	public String toXmlString() {
		return Double.toString(data);
	}

	@Override
	public IrodsBaseType getType() {
		return IrodsBaseType.DOUBLE;
	}

	@Override
	public byte[] packNative() {
		return ByteBuffer.allocate(DOUBLESIZE).putDouble(data).array();
	}

	@Override
	public int packNativeSize() {
		return DOUBLESIZE;
	}
	
	@Override
	public String toString() {
		return "DOUBLE(" + Double.toString(data) + ")";
	}

}
