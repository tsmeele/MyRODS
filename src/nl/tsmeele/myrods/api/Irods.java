package nl.tsmeele.myrods.api;

import java.io.IOException;
import java.time.Instant;

import nl.tsmeele.json.JObject;
import nl.tsmeele.myrods.irodsStructures.DataBinArray;
import nl.tsmeele.myrods.irodsStructures.DataStruct;
import nl.tsmeele.myrods.irodsStructures.RcAuthRequest;
import nl.tsmeele.myrods.irodsStructures.RcAuthResponse;
import nl.tsmeele.myrods.irodsStructures.RcCollCreate;
import nl.tsmeele.myrods.irodsStructures.RcConnect;
import nl.tsmeele.myrods.irodsStructures.RcDataObjChksum;
import nl.tsmeele.myrods.irodsStructures.RcDataObjClose;
import nl.tsmeele.myrods.irodsStructures.RcDataObjCreate;
import nl.tsmeele.myrods.irodsStructures.RcDataObjLseek;
import nl.tsmeele.myrods.irodsStructures.RcDataObjOpen;
import nl.tsmeele.myrods.irodsStructures.RcDataObjRead;
import nl.tsmeele.myrods.irodsStructures.RcDataObjUnlink;
import nl.tsmeele.myrods.irodsStructures.RcDataObjWrite;
import nl.tsmeele.myrods.irodsStructures.RcDisconnect;
import nl.tsmeele.myrods.irodsStructures.RcExecMyRule;
import nl.tsmeele.myrods.irodsStructures.RcGenQuery;
import nl.tsmeele.myrods.irodsStructures.RcGeneralAdmin;
import nl.tsmeele.myrods.irodsStructures.RcGenquery2;
import nl.tsmeele.myrods.irodsStructures.RcGetFileDescriptorInfo;
import nl.tsmeele.myrods.irodsStructures.RcGetLimitedPassword;
import nl.tsmeele.myrods.irodsStructures.RcGetResourceInfoForOperation;
import nl.tsmeele.myrods.irodsStructures.RcGetTempPassword;
import nl.tsmeele.myrods.irodsStructures.RcMiscSvrInfo;
import nl.tsmeele.myrods.irodsStructures.RcModAVUMetadata;
import nl.tsmeele.myrods.irodsStructures.RcModAccessControl;
import nl.tsmeele.myrods.irodsStructures.RcModDataObjMeta;
import nl.tsmeele.myrods.irodsStructures.RcObjStat;
import nl.tsmeele.myrods.irodsStructures.RcPamAuthRequest;
import nl.tsmeele.myrods.irodsStructures.RcReplicaOpen;
import nl.tsmeele.myrods.irodsStructures.RcSslEnd;
import nl.tsmeele.myrods.irodsStructures.RcSslStart;
import nl.tsmeele.myrods.irodsStructures.RcSwitchUser;
import nl.tsmeele.myrods.irodsStructures.RodsCall;
import nl.tsmeele.myrods.plumbing.IrodsProtocolType;
import nl.tsmeele.myrods.plumbing.ServerConnection;
import nl.tsmeele.myrods.plumbing.ServerConnectionDetails;
import nl.tsmeele.myrods.plumbing.MyRodsException;

/**
 * The class Irods provides the low-level client API interface for applications that wish to interact with an iRODS server.
 * It implements iRODS API calls relevant to a client, e.g. rcConnect, rcDisconnect, etc. 
 * @author Ton Smeele
 *
 */
public class Irods {
	
	// content of last received message
	public boolean error = false;
	public DataStruct errorMessage = null;
	public int intInfo = 0;
	public byte[] bs = null;
	
	// timestamps, in seconds since epoch
	public long connectTimeStamp = 0L;	// start of current connection
	public long authenticatedTimeStamp = 0L;	// last successful iRODS authentication
	
	// internal state, keep private
	protected ServerConnection serverConnection = new ServerConnection();
	protected String host;
	protected int port;
	protected String authenticatedPassword = null;

	
	
