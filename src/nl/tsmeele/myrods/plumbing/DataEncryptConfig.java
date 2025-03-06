package nl.tsmeele.myrods.plumbing;

import java.io.IOException;
import java.util.Random;

import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

/** Class DataEncryptConfig manages the user-configured encryption parameters associated with
 * an IrodsSession that will be used for portal based data transfers (transfers via 'high' ports).
 * @author Ton Smeele
 *
 */
public class DataEncryptConfig {
	// encryption parameters and their default values
	private String algorithm = "AES-256-CBC";
	private int keySize = 32;
	private int saltSize = 8;
	private int hashRounds = 16;
	private String sharedSecret = null;
	
	// constructor with user-specified parameters
	public DataEncryptConfig(String algorithm, int keySize, int saltSize, int hashRounds) {
		this.algorithm = algorithm;
		this.keySize = keySize;
		this.saltSize = saltSize;
		this.hashRounds = hashRounds;
		this.sharedSecret = randomString(30);
	}
	
	// constructor with default configuration
	public DataEncryptConfig() {
		this.sharedSecret = randomString(30);
	}
	
	public void sendConfigTo(IrodsOutputStream out) throws IOException {
		// first send a header-only message with configuration info
	    DataStruct header = new DataStruct("MsgHeader_PI");
	    header.add(new DataString("type", algorithm));  
	    header.add(new DataInt("msgLen", keySize));  
	    header.add(new DataInt("errorLen", saltSize));  
	    header.add(new DataInt("bsLen", hashRounds));  
	    header.add(new DataInt("intInfo", 0));
	    byte[] packedHeader = Packer.pack(IrodsProtocolType.XML_PROT, header);
	    out.writeInt32(packedHeader.length);
	    out.writeBytes(packedHeader);
	    
	    // next send a header + message containing a random shared secret
	    header = new DataStruct("MsgHeader_PI");
	    // DataString secret = new DataString("key",sharedSecret);
	    byte[] message =  sharedSecret.getBytes();    
	    header = new DataStruct("MsgHeader_PI");
	    header.add(new DataString("type", "SHARED_SECRET"));  
	    header.add(new DataInt("msgLen", message.length));  
	    header.add(new DataInt("errorLen", 0));  
	    header.add(new DataInt("bsLen", 0));  
	    header.add(new DataInt("intInfo", 0));
	    packedHeader = Packer.pack(IrodsProtocolType.XML_PROT, header);
	    out.writeInt32(packedHeader.length);
	    out.writeBytes(packedHeader);
	    out.writeBytes(message);
	    out.flush();
	}
	
	private String randomString(int len) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append( (char) random.nextInt(126) + 1);
		}
		return sb.toString();
	}
}
