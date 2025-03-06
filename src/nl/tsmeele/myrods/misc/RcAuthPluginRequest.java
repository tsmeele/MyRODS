package nl.tsmeele.myrods.misc;


import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;
import nl.tsmeele.myrods.irodsStructures.RodsApiCall;

/**
 * @author Ton Smeele
 *
 */
public class RcAuthPluginRequest extends RodsApiCall {
	
	public RcAuthPluginRequest(String authScheme, String context)  {
		super(Api.AUTH_PLUG_REQ_AN);
		DataStruct message = new DataStruct("authPlugReqInp_PI");
		message.add(new DataString("auth_scheme_", authScheme));
		message.add(new DataString("context_", context));
		msg.setMessage(message);
	}

	@Override
	public String unpackInstruction() {
		return "authPlugReqOut_PI";
	}

	
}
