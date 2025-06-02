package nl.tsmeele.myrods.high;


import java.io.IOException;
import java.util.ArrayList;
import nl.tsmeele.myrods.api.AccessType;
import nl.tsmeele.myrods.api.Columns;
import nl.tsmeele.myrods.api.Flag;
import nl.tsmeele.myrods.api.GenQueryInp;
import nl.tsmeele.myrods.api.GenQueryOut;
import nl.tsmeele.myrods.api.InxIvalPair;
import nl.tsmeele.myrods.api.InxValPair;
import nl.tsmeele.myrods.api.Irods;
import nl.tsmeele.myrods.api.KeyValPair;
import nl.tsmeele.myrods.api.Kw;
import nl.tsmeele.myrods.api.ModAVUMetadataInp;
import nl.tsmeele.myrods.api.ObjType;
import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.RcConnect;
import nl.tsmeele.myrods.plumbing.MyRodsException;
import nl.tsmeele.myrods.plumbing.ServerConnectionDetails;

public class Hirods extends Irods {
	public IrodsPool irodsPool = null;
	
	
	public Hirods(String host, int port) {
		super(host, port);
	}
	
	public Hirods cloneConnection() throws IOException, MyRodsException {
		if (!serverConnection.isConnected() || authenticatedPassword == null || irodsPool == null) {
			throw new MyRodsException("Unable to clone: Missing authenticated iRODS connection");
		}
		Hirods irods2 = new Hirods(host, port);
		// clone will reuse current pool
		irods2.irodsPool = irodsPool;
		ServerConnectionDetails sd = serverConnection.getSessionDetails();
		
		RcConnect rcConnect = new RcConnect(sd.startupPack, sd.clientPolicy);
		// clone connects to the server
		irods2.rcConnect(rcConnect);
		if (irods2.error) {
			throw new MyRodsException("Unable to clone: cannot connect to server");
		}
		// clone authenticates
		byte[] challenge = irods2.rcAuthRequest();
		if (irods2.error) {
			irods2.rcDisconnect();
			throw new MyRodsException("Unable to clone: authentication request failed");
		}
		String proxyUser = sd.startupPack.lookupString("proxyUser");
		String proxyZone = sd.startupPack.lookupString("proxyRcatZone");
		irods2.rcAuthResponse(proxyUser + "#" + proxyZone, authenticatedPassword, challenge);
		if (irods2.error) {
			irods2.rcDisconnect();
			throw new MyRodsException("Unable to clone: authentication failed");
		}
		return irods2;
	}

	
	public boolean pamLogin(String proxyUser, String proxyZone, String proxyPamPassword,
			String clientUser, String clientZone) throws MyRodsException, IOException {
		return pamLogin(proxyUser, proxyZone, proxyPamPassword,	clientUser, clientZone, 0);
	}
	
	public boolean pamLogin(String proxyUser, String proxyZone, String proxyPamPassword,
			String clientUser, String clientZone, int ttl) throws MyRodsException, IOException {
		rcConnect(proxyUser, proxyZone, clientUser, clientZone);
		if (error) return false;
		boolean originalSslState = serverConnection.isSsl();
		if (!originalSslState) {
			// upgrade session to ssl for exchange of proxyPamPassword
			rcSslStart();
			if (error || !serverConnection.isSsl()) return false;
		}
		// authenticate pam user in local zone and obtain time-limited password
		String proxyTtlPassword = rcPamAuthRequest(proxyUser, proxyPamPassword, ttl);
		boolean authError = error;
		if (!originalSslState) {
			// downgrade session back to original state
			rcSslEnd();
		}
		if (authError) return false;
		return nativeLogin(proxyUser, proxyZone, proxyTtlPassword, clientUser, clientZone);
	}
	
	
	public boolean nativeLogin(String proxyUser, String proxyZone, String proxyNativePassword,
			String clientUser, String clientZone) throws MyRodsException, IOException {
		if (!serverConnection.isConnected()) {
			rcConnect(proxyUser, proxyZone, clientUser, clientZone);
			if (error) return false;
		}
		byte[] challenge = rcAuthRequest();
		if (error) return false;
		rcAuthResponse(proxyUser + "#" + proxyZone, proxyNativePassword, challenge);
		if (error) return false;
		if (irodsPool == null) {
			irodsPool = new IrodsPool(this);
		}
		return true;
	}
	
