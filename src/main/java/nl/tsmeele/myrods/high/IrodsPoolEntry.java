package nl.tsmeele.myrods.high;

import java.time.Instant;


public class IrodsPoolEntry {
	public Hirods irods = null;
	public Long createTimeStamp;
	
	public IrodsPoolEntry(Hirods irods) {
		this.irods = irods;
		createTimeStamp = Instant.now().getEpochSecond();
	}
	

}
