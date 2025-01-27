package nl.tsmeele.myrods.high;

import java.io.IOException;

import nl.tsmeele.json.JSONParser;
import nl.tsmeele.json.JSONnumber;
import nl.tsmeele.json.JSONobject;
import nl.tsmeele.json.JSONstring;
import nl.tsmeele.myrods.api.RcDataObjClose;
import nl.tsmeele.myrods.api.RcDataObjLseek;
import nl.tsmeele.myrods.api.RcDataObjOpen;
import nl.tsmeele.myrods.api.RcDataObjRead;
import nl.tsmeele.myrods.api.RcDataObjWrite;
import nl.tsmeele.myrods.api.RcGetFileDescriptorInfo;
import nl.tsmeele.myrods.api.RcGetResourceInfoForOperation;
import nl.tsmeele.myrods.api.RcObjStat;
import nl.tsmeele.myrods.api.RcReplicaOpen;
import nl.tsmeele.myrods.apiDataStructures.DataObjInp;
import nl.tsmeele.myrods.apiDataStructures.Flag;
import nl.tsmeele.myrods.apiDataStructures.JsonInp;
import nl.tsmeele.myrods.apiDataStructures.KeyValPair;
import nl.tsmeele.myrods.apiDataStructures.Kw;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.apiDataStructures.ObjType;
import nl.tsmeele.myrods.apiDataStructures.OpenedDataObjInp;
import nl.tsmeele.myrods.apiDataStructures.OprType;
import nl.tsmeele.myrods.apiDataStructures.RodsObjStat;
import nl.tsmeele.myrods.irodsDataTypes.DataBinArray;
import nl.tsmeele.myrods.plumbing.ServerConnection;
import nl.tsmeele.myrods.plumbing.MyRodsException;
import nl.tsmeele.myrods.plumbing.SessionDetails;

public class Replica implements PosixFile {
	
	// replica properties
	private Long objSize = null;
	private String objPath = null;
	private Integer replNum = null;
	private String resource = null;

	// state
	private boolean openForRead = false;
	private boolean openForWrite = false;
	private boolean exists = false;
	private int fileDescriptor = 0;
	private String replicaToken = null;
	
	// connection
	private Session session = null;
	private ServerConnection irodsSession = null;
	private String host = null;
	
	private int numThreads = 0;
	
	public Session getSession() {
		return session;
	}
	
	public void setSession(Session session) {
		this.session = session;
		if (session == null) {
			this.irodsSession = null;
			return;
		}
		this.irodsSession = session.getIrodsSession();
	}
	
	public void setReplica(Session session, String objPath, String resource) {
		this.session = session;
		this.irodsSession = session.getIrodsSession();
		this.host = irodsSession.getSessionDetails().host;
		this.objPath = objPath;
		this.resource = resource;
		this.replNum = null;
	}
	
	public void setReplica(Session session, String objPath, int replNum) {
		this.session = session;
		this.irodsSession = session.getIrodsSession();
		this.host = irodsSession.getSessionDetails().host;
		this.objPath = objPath;
		this.replNum = replNum;
		this.resource = null;
	}
	
	public String getReplicaToken() {
		return replicaToken;
	}
	
	public String toString() {
		return getName();
	}
	
	@Override
	public String getName() {
		String hostRef = host == null ? "irods:" : host;
		String replRef = replNum == null ? "" : ";" + replNum;
		return "irods://" + hostRef + objPath + replRef;
	}
	
	@Override
	public PosixFile cloneProperties() {
		Replica r = new Replica();
		r.objPath = objPath;
		r.replNum = replNum;
		r.resource = resource;
		r.replicaToken = replicaToken;
		return r;
	}
	
	@Override
	public void lseek(long offset) throws MyRodsException, IOException {
		KeyValPair kv = new KeyValPair();
		OpenedDataObjInp openedDataObjInp = new OpenedDataObjInp(fileDescriptor, 0, Flag.SEEK_SET, OprType.NULL,
				offset, 0L, kv);
		RcDataObjLseek rcDataObjLseek = new RcDataObjLseek(openedDataObjInp);
		Message reply = rcDataObjLseek.sendTo(irodsSession);
		if (reply.getIntInfo() < 0) {
			throw new MyRodsException("Seek on replica to " + offset + " failed");
		}
	}

