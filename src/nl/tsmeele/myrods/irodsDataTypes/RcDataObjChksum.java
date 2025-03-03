package nl.tsmeele.myrods.irodsDataTypes;


import nl.tsmeele.myrods.api.RodsApiCall;
import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.DataObjInp;

/**
 * Calculates/checks checksums of replicas.
 * @author Ton Smeele
 *
 */
public class RcDataObjChksum extends RodsApiCall {
	
	
/* see /lib/api/include/irods/dataObjChksum.h :
 * 
 * Keywords supported by \p condInput:
	 - \p FORCE_CHKSUM_KW:  ("forceChksum")
	     - Instructs the server to compute and update the checksum.
	     - Must be set to an empty string.
	 - \p REPL_NUM_KW:	("replNum")
	     - Identifies a specific replica by the replica number.
	     - Accepts the replica number as a string.
	     - Incompatible with \p RESC_NAME_KW.
	     - Incompatible with \p CHKSUM_ALL_KW.
	 - \p RESC_NAME_KW:  ("rescName")
	     - Identifies a specific replica by the leaf resource name.
	     - Accepts the resource name as a string.
	     - Incompatible with \p REPL_NUM_KW.
	     - Incompatible with \p CHKSUM_ALL_KW.
	 - \p CHKSUM_ALL_KW:	("ChksumAll")
	     - Instructs the server to operate on all replicas.
	     - Must be set to an empty string.
	     - In \p lookup/update mode, reports if the replicas do not share identical checksums if no errors occur.
	     - Incompatible with \p RESC_NAME_KW.
	     - Incompatible with \p REPL_NUM_KW.
	 - \p ADMIN_KW:	("irodsAdmin")
	     - Instructs the server to execute the operation as an administrator.
	     - Must be set to an empty string.
	 - \p VERIFY_CHKSUM_KW:	("verifyChksum")
	     - Instructs the server to verify the checksum information (enables verification mode).
	     - Must be set to an empty string.
	     - Operates on all replicas unless a specific replica is targeted via \p REPL_NAME_KW or \p RESC_NAME_KW.
	     - No checksum is returned to the client when in verification mode.
	     - The following operations are performed:
	         1. Reports replicas with mismatched size information (physical vs catalog).
	         2. Reports replicas that are missing checksums to the client.
	         3. Reports replicas with mismatched checksums (computed vs catalog).
	         4. Reports if the replicas do not share identical checksums.
	     - If \p NO_COMPUTE_KW is set, step 3 will not be performed.
	     - If a specific replica is targeted, step 4 will not be performed.
	     - Verification results are reported to the client via the RcComm::rError object.
	 - \p NO_COMPUTE_KW:	("no_compute")
	     - Instructs the server to not compute a checksum in verification mode potentially leading to a performance boost.
	     - Must be set to an empty string.
	     - A modifier for \p VERIFY_CHKSUM_KW.
*/

	public RcDataObjChksum(DataObjInp dataObjInp)  {
		super(Api.DATA_OBJ_CHKSUM_AN);
		msg.setMessage(dataObjInp);
	}

	@Override
	public String unpackInstruction() {
		return "STR_PI";
	}

	
}
