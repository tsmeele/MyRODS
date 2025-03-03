package nl.tsmeele.myrods.api;

import java.io.IOException;

import nl.tsmeele.json.JSONParser;
import nl.tsmeele.json.JSONobject;
import nl.tsmeele.myrods.apiDataStructures.CollInp;
import nl.tsmeele.myrods.apiDataStructures.DataObjInp;
import nl.tsmeele.myrods.apiDataStructures.ExecMyRuleInp;
import nl.tsmeele.myrods.apiDataStructures.GenQueryInp;
import nl.tsmeele.myrods.apiDataStructures.GenQueryOut;
import nl.tsmeele.myrods.apiDataStructures.Genquery2Input;
import nl.tsmeele.myrods.apiDataStructures.IrodsCsNegType;
import nl.tsmeele.myrods.apiDataStructures.JsonInp;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.apiDataStructures.MiscSvrInfo;
import nl.tsmeele.myrods.apiDataStructures.MsParamArray;
import nl.tsmeele.myrods.apiDataStructures.ObjType;
import nl.tsmeele.myrods.apiDataStructures.OpenedDataObjInp;
import nl.tsmeele.myrods.apiDataStructures.RodsObjStat;
import nl.tsmeele.myrods.apiDataStructures.RodsVersion;
import nl.tsmeele.myrods.irodsDataTypes.DataBinArray;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;
import nl.tsmeele.myrods.irodsDataTypes.RcAuthRequest;
import nl.tsmeele.myrods.irodsDataTypes.RcAuthResponse;
import nl.tsmeele.myrods.irodsDataTypes.RcCollCreate;
import nl.tsmeele.myrods.irodsDataTypes.RcConnect;
import nl.tsmeele.myrods.irodsDataTypes.RcDataObjChksum;
import nl.tsmeele.myrods.irodsDataTypes.RcDataObjClose;
import nl.tsmeele.myrods.irodsDataTypes.RcDataObjCreate;
import nl.tsmeele.myrods.irodsDataTypes.RcDataObjLseek;
import nl.tsmeele.myrods.irodsDataTypes.RcDataObjOpen;
import nl.tsmeele.myrods.irodsDataTypes.RcDataObjRead;
import nl.tsmeele.myrods.irodsDataTypes.RcDataObjUnlink;
import nl.tsmeele.myrods.irodsDataTypes.RcDataObjWrite;
import nl.tsmeele.myrods.irodsDataTypes.RcDisconnect;
import nl.tsmeele.myrods.irodsDataTypes.RcExecMyRule;
import nl.tsmeele.myrods.irodsDataTypes.RcGenQuery;
import nl.tsmeele.myrods.irodsDataTypes.RcGenquery2;
import nl.tsmeele.myrods.irodsDataTypes.RcGetFileDescriptorInfo;
import nl.tsmeele.myrods.irodsDataTypes.RcGetLimitedPassword;
import nl.tsmeele.myrods.irodsDataTypes.RcGetResourceInfoForOperation;
import nl.tsmeele.myrods.irodsDataTypes.RcGetTempPassword;
import nl.tsmeele.myrods.irodsDataTypes.RcMiscSvrInfo;
import nl.tsmeele.myrods.irodsDataTypes.RcObjStat;
import nl.tsmeele.myrods.irodsDataTypes.RcPamAuthRequest;
import nl.tsmeele.myrods.irodsDataTypes.RcReplicaOpen;
import nl.tsmeele.myrods.irodsDataTypes.RcSslEnd;
import nl.tsmeele.myrods.irodsDataTypes.RcSslStart;
import nl.tsmeele.myrods.plumbing.IrodsProtocolType;
import nl.tsmeele.myrods.plumbing.ServerConnection;
import nl.tsmeele.myrods.plumbing.MyRodsException;

