package nl.tsmeele.myrods.plumbing;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

import nl.tsmeele.myrods.irodsDataTypes.Data;
import nl.tsmeele.myrods.irodsDataTypes.DataBinArray;
import nl.tsmeele.myrods.irodsDataTypes.DataCharArray;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataInt16;
import nl.tsmeele.myrods.irodsDataTypes.DataInt64;
import nl.tsmeele.myrods.irodsDataTypes.DataPIStr;
import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

/**
 * Class PackerNative packs data in line with the iRODS native protocol. 
 * @author Ton Smeele
 *
 */
public class PackerNative extends Packer {
	public static final byte[] NULL_POINTER = "%@#ANULLSTR$%".getBytes(StandardCharsets.UTF_8);

	
	@Override
	public byte[] pack(Data data) {
		if (data == null) {
			return new byte[0];
		}
		switch (data.getType()) {
		case BIN:	return ((DataBinArray)data).get();
		case INT:	return ByteBuffer.allocate(4).putInt( ((DataInt)data).get() ).array();
		case INT16: return ByteBuffer.allocate(2).putShort( ((DataInt16)data).get() ).array();
		case INT64: return ByteBuffer.allocate(8).putLong( ((DataInt64)data).get() ).array();
		case PISTR: return packString( ((DataPIStr)data).get() );
		case STR:	return packString( ((DataString)data).get() );
		case CHAR: 	return packString( ((DataCharArray)data).get(), ((DataCharArray)data).getMaxSize() );

		case POINTER: {
			Data pData = ((DataPtr) data).get(0);
			if (pData == null) {
				return packedNullPointer();
			}
			return pack(pData);
		}
		case ARRAY:
		case STRUCT: {
			Iterator<Data> it = ((DataStruct)data).iterator();
			ArrayList<byte[]> packedElements = new ArrayList<byte[]>();
			while (it.hasNext()) {
				packedElements.add(pack(it.next()));
			}
			int totalSize = 0;
			for (byte[] e : packedElements) {
				totalSize += e.length;
			}
			ByteBuffer buf = ByteBuffer.allocate(totalSize);
			for (byte[] e : packedElements) {
				buf.put(e);
			}
			return buf.array();
		}
		default:
		}
		return new byte[0];
	}
	
	public static byte[] packedNullPointer() {
		ByteBuffer buf = ByteBuffer.allocate(NULL_POINTER.length + 1);
		buf.put(NULL_POINTER);
		buf.put(new byte[1]);
		return buf.array();
	}
	
	private static byte[] packString(String s) {
		byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
		byte[] x = ByteBuffer.allocate(bytes.length + 1).put(bytes).put(InputBufferNative.NULLTERMINATOR).array();
		return x;
	}
	
	private static byte[] packString(String s, int allocSize) {
		byte[] strBytes = s.getBytes(StandardCharsets.UTF_8);
		byte[] bytes = new byte[allocSize];
		for (int i = 0; i < allocSize; i++) {
			if (i < strBytes.length) {
				bytes[i] = strBytes[i];
			} 
		}
		return bytes;
	}
	

	

}
