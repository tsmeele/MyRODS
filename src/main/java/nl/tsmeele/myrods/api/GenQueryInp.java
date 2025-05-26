package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class GenQueryInp extends DataStruct {
	// 	"GenQueryInp_PI", "int maxRows; int continueInx; int partialStartIndex; int options; 
	//              struct KeyValPair_PI; struct InxIvalPair_PI; struct InxValPair_PI;",

	//  maxRows:     max number of rows to return, if 0 close out the SQL statement call 
	//                 (i.e. instead of getting more rows until it is finished)
	//  continueInx: if non-zero, this is the value returned in the genQueryOut 
	//                 structure and the current call is to get more rows.
	//  rowOffset:    if positive, return rows starting with
    //                this index (skip earlier ones), 0-origin 
	//  options:      Bits for special options, currently:
    //    If RETURN_TOTAL_ROW_COUNT is set, the total
	//    number of available rows will be returned
	//    in totalRowCount (causes a little overhead
	//    so only request it if needed).  If rowOffset
	//    is also used, totalRowCount will include
	//    the skipped rows.
	//    If NO_DISTINCT is set, the normal 'distinct'
	//    keyword is not included in the SQL query.
	//    If QUOTA_QUERY is set, do the special quota
	//    query.
	//    If AUTO_CLOSE is set, close out the statement
	//    even if more rows are available.  -1 is
	//    returned as the continueInx if there were
	//    (possibly) additional rows available.
	//    If UPPER_CASE_WHERE is set, make the 'where'
	//    columns upper case.
		
	//  keyValPair  = general options for genQuery
	//                e.g. "zone" -> <zoneName>  to specify a remote zone
	
	//  inxIvalPair = specifies columns to select (key is column id, value is type of select
	// 						e.g. type is normal (0), SELECT_AVG (5), etc
	
	//  inxValPair  = specifies WHERE conditions, the key is the column id, the value is
	//                      a sql string that states the right-hand part of the condition
	//                      e.g. "= '/tempZone/home/rods/x'"

	//  NB: see class Flag for a specification of the int value of the above options.

	
	public GenQueryInp(int maxRows, int continueInx, int partialStartIndex, int options,
			KeyValPair keyValPair, InxIvalPair inxIvalPair, InxValPair inxValPair) {
		super("GenQueryInp_PI");
		init(maxRows, continueInx, partialStartIndex, options, keyValPair, inxIvalPair, inxValPair);
	}
	
	public GenQueryInp(KeyValPair keyValPair, InxIvalPair inxIvalPair, InxValPair inxValPair) {
		super("GenQueryInp_PI");
		init(256, 0, 0, 0, keyValPair, inxIvalPair, inxValPair);
	}
	
	// e.g. to fetch a new set of rows
	public void setContinueInx(int newContinueInx) {
		DataInt continueInx = (DataInt) lookupName("continueInx");
		continueInx.set(newContinueInx);
	}
	
	// e.g. to close a query (set maxRows to a value <= 0)
	public void setMaxRows(int newMaxRows) {
		DataInt maxRows = (DataInt) lookupName("maxRows");
		maxRows.set(newMaxRows);
	}
	
	private void init(int maxRows, int continueInx, int partialStartIndex, int options,
			KeyValPair keyValPair, InxIvalPair inxIvalPair, InxValPair inxValPair) {
		add(new DataInt("maxRows", maxRows));
		add(new DataInt("continueInx", continueInx));
		add(new DataInt("partialStartIndex", partialStartIndex));
		add(new DataInt("options", options));
		keyValPair = keyValPair == null ? new KeyValPair() : keyValPair;
		inxIvalPair = inxIvalPair == null ? new InxIvalPair() : inxIvalPair;
		inxValPair = inxValPair == null ? new InxValPair() : inxValPair;
		add(keyValPair);
		add(inxIvalPair);
		add(inxValPair);
	}

}
