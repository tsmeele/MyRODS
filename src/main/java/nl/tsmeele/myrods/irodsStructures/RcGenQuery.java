package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.GenQueryInp;

public class RcGenQuery extends RodsApiCall {

	public RcGenQuery(GenQueryInp genQueryInp) {
		super(Api.GEN_QUERY_AN);
		msg.setMessage(genQueryInp);
	}
	
	@Override
	public String unpackInstruction() {
		return "GenQueryOut_PI";
	}

}
