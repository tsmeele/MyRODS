package plumbing;

import api.Api;
import irodsType.DataStruct;

/**
 * The class IrodsMessage represents the unpacked version of the data structure 
 * that is the unit of communication between iRODS grid participants. 
 * @author Ton Smeele
 *
 */
public class IrodsMessage {
	private IrodsMessageType messageType = null;
	private DataStruct message = null;
	private DataStruct errorMessage = null;
	private DataStruct bs = null;
	private int intInfo;
	
	public IrodsMessage(IrodsMessageType messageType) {
		this.messageType = messageType;
	}
	
	public String toString() {
		String apiNum = "";
		if (messageType == IrodsMessageType.RODS_API_REQ) {
			apiNum = "(" + Api.lookup(intInfo) + ")";

		}
		return "MESSAGE " + messageType.getLabel() + "\n" +
				"Msg = " + (message == null ? "null" : message.toXmlString()) + "\n" +
				"Err = " + (errorMessage == null ? "null" : errorMessage.toXmlString()) + "\n" +
				"bs = " + (bs == null ? "null" : bs.packNativeSize() ) + "\n" +
				"info = " + intInfo + apiNum + "\n";
	}
	
	public IrodsMessageType getMessageType() {
		return messageType;
	}
	
	public void setMessageType(IrodsMessageType messageType) {
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
	
	public DataStruct getBs() {
		return bs;
	}
	
	public void setBs(DataStruct bs) {
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
