package nl.tsmeele.myrods.apiDataStructures;

import java.nio.charset.StandardCharsets;

import nl.tsmeele.json.JObject;
import nl.tsmeele.myrods.irodsStructures.DataBinArray;
import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataPtr;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class JsonInp extends DataStruct {

	public JsonInp(JObject json) {
		super("BinBytesBuf_PI");
		byte[] buf = json.toString().getBytes(StandardCharsets.UTF_8);
		add(new DataInt("buflen", buf.length));
		add(new DataPtr("buf", new DataBinArray("buf", buf)));
	}

	public JsonInp(String json) {
		super("BinBytesBuf_PI");
		byte[] buf = json.getBytes(StandardCharsets.UTF_8);
		add(new DataInt("buflen", buf.length));
		add(new DataPtr("buf", new DataBinArray("buf", buf)));
	}
	
}