	@Override
	public byte[] read(int bytes) throws MyRodsException, IOException {
		// "OpenedDataObjInp_PI", "int l1descInx; int len; int whence; int oprType; 
		//  double offset; double bytesWritten; struct KeyValPair_PI;",
		KeyValPair kv = new KeyValPair();
		OpenedDataObjInp openedDataObjInp = new OpenedDataObjInp(fileDescriptor, bytes, 0, OprType.NULL,
				0L, 0L, kv);
		RcDataObjRead rcDataObjRead = new RcDataObjRead(openedDataObjInp);
		Message reply = rcDataObjRead.sendTo(irodsSession);
		int status = reply.getIntInfo();
		if (status < 0) {
			throw new MyRodsException("Unable to read " + bytes + " bytes from replica");
		}
		byte[] buf = reply.getBs();
		if (buf != null) {
			return buf;
		}
		return new byte[0];
	}

	@Override
	public void write(byte[] bytes) throws MyRodsException, IOException {
		KeyValPair kv = new KeyValPair();
		int needToWrite = bytes.length;
		while (needToWrite > 0) {
			OpenedDataObjInp openedDataObjInp = 
					new OpenedDataObjInp(fileDescriptor, needToWrite, Flag.SEEK_CUR, OprType.PUT_OPR,
				0L, 0L, kv);
			RcDataObjWrite rcDataObjWrite = new RcDataObjWrite(openedDataObjInp, bytes);
			Message reply = rcDataObjWrite.sendTo(irodsSession);
			int status = reply.getIntInfo();
			if (status <= 0) {
				throw new MyRodsException("Unable to write to replica (" + status + ")");
			}
			needToWrite = needToWrite - status;
			byte[] remain = new byte[needToWrite];
			for (int i = 0; i < needToWrite; i++) {
				 remain[i] = bytes[status + i];
			}
			bytes = remain;
		}
	}

	@Override
	public void close() throws MyRodsException, IOException {
		KeyValPair kv = new KeyValPair();
		OpenedDataObjInp openedDataObjInp = new OpenedDataObjInp(fileDescriptor, 0, 0, null,
				0L, 0L, kv);
		RcDataObjClose rcDataObjClose = new RcDataObjClose(openedDataObjInp);
		Message reply = rcDataObjClose.sendTo(irodsSession);
		openForRead = false;
		openForWrite = false;
		replicaToken = null;
		if (reply.getIntInfo() < 0) {
			throw new MyRodsException("Error when closing replica (" + reply.getIntInfo() + ")");
		}		
	}

	@Override
	public void openCreate() throws MyRodsException, IOException {
		getResourceForReplica("CREATE", resource);
		doOpen(Flag.O_CREAT + Flag.O_RDWR);
		openForWrite = true;	
	}
	
	@Override
	public void openRead() throws IOException {
		getResourceForReplica("OPEN", resource);
		doOpen(Flag.O_RDONLY);
		openForRead = true;	
	}

	@Override
	public void openWriteTrunc() throws IOException {
		getResourceForReplica("WRITE", resource);
		doOpen(Flag.O_RDWR + Flag.O_TRUNC);
		openForWrite = true;	
	}

	@Override
	public void openWriteAppend() throws IOException {
		getResourceForReplica("WRITE", resource);
		doOpen(Flag.O_RDWR + Flag.O_APPEND);
		openForWrite = true;	
	}

	@Override
	public void openWrite() throws IOException {
		getResourceForReplica("WRITE", resource);
		doOpen(Flag.O_RDWR);
		openForWrite = true;
	}
	
