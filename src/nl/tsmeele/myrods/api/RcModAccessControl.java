package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.ModAccessControlInp;

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
