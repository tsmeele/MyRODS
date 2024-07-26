package main;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

import nl.tsmeele.log.Log;
import nl.tsmeele.myrods.api.RcDataObjUnlink;
import nl.tsmeele.myrods.api.RcModAVUMetadata;
import nl.tsmeele.myrods.api.RcModAccessControl;
import nl.tsmeele.myrods.apiDataStructures.DataObjInp;
import nl.tsmeele.myrods.apiDataStructures.KeyValPair;
import nl.tsmeele.myrods.apiDataStructures.Kw;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.apiDataStructures.ModAVUMetadataInp;
import nl.tsmeele.myrods.apiDataStructures.ModAccessControlInp;
import nl.tsmeele.myrods.high.Session;
import nl.tsmeele.myrods.plumbing.IrodsSession;
import nl.tsmeele.myrods.plumbing.MyRodsException;

/**
 * @author Ton Smeele
 *
 */
public class AccScienceTest {
	private String host;
	private int port;
	private String username;
	private String userzone;
	private String password;
	
	public AccScienceTest(String configPath) {
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
		Session session = new Session();
		if (session.pamLogin(host, port, username, userzone, password)) {
			System.out.println("Pam auth successful");
		} else {
			System.out.println("Could not login");
			System.exit(1);
		}
		IrodsSession irodsSession = session.getIrodsSession();
		System.out.println("is connected = " + irodsSession.isConnected());
		System.out.println("is ssl = " + irodsSession.isSsl());
		System.out.println("protocol = " + irodsSession.getProtocol().name());
				
		
		String existingObj = "/nluu6a/home/research-ton-test/mybook1.docx";
		String homeColl = "/nluu6a/home/research-ton-test";
		
		// show execution of a rule
		System.out.println("\n\nDEMO: Execution of a Rule\n");
		ExeRule rule = new ExeRule(irodsSession);
		rule.execute();
		
		// show execution of a general query
		System.out.println("\n\nDEMO: Execution of a General Query\n");
		ExeGeneralQuery query = new ExeGeneralQuery(irodsSession);
		query.execute(homeColl, "%");
		
		// show get file
		System.out.println("\n\nDEMO: Execution of a data object Get data transfer\n");
		GetOrPutFile tx = new GetOrPutFile(session);
		tx.setThreads(8);
		Log.timerStart();
		tx.get(existingObj, "/tmp/boek-4-jul-2024.docx");
		Log.timerRead("GET transfer done");
		System.out.println("Transfer was performed with " + tx.threadsUsed() + " threads");

		// show put file
		System.out.println("\n\nDEMO: Execution of a data object Put data transfer\n");
		Long epoch = Instant.now().getEpochSecond();
		String newDataObj = existingObj + epoch.toString() + ".docx";
		Log.timerStart();
		tx.put("/tmp/boek-4-jul-2024.docx", newDataObj);
		Log.timerRead("PUT transfer done");
		System.out.println("Transfer was performed with " + tx.threadsUsed() + " threads");

		
		// checksum a data object
		System.out.println("\n\nDEMO: Execution of a data object chksum\n");
		ChksumDataObject chksum = new ChksumDataObject(irodsSession);
		chksum.execute(newDataObj);
		
		// and remove the newly added data object
		DataObjInp dataObjInp = new DataObjInp(newDataObj, null);
		RcDataObjUnlink rcDataObjUnlink = new RcDataObjUnlink(dataObjInp);
		rcDataObjUnlink.sendTo(irodsSession);
		
		// mod access
		// "modAccessControlInp_PI", "int recursiveFlag; str *accessLevel; str *userName; str *zone; str *path;",

		System.out.println("\n\nDEMO ACCESS CONTROL\n");
		String targetUser = "a.p.m.smeele@uu.nl";
		String targetAcl = Kw.ACCESS_READ_OBJECT;
		String object = existingObj;
		ModAccessControlInp access = new ModAccessControlInp(0, targetAcl, targetUser, "nluu6a", object);
		RcModAccessControl rcModAccessControl = new RcModAccessControl(access);
		Message aclReply = rcModAccessControl.sendTo(irodsSession);
		if (aclReply.getIntInfo() >= 0) {
			System.out.println("Access for user " + targetUser + " on object " + object + " set to " + targetAcl);
		} else {
			System.out.println("Failed to set ACL for user " + targetUser + " on " + object + " ierror = " + aclReply.getIntInfo());
		}
		
		// add metadata
		System.out.println("\n\nDEMO METADATA\n");
		String metaN = "mykey";
		String metaV = "myvalue";
		String metaU = "myunit";
		String metaAVU = metaN + ":" + metaV + ":" + metaU;
		ArrayList<String> args = new ArrayList<String>();
		args.add("set");
		args.add("-D");
		args.add(existingObj);
		args.add(metaN);
		args.add(metaV);
		args.add(metaU);
		KeyValPair kv = new KeyValPair();
		ModAVUMetadataInp modAVUMetaDataInp = new ModAVUMetadataInp(args, kv);
		RcModAVUMetadata rcModAVUMetadata = new RcModAVUMetadata(modAVUMetaDataInp);
		Message metaReply = rcModAVUMetadata.sendTo(irodsSession);
		if (metaReply.getIntInfo() >= 0) {
			System.out.println("Metadata 'set' on object " + object + " AVU = " + metaAVU);
		} else {
			System.out.println("Failed to 'set' metadata on object " + object + " ierror = " + metaReply.getIntInfo());
		}
		// we're all done
		session.disconnect();
		System.exit(0);
	}
}
