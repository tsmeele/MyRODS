package plumbing;

import java.io.IOException;
import java.nio.ByteBuffer;

import api.Api;
import irodsType.Data;
import irodsType.DataInt;
import irodsType.DataString;
import irodsType.DataStruct;

/**
 * The class IrodsPackedMessage is responsible for packing/unpacking an IrodsMessage
 * for efficient exchange between data grid participants over a network connection.
 * @author Ton Smeele
 *
 */
public class IrodsPackedMessage {
	private IrodsProtocolType protocol = null;
	
	private byte[] packedHeader = new byte[0];
	private int intInfo;
	private IrodsMessageType messageType = null;
	private byte[] packedMessage = new byte[0];
	private byte[] packedErrorMessage = new byte[0];
	private byte[] packedBs = new byte[0];
	
	
	/**
	 * Constructs a packed message by reading its content from an inputstream.
	 * @param in
	 * @throws MyRodsException
	 */
	public IrodsPackedMessage(IrodsInputStream in) throws MyRodsException {
		protocol = in.getProtocol();
		// first read the header part from inputstream
		int headerSize = in.readInt32();
		packedHeader = in.readBytes(headerSize);
		// header is always xml-packed, regardless of chosen protocol
		DataStruct msgHeader = Data.unpackXml("MsgHeader_PI", ByteBuffer.wrap(packedHeader));
		// extract header parts 
		intInfo = ((DataInt) msgHeader.lookupName("intInfo")).get();		
		String type = ((DataString) msgHeader.lookupName("type")).get();
		messageType = IrodsMessageType.get(type);
		if (messageType == null) {
			throw new MyRodsException("Received unsupported message type: " + type);
		}
		int msgLen = ((DataInt) msgHeader.lookupName("msgLen")).get();
		int errorLen = ((DataInt) msgHeader.lookupName("errorLen")).get();
		int bsLen = ((DataInt) msgHeader.lookupName("bsLen")).get();

		// next, use header elements to read remainder message parts from inputstream
		packedMessage = in.readBytes(msgLen);
		packedErrorMessage = in.readBytes(errorLen);
		packedBs = in.readBytes(bsLen);
	}
	
	/**
	 * Constructs a packed message from a regular (unpacked) message.
	 * Used for outgoing communication purposes
	 * @param msg
	 * @param protocol
	 * @throws IOException
	 */
	public IrodsPackedMessage(IrodsMessage msg, IrodsProtocolType protocol) throws IOException {
		this.protocol = protocol;
		intInfo = msg.getIntInfo();
		messageType = msg.getMessageType();
		// pack the message parts
		packedMessage = packPart(protocol, msg.getMessage());
		packedErrorMessage = packPart(protocol, msg.getErrorMessage());
		packedBs = packPart(protocol, msg.getBs());
		// assemble the header
		DataStruct header = new DataStruct("MsgHeader_PI");
		header.add(new DataString("type", messageType.getLabel()));
		header.add(new DataInt("msgLen", packedMessage.length));
		header.add(new DataInt("errorLen", packedErrorMessage.length));
		header.add(new DataInt("bsLen", packedBs.length));
		header.add(new DataInt("intInfo", intInfo));
		// header is always packed using XML protocol
		packedHeader = packPart(IrodsProtocolType.XML_PROT, header);
	}
	
	public String toString() {
		String apiNum = "";
		if (messageType == IrodsMessageType.RODS_API_REQ) {
			apiNum = "(" + Api.lookup(intInfo) + ")";
		}
		return "PACKEDMESSAGE " + messageType.getLabel() + "\n" +
				"Msg(" + (packedMessage == null ? "null" : packedMessage.length) + ") " +
				"Err(" + (packedErrorMessage == null ? "null" : packedErrorMessage.length) + ") " +
				"bs(" + packedBs.length + ") " +
				"info = " + intInfo + apiNum +"\n";
	}
	
	public IrodsMessageType getType() {
		return messageType;
	}
	
		
	public IrodsMessage unpack(String unpackInstruction) throws MyRodsException  {
		IrodsMessage msg = new IrodsMessage(messageType);
		// copy information held in header
		msg.setIntInfo(intInfo);
		// unpack main message part
		DataStruct message;
		if (packedMessage.length == 0 || unpackInstruction == null) {
			message = new DataStruct("message", null);
		} else {
			message = Data.unpack(protocol, unpackInstruction, ByteBuffer.wrap(packedMessage) );
		} 
		msg.setMessage(message);
		// unpack error message part 
		DataStruct errorMessage;

		if (packedErrorMessage.length == 0) {
			errorMessage = new DataStruct("errorMessage", null);
		} else {
			errorMessage = Data.unpack(protocol, "RError_PI", ByteBuffer.wrap(packedErrorMessage) );
		} 	
		msg.setErrorMessage(errorMessage);
		// unpack binary data part
		DataStruct bs;
		if (packedBs.length == 0) {
			bs = new DataStruct("bs", null);
		} else {
			bs = Data.unpackXml("BinBytesBuf_PI", ByteBuffer.wrap(packedBs) );
		}
		msg.setBs(bs);
		return msg;
	}
	
	public void writeToOutputStream(IrodsOutputStream out) throws IOException {
		// send size of the header  NB: the header contains sizes of other parts
		out.writeInt32(packedHeader.length);
		// send the message parts
		out.writeBytes(packedHeader);
		out.writeBytes(packedMessage);
		out.writeBytes(packedErrorMessage);
		out.writeBytes(packedBs);
		out.flush();
	}

	
	
	private byte[] packPart(IrodsProtocolType protocol, DataStruct part) {
		if (part == null) {
			return new byte[0];
		}
		switch (protocol) {
		case NATIVE_PROT:
			return part.packNative();
		case XML_PROT:
			return (new DataString("packXml", part.packXml()) ).packNative();
		default:
		}
		return null;
	}	

}
