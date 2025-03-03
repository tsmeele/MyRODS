package main;

import java.io.IOException;

import nl.tsmeele.myrods.api.Irods;
import nl.tsmeele.myrods.apiDataStructures.DataObjInp;
import nl.tsmeele.myrods.apiDataStructures.KeyValPair;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.apiDataStructures.ObjType;
import nl.tsmeele.myrods.apiDataStructures.RodsObjStat;
import nl.tsmeele.myrods.irodsDataTypes.RcDataObjChksum;
import nl.tsmeele.myrods.irodsDataTypes.RcObjStat;
import nl.tsmeele.myrods.plumbing.ServerConnection;
import nl.tsmeele.myrods.plumbing.MyRodsException;

/** Demonstrator class to show how a checksum is added to a data object.
 *  Precondition: authenticated user session established
 * @author Ton Smeele
 *
 */
public class ChksumDataObject {
	private Irods irods;

	public ChksumDataObject(Irods irods) {
		this.irods = irods;
	}

	public void execute(String objectPath) throws MyRodsException, IOException {
		RodsObjStat rodsObjStat = irods.rcObjStat(objectPath, ObjType.DATAOBJECT);
		if (irods.error) {
			// errorcode -31000 = DATA OBJECT DOES NOT EXIST
			System.out.println("Data object " + objectPath + " does not exist, unable to continue. error = " + irods.returnCode);
			return;
		} else {
			System.out.println("Status information on '" + objectPath + "' before chksum call");
			System.out.println(rodsObjStat);
		}

		System.out.println("\nWe send a request to checksum the data object via a call to rcDataObjChksum");
		KeyValPair kvChksum = new KeyValPair();
		kvChksum.put("ChksumAll", "");
		DataObjInp dataObjInp = new DataObjInp(objectPath, kvChksum);
		String checksum = irods.rcDataObjChksum(dataObjInp);
		if (irods.error) {
			System.out.println("Chksum call reports failure, ierror = " + irods.returnCode );
		} else {
			System.out.println("Chksum call reports success. Checksum is: " + checksum);
		}

		System.out.println("\nStatus information on '" + objectPath + "' after chksum call");
		rodsObjStat = irods.rcObjStat(objectPath, ObjType.DATAOBJECT);
		if (irods.error) {
			// errorcode -31000 = DATA OBJECT DOES NOT EXIST
			System.out.println("Data object " + objectPath + " does not exist, unable to continue");
			return;
		} else {
			System.out.println(rodsObjStat);
		}

	}

}