package nl.tsmeele.myrods.irodsDataTypes;

import nl.tsmeele.myrods.api.RodsApiCall;
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
