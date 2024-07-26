package nl.tsmeele.myrods.api;


import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.CollInp;

/**
 * Creates a collection.
 * @author Ton Smeele
 *
 */
public class RcCollCreate extends RodsApiCall {
	
	public RcCollCreate(CollInp collInp)  {
		super(Api.COLL_CREATE_AN);
		msg.setMessage(collInp);
	}

	@Override
	public String unpackInstruction() {
		return null;
	}

	
}
