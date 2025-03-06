package nl.tsmeele.myrods.high;

import java.time.Instant;

import nl.tsmeele.myrods.apiDataStructures.Irods;

public class IrodsPoolEntry {
	public Irods irods = null;
	public Long createTimeStamp;
	
	public IrodsPoolEntry(Irods irods) {
		this.irods = irods;
		createTimeStamp = Instant.now().getEpochSecond();
	}
	

}
