package nl.tsmeele.myrods.irodsStructures;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import nl.tsmeele.myrods.plumbing.ServerConnection;
import nl.tsmeele.myrods.plumbing.MyRodsException;
import nl.tsmeele.myrods.api.Message;
import nl.tsmeele.myrods.plumbing.MessageSerializer;

/**
 * Superclass for all API calls to iRODS server.
 * @author Ton Smeele
 *
 */
public abstract class RodsCall {
	protected Message msg = null;

	/**
	 * Sends a request to the server and receives a reply in return.
	 * Subclasses can override this behavior 
	 * @param session
	 * @return reply from server
	 * @throws IOException
	 * @throws MyRodsException
	 */
	public Message sendTo(ServerConnection session) throws IOException, MyRodsException {
		session.getOutputStream().writeMessage(msg);
		MessageSerializer reply = session.getInputStream().readMessage();
		Message message = reply.unpack(unpackInstruction());
		if (unpackInstruction() != null) {
			// facilitate casting of message to the output class 
			//    e.g.   MsParamArray m = (MsParamArray) message.getMessage()
			// note that the output class will need to have a constructor 
			// that accepts a single parameter of class DataStruct
			DataStruct msg = message.getMessage();
			msg = convertToOutputClass(msg);
			message.setMessage(msg);
		}
		return message;
	}
	
	public Message getIrodsMessage() {
		return msg;
	}

	public String toString() {
		return msg.toString();
	}
	
	public abstract String unpackInstruction();
	
	/**
	 * Converts an instance of DataStruct to an instance of the specified subclass
	 * of DataStruct.
	 * 
	 * @param inStruct	object to be converted
	 * @return converted object (or original object if conversion failed)
	 */
	public static DataStruct convertToOutputClass(DataStruct inStruct) {
		Object obj = inStruct;
		String className = inStruct.getName();
		Class<?> targetClass;
		if (className.endsWith("_PI")) {
			className = className.substring(0,className.length() - 3);
		}
		try {
			targetClass = 
					Class.forName("nl.tsmeele.myrods.api." + className);
			Constructor<?> constr = targetClass.getConstructor(DataStruct.class);
			obj = constr.newInstance(inStruct);			
		} catch (ClassNotFoundException | NoSuchMethodException | 
				SecurityException | InstantiationException | 
				IllegalAccessException | IllegalArgumentException | 
				InvocationTargetException e) {
			// unable to convert, we keep original DataStruct object as is
		}
		return (DataStruct) obj;
	}
	
	
}
