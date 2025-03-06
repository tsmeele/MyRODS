package nl.tsmeele.myrods.api;

/** Class to hold predefined iRODS keywords and their string value.
 * @author Ton Smeele
 *
 */
public class Kw {
	
	// The predefined iRODS keywords are defined in lib/core/include/irods/rodsKeyWdDef.h

	
	/* The following are the keyWord definition for the condInput key/value pair */

	public static final String ALL_KW                                     ="all";          /* operation done on all replica */
	public static final String COPIES_KW                                  ="copies";       /* the number of copies */
	public static final String EXEC_LOCALLY_KW                            ="execLocally";  /* execute locally */
	public static final String FORCE_FLAG_KW                              ="forceFlag";    /* force update */
	public static final String CLI_IN_SVR_FIREWALL_KW                     ="cliInSvrFirewall";/* cli behind same firewall */
	public static final String REG_CHKSUM_KW                              ="regChksum";    /* register checksum */
	public static final String VERIFY_CHKSUM_KW                           ="verifyChksum";/* verify checksum */
	public static final String VERIFY_BY_SIZE_KW                          ="verifyBySize";/* verify by size - used by irsync */
	public static final String OBJ_PATH_KW                                ="objPath";      /* logical path of the object */
	public static final String RESC_NAME_KW                               ="rescName";     /* resource name */
	public static final String DEST_RESC_NAME_KW                          ="destRescName"; /* destination resource name */
	public static final String DEF_RESC_NAME_KW                           ="defRescName";  /* default resource name */
	// BACKUP_RESC_NAME has been deprecated.
	public static final String BACKUP_RESC_NAME_KW                        ="backupRescName";/* destination resource name */
	public static final String LEAF_RESOURCE_NAME_KW                      ="leafRescName";
	public static final String DATA_TYPE_KW                               ="dataType";     /* data type */
	public static final String DATA_SIZE_KW                               ="dataSize";
	public static final String CHKSUM_KW                                  ="chksum";
	public static final String ORIG_CHKSUM_KW                             ="orig_chksum";
	public static final String VERSION_KW                                 ="version";
	public static final String FILE_PATH_KW                               ="filePath";     /* the physical file path */
	public static final String BUN_FILE_PATH_KW                           ="bunFilePath";/* the physical bun file path */ // JMC - backport 4768
	public static final String REPL_NUM_KW                                ="replNum";      /* replica number */
	public static final String WRITE_FLAG_KW                              ="writeFlag";    /* whether it is opened for write */
	public static final String REPL_STATUS_KW                             ="replStatus";   /* status of the replica */
	public static final String ALL_REPL_STATUS_KW                         ="allReplStatus";/* update all replStatus */
	public static final String METADATA_INCLUDED_KW                       ="metadataIncluded";/* for atomic puts of data / metadata */
	public static final String ACL_INCLUDED_KW                            ="aclIncluded";/* for atomic puts of data / metadata */
	public static final String DATA_INCLUDED_KW                           ="dataIncluded";/* data included in the input buffer */
	public static final String DATA_OWNER_KW                              ="dataOwner";
	public static final String DATA_OWNER_ZONE_KW                         ="dataOwnerZone";
	public static final String DATA_EXPIRY_KW                             ="dataExpiry";
	public static final String DATA_COMMENTS_KW                           ="dataComments";
	public static final String DATA_CREATE_KW                             ="dataCreate";
	public static final String DATA_MODIFY_KW                             ="dataModify";
	public static final String DATA_ACCESS_KW                             ="dataAccess";
	public static final String DATA_ACCESS_INX_KW                         ="dataAccessInx";
	public static final String NO_OPEN_FLAG_KW                            ="noOpenFlag";
	public static final String PHYOPEN_BY_SIZE_KW                         ="phyOpenBySize";
	public static final String STREAMING_KW                               ="streaming";
	public static final String DATA_ID_KW                                 ="dataId";
	public static final String COLL_ID_KW                                 ="collId";
	public static final String DATA_MODE_KW                               ="dataMode";
	public static final String DATA_NAME_KW                               ="data_name";
	public static final String STATUS_STRING_KW                           ="statusString";
	public static final String DATA_MAP_ID_KW                             ="dataMapId";
	public static final String NO_PARA_OP_KW                              ="noParaOpr";
	public static final String LOCAL_PATH_KW                              ="localPath";
	public static final String RSYNC_MODE_KW                              ="rsyncMode";
	public static final String RSYNC_DEST_PATH_KW                         ="rsyncDestPath";
	public static final String RSYNC_CHKSUM_KW                            ="rsyncChksum";
	public static final String CHKSUM_ALL_KW                              ="ChksumAll";
	public static final String FORCE_CHKSUM_KW                            ="forceChksum";
	public static final String COLLECTION_KW                              ="collection";
	public static final String ADMIN_KW                                   ="irodsAdmin";
	public static final String ADMIN_RMTRASH_KW                           ="irodsAdminRmTrash";
	public static final String UNREG_KW                                   ="unreg";
	public static final String RMTRASH_KW                                 ="irodsRmTrash";
	public static final String RECURSIVE_OPR__KW                          ="recursiveOpr";
	public static final String COLLECTION_TYPE_KW                         ="collectionType";
	public static final String COLLECTION_INFO1_KW                        ="collectionInfo1";
	public static final String COLLECTION_INFO2_KW                        ="collectionInfo2";
	public static final String COLLECTION_MTIME_KW                        ="collectionMtime";
	public static final String SEL_OBJ_TYPE_KW                            ="selObjType";
	public static final String STRUCT_FILE_OPR_KW                         ="structFileOpr";
	public static final String ALL_MS_PARAM_KW                            ="allMsParam";
	public static final String UNREG_COLL_KW                              ="unregColl";
	public static final String UPDATE_REPL_KW                             ="updateRepl";
	// RBUDP_TRANSFER_KW has been deprecated
	public static final String RBUDP_TRANSFER_KW                          ="rbudpTransfer";
	public static final String VERY_VERBOSE_KW                            ="veryVerbose";
	// RBUDP_SEND_RATE_KW has been deprecated
	public static final String RBUDP_SEND_RATE_KW                         ="rbudpSendRate";
	// RBUDP_PACK_SIZE_KW has been deprecated
	public static final String RBUDP_PACK_SIZE_KW                         ="rbudpPackSize";
	public static final String ZONE_KW                                    ="zone";
	public static final String REMOTE_ZONE_OPR_KW                         ="remoteZoneOpr";
	public static final String REPL_DATA_OBJ_INP_KW                       ="replDataObjInp";
	public static final String CROSS_ZONE_CREATE_KW                       ="replDataObjInp"; /* use the same for backward compatibility */
	public static final String VERIFY_VAULT_SIZE_EQUALS_DATABASE_SIZE_KW  ="verifyVaultSizeEqualsDatabaseSize";
	public static final String QUERY_BY_DATA_ID_KW                        ="queryByDataID";
	public static final String SU_CLIENT_USER_KW                          ="suClientUser";
	public static final String RM_BUN_COPY_KW                             ="rmBunCopy";
	public static final String KEY_WORD_KW                                ="keyWord";  /* the msKeyValStr is a keyword */
	public static final String CREATE_MODE_KW                             ="createMode";/* a msKeyValStr keyword */
	public static final String OPEN_FLAGS_KW                              ="openFlags";/* a msKeyValStr keyword */
	public static final String OFFSET_KW                                  ="offset";/* a msKeyValStr keyword */
	/* DATA_SIZE_KW already defined */
	public static final String NUM_THREADS_KW                             ="numThreads";/* a msKeyValStr keyword */
	public static final String OPR_TYPE_KW                                ="oprType";/* a msKeyValStr keyword */
	public static final String OPEN_TYPE_KW                               ="openType";
	public static final String COLL_FLAGS_KW                              ="collFlags";/* a msKeyValStr keyword */
	public static final String TRANSLATED_PATH_KW                         ="translatedPath"; /* the path translated */
	public static final String NO_TRANSLATE_LINKPT_KW                     ="noTranslateMntpt"; /* don't translate mntpt */
	public static final String BULK_OPR_KW                                ="bulkOpr"; /* the bulk operation */
	public static final String NON_BULK_OPR_KW                            ="nonBulkOpr"; /* non bulk operation */
	public static final String EXEC_CMD_RULE_KW                           ="execCmdRule";/* the rule that invoke execCmd */
	public static final String EXEC_MY_RULE_KW                            ="execMyRule";/* the rule is invoked by rsExecMyRule */
	public static final String STREAM_STDOUT_KW                           ="streamStdout";  /* the stream stdout for
	* execCmd */
	public static final String REG_REPL_KW                                ="regRepl"; /* register replica */
	public static final String AGE_KW                                     ="age"; /* age of the file for itrim */
	public static final String DRYRUN_KW                                  ="dryrun"; /* do a dry run */
	public static final String NO_COMPUTE_KW                              ="no_compute"; /* do not compute anything (similar to dryrun) */
	public static final String ACL_COLLECTION_KW                          ="aclCollection"; /* the collection from which
	* the ACL should be used */
	public static final String NO_CHK_COPY_LEN_KW                         ="noChkCopyLen"; /* Don't check the len
	* when transferring  */
	public static final String TICKET_KW                                  ="ticket";       /* for ticket-based-access */
	public static final String PURGE_CACHE_KW                             ="purgeCache";   /* purge the cache copy right JMC - backport 4537
	* after the operation */
	public static final String EMPTY_BUNDLE_ONLY_KW                       ="emptyBundleOnly";/* delete emptyBundleOnly */ // JMC - backport 4552
	public static final String REPLICA_TOKEN_KW                           ="replicaToken";

