package main;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import nl.tsmeele.log.Log;
import nl.tsmeele.log.LogLevel;
import nl.tsmeele.myrods.api.CollInp;
import nl.tsmeele.myrods.api.CollOprStat;
import nl.tsmeele.myrods.api.Columns;
import nl.tsmeele.myrods.api.DataObjInp;
import nl.tsmeele.myrods.api.Flag;
import nl.tsmeele.myrods.api.GenQueryInp;
import nl.tsmeele.myrods.api.GenQueryOut;
import nl.tsmeele.myrods.api.InxIvalPair;
import nl.tsmeele.myrods.api.InxValPair;
import nl.tsmeele.myrods.api.IrodsCsNegType;
import nl.tsmeele.myrods.api.KeyValPair;
import nl.tsmeele.myrods.api.Kw;
import nl.tsmeele.myrods.api.ObjType;
import nl.tsmeele.myrods.api.OpenedDataObjInp;
import nl.tsmeele.myrods.high.DataObject;
import nl.tsmeele.myrods.high.DataTransfer;
import nl.tsmeele.myrods.high.DataTransferSingleThreaded;
import nl.tsmeele.myrods.high.Hirods;
import nl.tsmeele.myrods.high.PosixFile;
import nl.tsmeele.myrods.high.PosixFileFactory;
import nl.tsmeele.myrods.high.Replica;
import nl.tsmeele.myrods.irodsStructures.Data;
import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.plumbing.IrodsProtocolType;
import nl.tsmeele.myrods.plumbing.MyRodsException;

public class Benchmark {
	static final String CONFIGPATH = "benchmark.ini";
	static String zone, host, username, userzone, password;
	static int port;
	static Hirods session;
	static String testCollection;

	public static void main(String[] args) throws MyRodsException, IOException {
		// setup message tracking
		String[] filter = {"nl.tsmeele.myrods.plumbing.MessageSerializer"};
		Log.setDebugOutputFilter(filter);
		// connect to iRODS (using native protocol)
		Log.setLoglevel(LogLevel.INFO);
		Log.info("Benchmark application -  test message sizes");
		configureServerConnection();
		session = new Hirods(host, port);
		connectAndLogin(IrodsProtocolType.NATIVE_PROT);

		// create initial state for benchmark
		prepareTest();
		session.rcDisconnect();

		// execute benchmark tests
		performTest(IrodsProtocolType.NATIVE_PROT);
		performTest(IrodsProtocolType.XML_PROT429);

		
		// cleanup benchmark data 
		connectAndLogin(IrodsProtocolType.NATIVE_PROT);
		cleanup();
		session.rcDisconnect();
	}
	
	private static void performTest(IrodsProtocolType protocol) throws MyRodsException, IOException {
		Log.info("----- START BENCHMARK TEST " + protocol.name());
		Log.setLoglevel(LogLevel.DEBUG);
		
		Log.info("--- rcConnect followed by native login");
		connectAndLogin(protocol);
		
		Log.info("--- rcObjStat of collection " + testCollection);
		session.rcObjStat(testCollection, ObjType.COLLECTION);
		
		Log.info("--- rcgenQuery: SELECT DATA_NAME, DATA_SIZE WHERE COLL_NAME = '" + testCollection + "'");
		InxIvalPair inxIvalPair = new InxIvalPair();
		InxValPair inxValPair = new InxValPair();
		inxIvalPair.put(Columns.DATA_NAME.getId(), Flag.SELECT_NORMAL);
		inxIvalPair.put(Columns.DATA_SIZE.getId(), Flag.SELECT_NORMAL);
		inxValPair.put(Columns.COLL_NAME.getId(), "= '"+ testCollection + "'");
		int maxRows = 256;
		GenQueryInp genQueryInp = new GenQueryInp(maxRows, 0, 0, 0,
				new KeyValPair(), inxIvalPair , inxValPair);
		boolean done = false;
		while (!done) {
			GenQueryOut genOut = session.rcGenQuery(genQueryInp);
			if (session.error) break;
			// .... could process the returned rows here
			Log.info("received " + genOut.rowCount + " rows");
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
		GenQueryOut genOut = session.rcGenQuery(genQueryInp);
		Log.info("received " + genOut.rowCount + " rows");
		// investigate reason for genOut containing unexpected high amount of data in reply to close query
		// turns out to be produced by a row header which has fixed 50 columns attribute info
//		Iterator<Data> it = genOut.iterator();
//		while (it.hasNext()) {
//			Data d = it.next();
//			System.out.println(d.toString());
//		}
		
		// get data object
		Replica replica = PosixFileFactory.createReplica(session, testCollection + "/test1");
		replica.openRead();
		boolean eof = false;
		int bytesRequested = 1024*1024;
		while (!eof) {
			byte[] bytes = replica.read(bytesRequested);
			if (bytes.length < bytesRequested) eof = true;
		}
		replica.close();
		
		Log.info("--- rcDisconnect");
		session.rcDisconnect();
		
		Log.info("----- END BENCHMARK TEST " + protocol.name());
		Log.setLoglevel(LogLevel.INFO);
	}
	
	
	
	private static void configureServerConnection() {
		ConfigReader configReader = new ConfigReader();
		String[] requiredKeywords = {"host","port","username","zone","password"};
		Map<String,String> config = configReader.readConfig(CONFIGPATH, requiredKeywords);
		host = config.get("host");
		String portStr = config.get("port");
		port = Integer.parseInt(portStr);
		username = config.get("username");
		userzone = config.get("zone");
		zone = userzone;
		password = config.get("password");
		testCollection = "/" + zone + "/home/" + username + "/benchmark";
	}
	
	private static void connectAndLogin(IrodsProtocolType protocol) throws IOException {
		session.rcConnect(protocol, 0, 0, username, userzone, username, userzone, "benchmark", IrodsCsNegType.CS_NEG_DONT_CARE);
		session.nativeLogin(username, userzone, password, username, userzone);
		if (session.error) {
			throw new MyRodsException("Unable to login to iRODS server, error = " + session.intInfo);
		}
	}
	
	private static void prepareTest() throws MyRodsException, IOException {
		Log.info("Preparing benchmark setup");
		CollInp collInp = new CollInp(testCollection);
		session.rcCollCreate(collInp);
		PosixFile local = PosixFileFactory.createLocalFile(CONFIGPATH);	// arbitrary file
		for (int i=0; i<100; i++) {
			String dataObjName = testCollection + "/test" + i;
			Log.debug("Creating " + dataObjName);
			// put file as new data object
			PosixFile replica = PosixFileFactory.createReplica(session, dataObjName);
			DataTransfer tx = new DataTransferSingleThreaded(local, replica);
			tx.transfer();
		}
		Log.info("Preparations done");
	}
	
	private static void cleanup() throws MyRodsException, IOException {
		Log.info("Cleaning benchmark setup");
		KeyValPair condInput = new KeyValPair();
		condInput.put(Kw.RECURSIVE_OPR__KW, null);
		CollInp collInp = new CollInp(testCollection, 0, 0, condInput );
		CollOprStat stat = session.rcRmColl(collInp);
		Log.debug(stat.toString());
		Log.info("Cleanup done");
	}
	

}
