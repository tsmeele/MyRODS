package nl.tsmeele.myrods.plumbing;

/** PackerXml429 packs data according to an XML based protocol used by iRODS 4.2.9+ releases.
 * @author Ton Smeele
 *
 */
public class PackerXml429 extends PackerXml {
	
	// as of rods4.2.9 the encoding for apostroph is more aligned with XMl specs
	@Override
	public String escapeXml(String s) {
	s = s.replace("&", "&amp;");
	s = s.replace("<", "&lt;");
	s = s.replace(">", "&gt;");
	s = s.replace("'", "&apos;");	// changed
	s = s.replace("\"", "&quot;");
	return s;
	}
}
