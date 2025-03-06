package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.DataObjInp;

public class RcDataObjUnlink extends RodsApiCall{
	
	/*   Elements of dataObjInp used:
	 *    objPath 	- full path of the data object.
	 *    oprType 	- 0 normally.When set to UNREG_OPR, the data object
	 *         			is unregistered but the physical file is not deleted.
	 *    condInput - keyword/value pair input. Valid keywords:
	 *       			FORCE_FLAG_KW - delete the data object. If it is not set, the data
	 *         					object is moved to trash. This keyWd has no value.
	 *       			REPL_NUM_KW - The replica number of the replica to delete.
	 *       			RESC_NAME_KW - delete replica stored in this resource.
	 */
	
	
	public RcDataObjUnlink(DataObjInp dataObjInp) {
		super(Api.DATA_OBJ_UNLINK_AN);
		msg.setMessage(dataObjInp);
	}

	@Override
	public String unpackInstruction() {
		return null;	// returns intInfo = 0 on success
	}

}
