package nl.tsmeele.myrods.apiDataStructures;

import java.nio.charset.StandardCharsets;

import nl.tsmeele.json2.JObject;
import nl.tsmeele.myrods.irodsDataTypes.DataBinArray;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

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
