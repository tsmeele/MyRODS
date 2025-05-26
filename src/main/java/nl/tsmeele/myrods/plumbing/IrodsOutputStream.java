package nl.tsmeele.myrods.plumbing;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import nl.tsmeele.myrods.api.Message;

/**
 * The class IrodsOutputStream buffers and packs outgoing Irods messages.
 * @author Ton Smeele
 *
 */
public class IrodsOutputStream extends OutputStream {
	private IrodsProtocolType protocol = IrodsProtocolType.XML_PROT;
	private BufferedOutputStream bufferedOut;
	

	public IrodsOutputStream(OutputStream out, IrodsProtocolType protocol) {
		super();
		this.protocol = protocol;
		this.bufferedOut = new BufferedOutputStream(out);
	}

	
	public IrodsProtocolType getProtocol() {
		return protocol;
	}
	
	public void setProtocol(IrodsProtocolType protocol) {
		this.protocol = protocol;
	}
	
	
	/**
	 * stream data using agreed upon protocol
	 * @param msg
	 * @throws IOException
	 */
	public void writeMessage(Message msg) throws IOException {
		MessageSerializer pMsg = new MessageSerializer(msg, protocol);
		// send message (includes flush)
		pMsg.writeToOutputStream(this);
	}
	
	public void writeBytes(byte[] bytes) throws IOException {
		if (bytes.length > 0) {
			bufferedOut.write(bytes);
		}
	}
	
	public void writeInt32(int i) throws IOException {
		bufferedOut.write(ByteBuffer.allocate(4).putInt(i).array());
	}
	
	@Override
	public void flush() throws IOException {
		bufferedOut.flush();
	}
	
	@Override
	public void write(int i) throws IOException {
		bufferedOut.write(i);
	}

}