	// TODO: remove this method, should use rcGetMiscSvrInfo instead
	public String getLocalZone() throws MyRodsException, IOException {
		if (!isAuthenticated()) return null;
		// SELECT clause
		InxIvalPair inxIvalPair = new InxIvalPair();
		inxIvalPair.put(Columns.ZONE_NAME.getId(), Flag.SELECT_NORMAL);	
		// WHERE clause
		InxValPair inxValPair = new InxValPair();
		inxValPair.put(Columns.ZONE_TYPE.getId(), "= 'local'");
		int maxRows = 1;
		GenQueryInp genQueryInp = new GenQueryInp(maxRows, 0, 0, Flag.AUTO_CLOSE,
				new KeyValPair(), inxIvalPair , inxValPair);
		GenQueryOut genOut = rcGenQuery(genQueryInp);
		if (error || genOut.columnCount < 1 || genOut.rowCount < 1) return null;
		return genOut.data[0][0];
	}
	
	public String getUserType(String userName, String zone) throws MyRodsException, IOException {
		if (!this.isAuthenticated()) return null;
		// SELECT clause
		InxIvalPair inxIvalPair = new InxIvalPair();
		inxIvalPair.put(Columns.USER_TYPE.getId(), Flag.SELECT_NORMAL);	
		// WHERE clause
		InxValPair inxValPair = new InxValPair();
		inxValPair.put(Columns.USER_NAME.getId(), "= '" + userName + "'");
		inxValPair.put(Columns.USER_ZONE.getId(), "= '" + zone + "'");
		int maxRows = 1;
		GenQueryInp genQueryInp = new GenQueryInp(maxRows, 0, 0, Flag.AUTO_CLOSE,
				new KeyValPair(), inxIvalPair , inxValPair);
		GenQueryOut genOut = rcGenQuery(genQueryInp);
		if (error || genOut.columnCount < 1 || genOut.rowCount < 1) return null;
		String userType = genOut.data[0][0];
		return userType;
	}
	


	public ArrayList<AVU> getAvus(String rodsObjType, String name) throws MyRodsException, IOException {
		return getAvus(rodsObjType, name, false);
	}
	
