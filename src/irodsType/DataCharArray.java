package irodsType;

import java.nio.ByteBuffer;

/**
 * The class DataCharArray defines a fixed size array of chars.
 * @author Ton Smeele
 *
 */
public class DataCharArray extends DataBinArray {

	public DataCharArray(String variableName, byte[] binary) {
		super(variableName, binary);
	}
	
	public DataCharArray(String variableName, ByteBuffer b, int len) {
		super(variableName, b, len);
	}
	
	@Override
	public String toXmlString() {
		return escapeXml(new String(data));
	}
	
	
	@Override
	public IrodsBaseType getType() {
		return IrodsBaseType.CHAR;
	}
	
	@Override
	public String toString() {
		return "CHAR('" + new String(data) + "')";
	}
}
