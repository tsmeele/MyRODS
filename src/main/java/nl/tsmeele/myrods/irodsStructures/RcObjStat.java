package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.DataObjInp;
import nl.tsmeele.myrods.api.KeyValPair;
import nl.tsmeele.myrods.api.Kw;
import nl.tsmeele.myrods.api.ObjType;

public class RcObjStat extends RodsApiCall {
	// defined in lib/core/include/irods/rodsKeyWdDef.h
	
	public RcObjStat(String objPath) {
		super(Api.OBJ_STAT_AN);
		init(objPath, null);
	}
	
	public RcObjStat(String objPath, ObjType objType) {
		super(Api.OBJ_STAT_AN);
		init(objPath, objType);
	}

	public void init(String objPath, ObjType objType) {
		KeyValPair keyValPair = new KeyValPair();
		if (objType != null) {
			switch (objType) {
			case DATAOBJECT: 
			case COLLECTION:
				keyValPair.put(Kw.SEL_OBJ_TYPE_KW, objType.getLabel());
			default:
			}
		}
		DataObjInp obj = new DataObjInp(objPath, keyValPair);
		msg.setMessage(obj);
	}
	
	@Override
	public String unpackInstruction() {
		return "RodsObjStat_PI";
	}

}
