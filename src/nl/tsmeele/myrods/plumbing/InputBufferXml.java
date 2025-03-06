package nl.tsmeele.myrods.plumbing;

import java.util.Base64;

import nl.tsmeele.myrods.irodsStructures.DataBinArray;
import nl.tsmeele.myrods.irodsStructures.DataCharArray;
import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataInt16;
import nl.tsmeele.myrods.irodsStructures.DataInt64;
import nl.tsmeele.myrods.irodsStructures.DataPIStr;
import nl.tsmeele.myrods.irodsStructures.DataString;

/**Implements the xml unpack protocol as in use before iRODS release 4.2.9.
 * @author Ton Smeele
 *
 */
public class InputBufferXml implements InputBuffer {
	private static final boolean STARTTAG = false;
	private static final boolean ENDTAG = true;
	private TagParser tags = null;
	
	public InputBufferXml(byte[] packedData) {
		tags = new TagParser(new String(packedData));		
	}

	@Override
	public void endUnpack() throws MyRodsException {
		// make sure we completely processed all input
		if (tags.isRead()) {
			tags.advanceOneTag();
		}
		if (tags.hasTag()) {
			throw new MyRodsException("A tag " + unescapeXml(tags.getTagLabel()) + 
					" remains after processing of input data has completed");
		}
	}


	@Override
	public void startUnpackStruct(String packInstruction) throws MyRodsException {
		advanceTagIfNeeded(STARTTAG, packInstruction);
		// consume and check the start tag
		tags.setTagRead(true);
		// a packing instruction does not have tagcontent, just get the label and tagtype
		String tagLabel = unescapeXml(tags.getTagLabel());
		boolean endTag = tags.isEndTag();
		if (!tagLabel.equals(packInstruction)) {
			throw new MyRodsException("Found request for an unsupported packing Instruction: " + tagLabel);
		}
		if (endTag) {
			throw new MyRodsException("Found an endtag for " + tagLabel + " while a starttag was expected");
		}		
	}


	@Override
	public void endUnpackStruct(String packInstruction) throws MyRodsException {
		readEndTag(packInstruction);
	}
	
	
	@Override
	public boolean unpackNullPointer(String name) throws MyRodsException {
		advanceTagIfNeeded(STARTTAG, name);
		// peek ahead at the tag (we do not "read" it yet)
		String tagLabel = unescapeXml(tags.getTagLabel());
		//System.out.println("TAG label=" + tagLabel + "| endTag=" + endTag + "| content =" + tagContent + "|");

		// If the tag label differs from the expected name then 
		// the tag we are looking for is missing out in the data stream.
		// By convention, this signals a null value for the named data element
		return !name.equals(tagLabel);
	}

	@Override
	public DataInt unpackInt(String name) throws MyRodsException {
		readStartTag(name);
		DataInt i = null;
		try {
			i = new DataInt(name, Integer.parseInt( unescapeXml(tags.getTagContent()) ));
		} catch (Exception e) {
			i = new DataInt(name, 0);
		}
		readEndTag(name);
		return i;
	}

	@Override
	public DataInt16 unpackInt16(String name) throws MyRodsException {
		readStartTag(name);
		DataInt16 i = null;
		try {
			i = new DataInt16(name, Short.parseShort( unescapeXml(tags.getTagContent()) ));
		} catch (Exception e) {
			i = new DataInt16(name, (short)0);
		}
		readEndTag(name);
		return i;
	}

	@Override
	public DataInt64 unpackInt64(String name) throws MyRodsException {
		readStartTag(name);
		DataInt64 i = null;
		try {
			i = new DataInt64(name, Long.parseLong( unescapeXml(tags.getTagContent()) ));
		} catch (Exception e) {
			i = new DataInt64(name, 0L);
		}
		readEndTag(name);
		return i;
	}

	@Override
	public DataString unpackString(String name, int allocSize) throws MyRodsException {
		readStartTag(name);
		DataString s = null;
		if (allocSize > 1) {
			s = new DataString(name,unescapeXml(tags.getTagContent()), allocSize );
		} else {
			s = new DataString(name,unescapeXml(tags.getTagContent()) );
		}
		readEndTag(name);
		return s;
	}

	@Override
	public DataPIStr unpackPIStr(String name, int allocSize) throws MyRodsException {
		readStartTag(name);
		DataPIStr s = null;
		if (allocSize > 1) {
			s = new DataPIStr(name,unescapeXml(tags.getTagContent()), allocSize );
		} else {
			s = new DataPIStr(name,unescapeXml(tags.getTagContent()) );
		}
		readEndTag(name);
		return s;
	}

	@Override
	public DataBinArray unpackBinArray(String name, int allocSize) throws MyRodsException {
		readStartTag(name);
		String tagContent = unescapeXml(tags.getTagContent());		
		byte[] decoded = Base64.getDecoder().decode(tagContent);
		if (allocSize == 0) {
			allocSize = decoded.length;
		}
		byte[] bytes = new byte[allocSize];
		for (int i = 0; i < allocSize; i++) {
			if (i < decoded.length) {
				bytes[i] = decoded[i];
			} 
		}		
		DataBinArray bin = new DataBinArray(name, bytes);
		readEndTag(name);
		return bin;
	}

	@Override
	public DataCharArray unpackCharArray(String name, int allocSize) throws MyRodsException {
		readStartTag(name);
		DataCharArray c = new DataCharArray(name,unescapeXml(tags.getTagContent()), allocSize );
		readEndTag(name);
		return c;
	}
	

	private void advanceTagIfNeeded(boolean endTag, String name) throws MyRodsException {
		if (tags.isRead()) {
			tags.advanceOneTag();
		}
		if (!tags.hasTag()) {
			if (endTag) {
				throw new MyRodsException("Missing endtag for " + name);
			} else {
				throw new MyRodsException("Input exhausted during unpack of instruction " + name);
			}
		}
	}
	
	private void readStartTag(String name) throws MyRodsException {
		advanceTagIfNeeded(STARTTAG, name);
		tags.setTagRead(true);
		String tagLabel = unescapeXml(tags.getTagLabel()); 
		if ( !tagLabel.equals(name)  ) {
			throw new MyRodsException("Expected starttag for " + name + " but got " + tagLabel);
		}
		if (tags.isEndTag()) {
			throw new MyRodsException("Expected a starttag for " + name + " but got an endtag");
		}
	}
	
	private void readEndTag(String name) throws MyRodsException {
		advanceTagIfNeeded(ENDTAG, name);
		tags.setTagRead(true);
		String tagLabel = unescapeXml(tags.getTagLabel()); 
		if ( !tagLabel.equals(name)  ) {
			throw new MyRodsException("Expected endtag for " + name + " but got " + tagLabel);
		}
		if (!tags.isEndTag()) {
			throw new MyRodsException("Expected an endtag for " + name + " but got a starttag");
		}
	}



	
	public String unescapeXml(String s) {
		s = s.replace("&quot;","\"");
		s = s.replace("&apos;", "`");
		s = s.replace("&gt;", ">");
		s = s.replace("&lt;", "<");
		s = s.replace("&amp;", "&");
		return s;
	}
	
	

}
