package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.DataObjInp;

public class RcDataObjCreate extends RodsApiCall {

	/* valid keywords in condInput KeyValPair:
	    \n DATA_TYPE_KW - the data type of the data object.
	    \n DEST_RESC_NAME_KW - The resource to store this data object
	    \n FILE_PATH_KW - The physical file path for this data object if the
	        normal resource vault is not used.
	    \n FORCE_FLAG_KW - overwrite existing copy. This keyWd has no value
	*/
	
	
	public RcDataObjCreate(DataObjInp dataObjInp) {
		super(Api.DATA_OBJ_CREATE_AN);
		msg.setMessage(dataObjInp);
	}

	@Override
	public String unpackInstruction() {
		// the open object descriptor is returned via intInfo
		return null;
	}

}
