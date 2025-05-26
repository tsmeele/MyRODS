package nl.tsmeele.myrods.plumbing;

import nl.tsmeele.myrods.irodsStructures.Data;

/** Class Packer packs data according to the selected protocol strategy.
 * @author Ton Smeele
 *
 */
public abstract class Packer {
	
	/** packs an arbitrary data structure using a known protocol
	 * @param protocol	protocol to use as a strategy
	 * @param data		source data structure
	 * @return			packed data
	 */
	public static byte[] pack(IrodsProtocolType protocol, Data data) {

		switch (protocol) {
		case NATIVE_PROT: 	return (new PackerNative()).pack(data);
		case XML_PROT: 		return (new PackerXml()).pack(data);
		case XML_PROT429: 	return (new PackerXml429()).pack(data);
		default: return new byte[0];
		}
	}
	
	
	public abstract byte[] pack(Data data);
	
	
	
}
