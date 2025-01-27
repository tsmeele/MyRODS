package nl.tsmeele.myrods.api;

import java.io.IOException;

import nl.tsmeele.myrods.apiDataStructures.IrodsCsNegType;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.apiDataStructures.MiscSvrInfo;
import nl.tsmeele.myrods.apiDataStructures.ObjType;
import nl.tsmeele.myrods.apiDataStructures.RodsObjStat;
import nl.tsmeele.myrods.apiDataStructures.RodsVersion;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;
import nl.tsmeele.myrods.plumbing.IrodsProtocolType;
import nl.tsmeele.myrods.plumbing.IrodsSession;
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
	private IrodsSession irodsSession = new IrodsSession();
	private Message response = null;

	
	public Irods(String host, int port) throws MyRodsException {
		this.host = host;
		this.port = port;
	}
	
	private DataStruct exchangeRequest(RodsCall request) throws MyRodsException, IOException {
		if (!irodsSession.isConnected()) {
			throw new MyRodsException("Unable to execute API calls, not connected");
		}
		response = request.sendTo(irodsSession);
		returnCode = response.getIntInfo();
		error = returnCode < 0;
		errorMessage = response.getErrorMessage();
		bs = response.getBs();
		return response.getMessage();
	}
	
	public RodsVersion rcConnect(int reconnFlag, int connectCnt, String proxyUser, String proxyZone, 
			String clientUser, String clientZone) throws MyRodsException, IOException {
		irodsSession.connect(host, port);
		DataStruct response = exchangeRequest(new RcConnect(reconnFlag, connectCnt, 
				proxyUser, proxyZone, clientUser, clientZone));
		return new RodsVersion(response);
	}
	
	public RodsVersion rcConnect(IrodsProtocolType irodsProt, int reconnFlag, int connectCnt,
			String proxyUser, String proxyZone, String clientUser, String clientZone,
			String applicationName, IrodsCsNegType clientPolicy) throws IOException {
		irodsSession.connect(host, port);
		RcConnect rcConnect = new RcConnect(irodsProt,reconnFlag, connectCnt, proxyUser, proxyZone,
				clientUser, clientZone, applicationName, clientPolicy);
		DataStruct response = exchangeRequest(rcConnect);
		return new RodsVersion(response);
	}
	
	public void rcDisconnect() throws MyRodsException, IOException {
		RcDisconnect request = new RcDisconnect();
		request.sendTo(irodsSession);	// skip action to receive a response
		returnCode = 0;
		error = false;
		errorMessage = null;
		bs = null;
		irodsSession.disconnect();
	}
	
	public MiscSvrInfo rcMiscSvrInfo() throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcMiscSvrInfo());
		return new MiscSvrInfo(response);
	}
	
	public RodsObjStat rcObjStat(String objPath, ObjType objType) throws MyRodsException, IOException {
		DataStruct response = exchangeRequest(new RcObjStat(objPath, objType));
		return new RodsObjStat(response);
	}
	
	

	
}
