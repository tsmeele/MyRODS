package nl.tsmeele.myrods.irodsDataTypes;

import nl.tsmeele.myrods.api.RodsApiCall;
import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.OpenedDataObjInp;

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