	public static final String REGISTER_AS_INTERMEDIATE_KW                ="registerAsIntermediate";
	public static final String STALE_ALL_INTERMEDIATE_REPLICAS_KW         ="staleAllIntermediateReplicas";
	public static final String SOURCE_L1_DESC_KW                          ="sourceL1Desc";

	// =-=-=-=-=-=-=-
	// JMC - backport 4599
	public static final String LOCK_TYPE_KW                               ="lockType";     /* valid values are READ_LOCK_TYPE
	* WRITE_LOCK_TYPE and UNLOCK_TYPE */
	public static final String LOCK_CMD_KW                                ="lockCmd";      /* valid values are SET_LOCK_WAIT_CMD,
	* SET_LOCK_CMD and GET_LOCK_CMD */
	public static final String LOCK_FD_KW                                 ="lockFd";       /* Lock file desc for unlock */
	public static final String MAX_SUB_FILE_KW                            ="maxSubFile";/* max number of files for tar file bundles */
	public static final String MAX_BUNDLE_SIZE_KW                         ="maxBunSize";/* max size of a tar bundle in Gbs */
	public static final String NO_STAGING_KW                              ="noStaging";

	// =-=-=-=-=-=-=-
	/* max number of files for tar file bundles */ // JMC - backport 4771
	//public static final String MAX_SUB_FILE_KW                            ="maxSubFile";

