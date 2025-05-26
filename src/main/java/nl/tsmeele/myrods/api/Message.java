package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.irodsStructures.DataStruct;

/**
 * The class Message represents the unpacked version of the data structure 
 * that is the unit of communication between iRODS grid participants. 
 * @author Ton Smeele
 *
 */
public class Message {
	private MessageType messageType = null;
	private DataStruct message = null;
	private DataStruct errorMessage = null;
	private byte[] bs = new byte[0];
	private int intInfo;
	
	public Message(MessageType messageType) {
		this.messageType = messageType;
	}
	
	public String toString() {
		String apiNum = "";
		if (messageType == MessageType.RODS_API_REQ) {
			apiNum = " (" + Api.lookup(intInfo) + ")";

		}
		return "MESSAGE " + messageType.getLabel() + "\n" +
				"Msg = " + (message == null || message.size() == 0 ? "null" : 
					     message.toString() ) + "\n" +
				"Err = " + (errorMessage == null || errorMessage.size() == 0 ? "null" : 
						 errorMessage.toString() ) + "\n" +
				"bs = " + bs.length + "\n" +
				"info = " + intInfo + apiNum + "\n";
	}
	
	public MessageType getMessageType() {
		return messageType;
	}
	
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
	
	public DataStruct getMessage() {
		return message;
	}
	
	public void setMessage(DataStruct message) {
		this.message = message;
	}
	
	public DataStruct getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(DataStruct errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public byte[] getBs() {
		return bs;
	}
	
	public void setBs(byte[] bs) {
		this.bs = bs;
	}
	
	public int getIntInfo() {
		return intInfo;
	}
	
	public void setIntInfo(int intInfo) {
		this.intInfo = intInfo;
	}
	
	public void setIntInfo(Api apiLabel) {
		this.intInfo = apiLabel.id();
	}
	
	
	

	
}
