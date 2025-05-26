package nl.tsmeele.myrods.api;

/** Class to hold definitions for various int type literal values.
 * @author Ton Smeele
 */
public class Flag {
	
	// openFlag
	// defined in Linux e.g. /usr/include/x86_64-linux-gnu/bits/fcntl-linux.h
	public static final int O_CREAT = 0100;
	public static final int O_RDONLY = 00;
	public static final int O_WRONLY = 01;
	public static final int O_RDWR = 02;
	public static final int O_TRUNC = 01000;
	public static final int O_APPEND = 02000;
	public static final int O_EXCL = 0200;
	
	// whence
	// defined in Linux e.g. /usr/include/fcntl.h
	public static final int SEEK_SET = 0;		// seek to start file + offset
	public static final int SEEK_CUR = 1;		// seek to current position + offset
	public static final int SEEK_END = 2;		// seek to end position + offset
	
	// options for use with general query
	// defined in lib/core/include/irods/rodsGenQuery.h
	public static final int RETURN_TOTAL_ROW_COUNT = 0x20;
	public static final int NO_DISTINCT = 0x40;
	public static final int QUOTA_QUERY = 0x80;
	public static final int AUTO_CLOSE = 0x100;
	public static final int UPPER_CASE_WHERE = 0x200;
	// bits to set in the value array
	//  values 0 and 1 will just return the column content
	public static final int SELECT_NORMAL = 1;
	// defined in lib/core/include/irods/rodsGenQuery.h
	public static final int SELECT_MIN = 2;
	public static final int SELECT_MAX = 3;
	public static final int SELECT_SUM = 4;
	public static final int SELECT_AVG = 5;
	public static final int SELECT_COUNT = 6;
	public static final int ORDER_BY = 0x400;
	public static final int ORDER_BY_DESC = 0x800;
}
