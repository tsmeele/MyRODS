package nl.tsmeele.myrods.pump;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;

import nl.tsmeele.log.Log;
import nl.tsmeele.log.LogLevel;
import nl.tsmeele.myrods.api.AccessType;
import nl.tsmeele.myrods.api.ObjType;
import nl.tsmeele.myrods.api.RodsObjStat;
import nl.tsmeele.myrods.high.DataObject;
import nl.tsmeele.myrods.plumbing.MyRodsException;

public class PumpMain {
	
	/*
	 * - size check source/dest


1 Gbit/sec -> 100MB/sec  * 3600 = 360 GB/uur -> 1TB / 3uur -> 8 TB/etmaal



	 */

	public static void main(String[] args) throws IOException {
		Context ctx = new Context();
		try {
			ctx.processArgs(args);
		} catch (MyRodsException e) {
			System.err.println(e.getMessage() + "\n");
			System.out.println(Context.usage());
			System.exit(1);
		}
		boolean verbose = ctx.options.containsKey("verbose");
		boolean debug = ctx.options.containsKey("debug");
		if (debug) {
			System.out.println(ctx);
			Log.setLoglevel(LogLevel.DEBUG);
			String[] classFilter = { "nl.tsmeele.myrods.pump" };
			Log.setDebugOutputFilter(classFilter);
		}
		if (ctx.options.containsKey("help")) {
			System.out.println(Context.usage());
			System.exit(0);
		}
		
		// ensure that we got sufficient info
		if (ctx.sourceObject == null || ctx.destinationCollection == null) {
			System.err.println("ERROR: Missing commandline argument for source and/or destination");
			System.exit(1);
		}
		
		//  open the server connections
		Pirods source = new Pirods(ctx, true);
		Pirods destination = new Pirods(ctx, false);
		
		// execute activities on the open connections
		long startCopy = timeStamp();
		boolean result = actions(ctx, source, destination);
		long elapsed = timeStamp() - startCopy;
		if (verbose || debug) System.out.println("Total elapsed time " + elapsed + " seconds");
		// close any open connections
		source.rcDisconnect();
		destination.rcDisconnect();
		System.exit(result? 0 : 2);
	}
	