/**
 * The class Irods provides the low-level client API interface for applications that wish to interact with an iRODS server.
 * It implements iRODS API calls relevant to a client, e.g. rcConnect, rcDisconnect, etc. 
 * @author Ton Smeele
 *
 */
public class Irods {
	public boolean error = false;
	public DataStruct errorMessage = null;
	public int returnCode = 0;
	public byte[] bs = null;
	
	private String host;
	private int port;
	private ServerConnection serverConnection = new ServerConnection();
	private Message response = null;

	
	public Irods(String host, int port) throws MyRodsException {
		this.host = host;
		this.port = port;
	}
	
	private DataStruct exchangeRequest(RodsCall request) throws MyRodsException, IOException {
		if (!serverConnection.isConnected()) {
			throw new MyRodsException("Unable to execute API calls, not connected");
		}
		response = request.sendTo(serverConnection);
		returnCode = response.getIntInfo();
		error = returnCode < 0;
		errorMessage = response.getErrorMessage();
		bs = response.getBs();
		return response.getMessage();
	}
	
	public RodsVersion rcConnect(int reconnFlag, int connectCnt, String proxyUser, String proxyZone, 
			String clientUser, String clientZone) throws MyRodsException, IOException {
		serverConnection.connect(host, port);
		DataStruct response = exchangeRequest(new RcConnect(reconnFlag, connectCnt, 
				proxyUser, proxyZone, clientUser, clientZone));
		return new RodsVersion(response);
	}
	
	//    API CALLS START HERE
	
	// CATEGORY: CONNECTIVITY & AUTHENTICATION
	
	public RodsVersion rcConnect(IrodsProtocolType irodsProt, int reconnFlag, int connectCnt,
			String proxyUser, String proxyZone, String clientUser, String clientZone,
			String applicationName, IrodsCsNegType clientPolicy) throws IOException {
		serverConnection.connect(host, port);
		RcConnect rcConnect = new RcConnect(irodsProt,reconnFlag, connectCnt, proxyUser, proxyZone,
				clientUser, clientZone, applicationName, clientPolicy);
		DataStruct response = exchangeRequest(rcConnect);
		return new RodsVersion(response);
	}
	
	public void rcDisconnect() throws MyRodsException, IOException {
		RcDisconnect request = new RcDisconnect();
		request.sendTo(serverConnection);	// skip action to receive a response
		returnCode = 0;
		error = false;
		errorMessage = null;
		bs = null;
		serverConnection.disconnect();
	}
	
