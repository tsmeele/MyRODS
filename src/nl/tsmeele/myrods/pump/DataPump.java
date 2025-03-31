package nl.tsmeele.myrods.pump;

import java.io.IOException;

import nl.tsmeele.log.Log;
import nl.tsmeele.myrods.api.CollInp;
import nl.tsmeele.myrods.api.DataObjInp;
import nl.tsmeele.myrods.api.KeyValPair;
import nl.tsmeele.myrods.api.Kw;
import nl.tsmeele.myrods.api.ObjType;
import nl.tsmeele.myrods.api.RodsObjStat;
import nl.tsmeele.myrods.high.DataObject;
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
		
		// SOURCE LOGIN
		// if we are unable to login on source as clientUser, we will use our proxyUser 
		// NB: Could be caused by creator of data object is no longer a user on the zone
		source.login(clientUsername, clientZone);
		boolean useProxySource = source.error;
		boolean useProxyDestination = useProxySource;
		if (useProxySource) {
			source.rcDisconnect();	// need to reconnect as proxy user
			source.login();
			if (source.error) {
				System.err.println(source.pUsername + " reconnect to source failed. iRODS error = " + source.intInfo);
				return;
			}
			Log.debug("Source logged in as proxy user");
		}
		// DESTINATION LOGIN
		// do not try to find an equivalent clientUser if it was a remote user on source
		// as this could cause inappropriate mapping
		if (!clientZone.equals(sLocalZone)) {
			Log.debug("Owner '" + firstObj.getOwner() + "' is not a local-zone user on source.");
			useProxyDestination = true;
		}
		// attempt to login on destination as equivalent client user
		if (!useProxyDestination) {
			destination.login(clientUsername, dLocalZone);
			if (destination.error) {
				Log.debug("Destination client user login failed, iRODS error = " + destination.intInfo);
				useProxyDestination = true;	// login failed, we will use proxy user instead
			}
		}
		// try proxy login if client user login is inappropriate or failed
		if (useProxyDestination) {
			destination.rcDisconnect();
			destination.login();
			if (destination.error) {
				System.err.println(destination.pUsername + " reconnect to destination failed. iRODS error = " + destination.intInfo);
				return;
			}
			Log.debug("Destination logged in as proxy user");
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
				source.rcDisconnect();
				if (useProxySource) {
					source.login();
				} else {
					source.login(clientUsername, clientZone);
				}
				// only attempt destination login if source login was successful
				if (!source.error) {
					destination.rcDisconnect();
					if (useProxyDestination) {
						destination.login();
					} else {
						destination.login(clientUsername, dLocalZone);
					}
					if (destination.error) {
						System.err.println("Reconnect to destination failed, iRODS error = " + source.intInfo);
					}
				} else {
					System.err.println("Reconnect to source failed, iRODS error = " + source.intInfo);
				}
				if (source.error || destination.error) {
					System.err.println("Transfer of data objects for owner " + clientUsername + " aborted");
					return;
				}
			}
			// upon successful successful, check integrity of result using data size info
			// update log with result
			if (!transferError) {
				RodsObjStat rodsObjStat = destination.rcObjStat(destObjPath, ObjType.DATAOBJECT);
				if (destination.error) {
					String message = "iRODS error: " + destination.intInfo + " (at destination) ";
					logFile.LogError(obj.getPath(), message);
					System.err.println(message + " while checking info on '" + destObjPath + "'");
					transferError = true; // flag attempt to perform cleanup of partial data object
				}
				if (rodsObjStat.objSize == obj.dataSize) {
					if (verbose || debug) System.out.println("Copied and OK: " + obj.getPath());
					logFile.LogDone(obj.getPath());
				} else {
					String message = " copied replica size (" + rodsObjStat.objSize + ") does not match source size (" + obj.dataSize + ")";
					logFile.LogError(obj.getPath(), message);
					System.err.println("Copy failed: " + obj.getPath() + message);
					transferError = true;	// flag cleanup needed of partial data object
				} 
			}
			// if transfer failed or resulting object is of insufficient quality,
			// attempt to remove the 'partial' data object at destination
			if (transferError) {
				DataObjInp dataObjInp = new DataObjInp(destObjPath, null);
				destination.rcDataObjUnlink(dataObjInp);
				if (destination.error) {
					Log.debug("unable to unlink '" + destObjPath + "' iRODS error = " + destination.intInfo);
				} else {
					if (verbose || debug) System.err.println("Cleaned up partial replica copy of data object at destination");
				}
			} 	
		} // end For
		source.rcDisconnect();
		destination.rcDisconnect();
	}
	
	private boolean ensureCollectionExists(Pirods destination, String collName) throws MyRodsException, IOException {
		final int CATALOG_ALREADY_HAS_ITEM_BY_THAT_NAME = -809000;
		KeyValPair condInput = new KeyValPair();
		condInput.put(Kw.RECURSIVE_OPR__KW, "");	// create intermediate collections too 
		CollInp collInp = new CollInp(collName, 0, 0, condInput);
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
