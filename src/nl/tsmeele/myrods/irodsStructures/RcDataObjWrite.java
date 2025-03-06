package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.OpenedDataObjInp;

public class RcDataObjWrite extends RodsApiCall {

	public RcDataObjWrite(OpenedDataObjInp openedDataObjInp, byte[] buf) {
		super(Api.DATA_OBJ_WRITE_AN);
		msg.setMessage(openedDataObjInp);
		msg.setBs(buf);
	}
	
	@Override
	public String unpackInstruction() {
		// has no output struct, returns bytes written in IntInfo
		//  > 0 = bytes written  (this can be less than 'len' requested, e.g. because EOF reached
		//  < 0 = error
		return null;  
	}

}