	/* OBJ_PATH_KW already defined */

	/* OBJ_PATH_KW already defined */
	/* COLL_NAME_KW already defined */
	public static final String FILE_UID_KW                                ="fileUid";
	public static final String FILE_OWNER_KW                              ="fileOwner";
	public static final String FILE_GID_KW                                ="fileGid";
	public static final String FILE_GROUP_KW                              ="fileGroup";
	public static final String FILE_MODE_KW                               ="fileMode";
	public static final String FILE_CTIME_KW                              ="fileCtime";
	public static final String FILE_MTIME_KW                              ="fileMtime";
	public static final String FILE_SOURCE_PATH_KW                        ="fileSourcePath";
	public static final String EXCLUDE_FILE_KW                            ="excludeFile";

	public static final String GET_RESOURCE_INFO_OP_TYPE_KW               ="getResourceInfoOpType";

	/* The following are the keyWord definition for the rescCond key/value pair */
	/* RESC_NAME_KW is defined above */

	public static final String RESC_ZONE_KW                               ="zoneName";
	public static final String RESC_LOC_KW                                ="rescLoc";  /* resc_net in DB */
	public static final String RESC_TYPE_KW                               ="rescType";
	public static final String RESC_CLASS_KW                              ="rescClass";
	public static final String RESC_VAULT_PATH_KW                         ="rescVaultPath";/* resc_def_path in DB */
	public static final String RESC_STATUS_KW                             ="rescStatus";
	public static final String GATEWAY_ADDR_KW                            ="gateWayAddr";
	public static final String RESC_MAX_OBJ_SIZE_KW                       ="rescMaxObjSize";
	public static final String FREE_SPACE_KW                              ="freeSpace";
	public static final String FREE_SPACE_TIME_KW                         ="freeSpaceTime";
	public static final String FREE_SPACE_TIMESTAMP_KW                    ="freeSpaceTimeStamp";
	public static final String QUOTA_LIMIT_KW                             ="quotaLimit";
	public static final String RESC_INFO_KW                               ="rescInfo";
	public static final String RESC_TYPE_INX_KW                           ="rescTypeInx";
	public static final String RESC_CLASS_INX_KW                          ="rescClassInx";
	public static final String RESC_ID_KW                                 ="rescId";
	public static final String RESC_COMMENTS_KW                           ="rescComments";
	public static final String RESC_CREATE_KW                             ="rescCreate";
	public static final String RESC_MODIFY_KW                             ="rescModify";

