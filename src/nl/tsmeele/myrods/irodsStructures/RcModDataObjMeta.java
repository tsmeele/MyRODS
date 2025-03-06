package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.ModDataObjMetaInp;

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
