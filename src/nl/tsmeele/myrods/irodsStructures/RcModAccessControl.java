package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.ModAccessControlInp;

public class RcModAccessControl extends RodsApiCall {

	public RcModAccessControl(ModAccessControlInp modAccessControlInp) {
		super(Api.MOD_ACCESS_CONTROL_AN);
		msg.setMessage(modAccessControlInp);
	}

	@Override
	public String unpackInstruction() {
		return null;
	}

	
}
