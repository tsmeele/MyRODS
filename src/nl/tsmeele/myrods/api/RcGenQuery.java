package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.GenQueryInp;

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
