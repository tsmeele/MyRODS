package nl.tsmeele.myrods.plumbing;

import java.util.HashMap;

/**
 * Class PackMapConstants facilitates lookup of hardcoded iRODS server constants.
 * These constants are referenced in pack instructions.
 * @author Ton Smeele
 *
 */
public class PackInstructionsConstants extends HashMap<String,Integer> {
	private static final long serialVersionUID = 1L;

	public PackInstructionsConstants() {
		// For underpinning data see: https://github.com/irods/irods/lib/core/include/irods/rodsDef.h
		// (unless specified otherwise)
		this.put("MAX_PATH_ALLOWED", 1024);
		this.put("MAX_NAME_LEN", 1088);   // MAX_PATH_ALLOWED + 64
		this.put("HEADER_TYPE_LEN", 128);
		this.put("NAME_LEN", 64);
		this.put("ERR_MSG_LEN", 1024);  // rodsError.h
		this.put("LONG_NAME_LEN", 256);
		this.put("MAX_ERROR_SIZE", 80); // rodsPackTable.h
		this.put("OBJID_DIM", 2);	// rodsPackTable.h
		this.put("H5S_MAX_RANK", 32);  // rodsPackTable.h
		this.put("H5DATASPACE_MAX_RANK", 32);   // rodsPackTable.h
		this.put("CHALLENGE_LEN", 64);  // lib/api/include/irods/authenticate.h
	    this.put("RESPONSE_LEN", 16);  // lib/api/include/irods/authenticate.h
	    this.put("MAX_PASSWORD_LEN",  50); // lib/api/include/irods/authenticate.h
	    this.put("TIME_LEN", 33); 
	    this.put("MAX_SQL_ATTR", 50); // lib/core/include/irods/rodsGenQuery.h
	}

}
