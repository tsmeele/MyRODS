package nl.tsmeele.myrods.irodsDataTypes;

import nl.tsmeele.myrods.api.RodsApiCall;
import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.OpenedDataObjInp;

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
