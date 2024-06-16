package irodsType;

import java.nio.ByteBuffer;

import plumbing.MyRodsException;

/**
 * The class DataPIStr defines a null-terminated char array where the content identifies a packing instruction.
 * @author Ton Smeele
 *
 */
public class DataPIStr extends DataString {

	public DataPIStr(String variableName, String text) {
		super(variableName, text);
	}
	
	public DataPIStr(String variableName, String text, int maxSize) {
		super(variableName, text, maxSize);
	}
	
	public DataPIStr(String variableName, ByteBuffer b, int alloc) throws MyRodsException {
		super(variableName, b, alloc);
	}
	
	@Override
	public IrodsBaseType getType() {
		return IrodsBaseType.PISTR;
	}
	
	@Override
	public String toString() {
		return "PISTR('" + get() + "')";
	}
	
}
