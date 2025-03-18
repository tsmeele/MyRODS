package nl.tsmeele.myrods.pump;

import java.io.IOException;

import nl.tsmeele.log.Log;
import nl.tsmeele.myrods.api.CollInp;
import nl.tsmeele.myrods.api.DataObjInp;
import nl.tsmeele.myrods.api.ObjType;
import nl.tsmeele.myrods.api.RodsObjStat;
import nl.tsmeele.myrods.high.DataTransfer;
import nl.tsmeele.myrods.high.DataTransferMultiThreaded;
import nl.tsmeele.myrods.high.DataTransferSingleThreaded;
import nl.tsmeele.myrods.high.PosixFileFactory;
import nl.tsmeele.myrods.high.Replica;
import nl.tsmeele.myrods.plumbing.MyRodsException;

public class DataPump {
	private Context ctx;
	private String sLocalZone;
	private String dLocalZone;
	private DataObjectList list;
	private boolean verbose;
	private boolean debug;
	private int threads = 1;
	
	
	public DataPump(Context ctx, String sLocalZone, String dLocalZone, DataObjectList list) {
		this.ctx = ctx;
		this.sLocalZone = sLocalZone;
		this.dLocalZone = dLocalZone;
		this.list = list;	
		verbose = ctx.options.containsKey("verbose");
		debug = ctx.options.containsKey("debug");
		String sThreads = ctx.options.get("threads");
		if (sThreads != null) {
			try {
				threads = Integer.parseInt(sThreads);
			} catch (NumberFormatException e) {
				// ignore illegal argument, use single thread
			}
			if (threads < 1) {
				threads = 1;
			}
		}
	}
	
	
	
