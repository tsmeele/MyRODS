package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.OpenedDataObjInp;

public class RcDataObjLseek extends RodsApiCall {

	public RcDataObjLseek(OpenedDataObjInp openedDataObjInp) {
		super(Api.DATA_OBJ_LSEEK_AN);
		msg.setMessage(openedDataObjInp);
	}
	
	@Override
	public String unpackInstruction() {
		return null;
	}

}
