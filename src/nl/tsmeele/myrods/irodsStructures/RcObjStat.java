package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.DataObjInp;
import nl.tsmeele.myrods.apiDataStructures.KeyValPair;
import nl.tsmeele.myrods.apiDataStructures.Kw;
import nl.tsmeele.myrods.apiDataStructures.ObjType;

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
