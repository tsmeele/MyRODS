package nl.tsmeele.myrods.plumbing;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class PackMap manages a map of all supported packing instructions.
 * See also class PackMapConstants for predefined constants used in packing instructions.
 * @author Ton Smeele
 *
 */
public class PackInstructions  extends HashMap<String, ArrayList<String>> {
	private static final long serialVersionUID = 1L;

	public PackInstructions() {
		populate(table());
	}
	
	private void populate(String[] packInstructions) {
		for (int i = 0; i < packInstructions.length; i = i + 2) {
			ArrayList<String> instructions = new ArrayList<String>();
			for (String s : packInstructions[i + 1].split(";") ) {
				instructions.add(s);
			}
			this.put(packInstructions[i], instructions);
		}	
	}
	

	
	/**
	 * Returns the list of concrete packing instructions, as published in iRODS server software source file
	 * See https://github.com/irods/irods/lib/core/include/irods/rodsPackInstruct.h
	 * @return
	 */
	private String[] table() {
		String[] packInstructions = {
				"IRODS_STR_PI", "str myStr[MAX_NAME_LEN];",
				"STR_PI", "str myStr;",
				"CHAR_PI", "char myChar;",
				"STR_PTR_PI", "str *myStr;",
				"PI_STR_PI", "piStr myStr[MAX_NAME_LEN];",
				"INT_PI", "int myInt;",
				"INT16_PI", "int16 myInt;",
				"BUF_LEN_PI", "int myInt;",
				"DOUBLE_PI", "double myDouble;",

				/* packInstruct for msgHeader_t */
				"MsgHeader_PI", "str type[HEADER_TYPE_LEN]; int msgLen; int errorLen; int bsLen; int intInfo;",

				/* packInstruct for startupPack_t */
				"StartupPack_PI", "int irodsProt; int reconnFlag; int connectCnt; str proxyUser[NAME_LEN]; str proxyRcatZone[NAME_LEN]; str clientUser[NAME_LEN]; str clientRcatZone[NAME_LEN]; str relVersion[NAME_LEN]; str apiVersion[NAME_LEN]; str option[LONG_NAME_LEN];",

				/* packInstruct for version_t */

				"Version_PI", "int status; str relVersion[NAME_LEN]; str apiVersion[NAME_LEN]; int reconnPort; str reconnAddr[LONG_NAME_LEN]; int cookie;",

				/* packInstruct for rErrMsg_t */

				"RErrMsg_PI", "int status; str msg[ERR_MSG_LEN];",

				/* packInstruct for rError_t */

				"RError_PI", "int count; struct *RErrMsg_PI[count];",

				"RHostAddr_PI", "str hostAddr[LONG_NAME_LEN]; str rodsZone[NAME_LEN]; int port; int dummyInt;",

				"RODS_STAT_T_PI", "double st_size; int st_dev; int st_ino; int st_mode; int st_nlink; int st_uid; int st_gid; int st_rdev; int st_atim; int st_mtim; int st_ctim; int st_blksize; int st_blocks;",

				"RODS_DIRENT_T_PI", "int d_offset; int d_ino; int d_reclen; int d_namlen; str d_name[DIR_LEN];",

				"KeyValPair_PI", "int ssLen; str *keyWord[ssLen]; str *svalue[ssLen];",

				// =-=-=-=-=-=-=-
				// pack struct for client server negotiations
				"CS_NEG_PI", "int status; str result[MAX_NAME_LEN];",

				"InxIvalPair_PI", "int iiLen; int *inx(iiLen); int *ivalue(iiLen);",

				"InxValPair_PI", "int isLen; int *inx(isLen); str *svalue[isLen];",

				"DataObjInp_PI", "str objPath[MAX_NAME_LEN]; int createMode; int openFlags; double offset; double dataSize; int numThreads; int oprType; struct *SpecColl_PI; struct KeyValPair_PI;",

				"OpenedDataObjInp_PI", "int l1descInx; int len; int whence; int oprType; double offset; double bytesWritten; struct KeyValPair_PI;",

				"PortList_PI", "int portNum; int cookie; int sock; int windowSize; str hostAddr[LONG_NAME_LEN];",

				"PortalOprOut_PI", "int status; int l1descInx; int numThreads; str chksum[NAME_LEN]; struct PortList_PI;",

				"DataOprInp_PI", "int oprType; int numThreads; int srcL3descInx; int destL3descInx; int srcRescTypeInx; int destRescTypeInx; double offset; double dataSize; struct KeyValPair_PI;",

				"CollInpNew_PI", "str collName[MAX_NAME_LEN]; int flags; int oprType; struct KeyValPair_PI;",

				"GenQueryInp_PI", "int maxRows; int continueInx; int partialStartIndex; int options; struct KeyValPair_PI; struct InxIvalPair_PI; struct InxValPair_PI;",
				
				"SqlResult_PI", "int attriInx; int reslen; str *value(rowCnt)(reslen);",

				"GenQueryOut_PI", "int rowCnt; int attriCnt; int continueInx; int totalRowCount; struct SqlResult_PI[MAX_SQL_ATTR];",
				"GenArraysInp_PI", "int rowCnt; int attriCnt; int continueInx; int totalRowCount; struct KeyValPair_PI; struct SqlResult_PI[MAX_SQL_ATTR];",
				"DataObjInfo_PI", "str objPath[MAX_NAME_LEN]; str rescName[NAME_LEN]; str rescHier[MAX_NAME_LEN]; str dataType[NAME_LEN]; double dataSize; str chksum[NAME_LEN]; str version[NAME_LEN]; str filePath[MAX_NAME_LEN]; str dataOwnerName[NAME_LEN]; str dataOwnerZone[NAME_LEN]; int  replNum; int  replStatus; str statusString[NAME_LEN]; double  dataId; double collId; int  dataMapId; int flags; str dataComments[LONG_NAME_LEN]; str dataMode[SHORT_STR_LEN]; str dataExpiry[TIME_LEN]; str dataCreate[TIME_LEN]; str dataModify[TIME_LEN]; str dataAccess[NAME_LEN]; int  dataAccessInx; int writeFlag; str destRescName[NAME_LEN]; str backupRescName[NAME_LEN]; str subPath[MAX_NAME_LEN]; int *specColl;  int regUid; int otherFlags; struct KeyValPair_PI; str in_pdmo[MAX_NAME_LEN]; int *next; double rescId;",

				/* transStat_t is being replaced by transferStat_t because of the 64 bits
				 * padding */
				"TransStat_PI", "int numThreads; double bytesWritten;",
				"TransferStat_PI", "int numThreads; int flags; double bytesWritten;",

				"AuthInfo_PI", "str authScheme[NAME_LEN]; int authFlag; int flag; int ppid; str host[NAME_LEN]; str authStr[NAME_LEN];",
				"UserOtherInfo_PI", "str userInfo[NAME_LEN]; str userComments[NAME_LEN]; str userCreate[TIME_LEN]; str userModify[TIME_LEN];",

				"UserInfo_PI", "str userName[NAME_LEN]; str rodsZone[NAME_LEN]; str userType[NAME_LEN]; int sysUid; struct AuthInfo_PI; struct UserOtherInfo_PI;",
				"CollInfo_PI", "double collId; str collName[MAX_NAME_LEN]; str collParentName[MAX_NAME_LEN]; str collOwnerName[NAME_LEN]; str collOwnerZone[NAME_LEN]; int collMapId; int collAccessInx; str collComments[LONG_NAME_LEN]; str collInheritance[LONG_NAME_LEN]; str collExpiry[TIME_LEN]; str collCreate[TIME_LEN]; str collModify[TIME_LEN]; str collAccess[NAME_LEN]; str collType[NAME_LEN]; str collInfo1[MAX_NAME_LEN]; str collInfo2[MAX_NAME_LEN]; struct KeyValPair_PI; int *next;",
				

				"Rei_PI", "int status; str statusStr[MAX_NAME_LEN]; str ruleName[NAME_LEN]; int *rsComm; str pluginInstanceName[MAX_NAME_LEN]; struct *MsParamArray_PI; struct MsParamArray_PI; int l1descInx; struct *DataObjInp_PI; struct *DataObjInfo_PI; str rescName[NAME_LEN]; struct *UserInfo_PI; struct *UserInfo_PI; struct *CollInfo_PI; struct *UserInfo_PI; struct *KeyValPair_PI; str ruleSet[RULE_SET_DEF_LENGTH]; int *next;",

				"ReArg_PI", "int myArgc; str *myArgv[myArgc];",
				"ReiAndArg_PI", "struct *Rei_PI; struct ReArg_PI;",

				"BytesBuf_PI", "int buflen; char *buf(buflen);",

				/* PI for dataArray_t */
				"charDataArray_PI", "int type; int len; char *buf(len);",
				"strDataArray_PI", "int type; int len; str *buf[len];",
				"intDataArray_PI", "int type; int len; int *buf(len);",
				"int16DataArray_PI", "int type; int len; int16 *buf(len);",
				"int64DataArray_PI", "int type; int len; double *buf(len);",

				"BinBytesBuf_PI", "int buflen; bin *buf(buflen);",

				"MsParam_PI", "str *label; piStr *type; ?type *inOutStruct; struct *BinBytesBuf_PI;",

				"MsParamArray_PI", "int paramLen; int oprType; struct *MsParam_PI[paramLen];",

				"TagStruct_PI", "int ssLen; str *preTag[ssLen]; str *postTag[ssLen]; str *keyWord[ssLen];",

				"RodsObjStat_PI", "double objSize; int objType; int dataMode; str dataId[NAME_LEN]; str chksum[NAME_LEN]; str ownerName[NAME_LEN]; str ownerZone[NAME_LEN]; str createTime[TIME_LEN]; str modifyTime[TIME_LEN]; struct *SpecColl_PI;",

				"ReconnMsg_PI", "int status; int cookie; int procState; int flag;",
				"VaultPathPolicy_PI", "int scheme; int addUserName; int trimDirCnt;",
				"StrArray_PI", "int len; int size; str *value(len)(size);",
				"IntArray_PI", "int len; int *value(len);",

				"SpecColl_PI", "int collClass; int type; str collection[MAX_NAME_LEN]; str objPath[MAX_NAME_LEN]; str resource[NAME_LEN]; str rescHier[MAX_NAME_LEN]; str phyPath[MAX_NAME_LEN]; str cacheDir[MAX_NAME_LEN]; int cacheDirty; int replNum;",

				"SubFile_PI", "struct RHostAddr_PI; str subFilePath[MAX_NAME_LEN]; int mode; int flags; double offset; struct *SpecColl_PI;",
				/* XXXXX start of HDF5 PI */
				"h5error_PI", "str major[MAX_ERROR_SIZE]; str minor[MAX_ERROR_SIZE];",
				"h5File_PI", "int fopID; str *filename; int ffid; struct *h5Group_PI; struct h5error_PI;int ftime;",
				"h5Group_PI", "int gopID; int gfid; int gobjID[OBJID_DIM]; str *gfullpath; int $dummyParent; int nGroupMembers; struct *h5Group_PI(nGroupMembers); int nDatasetMembers; struct *h5Dataset_PI(nDatasetMembers); int nattributes; struct *h5Attribute_PI(nattributes); struct h5error_PI;int gtime;",
				
				/* XXXXX need to fix the type dependence */
				"h5Dataset_PI", "int dopID; int dfid; int dobjID[OBJID_DIM]; int dclass; int nattributes; str *dfullpath; struct *h5Attribute_PI(nattributes); struct h5Datatype_PI; struct h5Dataspace_PI; int nvalue; int dtime; % dclass:3,6,9 = str *value[nvalue]:default= char *value(nvalue); struct h5error_PI;",
				/* XXXXX need to fix the type dependence */
				"h5Attribute_PI", "int aopID; int afid; str *aname; str *aobj_path; int aobj_type; int aclass; struct h5Datatype_PI; struct h5Dataspace_PI; int nvalue; % aclass:3,6,9 = str *value[nvalue]:default= char *value(nvalue); struct h5error_PI;",
				"h5Datatype_PI", "int tclass; int torder; int tsign; int tsize; int ntmenbers; int *mtypes(ntmenbers); str *mnames[ntmenbers];",
				"h5Dataspace_PI", "int rank; int dims[H5S_MAX_RANK]; int npoints; int start[H5DATASPACE_MAX_RANK]; int stride[H5DATASPACE_MAX_RANK]; int count[H5DATASPACE_MAX_RANK];",
				/* content of collEnt_t cannot be freed since they are pointers in "value"
				 * of sqlResult */
				"CollEnt_PI", "int objType; int replNum; int replStatus; int dataMode; double dataSize; str $collName; str $dataName; str $dataId; str $createTime; str $modifyTime; str $chksum; str $resource; str $resc_hier; str $phyPath; str $ownerName; str $dataType; struct SpecColl_PI;",
				"CollOprStat_PI", "int filesCnt; int totalFileCnt; double bytesWritten; str lastObjPath[MAX_NAME_LEN];",
				/* XXXXX end of HDF5 PI */
				"RuleStruct_PI", "int maxNumOfRules; str *ruleBase[maxNumOfRules]; str *action[maxNumOfRules]; str *ruleHead[maxNumOfRules]; str *ruleCondition[maxNumOfRules]; str *ruleAction[maxNumOfRules]; str *ruleRecovery[maxNumOfRules]; double ruleId[maxNumOfRules];",
				
				"DVMapStruct_PI", "int maxNumOfDVars; str *varName[maxNumOfDVars]; str *action[maxNumOfDVars]; str *var2CMap[maxNumOfDVars]; double varId[maxNumOfDVars];",
				"FNMapStruct_PI", "int maxNumOfFMaps; str *funcName[maxNumOfFMaps]; str *func2CMap[maxNumOfFMaps]; double fmapId[maxNumOfFMaps];",
				"MsrvcStruct_PI", "int maxNumOfMsrvcs; double msrvcId[maxNumOfMsrvcs]; str moduleName[maxNumOfMsrvcs]; str msrvcName[maxNumOfMsrvcs];  str msrvcSiganture[maxNumOfMsrvcs];  str msrvcVersion[maxNumOfMsrvcs];  str msrvcHost[maxNumOfMsrvcs];  str msrvcLocation[maxNumOfMsrvcs];  str msrvcLanguage[maxNumOfMsrvcs];  str msrvcTypeName[maxNumOfMsrvcs];  double msrvcStatus[maxNumOfMsrvcs];",
				"DataSeg_PI", "double len; double offset;",
				"FileRestartInfo_PI", "str fileName[MAX_NAME_LEN]; str objPath[MAX_NAME_LEN]; int numSeg; int flags; double fileSize; struct DataSeg_PI[numSeg];",
				
				// retrieved from lib/api/include/irods/getMiscSvrInfo.h
				"MiscSvrInfo_PI", "int serverType; int serverBootTime; str relVersion[NAME_LEN]; str apiVersion[NAME_LEN]; str rodsZone[NAME_LEN];",
				// lib/api/include/irods/authRequest.h
				"authRequestOut_PI", "bin *challenge(CHALLENGE_LEN);",
				// lib/api/include/irods/authResponse.h
				"authResponseInp_PI", "bin *response(RESPONSE_LEN); str *username;",
				"sslStartInp_PI", "str *arg0;",	// lib/api/include/irods/sslStart.h
				"sslEndInp_PI", "str *arg0;",	// lib/api/include/irods/sslEnd.h
				"getTempPasswordOut_PI", "str stringToHashWith[MAX_PASSWORD_LEN];", // lib/api/include/irods/getTempPassword.h
				"pamAuthRequestInp_PI", "str *pamUser; str *pamPassword; int timeToLive;", // lib/api/include/irods/pamAuthRequest.h
				"pamAuthRequestOut_PI", "str *irodsPamPassword;",  // lib/api/include/irods/pamAuthRequest.h
				"getLimitedPasswordInp_PI", "int ttl; str *unused1;", // lib/api/include/irods/getLimitedPassword.h
				"getLimitedPasswordOut_PI", "str stringToHashWith[MAX_PASSWORD_LEN];", // lib/api/include/irods/getLimitedPassword.h
				// lib/api/include/irods/specificQuery.h
				"specificQueryInp_PI", "str *sql; str *arg1; str *arg2; str *arg3; str *arg4; str *arg5; str *arg6; str *arg7; str *arg8; str *arg9; str *arg10; int maxRows; int continueInx; int rowOffset; int options; struct KeyValPair_PI;",
				// lib/api/include/irods/authCheck.h
				"authCheckInp_PI", "str *challenge; str *response; str *username;",
				"authCheckOut_PI", "int privLevel; int clientPrivLevel; str *serverResponse;",
				// lib/api/include/irods/authPluginRequest.h
				"authPlugReqInp_PI", "str auth_scheme_[MAX_NAME_LEN]; str context_[MAX_NAME_LEN];",
				"authPlugReqOut_PI", "str result_[MAX_NAME_LEN];",
				// lib/api/include/irods/check_auth_credentials.h
				"CheckAuthCredentialsInput_PI", "str username[64]; str zone[64]; str password[250];",	// !! hardcoded lengths
				// lib/api/include/irods/chkObjPermAndStat.h
				"ChkObjPermAndStat_PI", "str objPath[MAX_NAME_LEN]; str permission[NAME_LEN]; int flags; int status; struct KeyValPair_PI;",
				// lib/api/include/irods/generalAdmin.h
				"generalAdminInp_PI", "str *arg0; str *arg1; str *arg2; str *arg3; str *arg4; str *arg5; str *arg6; str *arg7;  str *arg8;  str *arg9;",
				// lib/api/include/irods/genquery2.h
				"Genquery2Input_PI", "str *query_string; str *zone; int sql_only; int column_mappings;",
				// lib/api/include/irods/execMyRule.h
				"ExecMyRuleInp_PI", "str myRule[META_STR_LEN]; struct RHostAddr_PI; struct KeyValPair_PI; str outParamDesc[LONG_NAME_LEN]; struct *MsParamArray_PI;",
				// ExecCmdOut_PI
				// lib/api/include/irods/execCmd.h
				"ExecCmdOut_PI", "struct BinBytesBuf_PI; struct BinBytesBuf_PI; int status;",
				// lib/api/include/irods/modAVUMetadata.h
				"ModAVUMetadataInp_PI", "str *arg0; str *arg1; str *arg2; str *arg3; str *arg4; str *arg5; str *arg6; str *arg7;  str *arg8;  str *arg9; struct KeyValPair_PI;",
				// lib/api/include/irods/modDataObjMeta.h
				"ModDataObjMeta_PI", "struct *DataObjInfo_PI; struct *KeyValPair_PI;",
				// lib/api/include/irods/modAccessControl.h
				"modAccessControlInp_PI", "int recursiveFlag; str *accessLevel; str *userName; str *zone; str *path;",
				
		};
		return packInstructions;
	}
	
	
	
}
