package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.Genquery2Input;

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
