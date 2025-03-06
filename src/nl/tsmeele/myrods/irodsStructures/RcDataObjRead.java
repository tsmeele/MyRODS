package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.OpenedDataObjInp;

public class RcDataObjRead extends RodsApiCall {
	
	public RcDataObjRead(OpenedDataObjInp openedDataObjInp) {
		super(Api.DATA_OBJ_READ_AN);
		msg.setMessage(openedDataObjInp);
	}

	@Override
	public String unpackInstruction() {
		// also returns bytes read in IntInfo 
		//  > 0 = bytes read  (this can be less than 'len' requested, e.g. because EOF reached
		//  < 0 = error
		return null;	// empty reply main message, data is returned via bs message part
	}

}
