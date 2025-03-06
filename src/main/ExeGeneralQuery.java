package main;

import java.io.IOException;

import nl.tsmeele.myrods.api.Columns;
import nl.tsmeele.myrods.api.GenQueryInp;
import nl.tsmeele.myrods.api.GenQueryOut;
import nl.tsmeele.myrods.api.InxIvalPair;
import nl.tsmeele.myrods.api.InxValPair;
import nl.tsmeele.myrods.api.Irods;
import nl.tsmeele.myrods.api.KeyValPair;
import nl.tsmeele.myrods.api.Message;
import nl.tsmeele.myrods.irodsStructures.RcGenQuery;
import nl.tsmeele.myrods.plumbing.ServerConnection;
import nl.tsmeele.myrods.plumbing.MyRodsException;

/** Demonstrator class to show General Query use.
 *  Preconditions: authenticated iRODS session
 * @author Ton Smeele
 *
 */
public class ExeGeneralQuery {
	private Irods irodsSession;

	public ExeGeneralQuery(Irods irodsSession) {
		this.irodsSession = irodsSession;
	}

	public void execute(String collectionPath, String dataObjectPath) throws MyRodsException, IOException {

		System.out.println("We will execute the following query:\n" +
				"SELECT DATA_NAME, DATA_ID WHERE COLL_NAME = '" + collectionPath + "' and DATA_NAME like '" +
				dataObjectPath + "'");
		int maxRows = 256;
		int continueInx = 0;
		int partialStartIndex = 0;
		int queryOptions = 0;

		// provide columns to be returned  (SELECT clause)
		// value 1 = regular column, no sum or other operators
		InxIvalPair inxIvalPair = new InxIvalPair();
		inxIvalPair.put(Columns.DATA_ID.getId(), 1);	
		inxIvalPair.put(Columns.DATA_NAME.getId(), 1);	
		// provide preconditions (WHERE clause)
		InxValPair inxValPair = new InxValPair();
		inxValPair.put(Columns.COLL_NAME.getId(), "= '" + collectionPath + "'");
		inxValPair.put(Columns.DATA_NAME.getId(), "like '" + dataObjectPath + "'");

		// execute the query
		GenQueryInp genQueryInp = new GenQueryInp(maxRows, continueInx, partialStartIndex,
				queryOptions, new KeyValPair(), inxIvalPair , inxValPair);
		GenQueryOut genOut = irodsSession.rcGenQuery(genQueryInp);
		if (irodsSession.error) {
			System.out.println("Query failed, ierror = " + irodsSession.intInfo);
			return;
		}
		System.out.println("query succesful!");
		System.out.println(genOut);
	}	
}