	/* The following are the keyWord definition for the userCond key/value pair */

	public static final String USER_NAME_CLIENT_KW                        ="userNameClient";
	public static final String RODS_ZONE_CLIENT_KW                        ="rodsZoneClient";
	public static final String HOST_CLIENT_KW                             ="hostClient";
	public static final String CLIENT_ADDR_KW                             ="clientAddr";
	public static final String USER_TYPE_CLIENT_KW                        ="userTypeClient";
	public static final String AUTH_STR_CLIENT_KW                         ="authStrClient";/* user distin name */
	public static final String USER_AUTH_SCHEME_CLIENT_KW                 ="userAuthSchemeClient";
	public static final String USER_INFO_CLIENT_KW                        ="userInfoClient";
	public static final String USER_COMMENT_CLIENT_KW                     ="userCommentClient";
	public static final String USER_CREATE_CLIENT_KW                      ="userCreateClient";
	public static final String USER_MODIFY_CLIENT_KW                      ="userModifyClient";
	public static final String USER_NAME_PROXY_KW                         ="userNameProxy";
	public static final String RODS_ZONE_PROXY_KW                         ="rodsZoneProxy";
	public static final String HOST_PROXY_KW                              ="hostProxy";
	public static final String USER_TYPE_PROXY_KW                         ="userTypeProxy";
	public static final String AUTH_STR_PROXY_KW                          ="authStrProxy";/* dn */
	public static final String USER_AUTH_SCHEME_PROXY_KW                  ="userAuthSchemeProxy";
	public static final String USER_INFO_PROXY_KW                         ="userInfoProxy";
	public static final String USER_COMMENT_PROXY_KW                      ="userCommentProxy";
	public static final String USER_CREATE_PROXY_KW                       ="userCreateProxy";
	public static final String USER_MODIFY_PROXY_KW                       ="userModifyProxy";
	public static final String ACCESS_PERMISSION_KW                       ="accessPermission";
	public static final String NO_CHK_FILE_PERM_KW                        ="noChkFilePerm";

	/* The following are the keyWord definition for the collCond key/value pair */

	public static final String COLL_NAME_KW                               ="collName";
	public static final String COLL_PARENT_NAME_KW                        ="collParentName";/* parent_coll_name in DB  */
	public static final String COLL_OWNER_NAME_KW                         ="collOwnername";
	public static final String COLL_OWNER_ZONE_KW                         ="collOwnerZone";
	public static final String COLL_MAP_ID_KW                             ="collMapId";
	public static final String COLL_INHERITANCE_KW                        ="collInheritance";
	public static final String COLL_COMMENTS_KW                           ="collComments";
	public static final String COLL_EXPIRY_KW                             ="collExpiry";
	public static final String COLL_CREATE_KW                             ="collCreate";
	public static final String COLL_MODIFY_KW                             ="collModify";
	public static final String COLL_ACCESS_KW                             ="collAccess";
	public static final String COLL_ACCESS_INX_KW                         ="collAccessInx";
	//public static final String COLL_ID_KW                                 ="collId";

