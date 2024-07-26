package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.Genquery2Input;

public class RcGenquery2 extends RodsApiCall {

	public RcGenquery2(Genquery2Input genquery2Input) {
		super(Api.GENQUERY2_AN);
		msg.setMessage(genquery2Input);
	}
	
	@Override
	public String unpackInstruction() {
		return "STR_PI";
	}

}
