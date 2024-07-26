package nl.tsmeele.myrods.plumbing;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import nl.tsmeele.myrods.irodsDataTypes.DataBinArray;
import nl.tsmeele.myrods.irodsDataTypes.DataCharArray;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataInt16;
import nl.tsmeele.myrods.irodsDataTypes.DataInt64;
import nl.tsmeele.myrods.irodsDataTypes.DataPIStr;
import nl.tsmeele.myrods.irodsDataTypes.DataString;

public class InputBufferNative implements InputBuffer {
	// the platform independent literal that depicts a null pointer
	public static final byte NULLTERMINATOR = 0;
	private ByteBuffer buf = null;

	public InputBufferNative(byte[] packedData) {
		buf = ByteBuffer.wrap(packedData);
	}

	@Override
	public void endUnpack() throws MyRodsException {
		if (buf.hasRemaining()) {
			throw new MyRodsException("Bytes remaining after processing of input data has completed");
		}
	}

	@Override
	public void startUnpackStruct(String name) {
		// no actions needed
	}

	@Override
	public void endUnpackStruct(String name) {
		// no actions needed		
	}

	@Override
	public boolean unpackNullPointer(String name) {
		byte[] nullPtr = PackerNative.packedNullPointer();
		if (buf.remaining() < nullPtr.length) {
			return false;
		}
		// test if next input bytes equal the literal string for the null pointer
		byte[] bytes = new byte[nullPtr.length];
		buf.mark();		
		buf.get(bytes, 0, nullPtr.length);
		boolean isNullPointer = true;
		for (int i = 0; i < nullPtr.length; i++) {
			if (bytes[i] != nullPtr[i]) {
				isNullPointer = false;
				break;
			}
		}
		if (isNullPointer) {
			// we have read the null pointer bytes
			return true;
		}
		// not a nullpointer: unread input bytes, needs to be read later on by variable type
		buf.reset();
		return false;
	}

	@Override
	public DataInt unpackInt(String name) {
		return new DataInt(name, buf.getInt());
	}

	@Override
	public DataInt16 unpackInt16(String name) {
		return new DataInt16(name, buf.getShort());
	}

	@Override
	public DataInt64 unpackInt64(String name) {
		return new DataInt64(name, buf.getLong());
	}

	@Override
	public DataString unpackString(String name, int allocSize) throws MyRodsException {
		byte[] bytes = readStringFromBuffer(name);
		DataString s = null;
		if (allocSize > 1) {
			s = new DataString(name, new String(bytes, StandardCharsets.UTF_8), allocSize);
		} else {
			s = new DataString(name, new String(bytes, StandardCharsets.UTF_8));
		}
		return s;
	}
	
	@Override
	public DataPIStr unpackPIStr(String name, int allocSize) throws MyRodsException {
		byte[] bytes = readStringFromBuffer(name);
		DataPIStr s = null;
		if (allocSize > 1) {
			s = new DataPIStr(name, new String(bytes, StandardCharsets.UTF_8), allocSize);
		} else {
			s = new DataPIStr(name, new String(bytes, StandardCharsets.UTF_8));
		}
		return s;
	}
		
	private byte[] readStringFromBuffer(String name) throws MyRodsException {
		// peek input to find string size
		buf.mark();
		int stringLength = 0;  // space for the null terminator
		if (!buf.hasRemaining()) {
			throw new MyRodsException("Empty buffer encountered while unpacking string variable " + name);
		}
		boolean nullTerminated = false;
		while (buf.hasRemaining()) {
			byte b = buf.get();
			if (b == NULLTERMINATOR) {
				nullTerminated = true;
				break;
			}
			stringLength++;
		}
		buf.reset();
		// read string from buffer
		byte[] bytes = new byte[stringLength]; 
		for (int i = 0; i < stringLength; i++) {
			bytes[i] = buf.get();
		}
		if (nullTerminated) {
			// read nullterminator
			buf.get();
		}
		return bytes;
	}


	@Override
	public DataBinArray unpackBinArray(String name, int allocSize) throws MyRodsException {
		byte[] bytes = new byte[allocSize];
		if (buf.remaining() < allocSize) {
			throw new MyRodsException("Empty buffer encountered while unpacking bin variable " + name);
		}
		buf.get(bytes, 0, allocSize);
		return new DataBinArray(name, bytes);
	}

	@Override
	public DataCharArray unpackCharArray(String name, int allocSize) throws MyRodsException {
		byte[] bytes = new byte[allocSize];
		if (buf.remaining() < allocSize) {
			throw new MyRodsException("Empty buffer encountered while unpacking char variable " + name);
		}
		buf.get(bytes, 0, allocSize);
		
		return new DataCharArray(name, new String(bytes, StandardCharsets.UTF_8), allocSize);
	}

}