	public Irods(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public IrodsCsNegType getServerPolicy() {
		return serverConnection.getSessionDetails().serverPolicy;
	}
	
	public String getServerRelVersion() {
		return serverConnection.getSessionDetails().relVersion.get();
	}
	
	public boolean isSsl() {
		return serverConnection.isSsl();
	}
	
	public boolean isConnected() {
		return serverConnection.isConnected();
	}
	
	public boolean isAuthenticated() {
		return authenticatedTimeStamp > 0L;
	}
	
	public IrodsProtocolType getProtocol() {
		return serverConnection.getProtocol();
	}
	
	public String getHost() {
		return host;
	}
	
	
//	public Irods cloneConnection() throws IOException, MyRodsException {
//		if (!serverConnection.isConnected() || authenticatedPassword == null) {
//			throw new MyRodsException("Unable to clone: Missing authenticated iRODS connection");
//		}
//		Irods irods2 = new Irods(host, port);
//		ServerConnectionDetails sd = serverConnection.getSessionDetails();
//		
//		RcConnect rcConnect = new RcConnect(sd.startupPack, sd.clientPolicy);
//		// clone connects to the server
//		irods2.rcConnect(rcConnect);
//		if (irods2.error) {
//			throw new MyRodsException("Unable to clone: cannot connect to server");
//		}
//		// clone authenticates
//		byte[] challenge = irods2.rcAuthRequest();
//		if (irods2.error) {
//			irods2.rcDisconnect();
//			throw new MyRodsException("Unable to clone: authentication request failed");
//		}
//		String proxyUser = sd.startupPack.lookupString("proxyUser");
//		String proxyZone = sd.startupPack.lookupString("proxyRcatZone");
//		irods2.rcAuthResponse(proxyUser + "#" + proxyZone, authenticatedPassword, challenge);
//		if (irods2.error) {
//			irods2.rcDisconnect();
//			throw new MyRodsException("Unable to clone: authentication failed");
//		}
//		return irods2;
//	}

	
	//    API CALLS START HERE
	
	// CATEGORY: CONNECTIVITY & AUTHENTICATION
	


	// standard connect, uses default values for options
	public RodsVersion rcConnect(String proxyUser, String proxyZone, 
			String clientUser, String clientZone) throws MyRodsException, IOException {
		RcConnect startupPack = new RcConnect(proxyUser, proxyZone, clientUser, clientZone);
		return rcConnect(startupPack);
	}
	
	// general connect, values specified for all options
	public RodsVersion rcConnect(IrodsProtocolType irodsProt, int reconnFlag, int connectCnt,
			String proxyUser, String proxyZone, String clientUser, String clientZone,
			String applicationName, IrodsCsNegType clientPolicy) throws IOException {
		RcConnect startupPack = new RcConnect(irodsProt,reconnFlag, connectCnt, proxyUser, proxyZone,
				clientUser, clientZone, applicationName, clientPolicy);
		return rcConnect(startupPack);
	}
	
	// execute rcConnect using a prepared startup pack
	protected RodsVersion rcConnect(RcConnect startupPack) throws MyRodsException, IOException {
		if (!serverConnection.isConnected()) {
			serverConnection.connect(host, port);
		}
		DataStruct response = exchangeRequest(startupPack);
		if (!error) {
			connectTimeStamp = Instant.now().getEpochSecond();
			authenticatedTimeStamp = 0L;
			authenticatedPassword = null;
		}
		return new RodsVersion(response);
	}
	
	public void rcDisconnect() throws MyRodsException, IOException {
		if (!serverConnection.isConnected()) return;
		RcDisconnect request = new RcDisconnect();
		request.sendTo(serverConnection);	// skip action to receive a response
		intInfo = 0;
		error = false;
		errorMessage = null;
		bs = null;
		serverConnection.disconnect();
		connectTimeStamp = 0L;
		authenticatedTimeStamp = 0L;
		authenticatedPassword = null;
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
		if (!error) {
			// keep timestamp and credential to support parallel connect
			authenticatedTimeStamp = Instant.now().getEpochSecond();
			authenticatedPassword = password;	
		} else {
			authenticatedTimeStamp = 0L;
			authenticatedPassword = null;
		}
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
	
	public void rcSwitchUser(SwitchUserInp switchUserInp) throws MyRodsException, IOException {
		exchangeRequest(new RcSwitchUser(switchUserInp));
	}
	

	// CATEGORY: METADATA AND ADMINISTRATION
	
	public void rcModAVUMetadata(ModAVUMetadataInp modAVUMetadataInp) throws MyRodsException, IOException {
		exchangeRequest(new RcModAVUMetadata(modAVUMetadataInp));
	}
	
	public void rcModDataObjMeta(ModDataObjMetaInp modDataObjMetaInp) throws MyRodsException, IOException {
		exchangeRequest(new RcModDataObjMeta(modDataObjMetaInp));
	}
	
	public void rcGeneralAdmin(GeneralAdminInp generalAdminInp) throws MyRodsException, IOException {
		exchangeRequest(new RcGeneralAdmin(generalAdminInp));
	}
	
	
	// CATEGORY: COLLECTIONS AND DATA OBJECTS
	
	
	public RodsObjStat rcObjStat(String objPath, ObjType objType) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcObjStat(objPath, objType));
		return new RodsObjStat(response);
	}
	
	public void rcModAccessControl(ModAccessControlInp modAccessControlInp) throws MyRodsException, IOException {
		exchangeRequest(new RcModAccessControl(modAccessControlInp));
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
	
	public JObject rcReplicaOpen(DataObjInp dataObjInp) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcReplicaOpen(dataObjInp));
		return dataBinArray2JSONobject(response);
	}
	
	public JObject rcGetFileDescriptorInfo(JsonInp jsonInp) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcGetFileDescriptorInfo(jsonInp));
		return dataBinArray2JSONobject(response);
	}
	
	public JObject rcGetResourceInfoForOperation(String dataObjPath, String operation, String rescHier) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcGetResourceInfoForOperation(dataObjPath, operation, rescHier));
		return str2JSONobject(response);
	}
	
	public JObject rcGetResourceInfoForOperation(String dataObjPath, String operation, int rescNum) throws MyRodsException, IOException {
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
	
	private JObject str2JSONobject(DataStruct response) {
		String myStr = response.lookupString("myStr");
		if (myStr == null) {
			return null;
		}
		JObject json = new JObject(myStr);
		return json;
	}
	
	private JObject dataBinArray2JSONobject(DataStruct response) {
		JObject jsonObject = null;
		try {
			DataBinArray bin = (DataBinArray) response.lookupName("buf");
			String s = bin.getAsString();
			jsonObject = new JObject(s);
		} catch (NullPointerException e) {
			return null;
		}
		return jsonObject;
	}
	
	private DataStruct exchangeRequest(RodsCall request) throws MyRodsException, IOException {
		if (!serverConnection.isConnected()) {
			throw new MyRodsException("Unable to execute API calls, not connected");
		}
		Message response = request.sendTo(serverConnection);
		intInfo = response.getIntInfo();
		error = intInfo < 0;
		errorMessage = response.getErrorMessage();
		bs = response.getBs();
		return response.getMessage();
	}
	
}
