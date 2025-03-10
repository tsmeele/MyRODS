package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.SwitchUserInp;

public class RcSwitchUser extends RodsApiCall {

	// since iRODS 4.3.1
	public RcSwitchUser(SwitchUserInp switchUserInp) {
		super(Api.SWITCH_USER_APN);
		msg.setMessage(switchUserInp);
	}
	
	@Override
	public String unpackInstruction() {
		return null;
	}

}
