package nl.tsmeele.myrods.misc;


import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.KeyValPair;
import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;
import nl.tsmeele.myrods.irodsStructures.RodsApiCall;

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
