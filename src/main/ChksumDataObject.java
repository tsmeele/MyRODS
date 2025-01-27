package main;

import java.io.IOException;

import nl.tsmeele.myrods.api.RcDataObjChksum;
import nl.tsmeele.myrods.api.RcObjStat;
import nl.tsmeele.myrods.apiDataStructures.DataObjInp;
import nl.tsmeele.myrods.apiDataStructures.KeyValPair;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.apiDataStructures.ObjType;
import nl.tsmeele.myrods.apiDataStructures.RodsObjStat;
import nl.tsmeele.myrods.plumbing.ServerConnection;
import nl.tsmeele.myrods.plumbing.MyRodsException;

/** Demonstrator class to show how a checksum is added to a data object.
 *  Precondition: authenticated user session established
 * @author Ton Smeele
 *
 */
public class ChksumDataObject {
	private ServerConnection irodsSession;

	public ChksumDataObject(ServerConnection irodsSession) {
		this.irodsSession = irodsSession;
	}

	public void execute(String objectPath) throws MyRodsException, IOException {
		RcObjStat rcObjStat = new RcObjStat(objectPath, ObjType.DATAOBJECT);
		Message reply = rcObjStat.sendTo(irodsSession);
		if (reply.getIntInfo() < 0) {
			// errorcode -31000 = DATA OBJECT DOES NOT EXIST
			System.out.println("Data object " + objectPath + " does not exist, unable to continue");
			return;
		} else {
			System.out.println(reply.getMessage());
			RodsObjStat stat = (RodsObjStat) reply.getMessage();
			System.out.println("Status information on '" + objectPath + "' before chksum call");
			System.out.println(stat);
		}

		System.out.println("\nWe send a request to checksum the data object via a call to rcDataObjChksum");
		KeyValPair kvChksum = new KeyValPair();
		kvChksum.put("ChksumAll", "");
		DataObjInp dataObjInp = new DataObjInp(objectPath, kvChksum);
		RcDataObjChksum rcDataObjChksum = new RcDataObjChksum(dataObjInp);
		reply = rcDataObjChksum.sendTo(irodsSession);
		int info = reply.getIntInfo();
		if (info >= 0) {
			System.out.println("Chksum call reports success");
		} else {
			System.out.println("Chksum call reports failure, ierror = " + info );
		}

		System.out.println("\nStatus information on '" + objectPath + "' after chksum call");
		reply = rcObjStat.sendTo(irodsSession);
		if (reply.getIntInfo() < 0) {
			System.out.println("Data object " + objectPath + " does not exist, unable to continue");
			return;
		} else {
			RodsObjStat stat = (RodsObjStat) reply.getMessage();
			System.out.println("Status information on '" + objectPath + "' after chksum call");
			System.out.println(stat);
		}

	}

}