	public ArrayList<AVU> getAvus(String rodsObjType, String name, boolean admin) throws MyRodsException, IOException {
		if (!isAuthenticated()) return null;
		ArrayList<AVU> avus = new ArrayList<AVU>();
		InxIvalPair inxIvalPair = new InxIvalPair();
		InxValPair inxValPair = new InxValPair();
		KeyValPair condInput = new KeyValPair();
		if (admin) {
			condInput.put(Kw.ADMIN_KW, null);
		}
		switch (rodsObjType.toLowerCase()) {
		case "-d": {
			inxIvalPair.put(Columns.META_DATA_ATTR_NAME.getId(), Flag.SELECT_NORMAL);
			inxIvalPair.put(Columns.META_DATA_ATTR_VALUE.getId(), Flag.SELECT_NORMAL);
			inxIvalPair.put(Columns.META_DATA_ATTR_UNITS.getId(), Flag.SELECT_NORMAL);
			inxValPair.put(Columns.DATA_NAME.getId(), "= '"+ DataObject.basename(name) + "'");
			inxValPair.put(Columns.COLL_NAME.getId(), "= '"+ DataObject.parent(name) + "'");
			break;
		}
		case "-c": {
			inxIvalPair.put(Columns.META_COLL_ATTR_NAME.getId(), Flag.SELECT_NORMAL);
			inxIvalPair.put(Columns.META_COLL_ATTR_VALUE.getId(), Flag.SELECT_NORMAL);
			inxIvalPair.put(Columns.META_COLL_ATTR_UNITS.getId(), Flag.SELECT_NORMAL);
			inxValPair.put(Columns.COLL_NAME.getId(), "= '"+ name + "'");
			break;
		}
		case "-r": {
			inxIvalPair.put(Columns.META_RESC_ATTR_NAME.getId(), Flag.SELECT_NORMAL);
			inxIvalPair.put(Columns.META_RESC_ATTR_VALUE.getId(), Flag.SELECT_NORMAL);
			inxIvalPair.put(Columns.META_RESC_ATTR_UNITS.getId(), Flag.SELECT_NORMAL);
			inxValPair.put(Columns.RESC_NAME.getId(), "= '"+ name + "'");
			break;
		}
		case "-u": {
			inxIvalPair.put(Columns.META_USER_ATTR_NAME.getId(), Flag.SELECT_NORMAL);
			inxIvalPair.put(Columns.META_USER_ATTR_VALUE.getId(), Flag.SELECT_NORMAL);
			inxIvalPair.put(Columns.META_USER_ATTR_UNITS.getId(), Flag.SELECT_NORMAL);
			int hash = name.lastIndexOf('#');
			if (hash < 0) {
				inxValPair.put(Columns.USER_NAME.getId(), "= '"+ name + "'");
			} else {
				inxValPair.put(Columns.USER_NAME.getId(), "= '"+ name.substring(0,hash) + "'");
				inxValPair.put(Columns.USER_ZONE.getId(), "= '"+ name.substring(hash + 1) + "'");
			}
			break;
		}
		default:
			// unsupported object type
			return avus;
		}
		int maxRows = 256;
		GenQueryInp genQueryInp = new GenQueryInp(maxRows, 0, 0, 0,
				condInput, inxIvalPair , inxValPair);
		// execute query and retrieve all AVU's 
		GenQueryIterator it = genQueryIterator(genQueryInp);
		while (it.hasNext()) {
			GenQueryOut genOut = it.next();
			for (int i = 0; i < genOut.rowCount; i++) {
				AVU avu = new AVU(genOut.data[i][0], genOut.data[i][1], genOut.data[i][2]);
				avus.add(avu);
			}
		}
		return avus;
	}

	public boolean addAvus(String rodsObjType, String name, ArrayList<AVU> avus) throws MyRodsException, IOException {
		return addAvus(rodsObjType, name, avus, false);
	}
	
	public boolean addAvus(String rodsObjType, String name, ArrayList<AVU> avus, boolean admin) throws MyRodsException, IOException {
		if (!isAuthenticated()) return false;
		KeyValPair condInput = new KeyValPair();
		if (admin) {
			condInput.put(Kw.ADMIN_KW, null);
		}
		for (AVU avu : avus) {
			ArrayList<String> args = new ArrayList<String>();
			args.add("add");
			args.add(rodsObjType);
			args.add(name);
			args.add(avu.name);			// attribute name and value must be present
			args.add(avu.value); 
			if (avu.units != null) {
				args.add(avu.units);	// whereas attribute units is optional
			}
			ModAVUMetadataInp modAVUMetaDataInp = new ModAVUMetadataInp(args, condInput);
			rcModAVUMetadata(modAVUMetaDataInp);
			if (error) {
				return false;
			}
		}
		return true;
	}
	
	// The creation of a genQueryIterator will open a query at the iRODS server.
	// To avoid keeping queries open, the client application MUST continue calls to next() until hasNext() is exhausted.
	// Alternatively the client application may call the iterator method closeQuery() to close without reading all row sets
	public GenQueryIterator genQueryIterator(GenQueryInp genQueryInp) throws MyRodsException, IOException {
		return new GenQueryIterator(this, genQueryInp);
	}
	
