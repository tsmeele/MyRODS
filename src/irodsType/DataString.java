package irodsType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import plumbing.MyRodsException;

/**
 * The class DataString defines a null-terminated character array.
 * @author Ton Smeele
 *
 */
public class DataString extends Data {
	private static byte NULLTERMINATOR = 0;
	private String text;
	private int maxSize; // includes null-terminator

	public DataString(String variableName, String text) {
		super(variableName);
		if (text == null) {
			nullValue = true;
			text = "";
			maxSize = 1;
		}
		this.text = text;
		maxSize = text.length() + 1;	// extra char for null-terminator
		}
	
	public DataString(String variableName, String text, int maxSize) {
		super(variableName);
		this.maxSize = Math.max(maxSize, text.length() + 1);
		this.text = text;
	}
	
	public DataString(String variableName, ByteBuffer buf, int maxSize) throws MyRodsException {
		super(variableName);
		buf.mark();
		int stringLength = 0;
		if (!buf.hasRemaining()) throw new MyRodsException("Empty buffer found upon create/unpack string");
		while (buf.hasRemaining() && buf.get() != NULLTERMINATOR) {
			stringLength++;
		}
		buf.reset();
		byte[] stringBuf = new byte[stringLength]; 
		for (int i = 0; i < stringLength; i++) {
			stringBuf[i] = buf.get();
		}
		if (buf.hasRemaining()) {
			// read null-terminator if present
			buf.get();
		}
		// charset is imposed by iRODS communication protocol
		this.text = new String(stringBuf, StandardCharsets.UTF_8);
		this.maxSize = Math.max(maxSize, text.length());
	}

	public String get() {
		return text;
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	
	
	@Override
	public String toXmlString() {
		return escapeXml(text);
	}

	@Override
	public IrodsBaseType getType() {
		return IrodsBaseType.STR;
	}

	@Override
	public byte[] packNative() {
		byte[] textBytes = text.getBytes();
		return ByteBuffer.allocate(textBytes.length + 1).put(textBytes).put(NULLTERMINATOR).array();
	}

	@Override
	public int packNativeSize() {
		byte[] bytes = text.getBytes();	
		return bytes.length + 1;  // string and terminator
	}

	@Override
	public String toString() {
		return "STR('" + text + "')";
	}

}
