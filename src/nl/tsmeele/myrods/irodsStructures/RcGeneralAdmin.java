package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.GeneralAdminInp;

public class RcGeneralAdmin extends RodsApiCall {


	
	
	public RcGeneralAdmin(GeneralAdminInp generalAdminInp) {
		super(Api.GENERAL_ADMIN_AN);
		msg.setMessage(generalAdminInp);
	}

	@Override
	public String unpackInstruction() {
		return null;
	}

}
