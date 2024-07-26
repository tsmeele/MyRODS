package nl.tsmeele.myrods.apiDataStructures;

// used for dataObjInp, portalOpr, l1desc
public enum OprType {
	NULL(0),	// not listed as an iRODS keyword, added here for convenience reasons
	DONE_OPR(9999),
	PUT_OPR(1),
	GET_OPR(2),
	SAME_HOST_COPY_OPR(3),
	COPY_TO_LOCAL_OPR(4),
	COPY_TO_REM_OPR(5),
	REPLICATE_OPR(6),
	REPLICATE_DEST(7),
	REPLICATE_SRC(8),
	COPY_DEST(9),
	COPY_SRC(10),
	RENAME_DATA_OBJ(11),
	RENAME_COLL(12),
	MOVE_OPR(13),
	RSYNC_OPR(14),
	PHYMV_OPR(15),
	PHYMV_SRC(16),
	PHYMV_DEST(17),
	QUERY_DATA_OBJ(18),
	QUERY_DATA_OBJ_RECUR(19),
	QUERY_COLL_OBJ(20),
	QUERY_COLL_OBJ_RECUR(21),
	RENAME_UNKNOWN_TYPE(22),
	REMOTE_ZONE_OPR(24),
	UNREG_OPR(26);

	private int id;

	private OprType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
}
