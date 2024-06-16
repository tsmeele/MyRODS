package irodsType;

import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * Data subtype to represent binary data.
 * @author Ton Smeele
 *
 */
public class DataBinArray extends Data {
	protected byte[] data;

	public DataBinArray(String variableName, byte[] binary) {
		super(variableName);
		if (binary == null) {
			nullValue = true;
			data = new byte[0];
		}
		this.data = new byte[binary.length];
		for (int i = 0; i < binary.length; i++) {
			data[i] = binary[i];
		}
	}
	
	public DataBinArray(String variableName, ByteBuffer b, int len) {
		super(variableName);
		data = new byte[len];
		b.get(data, 0, len);
	}
	
	public DataBinArray(String variableName, String encoded, int len) {
		super(variableName);
		data = new byte[len];
		byte[] decoded = Base64.getDecoder().decode(encoded);
		for (int i = 0; i < len; i++) {
			if (i < decoded.length) {
				data[i] = decoded[i];
			}
			else {
				data[i] = 0;
			}
		}
	}
	
	public byte[] get() {
		return data;
	}

	@Override
	public String toXmlString() {
		return Base64.getEncoder().encodeToString(data);
	}

	@Override
	public IrodsBaseType getType() {
		return IrodsBaseType.BIN;
	}

	@Override
	public byte[] packNative() {
		byte[] binary = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			binary[i] = data[i];
		}
 		return binary;
	}

	@Override
	public int packNativeSize() {
		return data.length;
	}

	@Override
	public String toString() {
		return "BIN(size:" + data.length + " bytes)";
	}
	
}
