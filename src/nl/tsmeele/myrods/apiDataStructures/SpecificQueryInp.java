package nl.tsmeele.myrods.apiDataStructures;

import java.util.List;

import nl.tsmeele.myrods.irodsStructures.DataInt;
import nl.tsmeele.myrods.irodsStructures.DataString;
import nl.tsmeele.myrods.irodsStructures.DataStruct;

public class SpecificQueryInp extends DataStruct {
	//	"specificQueryInp_PI", "str *sql; str *arg1; str *arg2; str *arg3; str *arg4; 
	//      str *arg5; str *arg6; str *arg7; str *arg8; str *arg9; str *arg10; 
	//      int maxRows; int continueInx; int rowOffset; int options; struct KeyValPair_PI;"
	
	//  maxRows:     max number of rows to return, if 0 close out the SQL statement call 
	//                 (i.e. instead of getting more rows until it is finished)
	//  continueInx: if non-zero, this is the value returned in the genQueryOut 
	//                 structure and the current call is to get more rows.
	//  rowOffset:   currently unused
	//  options:     currently unused


	public SpecificQueryInp(String sql, List<String> args, int maxRows, 
			int continueInx, KeyValPair keyValPair) {
		super("SpecificQueryInp_PI");
		init(sql, args, maxRows, continueInx, 0, 0, keyValPair);	
	}
	
	private void init(String sql, List<String> args, int maxRows, int continueInx, 
			int rowOffset, int options, KeyValPair keyValPair) {
		add(new DataString("sql", sql));
		// rcSpecificQuery allows for max 10 args, we have to ignore any further args
		for (int i = 0; i < Math.min(10, args.size()); i++) {
			add(new DataString("arg" + String.valueOf(i + 1), args.get(i)) );
		}
		add(new DataInt("maxRows", maxRows));
		add(new DataInt("continueInx", continueInx));
		add(new DataInt("rowOffset", rowOffset));
		add(new DataInt("options", options));
		add(keyValPair);
	}
	
}
