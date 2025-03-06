package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsStructures.DataBinArray;
import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class BytesBuf extends DataStruct {

	public BytesBuf(byte[] buf) {
		super("BytesBuf_PI");
		add(new DataInt("buflen", buf.length));
		add(new DataBinArray("buf", buf));
	}

}
