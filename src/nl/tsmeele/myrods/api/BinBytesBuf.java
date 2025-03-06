package nl.tsmeele.myrods.api;


import nl.tsmeele.myrods.irodsStructures.DataBinArray;
import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataPtr;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class BinBytesBuf extends DataStruct {

	// "BinBytesBuf_PI", "int buflen; bin *buf(buflen);",

	public BinBytesBuf(byte[] buf) {
		super("BinBytesBuf_PI");
		add(new DataInt("buflen", buf.length));
		add(new DataPtr("buf", new DataBinArray("buf", buf)));
	}
	

}
