package nl.tsmeele.myrods.high;

import java.io.IOException;
import java.util.Iterator;

import nl.tsmeele.myrods.api.Flag;
import nl.tsmeele.myrods.api.GenQueryInp;
import nl.tsmeele.myrods.api.GenQueryOut;
import nl.tsmeele.myrods.api.Irods;
import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.plumbing.MyRodsException;

public class GenQueryIterator implements Iterator<GenQueryOut> {
	private int maxRows = 256;	// default (and preferred) row set size
	private Irods hirods;
	private GenQueryInp genQueryInp; 
	private GenQueryOut genQueryOut;
	private boolean moreRowSets = true;
	
	public GenQueryIterator(Irods hirods, GenQueryInp genQueryInp) throws MyRodsException, IOException {
		this.hirods = hirods;
		this.genQueryInp = genQueryInp;
		// make sure the query uses a sound row set size
		DataInt genInpMaxRows = (DataInt) genQueryInp.lookupName("maxRows");
		if (genInpMaxRows.get() <= 0) {
			genInpMaxRows.set(maxRows);
		} else {
			maxRows = genInpMaxRows.get();
		}
		// execute query and cache returned first row set
		genQueryOut = hirods.rcGenQuery(genQueryInp);
		if (hirods.error) {
			// problem executing query, we will indicate this by not returning a first row set
			moreRowSets = false;
		}
	}

	@Override
	public boolean hasNext() {
		return moreRowSets;
	}

	@Override
	public GenQueryOut next() {
		if (!moreRowSets) {
			return null;
		}
		// prepare genQueryInp for reading at next set of rows
		DataInt continueInx = (DataInt) genQueryInp.lookupName("continueInx");
		continueInx.set(genQueryOut.continueInx);
	
		if (genQueryOut.rowCount < maxRows) {
			// turns out that we read the last row set, prepare genQueryInp to close query
			moreRowSets = false;
			DataInt genInpMaxRows = (DataInt) genQueryInp.lookupName("maxRows");
			genInpMaxRows.set(0);
		}
		
		// take special action for AUTO_CLOSE
		DataInt queryOptions = (DataInt) genQueryInp.lookupName("options");
		if ((queryOptions.get() & Flag.AUTO_CLOSE) > 0) {
			// AUTO_CLOSE option was requested, hence the server will have closed the query after our first call
			// we return the first row set and flag that this is the last one
			moreRowSets = false;
			return genQueryOut;
		}

		GenQueryOut thisRowSet = genQueryOut;
		// load the next row set in cache OR close query 
		// and return the previously cached row set
		try {
			genQueryOut = hirods.rcGenQuery(genQueryInp);
			if (hirods.error) {
				moreRowSets = false;
			}
		} catch (IOException e) {
			moreRowSets = false;
		}
		return thisRowSet;
	}

	public void closeQuery() {
		if (!moreRowSets) {
			return;
		}
		moreRowSets = false;
		DataInt genInpMaxRows = (DataInt) genQueryInp.lookupName("maxRows");
		genInpMaxRows.set(0);
		try {
			genQueryOut = hirods.rcGenQuery(genQueryInp);
		} catch (IOException e) {
			// ignore, we already have changed moreRowSets to false to indicate closed query.
		}
	}
	
	
}
