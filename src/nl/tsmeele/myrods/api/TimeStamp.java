package nl.tsmeele.myrods.api;

import java.time.Instant;
import java.time.ZoneId;

import nl.tsmeele.myrods.irodsStructures.DataString;

public class TimeStamp {
	public long epochSecond = 0L;	// seconds since epoch

	public TimeStamp(long epoch) {
		this.epochSecond = epoch;
	}
	
	public TimeStamp(DataString epochString) {
		try {
			epochSecond = Long.parseLong(epochString.get());
		} catch (Exception e) {
		epochSecond = 0L;
		}
	}
	
	public long get() {
		return epochSecond;
	}
	
	public String utc() {
		return Instant.ofEpochSecond(epochSecond).toString();
	}
	
	public String localTime() {
		ZoneId zoneId = ZoneId.systemDefault();
		return Instant.ofEpochSecond(epochSecond).atZone(zoneId).toString();
	}
	
	public String toString() {
		return localTime();
	}
	

}
