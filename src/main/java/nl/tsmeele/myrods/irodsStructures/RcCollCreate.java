package nl.tsmeele.myrods.irodsStructures;


import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.CollInp;

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
