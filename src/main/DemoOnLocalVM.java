package main;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

import nl.tsmeele.log.Log;
import nl.tsmeele.myrods.apiDataStructures.DataObjInp;
import nl.tsmeele.myrods.apiDataStructures.KeyValPair;
import nl.tsmeele.myrods.apiDataStructures.Kw;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.apiDataStructures.ModAVUMetadataInp;
import nl.tsmeele.myrods.apiDataStructures.ModAccessControlInp;
import nl.tsmeele.myrods.high.Hirods;
import nl.tsmeele.myrods.irodsDataTypes.RcDataObjUnlink;
import nl.tsmeele.myrods.irodsDataTypes.RcModAVUMetadata;
import nl.tsmeele.myrods.irodsDataTypes.RcModAccessControl;
import nl.tsmeele.myrods.plumbing.ServerConnection;
import nl.tsmeele.myrods.plumbing.MyRodsException;

/**
 * @author Ton Smeele
 *
 */
public class DemoOnLocalVM {
	private String host;
	private int port;
	private String username;
	private String userzone;
	private String password;
	
	public DemoOnLocalVM(String configPath) {
		// read config parms from file
		ConfigReader configReader = new ConfigReader();
		String[] requiredKeywords = {"host","port","username","zone","password"};
		Map<String,String> config = configReader.readConfig(configPath, requiredKeywords);
		host = config.get("host");
		String portStr = config.get("port");
		port = Integer.parseInt(portStr);
		username = config.get("username");
		userzone = config.get("zone");
		password = config.get("password");
	}
	
	public void execute() throws MyRodsException, IOException {
		System.out.println("ANONYMOUS CONNECTION PART - START OF DEMO\n");

		// show server report
		System.out.println("DEMO: Obtain server attribute information\n");
		ServerReport report = new ServerReport();
		System.out.println(report.execute(host, port));
		
		System.out.println("\n\nAUTHENTICATED USER PART - START OF DEMO\n");
		
		// Establish connection with server and authenticate
		System.out.println("Connect and login:\n");
		Hirods session = new Hirods(host, port);
		if (session.nativeLogin(username, userzone, password, username, userzone)) {
			System.out.println("Native auth successful");
		} else {
			System.out.println("Could not login");
			System.exit(1);
		}
		System.out.println("is connected = " + session.isConnected());
		System.out.println("is ssl = " + session.isSsl());
		System.out.println("protocol = " + session.getProtocol().name());
		
		
		// show execution of a rule
		System.out.println("\n\nDEMO: Execution of a Rule\n");
		ExeRule rule = new ExeRule(session);
		rule.execute();
		
		// show execution of a general query
		System.out.println("\n\nDEMO: Execution of a General Query\n");
		ExeGeneralQuery query = new ExeGeneralQuery(session);
		query.execute("/tempZone/home/ton", "%");
		
		// show get file
		System.out.println("\n\nDEMO: Execution of a data object Get data transfer\n");
		GetOrPutFile tx = new GetOrPutFile(session);
		tx.setThreads(0); // 0 = multithreaded
		Log.timerStart();
		tx.get("/tempZone/home/ton/mybook1.docx", "/tmp/boek-4-jul-2024.docx");
		Log.timerRead("GET transfer done");
		System.out.println("Transfer was performed with " + tx.threadsUsed() + " threads");

		// show put file
		System.out.println("\n\nDEMO: Execution of a data object Put data transfer\n");
		Long epoch = Instant.now().getEpochSecond();
		String newDataObj = "/tempZone/home/ton/mybook" + epoch.toString() + ".docx";
		Log.timerStart();
		tx.put("/tmp/boek-4-jul-2024.docx", newDataObj);
		Log.timerRead("PUT transfer done");
		System.out.println("Transfer was performed with " + tx.threadsUsed() + " threads");

//		// checksum a data object
//		System.out.println("\n\nDEMO: Execution of a data object chksum\n");
//		ChksumDataObject chksum = new ChksumDataObject(irodsSession);
//		chksum.execute(newDataObj);
//		
		// and remove the newly added data object
		boolean remove = true;
		if (remove) {
			DataObjInp dataObjInp = new DataObjInp(newDataObj, null);
			session.rcDataObjUnlink(dataObjInp);
		}
		
		// mod access
		// "modAccessControlInp_PI", "int recursiveFlag; str *accessLevel; str *userName; str *zone; str *path;",

		System.out.println("\n\nDEMO ACCESS CONTROL\n");
		String targetUser = "bob";
		String targetAcl = Kw.ACCESS_CREATE_METADATA;
		String object = "/tempZone/home/ton/mybook1.docx";
		ModAccessControlInp access = new ModAccessControlInp(0, targetAcl, targetUser, "tempZone", object);
		session.rcModAccessControl(access);
		if (session.error) {
			System.out.println("Failed to set ACL for user " + targetUser + " on " + object + " ierror = " + session.intInfo);
		} else {
			System.out.println("Access for user " + targetUser + " on object " + object + " set to " + targetAcl);
		}
		
		// add metadata
		System.out.println("\n\nDEMO METADATA\n");
		String metaN = "mykey";
		String metaV = "myvalue";
		String metaU = "myunit";
		String metaAVU = metaN + ":" + metaV + ":" + metaU;
		
		ArrayList<String> args = new ArrayList<String>();
		args.add("set");
		args.add("-d");
		args.add("/tempZone/home/ton/mybook1.docx");
		args.add(metaN);
		args.add(metaV);
		args.add(metaU);
		KeyValPair kv = new KeyValPair();
		ModAVUMetadataInp modAVUMetaDataInp = new ModAVUMetadataInp(args, kv);
		session.rcModAVUMetadata(modAVUMetaDataInp);
		if (session.error) {
			System.out.println("Failed to 'set' metadata on object " + object + " ierror = " + session.intInfo);
		} else {
			System.out.println("Metadata 'set' on object " + object + " AVU = " + metaAVU);
		}
		// we're all done
		session.rcDisconnect();
		System.exit(0);
	}
}
