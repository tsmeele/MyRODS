package nl.tsmeele.myrods.plumbing;

import nl.tsmeele.myrods.apiDataStructures.IrodsCsNegType;
import nl.tsmeele.myrods.irodsDataTypes.DataInt;
import nl.tsmeele.myrods.irodsDataTypes.DataString;
import nl.tsmeele.myrods.irodsDataTypes.DataStruct;

/** SessionDetails holds attributes accumulated during an IrodsSession.
 * These properties are populated gradually using data obtained from iRODS api calls, 
 * as the user connects, authenticates, etc.
 * @author Ton Smeele
 *
 */
public class SessionDetails {
	public DataString relVersion = null;
	public DataString apiVersion = null;
	public DataInt reconnPort = null;
	public DataString reconnAddr = null;
	public DataInt cookie = null;
	public IrodsCsNegType serverPolicy = null;
	public DataEncryptConfig dataEncryptConfig = null;
	public String host = null;
	public int port = 0;
	public DataStruct connectMsg = null;
	//public String nativePassword = null;
	
	
	/**
	 * Compares two version strings
	 * @param versionA	version of A
	 * @param versionB	version of B
	 * @return  0 if versions are same, negative if A precedes B, positive otherwise
	 */
	public static int compareVersions(String versionA, String versionB) {
		// returns  pos if versionA > versionB
		String[] aList = versionA.split("[.]");
		String[] bList = versionB.split("[.]");
		int i = 0;
		while (aList.length > i && bList.length > i) {
			int compared;
			try {
				int a = Integer.parseInt(aList[i]);
				int b = Integer.parseInt(bList[i]);
				compared = Integer.compare(a, b);
			} catch (NumberFormatException e) {
				// not all numbers? we compare this part alphanumeric
				compared = aList[i].compareTo(bList[i]);
			}
			if (compared != 0) {
				return compared;
			}
			i++;
		}
		if (aList.length == bList.length) {
			return 0;
		}
		return aList.length < bList.length ? -1 : 1;
	}
	
}