	private void getResourceForReplica(String operation, String resource) throws MyRodsException, IOException {
		if (serverVersionNumberIsAtLeast("4.3.1")) {
			RcGetResourceInfoForOperation rcGetInfo = new RcGetResourceInfoForOperation(objPath, operation, resource); 
			Message reply = rcGetInfo.sendTo(irodsSession);
			if (reply.getIntInfo() < 0) {
				throw new MyRodsException("Unable to get resource info for replica (" + reply.getIntInfo() + ")");
			}
			String myStr = reply.getMessage().lookupString("myStr");
			JSONobject json = (JSONobject) JSONParser.parse(myStr);
			// TODO: support connections to hosts other than the connected host, if the resource is located elsewhere
			//resourceHost = ((JSONstring)json.get("host")).get();
			this.resource = ((JSONstring)json.get("resource_hierarchy")).get();
		} 
	}
	
	
	private void doOpen(int openFlags) throws IOException, MyRodsException {
		KeyValPair condInput = new KeyValPair();
		if (replNum != null) {
			condInput.put(Kw.REPL_NUM_KW, String.valueOf(replNum));
		} else {
			if (resource != null) {
				condInput.put(Kw.RESC_HIER_STR_KW, resource);
			}
		}
		if (replicaToken != null && openFlags != Flag.O_RDONLY) {
			condInput.put(Kw.REPLICA_TOKEN_KW, replicaToken);
		}
		DataObjInp dataObjInp = new DataObjInp(objPath, 0, openFlags, 0L, -1L, numThreads, OprType.NULL, null, condInput); 

		if (serverVersionNumberIsAtLeast("4.2.9")) {
			RcReplicaOpen rcReplicaOpen = new RcReplicaOpen(dataObjInp);
			Message reply = rcReplicaOpen.sendTo(irodsSession);
			if (reply.getIntInfo() < 0) {
				throw new MyRodsException("Unable to open replica (" + reply.getIntInfo() + ")");
			}
			fileDescriptor = reply.getIntInfo();
			exists = true;
			registerReplicaDetails((DataBinArray) reply.getMessage().lookupName("buf"));
		} else {
			// we are in contact with a pre-4.2.9 version iRODS server
			RcDataObjOpen rcDataObjOpen = new RcDataObjOpen(dataObjInp);
			Message reply = rcDataObjOpen.sendTo(irodsSession);
			if (reply.getIntInfo() < 0) {
				throw new MyRodsException("Unable to open data object' replica (" + reply.getIntInfo() + ")");
			}
			fileDescriptor = reply.getIntInfo();
			exists = true;
			if (serverVersionNumberIsAtLeast("4.2.8")) {
				JsonInp jsonInp = new JsonInp("{\"fd\":" + fileDescriptor + "}");
				RcGetFileDescriptorInfo rcGetFileDescriptorInfo = new RcGetFileDescriptorInfo(jsonInp);
				try {
					reply = rcGetFileDescriptorInfo.sendTo(irodsSession);
					registerReplicaDetails((DataBinArray) reply.getMessage().lookupName("buf"));
				} catch (Exception e) {
					// ignore, the API may be not available. impact is that we fail to obtain a replica token
				}
			}
		}
	}
	
	private void registerReplicaDetails(DataBinArray bin) {
		try {
		String s = bin.getAsString();
		JSONobject json = (JSONobject) JSONParser.parse(s);
		JSONstring jReplicaToken = (JSONstring) json.get("replica_token");
		replicaToken = jReplicaToken.get();
		JSONobject jDataObjInfo = (JSONobject) json.get("data_object_info");
		replNum = JSONnumber.toInt(jDataObjInfo.get("replica_number"));
		} catch (NullPointerException e) {}
	}

	@Override
	public boolean isFile() {
		if (exists) {
			return true;
		}
		// the status of the replica might be 'not exists' yet another option is that its status
		// simply has not been checked so far...we will need to query the server.
		RcObjStat rcObjStat = new RcObjStat(objPath, ObjType.DATAOBJECT);
		try {
			Message reply = rcObjStat.sendTo(irodsSession);
			if (reply.getIntInfo() >= 0) {
				RodsObjStat stat = (RodsObjStat) reply.getMessage();
				exists = true;
				objSize = stat.objSize;
			}
		} catch (Exception e) { } 	
		return exists;
	}
	
	@Override
	public Long getFileSize() {
		if (isFile() && objSize != null) {
			return objSize;
		}
		return null;
	}

	@Override
	public boolean isOpenWrite() {
		return openForWrite;
	}

	@Override
	public boolean isOpenRead() {
		return openForRead;
	}

	@Override
	public int maxThreads() {
		if (session == null || session.getSessionPool() == null) {
			return 0; // 0 means no limitation
		}
		if (openForWrite && replicaToken == null) {
			// we have not been able to get a replica token, possibly the iRODS server does not support it yet
			// restrict data transfer to single-threaded transfer
			return 1;
		}
		return session.getSessionPool().getMaxConnections() - 1;
	}

	private boolean serverVersionNumberIsAtLeast(String minimumVersion) {
		try {
			String serverVersion = irodsSession.getSessionDetails().relVersion.get();
			if (SessionDetails.compareVersions(serverVersion, "rods" + minimumVersion) >= 0) {
				return true;
			}
		} catch (NullPointerException e){
			return false;
		}
		return false;
	}
	
	
	
	
	
	






}
