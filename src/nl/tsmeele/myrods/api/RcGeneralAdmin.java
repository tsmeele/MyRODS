package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.GeneralAdminInp;

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
