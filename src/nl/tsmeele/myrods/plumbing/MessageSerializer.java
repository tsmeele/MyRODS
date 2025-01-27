package nl.tsmeele.myrods.plumbing;

import java.io.IOException;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.apiDataStructures.MessageType;
import nl.tsmeele.myrods.apiDataStructures.RError;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

/**
 * The class PackedMessage is responsible for packing/unpacking a Message
 * for efficient exchange between data grid participants over a network connection.
 * @author Ton Smeele
 *
 */
public class MessageSerializer {
	private IrodsProtocolType protocol = null;
	
	private byte[] packedHeader = new byte[0];
	private int intInfo;
	private MessageType messageType = null;
	public byte[] packedMessage = new byte[0];
	private byte[] packedErrorMessage = new byte[0];
	private byte[] packedBs = new byte[0];
	
	
	/**
	 * Constructs a packed message by reading its content from an inputstream.
	 * @param in
	 * @throws MyRodsException
	 */
	public MessageSerializer(IrodsInputStream in) throws MyRodsException {
		protocol = in.getProtocol();
		// first read the header part from inputstream
		int headerSize = in.readInt32();
		packedHeader = in.readBytes(headerSize);
		// header is always xml-packed, regardless of chosen protocol
		DataStruct msgHeader = Unpacker.unpack(IrodsProtocolType.XML_PROT, "MsgHeader_PI", packedHeader);
		// extract header parts 
		intInfo = ((DataInt) msgHeader.lookupName("intInfo")).get();		
		String type = ((DataString) msgHeader.lookupName("type")).get();
		messageType = MessageType.lookup(type);
		if (messageType == null) {
			throw new MyRodsException("Received unsupported message type: " + type);
		}
		int msgLen = ((DataInt) msgHeader.lookupName("msgLen")).get();
		int errorLen = ((DataInt) msgHeader.lookupName("errorLen")).get();
		int bsLen = ((DataInt) msgHeader.lookupName("bsLen")).get();
		// next, use header elements to read remainder message parts from inputstream
		//System.err.println("PMSG: " + msgHeader);
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
	public MessageSerializer(Message msg, IrodsProtocolType protocol) throws IOException {
		this.protocol = protocol;
		intInfo = msg.getIntInfo();
		messageType = msg.getMessageType();
		// pack the message parts
		packedMessage = Packer.pack(protocol, msg.getMessage());
		packedErrorMessage = Packer.pack(protocol, msg.getErrorMessage());
		packedBs = msg.getBs();
		// assemble the header
		DataStruct header = new DataStruct("MsgHeader_PI");
		header.add(new DataString("type", messageType.getLabel()));
		header.add(new DataInt("msgLen", packedMessage.length));
		header.add(new DataInt("errorLen", packedErrorMessage.length));
		header.add(new DataInt("bsLen", packedBs.length));
		header.add(new DataInt("intInfo", intInfo));
		// header is always packed using XML protocol
		packedHeader = Packer.pack(IrodsProtocolType.XML_PROT, header);
	}
	
	public String toString() {
		String apiNum = "";
		if (messageType == MessageType.RODS_API_REQ) {
			apiNum = "(" + Api.lookup(intInfo) + ")";
		}
		return "PACKEDMESSAGE " + messageType.getLabel() + "\n" +
				"Msg(" + (packedMessage == null ? "null" : packedMessage.length) + ") " +
				"Err(" + (packedErrorMessage == null ? "null" : packedErrorMessage.length) + ") " +
				"bs(" + packedBs.length + ") " +
				"info = " + intInfo + apiNum +"\n";
	}
	
	public MessageType getType() {
		return messageType;
	}
	
		
	public Message unpack(String unpackInstruction) throws MyRodsException  {
		Message msg = new Message(messageType);
		// copy information held in header
		msg.setIntInfo(intInfo);
		// unpack main message part
		DataStruct message;
		if (packedMessage.length == 0 || unpackInstruction == null) {
			message = new DataStruct("message");
		} else {
			message = Unpacker.unpack(protocol, unpackInstruction, packedMessage);
		} 
		msg.setMessage(message);
		// unpack error message part 
		if (packedErrorMessage.length == 0) {
			msg.setErrorMessage(null);
		} else {
			DataStruct errorMessage = Unpacker.unpack(protocol, "RError_PI", packedErrorMessage);
			msg.setErrorMessage(new RError(errorMessage));
		}
		msg.setBs(packedBs);
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

}
