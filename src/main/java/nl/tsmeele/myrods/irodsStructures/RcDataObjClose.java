package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.OpenedDataObjInp;

public class RcDataObjClose extends RodsApiCall {

	public RcDataObjClose(OpenedDataObjInp openedDataObjInp) {
		super(Api.DATA_OBJ_CLOSE_AN);
		msg.setMessage(openedDataObjInp);
	}
	
	@Override
	public String unpackInstruction() {
		// returns no struct
		return null;
	}

}
