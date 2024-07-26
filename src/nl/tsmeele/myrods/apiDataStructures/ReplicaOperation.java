package nl.tsmeele.myrods.apiDataStructures;

public enum ReplicaOperation {
	CREATE("CREATE"),
	WRITE_APPEND("WRITE"),		// seek ignored, will append at end-of-file
	WRITE_TRUNCATE("WRITE"),	// can seek to a position (but shouldnt)
	OPEN("OPEN"),	// for read/write,  need to seek to position
	UNLINK("UNLINK");

	private String label;
	
	private ReplicaOperation(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
