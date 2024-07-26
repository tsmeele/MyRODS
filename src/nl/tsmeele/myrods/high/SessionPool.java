package nl.tsmeele.myrods.high;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import nl.tsmeele.myrods.plumbing.MyRodsException;

/**
 * SessionPool manages the auxiliary sessions that a user may need to perform concurrent interactions 
 * with an iRODS server, e.g. multi-threaded transfers.
 * Idle sessions can be reused which saves time needed to establish a connection and o authenticate.
 * @author Ton Smeele
 *
 */
public class SessionPool {
	private static final long MAX_IDLE_MILLISECONDS = 60L * 1000;  // 1 minute, after which idle session is disconnected
	private boolean lock = false;
	private Session mainSession;
	private int maxConnections = 42;	// arbitrary default value, just seemed to be a proper number
	private ArrayList<Session> available = new ArrayList<Session>();
	private ArrayList<Session> allocated = new ArrayList<Session>();
	private Timer timer = null;

	public SessionPool(Session mainSession) {
		this.mainSession = mainSession;
	}
	
	public int getMaxConnections() {
		return maxConnections;
	}
	
	public int getActiveConnectionsCount() {
		return allocated.size();
	}
	
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}
	
	public Session getMainSession() {
		return mainSession;
	}
	
	public void stopPool() {
		lock();
		maxConnections = 0;
		// disconnect all sessions
		for (Session s : available) {
			s.disconnect();
		}
		for (Session s: allocated) {
			s.disconnect();
		}
		if (timer != null) {
			timer.cancel();
		}
		unlock();
	}

	
	public Session allocate() throws MyRodsException {
		if (!available.isEmpty()) {
			// reuse an available connection
			lock();
			Session s = available.remove(0);
			allocated.add(s);
			s.resetMarkTimeStamp();	// indicate that this session is no longer idle
			unlock();
			return s;
		}
		if (allocated.size() < maxConnections) {
			// add a new session to the pool
			Session s = mainSession.clone();
			if (s != null) {
				lock();
				allocated.add(s);
				unlock();
				return s;
			} 
		}
		throw new MyRodsException("Failed to open a secondary iRODS connection");
	}
	
	public void free(Session s) {
		lock();
		if (allocated.remove(s)) {
			available.add(s);
			cleanupAfter(s, MAX_IDLE_MILLISECONDS);
		}
		unlock();
	}
	
	private void cleanupAfter(Session s, long maxIdle) {;
		TimerTask task = new TimerTask() {
			public void run() {
				lock();
				// only cleanup if task has been idle for at least the maximum idle time
				if (s.secondsSinceMarkTimeStamp() != null && 
					s.secondsSinceMarkTimeStamp() * 1000 >= maxIdle &&
					available.contains(s)) {
					// it is time to cleanup this session
					s.disconnect();
					available.remove(s);
				} else {
				}
				unlock();
			}	
		};
		s.setMarkTimeStamp();	// set idle indicator
		// schedule a session cleanup task 
		if (timer == null) {
			timer = new Timer();
		}
		timer.schedule(task, maxIdle);
	}
	
	private synchronized void lock() {
		while (lock) {
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
			}
		}
		lock = true;
	}
	
	private synchronized void unlock() {
		lock = false;
	}
	
}