	public boolean checkAccess(String userName, String userZone, ObjType type, String objpath, AccessType desiredPermission) 
			throws MyRodsException, IOException {
		if (!isAuthenticated()) return false;
		// get a list of all groups where the user is a member
		ArrayList<String> users = getUserGroupIds(userName, userZone);
		// SELECT COLL_ACCESS_USER_ID WHERE COLL_ACCESS_TYPE >= desiredPermission AND COLL_NAME = collection
		InxIvalPair inxIvalPair = new InxIvalPair();
		InxValPair inxValPair = new InxValPair();
		if (type == ObjType.COLLECTION) {
			inxIvalPair.put(Columns.COLL_ACCESS_USER_ID.getId(), Flag.SELECT_NORMAL);
			inxValPair.put(Columns.COLL_NAME.getId(), "= '"+ objpath + "'");
		} else {
			inxIvalPair.put(Columns.DATA_ACCESS_USER_ID.getId(), Flag.SELECT_NORMAL);
			inxValPair.put(Columns.DATA_NAME.getId(), "= '"+ DataObject.basename(objpath) + "'");
			inxValPair.put(Columns.COLL_NAME.getId(), "= '"+ DataObject.parent(objpath) + "'");
		}
		// NB: a string compare suffices because the access types are all strings of 4 digits
		inxValPair.put(Columns.COLL_ACCESS_TYPE.getId(), ">= '" + String.valueOf(desiredPermission.getId()) + "'");
		int maxRows = 256;
		GenQueryInp genQueryInp = new GenQueryInp(maxRows, 0, 0, 0,
				new KeyValPair(), inxIvalPair , inxValPair);
		boolean hasDesiredPermission = false;
		boolean done = false;
		while (!done) {
			GenQueryOut genOut = rcGenQuery(genQueryInp);
			if (error) break;
			// browse through list of groups that are granted the desired access level to the collection
			for (int i = 0; i < genOut.rowCount; i++) {
				// check if the user is a member of this group
				if (users.contains(genOut.data[i][0])) {	// COLL_ACCESS_USER_ID
					hasDesiredPermission = true;
					done = true;
					break;
				}
			}
			// prepare for next set of rows
			DataInt continueInx = (DataInt) genQueryInp.lookupName("continueInx");
			continueInx.set(genOut.continueInx);
			if (genOut.rowCount < 256) {
				// last rowset received
				break;
			}
		}
		// close query
		DataInt genInpMaxRows = (DataInt) genQueryInp.lookupName("maxRows");
		genInpMaxRows.set(0);
		rcGenQuery(genQueryInp);
		return hasDesiredPermission;
	}
	
	private ArrayList<String> getUserGroupIds(String userName, String userZone) throws MyRodsException, IOException {
		// SELECT USER_GROUP_ID WHERE USER_NAME = userName AND USER_ZONE = userZone
		InxIvalPair inxIvalPair = new InxIvalPair();
		inxIvalPair.put(Columns.USER_GROUP_ID.getId(), Flag.SELECT_NORMAL);
		InxValPair inxValPair = new InxValPair();
		inxValPair.put(Columns.USER_NAME.getId(), "= '"+ userName + "'");
		inxValPair.put(Columns.USER_ZONE.getId(), "= '"+ userZone + "'");
		int maxRows = 256;
		GenQueryInp genQueryInp = new GenQueryInp(maxRows, 0, 0, 0,
				new KeyValPair(), inxIvalPair , inxValPair);
		ArrayList<String> users = new ArrayList<String>();
		boolean done = false;
		while (!done) {
			GenQueryOut genOut = rcGenQuery(genQueryInp);
			if (error) break;	// query error or CAT_NO_ROWS_FOUND
			for (int i = 0; i < genOut.rowCount; i++) {
				users.add(genOut.data[i][0]);	// USER_GROUP_ID
			}
			// prepare for next set of rows
			DataInt continueInx = (DataInt) genQueryInp.lookupName("continueInx");
			continueInx.set(genOut.continueInx);
			if (genOut.rowCount < 256) {
				// last rowset received
				break;
			}
		}
		// close query
		DataInt GenInpMaxRows = (DataInt) genQueryInp.lookupName("maxRows");
		GenInpMaxRows.set(0);
		rcGenQuery(genQueryInp);
		return users;
	}
}
