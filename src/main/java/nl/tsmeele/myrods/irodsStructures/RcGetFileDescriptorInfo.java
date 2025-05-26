package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.JsonInp;

public class RcGetFileDescriptorInfo extends RodsApiCall {

	/**
	 * get details on an open replica.
	 * @param json 	json object with file descriptor, structured as: {"fd": integer}
	 */
	public RcGetFileDescriptorInfo(JsonInp json) {
		super(Api.GET_FILE_DESCRIPTOR_INFO_APN);
		msg.setMessage(json);
	}

	/*
	 * Returned data is a byte buffer that contains a JSONobject structured as below example
	 * {"bytes_written":-1,"checksum":"","checksum_flag":0,"copies_needed":0,
	 *  "data_object_info":{"backup_resource_name":"","checksum":"","collection_id":609230,
	 *  			"condition_input":[],"data_access":"","data_access_index":0,"data_comments":"","data_create":"01721900246","data_expiry":"00000000000",
	 *  			"data_id":609285,"data_map_id":0,"data_mode":"0","data_modify":"01721900246",
	 *  			"data_owner_name":"ton","data_owner_zone":"tempZone","data_size":0,"data_type":"generic","destination_resource_name":"",
	 *  			"file_path":"/mnt/irods02/Vault/home/ton/Spreadsheet1.png1721900246.docx","flags":0,"in_pdmo":"",
	 *  			"is_replica_current":false,"next":null,"object_path":"/tempZone/home/ton/Spreadsheet1.png1721900246.docx","other_flags":0,
	 *  			"registering_user_id":0,"replica_number":0,"replica_status":2,"resource_hierarchy":"demoResc","resource_id":434468,
	 *  			"resource_name":"demoResc","special_collection":null,"status_string":"","sub_path":"","version":"","write_flag":0},
	 *  "data_object_input":{"condition_input":
	 *  			[{"key":"resc_hier","value":"demoResc"},{"key":"selected_hierarchy","value":"demoResc"},{"key":"selObjType","value":"dataObj"},
	 *  				{"key":"openType","value":"1"},{"key":"registerAsIntermediate","value":""},
	 *  				{"key":"filePath","value":"/mnt/irods02/Vault/home/ton/Spreadsheet1.png1721900246.docx"},
	 *  				{"key":"dataSize","value":"0"}],
	 *  			"create_mode":0,"data_size":-1,"number_of_threads":0,"object_path":"/tempZone/home/ton/Spreadsheet1.png1721900246.docx",
	 *  			"offset":0,"open_flags":66,"operation_type":0,"special_collection":null},
	 *  "data_object_input_replica_flag":1,"data_size":-1,"in_pdmo":"","in_use":true,"l3descInx":3,"lock_file_descriptor":0,"open_type":1,
	 *  "operation_status":0,"operation_type":0,"other_data_object_info":null,"plugin_data":null,"purge_cache_flag":0,
	 *  "remote_l1_descriptor_index":0,"remote_zone_host":null,"replica_status":2,
	 *  "replica_token":"359f7c2b-9661-451a-90bf-bc9c0c2fb0f3","replication_data_object_info":null,"source_l1_descriptor_index":0,"stage_flag":0}
	 * 
	 */
	
	
	@Override
	public String unpackInstruction() {
		return "BinBytesBuf_PI";
	}

}