	private static boolean actions(Context ctx, Pirods source, Pirods destination) throws MyRodsException, IOException {
		// login and make sure we have rodsadmin privs
		boolean verbose = ctx.options.containsKey("verbose");
		boolean debug = ctx.options.containsKey("debug");
		Log.debug("before login & check privs");
		boolean ok = loginAndAssertPrivs(source) && loginAndAssertPrivs(destination);
		Log.debug("login & privs check done");
		if (!ok) return false;
		if (verbose) System.out.println("Logged in with rodsadmin privs on source and destination server");
		
		// prepare a list of completed objects from resume log, if resuming
		HashMap<String,Boolean> resumeFilter = null;
		if (ctx.options.containsKey("resume")) {
			resumeFilter = LogFile.slurpCompletedObjects(ctx.options.get("resume"));
			if (resumeFilter == null) {
				System.err.println("ERROR: unable to open resume file '" + ctx.options.get("resume") + "'");
				return false;
			}
			Log.debug("resume file read");
		}
		
		RodsObjStat sourceObjStat = source.rcObjStat(ctx.sourceObject, null);
		if (source.error) {
			System.err.println("ERROR: Unable to find source object. iRODS error = " + source.intInfo);
			return false;
		}
		ObjType sourceObjType = ObjType.lookup(sourceObjStat.objType);
		RodsObjStat destinationObjStat = destination.rcObjStat(ctx.destinationCollection, null);
		if (destination.error) {
			System.err.println("ERROR: Unable to find destination collection. iRODS error = " + destination.intInfo);
			return false;
		}
		if (ObjType.lookup(destinationObjStat.objType) != ObjType.COLLECTION ) {
			System.err.println("ERROR: Destination must be collection type.");
			return false;
		}
		// find out if the proxy user has sufficient access to the source and destination objects
		boolean accessSource = source.checkAccess(ctx.sUsername, ctx.sZone, sourceObjType, 
				ctx.sourceObject, AccessType.READ);
		if (!accessSource) {
			System.err.println("Warning: user " + ctx.sUsername + " lacks 'read' access to source object\n" + 
							   "         Copy operation may fail if original object creator no longer exists on source");
		}
		boolean accessDestination = destination.checkAccess(ctx.dUsername, ctx.dZone, ObjType.COLLECTION, 
				ctx.destinationCollection, AccessType.OWN);
		if (!accessDestination) {
			System.err.println("Warning: user " + ctx.dUsername + " lacks 'own' access to destination collection\n" +
							   "         Copy operation may fail if original object creator does not exist on destination");
		}
		
		// assemble list of objects to copy
		DataObjectList list = null;
		if (sourceObjType == ObjType.DATAOBJECT) {
			// we will copy just one object
			list = new DataObjectList();
			list.add(new DataObject(
					DataObject.parent(ctx.sourceObject),
					DataObject.basename(ctx.sourceObject),
					sourceObjStat.objSize,
					sourceObjStat.ownerName,
					sourceObjStat.ownerZone
					));
			if (verbose) System.out.println("Selected source is a data object");
		} else {
			// we will copy a collection
			Log.debug("before assembling list of objects in collection");
			list = source.selectDataObjects(ctx.sourceObject);
			if (verbose || debug) System.out.println("Selected source is a collection with " + list.size() + " objects");
			Log.debug("got list of objects");
		}
		// filter object list by resume filter, if needed
		int originalSize = list.size();
		if (resumeFilter != null) {
			list = list.filterObjects(resumeFilter);
			if (verbose || debug) {
				System.out.println("Resume log contains " + resumeFilter.size() + " succesfully copied objects");
				if (list.size() < originalSize) {
					System.out.println("Copy list reduced by " + (originalSize - list.size()) + " objects");
				}
			}
		}
		// per owner, copy all data objects from source to destination
		HashMap<String,Boolean> owners = list.getOwners();
		if (verbose || debug) System.out.println("About to copy " + list.size() + " data object(s), created/owned by " 
					+ owners.keySet().size() + " distinct user(s)");
		
		// pump data to destination, per owner
		String sLocalZone = source.getLocalZone();
		String dLocalZone = destination.getLocalZone();
		// disconnect original connections, we will reconnect on behalf of data object owner(s) going forward
		source.rcDisconnect();
		destination.rcDisconnect();
		for (String owner : owners.keySet()) {
			Log.debug("start copy for user " + owner);
			DataObjectList subset = list.filterByOwner(owner);
			if (verbose || debug) System.out.println("Copying " + subset.size() + " objects on behalf of '" + owner + "'");
			DataPump dataPump = new DataPump(ctx, sLocalZone, dLocalZone, subset);
			try {
				dataPump.pump();
			} catch (MyRodsException e) {
				if (verbose || debug) System.err.println("MyRodsException while copying for " + owner + " :" + e.getMessage());
			} catch (IOException e) {
				if (verbose || debug) System.err.println("IOException while copying for " + owner + " :" + e.getMessage());
			}
		}
		return true;
	}
	
	
	

	private static boolean loginAndAssertPrivs(Pirods irods) throws MyRodsException, IOException {
		irods.login();
		if (irods.error) {
			int errorCode = irods.intInfo;
			System.err.println("ERROR: User " + irods.pUsername + "#" + irods.pZone + 
					" unable to login to " + irods.getHost() + " iRODS error = " + errorCode);
			return false;
		}
		if (!irods.isRodsAdmin()) {
			System.err.println("ERROR: User " + irods.pUsername + "#" + irods.pZone + 
					" lacks rodsadmin privileges on " + irods.getHost());
			return false;
		}
		return true;
	}
	

	
	private static long timeStamp() {
		return Instant.now().getEpochSecond();
	}

}
