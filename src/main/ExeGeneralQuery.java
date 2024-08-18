package main;

import java.io.IOException;

import nl.tsmeele.myrods.api.RcGenQuery;
import nl.tsmeele.myrods.apiDataStructures.Columns;
import nl.tsmeele.myrods.apiDataStructures.GenQueryInp;
import nl.tsmeele.myrods.apiDataStructures.GenQueryOut;
import nl.tsmeele.myrods.apiDataStructures.InxIvalPair;
import nl.tsmeele.myrods.apiDataStructures.InxValPair;
import nl.tsmeele.myrods.apiDataStructures.KeyValPair;
import nl.tsmeele.myrods.apiDataStructures.Message;
import nl.tsmeele.myrods.plumbing.IrodsSession;
import nl.tsmeele.myrods.plumbing.MyRodsException;

/** Demonstrator class to show General Query use.
 *  Preconditions: authenticated iRODS session
 * @author Ton Smeele
 *
 */
public class ExeGeneralQuery {
	private IrodsSession irodsSession;

	public ExeGeneralQuery(IrodsSession irodsSession) {
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
		RcGenQuery rcGenQuery = new RcGenQuery(genQueryInp);
		Message reply = rcGenQuery.sendTo(irodsSession);
		if (reply.getIntInfo() < 0) {
			System.out.println("Query failed, ierror = " + reply.getIntInfo());
			return;
		}
		GenQueryOut genOut = (GenQueryOut) reply.getMessage();
		System.out.println("query succesful!");
		System.out.println(genOut);
	}	
}
