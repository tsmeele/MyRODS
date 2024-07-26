package nl.tsmeele.myrods.plumbing;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * The class IrodsInputStream buffers incoming Irods packed messages.
 * Unpacking of the message is responsibility of the user, since the unpack method will depend on context.
 * @author Ton Smeele
 *
 */
public class IrodsInputStream extends InputStream {
	private IrodsProtocolType protocol = null;
	private BufferedInputStream bufferedIn;

	public IrodsInputStream(InputStream in, IrodsProtocolType protocol) {
		super();
		this.protocol = protocol;
		this.bufferedIn = new BufferedInputStream(in);
	}
	
	
	public IrodsProtocolType getProtocol() {
		return protocol;
	}
	
	public void setProtocol(IrodsProtocolType protocol) {
		this.protocol = protocol;
	}

	public PackedMessage readMessage() throws IOException {
		// the IrodsPackedMessage will read exactly one message from the input stream 
		return new PackedMessage(this);		
	}
	
	public int readInt32() throws MyRodsException {
		return ByteBuffer.wrap(readBytes(4)).getInt();
	}
	
	@Override
	public int read() throws IOException {
		return bufferedIn.read();
	}

	
	public byte[] readBytes(int count) throws MyRodsException {
		byte[] buffer = new byte[count];
		int totalRead = 0;
		while (totalRead < count) {
			int actuallyRead;
			try {
				actuallyRead = bufferedIn.read(buffer, totalRead, count - totalRead);
			} catch (IOException e) {
				throw new MyRodsException(e.getMessage());
			}
			if (actuallyRead == -1) { 
				throw new MyRodsException("Inputstream exhausted. Expected " + count + " bytes, got " + actuallyRead);
			}
			totalRead += actuallyRead;
		}
		return buffer;
	}
	
	
}
