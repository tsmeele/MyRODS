package nl.tsmeele.myrods.irodsDataTypes;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Data subtype to represent binary data.
 * @author Ton Smeele
 *
 */
public class DataBinArray extends Data {
	private static final int NULL_TERMINATOR = 0; 
	private byte[] data;
	
	public DataBinArray(String variableName, byte[] binary) {
		super(variableName, IrodsType.BIN);
		this.data = binary;
	}
	
	public byte[] get() {
		return data;
	}

	@Override
	public String toString() {
		return getName() + "(" + Base64.getEncoder().encodeToString(data) + ")";
	}
	
	public String getAsString() {
		// just use the first part of the array upto the string terminator (if present)
		// the remainder holds unused bytes of a fixed size array
		int len = 0;
		while (len < data.length && data[len] != NULL_TERMINATOR) {
			len++;
		}
		return new String(data, 0, len, StandardCharsets.UTF_8);
	}
	
	
}
