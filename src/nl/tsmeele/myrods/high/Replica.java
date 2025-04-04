package nl.tsmeele.myrods.high;

import java.io.IOException;

import nl.tsmeele.json.JNumber;
import nl.tsmeele.json.JObject;
import nl.tsmeele.json.JString;
import nl.tsmeele.myrods.api.DataObjInp;
import nl.tsmeele.myrods.api.Flag;
import nl.tsmeele.myrods.api.Irods;
import nl.tsmeele.myrods.api.JsonInp;
import nl.tsmeele.myrods.api.KeyValPair;
import nl.tsmeele.myrods.api.Kw;
import nl.tsmeele.myrods.api.ObjType;
import nl.tsmeele.myrods.api.OpenedDataObjInp;
import nl.tsmeele.myrods.api.OprType;
import nl.tsmeele.myrods.api.RodsObjStat;
import nl.tsmeele.myrods.plumbing.MyRodsException;
import nl.tsmeele.myrods.plumbing.ServerConnectionDetails;

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
	public Hirods session = null;
	//public IrodsPool irodsPool = null;
	private String host = null;
	
	private int numThreads = 0;
	
	protected Replica() {
	}
	
	protected void setSession(Hirods session) {
		this.session = session;
	}
	
	protected void setReplica(Hirods session, String objPath, String resource) {
		this.session = session;
		//this.irodsPool = session.irodsPool;
		this.host = session.getHost();
		this.objPath = objPath;
		this.resource = resource;
		this.replNum = null;
	}
	
	protected void setReplica(Hirods session, String objPath, int replNum) {
		this.session = session;
		//this.irodsPool = session.irodsPool;
		this.host = session.getHost();
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
		session.rcDataObjLseek(openedDataObjInp);
		if (session.error) {
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
		session.rcDataObjRead(openedDataObjInp);
		if (session.error) {
			throw new MyRodsException("Unable to read " + bytes + " bytes from replica");
		}
		byte[] buf = session.bs;
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
			session.rcDataObjWrite(openedDataObjInp, bytes);
			if (session.error) {
				throw new MyRodsException("Unable to write to replica (" + session.intInfo + ")");
			}
			needToWrite = needToWrite - session.intInfo;
			byte[] remain = new byte[needToWrite];
			for (int i = 0; i < needToWrite; i++) {
				 remain[i] = bytes[session.intInfo + i];
			}
			bytes = remain;
		}
	}

	@Override
	public void close() throws MyRodsException, IOException {
		KeyValPair kv = new KeyValPair();
		OpenedDataObjInp openedDataObjInp = new OpenedDataObjInp(fileDescriptor, 0, 0, null,
				0L, 0L, kv);
		session.rcDataObjClose(openedDataObjInp);
		openForRead = false;
		openForWrite = false;
		replicaToken = null;
		if (session.error) {
			throw new MyRodsException("Error when closing replica (" + session.intInfo + ")");
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
	
	private void getResourceForReplica(String operation, String rescHier) throws MyRodsException, IOException {
		if (serverVersionNumberIsAtLeast("4.3.1")) {
			JObject json = session.rcGetResourceInfoForOperation(objPath, operation, rescHier);
			if (session.error) {
				throw new MyRodsException("Unable to get resource info for replica (" + session.intInfo + ")");
			}
			// TODO: support connections to hosts other than the connected host, if the resource is located elsewhere
			//resourceHost = ((JSONstring)json.get("host")).get();
			JString jString = (JString) json.get("resource_hierarchy");
			 this.resource =jString.data;
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
			JObject json = session.rcReplicaOpen(dataObjInp);
			if (session.error) {
				throw new MyRodsException("Unable to open replica (" + session.intInfo + ")");
			}
			fileDescriptor = session.intInfo;
			exists = true;
			registerReplicaDetails(json);
		} else {
			// we are in contact with a pre-4.2.9 version iRODS server
			session.rcDataObjOpen(dataObjInp);
			if (session.error) {
				throw new MyRodsException("Unable to open data object' replica (" + session.intInfo + ")");
			}
			fileDescriptor = session.intInfo;
			exists = true;
			if (serverVersionNumberIsAtLeast("4.2.8")) {
				JsonInp jsonInp = new JsonInp("{\"fd\":" + fileDescriptor + "}");
				try {
					JObject json = session.rcGetFileDescriptorInfo(jsonInp);
					registerReplicaDetails(json);
				} catch (Exception e) {
					// ignore, the API may be not available. impact is that we fail to obtain a replica token
				}
			}
		}
	}
	
	private void registerReplicaDetails(JObject json) {
		try {
		JString jReplicaToken = (JString) json.get("replica_token");
		replicaToken = jReplicaToken.data;
		JObject jDataObjInfo = (JObject) json.get("data_object_info");
		JNumber jNumber = (JNumber) jDataObjInfo.get("replica_number");
		replNum = (int) jNumber.dataLong;
		} catch (NullPointerException e) {}
	}

	@Override
	public boolean isFile() {
		if (exists && objSize != null) {
			return true;
		}
		// the status of the replica might be 'not exists' yet another option is that its status
		// simply has not been checked so far...we will need to query the server.
		try {
			RodsObjStat rodsObjStat = session.rcObjStat(objPath, ObjType.DATAOBJECT);
			if (!session.error) {
				exists = true;
				objSize = rodsObjStat.objSize;
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
		if (session == null || session.irodsPool == null) {
			return 0; // 0 means no limitation
		}
		if (openForWrite && replicaToken == null) {
			// we have not been able to get a replica token, possibly the iRODS server does not support it yet
			// restrict data transfer to single-threaded transfer
			return 1;
		}
		return session.irodsPool.maxEntries - 1;
	}

	private boolean serverVersionNumberIsAtLeast(String minimumVersion) {
		try {
			String serverVersion = session.getServerRelVersion();
			if (ServerConnectionDetails.compareVersions(serverVersion, "rods" + minimumVersion) >= 0) {
				return true;
			}
		} catch (NullPointerException e){
			return false;
		}
		return false;
	}
	
	
	
	
	
	






}
