package nl.tsmeele.myrods.api;


import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.KeyValPair;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

/**
 * Verifies the native authentication credentials passed match the credentials stored in the ICAT.
 * requires rodsadmin priv
 * @author Ton Smeele
 *
 */
public class RcChkObjPermAndStat extends RodsApiCall {
	
	// flags:  0x1 = CHK_COLL_FOR_BUNDLE_OPR
	public RcChkObjPermAndStat(String objPath, String permission, int flags, int status, KeyValPair condInput)  {
		super(Api.CHK_OBJ_PERM_AND_STAT_AN);
		DataStruct message = new DataStruct("ChkObjPermAndStat_PI");
		message.add(new DataString("objPath", objPath));
		message.add(new DataString("permission", permission));
		message.add(new DataInt("flags", flags));
		message.add(new DataInt("status", status));
		message.add(condInput);
		msg.setMessage(message);
	}

	@Override
	public String unpackInstruction() {
		return null;
	}

	
}
