package nl.tsmeele.myrods.high;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import nl.tsmeele.myrods.api.Irods;
import nl.tsmeele.myrods.plumbing.MyRodsException;

public class IrodsPool {
	private static final long MAX_IDLE_MILLISECONDS = 60L * 1000;  // 1 minute, after which idle session is disconnected
	private static final int MAX_ENTRIES = 42;	// maximum number of active iRods server connections in pool

	public int maxEntries = MAX_ENTRIES;
	private Hirods parent;
	private ArrayList<IrodsPoolEntry> idle = new ArrayList<IrodsPoolEntry>();
	private ArrayList<Hirods> busy = new ArrayList<Hirods>();
	private Timer timer = null;

	public IrodsPool(Hirods parent) {
		this.parent = parent;
	}
	
	public synchronized Hirods allocate() throws MyRodsException, IOException {
		if (!idle.isEmpty()) {
			IrodsPoolEntry entry = idle.remove(0);
			busy.add(entry.irods);
			return entry.irods;
		}
		if (busy.size() > maxEntries) {
			throw new MyRodsException("Irods connection pool exhausted");
		}
		Hirods irods = parent.cloneConnection();
		busy.add(irods);
		return irods;
	}
	
	public synchronized void free(Hirods irods) {
		int i = busy.indexOf(irods);
		if (i >= 0) {
			busy.remove(i);
			IrodsPoolEntry entry = new IrodsPoolEntry(irods);
			idle.add(entry);
			scheduleIdleCleanup(entry, MAX_IDLE_MILLISECONDS);
		}
	}
	
	private synchronized void purgeEntry(IrodsPoolEntry entry)  {
		Irods irods = entry.irods;
		if (!idle.remove(entry)) {
			// we have arrived too late, entry no longer in idle
			return;
		}
		try {
			irods.rcDisconnect();
		}
		// ignore errors resulting from disconnect
		catch (MyRodsException e) {	} 
		catch (IOException e) {}
	}
	
	private void scheduleIdleCleanup(IrodsPoolEntry entry, long elapsed) {
		long now = Instant.now().getEpochSecond();
		TimerTask task = new TimerTask() {
			public void run() {
				long currentTime = Instant.now().getEpochSecond();
				// make sure we don't remove the entry too soon
				if (currentTime - entry.createTimeStamp >= elapsed) {
					purgeEntry(entry);
				}
			}
		};
		if (timer == null) {
			timer = new Timer();
		}
		timer.schedule(task, now + elapsed);
	}
	
	@Override
	public void finalize() {
		// gracefully end any remaining idle connections
		for (IrodsPoolEntry entry : idle) {
			try {
				entry.irods.rcDisconnect();
			} catch (MyRodsException e) {
				// ignore
			} catch (IOException e) {
				// ignore
			}
		}
		if (timer != null) {
			timer.cancel();
		}
	}
}
