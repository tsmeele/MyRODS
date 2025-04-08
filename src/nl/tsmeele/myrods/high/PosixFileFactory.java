package nl.tsmeele.myrods.high;

import nl.tsmeele.myrods.plumbing.MyRodsException;

public class PosixFileFactory {
	
	public static LocalFile createLocalFile(String path) {
		LocalFile local = new LocalFile();
		local.setPath(path);
		return local;
	}
	
	public static Replica createReplica(Hirods hirods, String logicalPath, String resource) throws MyRodsException {
		if (!hirods.isAuthenticated()) {
			throw new MyRodsException("Unable to reference replica, please authenticate first");
		}
		Replica replica = new Replica();
		replica.setReplica(hirods, logicalPath, resource);
		return replica;
	}
	
	public static Replica createReplica(Hirods hirods, String logicalPath, int replicaNumber) throws MyRodsException {
		if (!hirods.isAuthenticated()) {
			throw new MyRodsException("Unable to reference replica, please authenticate first");
		}
		Replica replica = new Replica();
		replica.setReplica(hirods, logicalPath, replicaNumber);
		return replica;
	}

	public static Replica createReplica(Hirods hirods, String logicalPath) throws MyRodsException {
		if (!hirods.isAuthenticated()) {
			throw new MyRodsException("Unable to reference replica, please authenticate first");
		}
		Replica replica = new Replica();
		replica.setReplica(hirods, logicalPath, null);
		return replica;
	}
}
