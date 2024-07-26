package nl.tsmeele.myrods.plumbing;

/** Implements the xml unpack protocol as in use per iRODS release 4.2.9.
 * @author Ton Smeele
 *
 */
public class InputBufferXml429 extends InputBufferXml {

	public InputBufferXml429(byte[] packedData) {
		super(packedData);
	}
	
	// as of rods4.2.9 the encoding for apostroph is more aligned with XMl specs
	
	@Override
	public String unescapeXml(String s) {
		s = s.replace("&quot;", "\"");
		s = s.replace("&apos;", "'");	// this transformation has changed!
		s = s.replace("&gt;", ">");
		s = s.replace("&lt;", "<");
		s = s.replace("&amp;", "&");
		return s;
	}

}
