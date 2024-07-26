package nl.tsmeele.myrods.apiDataStructures;

import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataInt64;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

public class OpenedDataObjInp extends DataStruct {
	// "OpenedDataObjInp_PI", "int l1descInx; int len; int whence; int oprType; 
	//  double offset; double bytesWritten; struct KeyValPair_PI;",

	public OpenedDataObjInp(Integer l1descInx, Integer len, Integer whence, OprType oprType,
			Long offset, Long bytesWritten, KeyValPair keyValPair) {
		
		super("OpenedDataObjInp_PI");
		add(new DataInt("l1descInx", l1descInx));	// iRODS file descriptor ptr
		add(new DataInt("len", len));				// requested number of bytes to read/write
													// can never read/write more than MAX_INT bytes
		add(new DataInt("whence", whence));			// will be populated by server (e.g. SEEK_CUR)
		add(new DataInt("oprType", (oprType == null ? 0 : oprType.getId()) )  );
		add(new DataInt64("offset", offset));		// requested starting lseek point in replica data file
		add(new DataInt64("bytesWritten", bytesWritten));
		add(keyValPair);							// options to influence open action
	}

}