	public void pump() throws MyRodsException, IOException {
		if (list.isEmpty()) return;
		Pirods source = new Pirods(ctx, true);
		Pirods destination = new Pirods(ctx, false);
		DataObject firstObj = list.get(0);
		String clientUsername = firstObj.ownerName;
		String clientZone = firstObj.ownerZone;
		
		if (!clientZone.equals(sLocalZone)) {
			System.err.println("Warning: Skipping data object copy for owner '" + firstObj.getOwner() + "'\n" +
							   "         Owner must be a local zone user on the source server.");
			return;
		}

		// login on source and destination on behalf of the owner
		if (! (loginOnBehalf(source, clientUsername, clientZone) && loginOnBehalf(destination, clientUsername, dLocalZone))
			) {
			return;
		}
		
		// we will copy data objects within a sub-collection, establish path to the root of source and destination trees
		String sRoot;
		if (firstObj.getPath().equals(ctx.sourceObject)) {
			// a data object was specified as source
			sRoot = firstObj.collName;
		} else {
			// a collection was specified as source
			sRoot = ctx.sourceObject;
		}
		String dRoot = ctx.destinationCollection;
		
		// open a log to record the results of completed actions
		LogFile logFile = new LogFile(ctx.options.get("log"));
		
		// copy listed set of data objects, create destination collections on the fly where needed
		PosixFileFactory posix = new PosixFileFactory();
		String currentCollection = "";
		list.sortByPath();
		for (DataObject obj : list) {
			// derive destination collection name from source path
			String sSubCollection = obj.collName.substring(sRoot.length());
			String dCollName;
			if (sSubCollection.length() > 0) {
				dCollName = dRoot + sSubCollection;
			} else {
				dCollName = dRoot;
			}
			// create destination collection (if it does not yet exist)
			if (!obj.collName.equals(currentCollection)) {
				currentCollection = obj.collName;
				Log.debug("Start of destination collection " + dCollName);
				if (!dCollName.equals(dRoot)) {
					if (!ensureCollectionExists(destination, dCollName)) {
						return;
					};
				}
			}
			// prepare replica references for this data object in source and destination
			String destObjPath = dCollName + "/" + obj.dataName;
			if (verbose || debug) System.out.println("...copying from " + obj.getPath() + " to " + destObjPath);
			Replica sourceReplica = posix.createReplica(source, obj.getPath());
			Replica destinationReplica = posix.createReplica(destination, destObjPath);
			if (destinationReplica.isFile()) {
				String message = "skipping, destination object already exists: '" + destObjPath + "'";
				logFile.LogError(obj.getPath(), message);
				System.err.println(message);
				continue;
			}
			// copy the data object
			DataTransfer tx = null;
			boolean transferError = false;
			if (threads < 2) {
				tx = new DataTransferSingleThreaded(sourceReplica, destinationReplica);
			} else {
				tx = new DataTransferMultiThreaded(sourceReplica, destinationReplica);
				((DataTransferMultiThreaded)tx).setThreads(threads);
			}
			try {
				tx.transfer();
			} catch (IOException e) {
				transferError = true;
				String message = "Transfer failed with exception: " + e.getMessage();
				logFile.LogError(obj.getPath(), message);
				if (verbose || debug) {
					System.err.println(obj.getPath() + ": " + message);
				}
			}
			// After a failed data object copy we will need to reconnect and login again
			if (transferError) {
				if (! (loginOnBehalf(source, clientUsername, clientZone) && loginOnBehalf(destination, clientUsername, dLocalZone))
						) {
					System.err.println("Transfer of data objects for owner " + clientUsername + " aborted");
					return;
				}
			}
			// check integrity of result using data size info
			RodsObjStat rodsObjStat = destination.rcObjStat(destObjPath, ObjType.DATAOBJECT);
			if (destination.error) {
				String message = "iRODS error: " + destination.intInfo + " (at destination) ";
				logFile.LogError(obj.getPath(), message);
				System.err.println(message + " while checking info on '" + destObjPath + "'");
				continue;
			} 
			if (rodsObjStat.objSize != obj.dataSize) {
				String message = " copied replica size (" + rodsObjStat.objSize + ") does not match source size (" + obj.dataSize + ")";
				logFile.LogError(obj.getPath(), message);
				System.err.println("Copy failed: " + obj.getPath() + message);
				// attempt to remove the 'partial' data object at destination
				DataObjInp dataObjInp = new DataObjInp(destObjPath, null);
				destination.rcDataObjUnlink(dataObjInp);
				if (destination.error) {
					Log.debug("unable to unlink '" + destObjPath + "' iRODS error = " + destination.intInfo);
				} else {
					if (verbose || debug) System.err.println("Cleaned up partial replica copy of data object at destination");
				}
			} else {
				logFile.LogDone(obj.getPath());
				if (verbose || debug) System.out.println("Copied and OK: " + obj.getPath());
			}	
		} // end For
		source.rcDisconnect();
		destination.rcDisconnect();
	}
	
	
	private boolean loginOnBehalf(Pirods irods, String clientUsername, String clientZone) throws MyRodsException, IOException {
		irods.login(clientUsername, clientZone);
		if (irods.error) {
			System.err.println("Error: Failed to login to zone " + clientZone + " on behalf of '" + clientUsername +  
					"'. iRODS error = " + irods.intInfo);
			return false;
		}
		Log.debug("logged in as " + clientUsername + "#" + clientZone);
		return true;
	}

	
	private boolean ensureCollectionExists(Pirods destination, String collName) throws MyRodsException, IOException {
		final int CATALOG_ALREADY_HAS_ITEM_BY_THAT_NAME = -809000;
		CollInp collInp = new CollInp(collName);
		destination.rcCollCreate(collInp);
		if (destination.error) {
			if (destination.intInfo == CATALOG_ALREADY_HAS_ITEM_BY_THAT_NAME) {
				// fine, collection already existed
				Log.debug("Destination collection already exists: " + collName);
				return true;
			}
			System.err.println("ERROR: Unable to create destination collection '" + collName + 
					"' iRODS error = " + destination.intInfo);
			return false;
		}
		if (verbose || debug) {
			System.out.println("Destination collection created: " + collName);
		}
		return true;
	}
	

}