	/*
	  The following are the keyWord definitions for the keyValPair_t input
	  to chlModRuleExec.
	*/
	public static final String RULE_NAME_KW                               ="ruleName";
	public static final String RULE_REI_FILE_PATH_KW                      ="reiFilePath";
	public static final String RULE_USER_NAME_KW                          ="userName";
	public static final String RULE_EXE_ADDRESS_KW                        ="exeAddress";
	public static final String RULE_EXE_TIME_KW                           ="exeTime";
	public static final String RULE_EXE_FREQUENCY_KW                      ="exeFrequency";
	public static final String RULE_PRIORITY_KW                           ="priority";
	public static final String RULE_ESTIMATE_EXE_TIME_KW                  ="estimateExeTime";
	public static final String RULE_NOTIFICATION_ADDR_KW                  ="notificationAddr";
	public static final String RULE_LAST_EXE_TIME_KW                      ="lastExeTime";
	public static final String RULE_EXE_STATUS_KW                         ="exeStatus";

	// The following key word is used by rxRuleExecSubmit and the delay server
	// to identify the rule execution info / context.
	public static final String RULE_EXECUTION_CONTEXT_KW                  ="rule_execution_context";

	//public static final String EXCLUDE_FILE_KW                            ="excludeFile";
	//public static final String AGE_KW                                     ="age"; /* age of the file for itrim */

	// =-=-=-=-=-=-=-
	// irods general keywords definitions
	public static final String RESC_HIER_STR_KW                           ="resc_hier";
	public static final String DEST_RESC_HIER_STR_KW                      ="dest_resc_hier";
	public static final String IN_PDMO_KW                                 ="in_pdmo";
	public static final String STAGE_OBJ_KW                               ="stage_object";
	public static final String SYNC_OBJ_KW                                ="sync_object";
	public static final String IN_REPL_KW                                 ="in_repl";

	// =-=-=-=-=-=-=-
	// irods tcp keyword definitions
	public static final String SOCKET_HANDLE_KW                           ="tcp_socket_handle";

	// =-=-=-=-=-=-=-
	// irods ssl keyword definitions
	public static final String SSL_HOST_KW                                ="ssl_host";
	public static final String SSL_SHARED_SECRET_KW                       ="ssl_shared_secret";
	public static final String SSL_KEY_SIZE_KW                            ="ssl_key_size";
	public static final String SSL_SALT_SIZE_KW                           ="ssl_salt_size";
	public static final String SSL_NUM_HASH_ROUNDS_KW                     ="ssl_num_hash_rounds";
	public static final String SSL_ALGORITHM_KW                           ="ssl_algorithm";

	// =-=-=-=-=-=-=-
	// irods data_object keyword definitions
	public static final String PHYSICAL_PATH_KW                           ="physical_path";
	public static final String MODE_KW                                    ="mode_kw";
	public static final String FLAGS_KW                                   ="flags_kw";
	public static final String OBJ_COUNT_KW                               ="object_count";
	// borrowed RESC_HIER_STR_KW

	// =-=-=-=-=-=-=-
	// irods file_object keyword definitions
	public static final String LOGICAL_PATH_KW                            ="logical_path";
	public static final String FILE_DESCRIPTOR_KW                         ="file_descriptor";
	public static final String L1_DESC_IDX_KW                             ="l1_desc_idx";
	public static final String SIZE_KW                                    ="file_size";
	public static final String REPL_REQUESTED_KW                          ="repl_requested";
	// borrowed IN_PDMO_KW

	// =-=-=-=-=-=-=-
	// irods structured_object keyword definitions
	public static final String HOST_ADDR_KW                               ="host_addr";
	public static final String ZONE_NAME_KW                               ="zone_name";
	public static final String PORT_NUM_KW                                ="port_num";
	public static final String SUB_FILE_PATH_KW                           ="sub_file_path";
	// borrowed OFFSET_KW
	// borrowed DATA_TYPE_KW
	// borrowed OPR_TYPE_KW

	// =-=-=-=-=-=-=-
	// irods spec coll keyword definitions
	public static final String SPEC_COLL_CLASS_KW                         ="spec_coll_class";
	public static final String SPEC_COLL_TYPE_KW                          ="spec_coll_type";
	public static final String SPEC_COLL_OBJ_PATH_KW                      ="spec_coll_obj_path";
	public static final String SPEC_COLL_RESOURCE_KW                      ="spec_coll_resource";
	public static final String SPEC_COLL_RESC_HIER_KW                     ="spec_coll_resc_hier";
	public static final String SPEC_COLL_PHY_PATH_KW                      ="spec_coll_phy_path";
	public static final String SPEC_COLL_CACHE_DIR_KW                     ="spec_coll_cache_dir";
	public static final String SPEC_COLL_CACHE_DIRTY                      ="spec_coll_cache_dirty";
	public static final String SPEC_COLL_REPL_NUM                         ="spec_coll_repl_num";

