package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.CollInp;

public class RcRmColl extends RodsApiCall {
	/**
	 * Removes a collection.
	 * @author Ton Smeele
	 * 
	 * The KeyValPair in CollInp can contain the following flags:
	 *    (note: these keywords have no value component)
	 *    RECURSIVE_OPR_KW  - recursively delete collection and its content
	 *    FORCE_FLAG_KW     - delete without moving to the trashcan
	 *    UNREG_COLL_KW     - unregister collection and content instead of delete
	 *    RMTRASH_KW        - delete the trash in this path
	 *    ADMIN_RMTRASH_KW  - admin user to delete other user's trash in this path
	 *
	 */
	public RcRmColl(CollInp collInp)  {
			super(Api.RM_COLL_AN);
			msg.setMessage(collInp);
		}

		@Override
		public String unpackInstruction() {
			return "CollOprStat_PI";
		}


}
