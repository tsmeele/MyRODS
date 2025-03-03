package nl.tsmeele.myrods.irodsDataTypes;

import nl.tsmeele.myrods.api.RodsApiCall;
import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.ModDataObjMetaInp;

public class RcModDataObjMeta extends RodsApiCall {

	public RcModDataObjMeta(ModDataObjMetaInp modDataObjMetaInp) {
		super(Api.MOD_DATA_OBJ_META_AN);
		msg.setMessage(modDataObjMetaInp);
	}

	@Override
	public String unpackInstruction() {
		return null;
	}

}
