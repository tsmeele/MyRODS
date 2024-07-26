package nl.tsmeele.myrods.plumbing;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;

import nl.tsmeele.myrods.irodsDataTypes.Data;
import nl.tsmeele.myrods.irodsDataTypes.DataArray;
import nl.tsmeele.myrods.irodsDataTypes.DataBinArray;
import nl.tsmeele.myrods.irodsDataTypes.DataCharArray;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataInt16;
import nl.tsmeele.myrods.irodsDataTypes.DataInt64;
import nl.tsmeele.myrods.irodsDataTypes.DataPIStr;
import nl.tsmeele.myrods.irodsDataTypes.DataPtr;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

/** PackerXml packs data in line with XML based protocol as used before iRODS 4.2.9.
 * @author Ton Smeele
 *
 */
public class PackerXml extends Packer {

	@Override
	public byte[] pack(Data data) {
		if (data == null) {
			return new byte[0];
		}
		String tagged = packType(data);
		return tagged.getBytes(StandardCharsets.UTF_8);
	}
	
	public String packType(Data data) {
		String name = data.getName();
		switch (data.getType()) {
		case INT: 	return tagOne(name, Integer.toString( ((DataInt)data).get() )  );
		case INT16:	return tagOne(name, Short.toString( ((DataInt16)data).get() )  );
		case INT64:	return tagOne(name, Long.toString( ((DataInt64)data).get() )  );
		case PISTR:	return tagOne(name, ((DataPIStr)data).get() );
		case STR:	return tagOne(name, ((DataString)data).get() );
		case CHAR:	return tagOne(name, ((DataCharArray)data).get() );
		case BIN:	return tagOne(name, Base64.getEncoder().encodeToString( ((DataBinArray)data).get() ));
		case POINTER: {
			Data pData = ((DataPtr)data).get();
			if (pData == null) {
				// iRODS protocol convention: the absence of a tag signals a nullpointer
				return "";
			}
			return packType(pData);
		}
		case ARRAY: return packElements((DataArray)data);
		case STRUCT: return tagComposed(name, packElements((DataStruct)data));
		default:
		}
		return "";
	}
	
	private String packElements(DataStruct data) {
	Iterator<Data> it = ((DataStruct)data).iterator();
	StringBuilder sb = new StringBuilder();
	while (it.hasNext()) {
		sb.append( packType(it.next()) );
	}
	return sb.toString();
	}
	
	private String tagComposed(String tag, String content) {
		return "<" + escapeXml(tag) + ">" + content + "</" + escapeXml(tag) + ">";
	}
	
	private String tagOne(String tag, String content) {
		return tagComposed(tag, escapeXml(content));
	}
	
	
	public String escapeXml(String s) {
	s = s.replace("&", "&amp;");
	s = s.replace("<", "&lt;");
	s = s.replace(">", "&gt;");
	s = s.replace("`", "&apos;");
	s = s.replace("\"", "&quot;");
	return s;
}
	
	

}
