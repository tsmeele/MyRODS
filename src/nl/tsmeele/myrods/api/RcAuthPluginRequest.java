package nl.tsmeele.myrods.api;


import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

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
