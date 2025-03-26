package nl.tsmeele.myrods.api;

import java.util.ArrayList;
import java.util.Iterator;

import nl.tsmeele.myrods.irodsStructures.Data;
import nl.tsmeele.myrods.irodsStructures.DataPtr;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

// "RErrMsg_PI", "int status; str msg[ERR_MSG_LEN];",
// "RError_PI", "int count; struct *RErrMsg_PI[count];",


public class RError extends DataStruct {

	public ArrayList<RErrMsg> errorMessages =  new ArrayList<RErrMsg>();

	public RError(DataStruct dataStruct) {
		super("RError_PI");
		Iterator<Data> it = dataStruct.iterator();
		if (!it.hasNext()) return;
		// read count
		it.next();
		// read messages
		while (it.hasNext()) {
			DataPtr ptr = (DataPtr) it.next();
			if (ptr.get(0) != null) { 
				RErrMsg rErrMsg = new RErrMsg((DataStruct) ptr.get(0));
				errorMessages.add(rErrMsg);
			}
		}
	}

}
