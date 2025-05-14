package nl.tsmeele.myrods.high;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import nl.tsmeele.myrods.api.Flag;
import nl.tsmeele.myrods.api.GenQueryInp;
import nl.tsmeele.myrods.api.GenQueryOut;
import nl.tsmeele.myrods.api.Irods;
import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.plumbing.MyRodsException;

public class GenQueryIterator implements Iterator<GenQueryOut> {
	private int maxRows = 256;	// default (and preferred) row set size
	private Irods irods;
	private GenQueryInp genQueryInp; 
	private GenQueryOut genQueryOut;
	private boolean moreRowSets = true;
	
	public GenQueryIterator(Irods irods, GenQueryInp genQueryInp) throws MyRodsException, IOException {
		this.irods = irods;
		this.genQueryInp = genQueryInp;
		// indicate we need a fresh query handle
		DataInt continueInx = (DataInt) this.genQueryInp.lookupName("continueInx");
		continueInx.set(0);
		// ensure the query has a sound row set size
		DataInt genInpMaxRows = (DataInt) genQueryInp.lookupName("maxRows");
		if (genInpMaxRows.get() <= 0) {
			genInpMaxRows.set(maxRows);
		} else {
			maxRows = genInpMaxRows.get();
		}
		// execute query and cache the returned first row set
		genQueryOut = irods.rcGenQuery(genQueryInp);
		if (irods.error) {
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
			throw new NoSuchElementException();
		}
		if (queryHasBeenClosedByServer()) {
			moreRowSets = false;
			// return the last cached row set
			return genQueryOut;
		}
		// there are more row sets to get
		// use the already open query handle for our next request
		DataInt continueInx = (DataInt) genQueryInp.lookupName("continueInx");
		continueInx.set(genQueryOut.continueInx);
		
		// return the currently cached row set, and replenish the cache with a new row set
		GenQueryOut currentRowSet = genQueryOut;
		try {
			genQueryOut = irods.rcGenQuery(genQueryInp);
			if (irods.error) {
				moreRowSets = false;
			} 
		} catch (IOException e) {
			moreRowSets = false;
		}
		return currentRowSet;
	}

	/**
	 * When hasNext() is true (hence more row sets available for processing), 
	 * and the client application wants to stop query processing,  
	 * the query should be closed explicitly by calling closeQuery().
	 */
	public void closeQuery() {
		if (!moreRowSets) {
			return;
		}
		moreRowSets = false;
		if (queryHasBeenClosedByServer()) {
			return;
		}	
		// use the open query handle for our request
		DataInt continueInx = (DataInt) genQueryInp.lookupName("continueInx");
		continueInx.set(genQueryOut.continueInx);
		// ask for closure by requesting zero rows 
		DataInt genInpMaxRows = (DataInt) genQueryInp.lookupName("maxRows");
		genInpMaxRows.set(0);
		try {
			genQueryOut = irods.rcGenQuery(genQueryInp);
		} catch (IOException e) {
			// ignore, we already have changed moreRowSets to false to indicate closed query.
		}
	}
	
	private boolean queryHasBeenClosedByServer() {
		/*
		 * A query will have been closed by the server when:
		 * a) either we have requested for immediate closure using the AUTO_CLOSE flag in our query
		 * b) or we have received less rows than the requested amount
		 */
		DataInt queryOptions = (DataInt) genQueryInp.lookupName("options");
		return (queryOptions.get() & Flag.AUTO_CLOSE) > 0 || genQueryOut.rowCount < maxRows;
	}
	
	
}
