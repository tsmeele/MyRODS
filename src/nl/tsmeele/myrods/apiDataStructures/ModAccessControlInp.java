package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataPtr;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class ModAccessControlInp extends DataStruct {

	// "modAccessControlInp_PI", "int recursiveFlag; str *accessLevel; str *userName; str *zone; str *path;",

	/* recursive flag:
	 * 0 = not recursive
	 * x = recursive  (TODO probably just non-zero suffices, needs to be tested)
	 *  
	 *  
	 * accessLevels: 
	 *    - old-style equivalent listed between brackets
	 *    - access levels are taken from plugins/database/src/db_plugin.cpp
	 *      they are listed in order, from less to more access
	 *    - beware: some access levels, listed between square brackets, are *not* intended 
	 *      to be used with regular objects and may cause issues when used that way
	 *    - below keywords are defined in class Kw
	 * 
	 * NULL    (alias: "null")
	 * ACCESS_NULL,									[ACCESS_EXECUTE, ACCESS_READ_ANNOTATION, ACCESS_READ_SYSTEM_METADATA],
     * ACCESS_READ_METADATA,
     * ACCESS_READ_OBJECT,    (alias: "read")		[ACCESS_WRITE_ANNOTATION],
     * ACCESS_CREATE_METADATA,
     * ACCESS_MODIFY_METADATA,  (alias: "write")
     * ACCESS_DELETE_METADATA, 						[ACCESS_ADMINISTER_OBJECT],
     * ACCESS_CREATE_OBJECT,
     * ACCESS_MODIFY_OBJECT,
     * ACCESS_DELETE_OBJECT,						[ACCESS_CREATE_TOKEN, ACCESS_DELETE_TOKEN, ACCESS_CURATE],
     * ACCESS_OWN  (alias: "own")
	 * 
	 * Other access levels: (applies to collection objects)
	 * ACCESS_INHERIT 
	 * ACCESS_NOINHERIT
	 * 
	 * path:
	 *   - basically the path to the object
	 *   - some quirks have been added, see below:
	 *         excerpt from lib/api/include/irods/modAccessControl.h :
	 *         #define MOD_RESC_PREFIX "resource:"  // Used to indicate a resource instead of requiring a change to the protocol
	 *         #define MOD_ADMIN_MODE_PREFIX "admin:" // To indicate admin mode, without protocol change.
	 */
	
	
	public ModAccessControlInp(int recursiveFlag, String accessLevel, String userName, String zone, String path) {
		super("modAccessControlInp_PI");
		add(new DataInt("recursiveFlag", recursiveFlag));
		add(new DataPtr("accesslevel",new DataString("accessLevel", accessLevel)));
		add(new DataPtr("userName", new DataString("userName", userName)));
		add(new DataPtr("zone", new DataString("zone", zone)));
		add(new DataPtr("path", new DataString("path", path)));
	}

}