	public MiscSvrInfo rcMiscSvrInfo() throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcMiscSvrInfo());
		return new MiscSvrInfo(response);
	}
	
	public String rcGetLimitedPassword(int ttlInHours) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcGetLimitedPassword(ttlInHours));
		return response.lookupString("stringToHashWith");
	}
	
	public String rcGetTempPassword( ) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcGetTempPassword());
		return response.lookupString("stringToHashWith");
	}
	
	public byte[] rcAuthRequest() throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcAuthRequest());
		return response.lookupByte("challenge");
	}
	
	public void rcAuthResponse(String userNameAndZone, String password, byte[] challenge) throws MyRodsException, IOException {
		exchangeRequest(new RcAuthResponse(userNameAndZone, password, challenge));
	}
	
	public boolean rcSslStart() throws MyRodsException, IOException {
		exchangeRequest(new RcSslStart(null));
		return !error && serverConnection.isSsl();
	}
	
	public boolean rcSslEnd() throws MyRodsException, IOException {
		exchangeRequest(new RcSslEnd(null));
		return !error && !serverConnection.isSsl();
	}
	
	public String rcPamAuthRequest(String userName, String pamPassword, int ttl) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcPamAuthRequest(userName, pamPassword, ttl));
		return response.lookupString("irodsPamPassword"); 
	}
	

	
	
	// CATEGORY: COLLECTIONS AND DATA OBJECTS
	
	
	public RodsObjStat rcObjStat(String objPath, ObjType objType) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcObjStat(objPath, objType));
		return new RodsObjStat(response);
	}
	
	public void rcCollCreate(CollInp collInp) throws MyRodsException, IOException {
		exchangeRequest(new RcCollCreate(collInp));
		return;
	}
	
	public void rcDataObjCreate(DataObjInp dataObjInp) throws MyRodsException, IOException {
		exchangeRequest(new RcDataObjCreate(dataObjInp));
	}
	
	public void rcDataObjUnlink(DataObjInp dataObjInp) throws MyRodsException, IOException {
		exchangeRequest(new RcDataObjUnlink(dataObjInp));
	}
	
	public void rcDataObjOpen(DataObjInp dataObjInp) throws MyRodsException, IOException {
		exchangeRequest(new RcDataObjOpen(dataObjInp));
	}
	
	public void rcDataObjLseek(OpenedDataObjInp openedDataObjInp) throws MyRodsException, IOException {
		exchangeRequest(new RcDataObjLseek(openedDataObjInp));
	}
	
	public void rcDataObjRead(OpenedDataObjInp openedDataObjInp) throws MyRodsException, IOException {
		exchangeRequest(new RcDataObjRead(openedDataObjInp));
	}

	public void rcDataObjWrite(OpenedDataObjInp openedDataObjInp, byte[] buf) throws MyRodsException, IOException {
		exchangeRequest(new RcDataObjWrite(openedDataObjInp, buf));
	}
	
	public void rcDataObjClose(OpenedDataObjInp openedDataObjInp) throws MyRodsException, IOException {
		exchangeRequest(new RcDataObjClose(openedDataObjInp));
	}
	
	public String rcDataObjChksum(DataObjInp dataObjInp) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcDataObjChksum(dataObjInp));
		return response.lookupString("myStr");
	}
	
	public JSONobject rcReplicaOpen(DataObjInp dataObjInp) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcReplicaOpen(dataObjInp));
		return dataBinArray2JSONobject(response);
	}
	
	public JSONobject rcGetFileDescriptorInfo(JsonInp jsonInp) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcGetFileDescriptorInfo(jsonInp));
		return dataBinArray2JSONobject(response);
	}
	
	public JSONobject rcGetResourceInfoForOperation(String dataObjPath, String operation, String rescHier) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcGetResourceInfoForOperation(dataObjPath, operation, rescHier));
		return str2JSONobject(response);
	}
	
	public JSONobject rcGetResourceInfoForOperation(String dataObjPath, String operation, int rescNum) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcGetResourceInfoForOperation(dataObjPath, operation, rescNum));
		return str2JSONobject(response);
	}
	
	
	// CATEGORY: QUERIES AND RULES
	
	public GenQueryOut rcGenQuery(GenQueryInp genQueryInp) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcGenQuery(genQueryInp));
		return new GenQueryOut(response);
	}
	
	// TODO: update return type to a more appropriate type
	public String rcGenquery2(Genquery2Input genquery2Input) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcGenquery2(genquery2Input));
		return response.lookupString("myStr");
	}
	
	public MsParamArray rcExecMyRule(ExecMyRuleInp execMyRuleInp) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcExecMyRule(execMyRuleInp));
		return new MsParamArray(response);
	}
	
	
	
	// internal helper methods
	
	private JSONobject str2JSONobject(DataStruct response) {
		String myStr = response.lookupString("myStr");
		if (myStr == null) {
			return null;
		}
		JSONobject json = (JSONobject) JSONParser.parse(myStr);
		return json;
	}
	
	private JSONobject dataBinArray2JSONobject(DataStruct response) {
		JSONobject jsonObject = null;
		try {
			DataBinArray bin = (DataBinArray) response.lookupName("buf");
			String s = bin.getAsString();
			jsonObject = (JSONobject) JSONParser.parse(s);
		} catch (NullPointerException e) {
			return null;
		}
		return jsonObject;
	}
	
}