	public static final String KEY_VALUE_PASSTHROUGH_KW                   ="key_value_passthrough";
	public static final String DISABLE_STRICT_ACL_KW                      ="disable_strict_acls";

	public static final String INSTANCE_NAME_KW                           ="instance_name";

	// When the value of the="data_modify_ts";field is set to this keyword in the input
	// to data_object_finalize, the mtime for that replica is set to the current time.
	public static final String SET_TIME_TO_NOW_KW                         ="set time to now";

	// A keyword for the replication resource plugin which indicates the hierarchy selected
	// at the time of resolution. This information is lost if the l1 descriptor goes away.
	public static final String SELECTED_HIERARCHY_KW                      ="selected_hierarchy";

	// data_object_finalize checks for this keyword in order to determine whether a
	// fileModified notification should be sent to the resource plugins.
	public static final String FILE_MODIFIED_KW="file_modified";

	// Residual keywords from the now-removed phyBundleColl header file
	public static final String BUNDLE_RESC      ="bundleResc";
	public static final String BUNDLE_STR       ="bundle";
	public static final String BUNDLE_RESC_CLASS="bundle";

	public static final String AVAILABLE_KW                               ="available";

	public static final String KW_SWITCH_PROXY_USER                       ="switch_proxy_user";
	public static final String KW_CLOSE_OPEN_REPLICAS                     ="close_open_replicas";
	public static final String KW_KEEP_SVR_TO_SVR_CONNECTIONS             ="keep_svr_to_svr_connections";


	
	
	
	/*
	 * excerpt and defintionsfrom:  server/icat/include/irods/icatDefines.h

	   These are the access permissions known to the system, listed in
	   order.  The defines are here to make it clear what these are and
	   where in the code they are being used.

	   These, and their integer values, are defined in the R_TOKN_MAIN
	   table.

	   Having a particular access permission implies that the user has
	   all of the lower ones.  For example, if you have "own", you have
	   all the rest.  And if you have "delete_metadata", you have
	   "modify_metadata".  The ICAT code generates sql that asks,
	   essentially, "does the user have ACCESS_x or better?"

	 */
	public static final String ACCESS_NULL                               = "null";
	public static final String ACCESS_EXECUTE                            = "execute";
	public static final String ACCESS_READ_ANNOTATION                    = "read_annotation";
	public static final String ACCESS_READ_SYSTEM_METADATA               = "read_system_metadata";
	public static final String ACCESS_READ_METADATA                      = "read_metadata";
	public static final String ACCESS_READ_OBJECT                        = "read_object";
	// -> rcModAccessControl expects "read". There appears to be no keyword for access level "read" 
	public static final String ACCESS_WRITE_ANNOTATION                   = "write_annotation";
	public static final String ACCESS_CREATE_METADATA                    = "create_metadata";
	public static final String ACCESS_MODIFY_METADATA                    = "modify_metadata";
	public static final String ACCESS_DELETE_METADATA                    = "delete_metadata";
	public static final String ACCESS_ADMINISTER_OBJECT                  = "administer_object";
	public static final String ACCESS_CREATE_OBJECT                      = "create_object";
	public static final String ACCESS_MODIFY_OBJECT                      = "modify_object";
	// -> rcModAccessControl expects "write". There appears to be no keyword for access level "write" 
	public static final String ACCESS_DELETE_OBJECT                      = "delete_object";
	public static final String ACCESS_CREATE_TOKEN                       = "create_token";
	public static final String ACCESS_DELETE_TOKEN                       = "delete_token";
	public static final String ACCESS_CURATE                             = "curate";
	public static final String ACCESS_OWN                                = "own";

	public static final String ACCESS_INHERIT                            = "inherit";
	public static final String ACCESS_NO_INHERIT                         = "noinherit";

	
	
	
	

}
