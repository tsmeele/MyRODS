package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.DataObjInp;
import nl.tsmeele.myrods.apiDataStructures.KeyValPair;
import nl.tsmeele.myrods.apiDataStructures.Kw;

public class RcGetResourceInfoForOperation extends RodsApiCall  {

	public RcGetResourceInfoForOperation(DataObjInp dataObjInp) {
		super(Api.GET_RESOURCE_INFO_FOR_OPERATION_AN);
		msg.setMessage(dataObjInp);
}
	
	// operation can be any of "CREATE", "WRITE","OPEN","UNLINK"
	// optional argument rescHier, can use null 
	public RcGetResourceInfoForOperation(String dataObjPath, String operation, String rescHier) {
		super(Api.GET_RESOURCE_INFO_FOR_OPERATION_AN);
		KeyValPair condInput = new KeyValPair();
		condInput.put(Kw.GET_RESOURCE_INFO_OP_TYPE_KW, operation);
		if (rescHier != null) {
			condInput.put(Kw.RESC_HIER_STR_KW,  rescHier);  // client preferred resource
		}
		DataObjInp dataObjInp = new DataObjInp(dataObjPath, 0, 0, 0L, 0L, 0, null, null, condInput); 
		msg.setMessage(dataObjInp);
	}
	
	public RcGetResourceInfoForOperation(String dataObjPath, String operation, int rescNum) {
		super(Api.GET_RESOURCE_INFO_FOR_OPERATION_AN);
		KeyValPair condInput = new KeyValPair();
		condInput.put(Kw.GET_RESOURCE_INFO_OP_TYPE_KW, operation);
		condInput.put(Kw.REPL_NUM_KW, String.valueOf(rescNum));
		DataObjInp dataObjInp = new DataObjInp(dataObjPath, 0, 0, 0L, 0L, 0, null, null, condInput); 
		msg.setMessage(dataObjInp);
	}
	
	
	@Override
	public String unpackInstruction() {
		return "STR_PI";
	}

}
