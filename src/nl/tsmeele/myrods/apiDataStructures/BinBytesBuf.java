package nl.tsmeele.myrods.apiDataStructures;


import nl.tsmeele.myrods.irodsDataTypes.DataBinArray;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

public class BinBytesBuf extends DataStruct {

	// "BinBytesBuf_PI", "int buflen; bin *buf(buflen);",

	public BinBytesBuf(byte[] buf) {
		super("BinBytesBuf_PI");
		add(new DataInt("buflen", buf.length));
		add(new DataPtr("buf", new DataBinArray("buf", buf)));
	}
	

}
