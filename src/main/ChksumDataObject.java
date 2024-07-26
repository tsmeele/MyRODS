package main;

import java.io.IOException;

import nl.tsmeele.myrods.api.RcDataObjChksum;
import nl.tsmeele.myrods.api.RcObjStat;
import nl.tsmeele.myrods.apiDataStructures.DataObjInp;
import nl.tsmeele.myrods.apiDataStructures.KeyValPair;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.apiDataStructures.ObjType;
import nl.tsmeele.myrods.apiDataStructures.RodsObjStatOut;
import nl.tsmeele.myrods.plumbing.IrodsSession;
import nl.tsmeele.myrods.plumbing.MyRodsException;

/** Demonstrator class to show how a checksum is added to a data object.
 *  Precondition: authenticated user session established
 * @author Ton Smeele
 *
 */
public class ChksumDataObject {
	private IrodsSession irodsSession;

	public ChksumDataObject(IrodsSession irodsSession) {
		this.irodsSession = irodsSession;
	}
	
	public void execute(String objectPath) throws MyRodsException, IOException {
		
	RcObjStat rcObjStat = new RcObjStat(objectPath, ObjType.DATAOBJECT);
	RodsObjStatOut stat = new RodsObjStatOut( rcObjStat.sendTo(irodsSession) );
	System.out.println("Status information on '" + objectPath + "' before chksum call");
	System.out.println(stat);
	if (!stat.objectExists()) {
		System.out.println("Data object " + objectPath + " does not exist, unable to continue");
		return;
	}
	
	System.out.println("\nWe send a request to checksum the data object via a call to rcDataObjChksum");
	KeyValPair kvChksum = new KeyValPair();
	kvChksum.put("ChksumAll", "");
	DataObjInp dataObjInp = new DataObjInp(objectPath, kvChksum);
	RcDataObjChksum rcDataObjChksum = new RcDataObjChksum(dataObjInp);
	Message reply = rcDataObjChksum.sendTo(irodsSession);
	int info = reply.getIntInfo();
	if (info >= 0) {
		System.out.println("Chksum call reports success");
	} else {
		System.out.println("Chksum call reports failure, ierror = " + info );
	}
	
	System.out.println("\nStatus information on '" + objectPath + "' after chksum call");
	rcObjStat = new RcObjStat(objectPath);
	stat = new RodsObjStatOut( rcObjStat.sendTo(irodsSession) );
	System.out.println(stat);
	}

}