package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.DataObjInp;

public class RcDataObjOpen extends RodsApiCall {

	public RcDataObjOpen(DataObjInp dataObjInp) {
		super(Api.DATA_OBJ_OPEN_AN);
		msg.setMessage(dataObjInp);
	}

	@Override
	public String unpackInstruction() {
		// the open object descriptor is returned via intInfo
		return null;
	}

}
