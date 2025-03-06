package nl.tsmeele.myrods.irodsStructures;


import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.DataObjInp;


public class RcReplicaOpen extends RodsApiCall  {
	
	public RcReplicaOpen(DataObjInp dataObjInp) {
		super(Api.REPLICA_OPEN_APN);       
        msg.setMessage(dataObjInp); 
	}
	
	// the returned bytes can be converted to a String which will have a JSON object:
	// 		DataBinArray bin = ((DataBinArray) reply.getMessage().lookupName("buf"));
	//      String json = bin.getAsAString();
	//
	// example json: 
	// {"bytes_written":-1,
	//  "checksum":"","checksum_flag":0,
	//  "copies_needed":0,
	//  "data_object_info": {
	//       "backup_resource_name":"",
	//       "checksum":"",
	//       "collection_id":10017,
	//       "condition_input":[
	//            {"key":"resc_hier","value":"demoResc"},
	//            {"key":"selected_hierarchy","value":"demoResc"},
	//            {"key":"destRescName","value":"demoResc"},
	//            {"key":"openType","value":"3"}],
	//       "data_access":"",
	//       "data_access_index":0,
	//       "data_comments":"",
	//       "data_create":"01718974727",
	//       "data_expiry":"00000000000",
	//       "data_id":10026,
	//       "data_map_id":0,
	//       "data_mode":"1",
	//       "data_modify":"01718974727",
	//       "data_owner_name":"ton",
	//       "data_owner_zone":"tempZone",
	//       "data_size":0,
	//       "data_type":"generic",
	//       "destination_resource_name":"",
	//       "file_path":"/var/lib/irods/Vault/home/ton/xx",
	//       "flags":0,
	//       "in_pdmo":"",
	//       "is_replica_current":false,
	//       "next":null,
	//       "object_path":"/tempZone/home/ton/xx",
	//       "other_flags":0,
	//       "registering_user_id":0,
	//       "replica_number":0,
	//       "replica_status":0,
	//       "resource_hierarchy":"demoResc",
	//       "resource_id":10013,
	//       "resource_name":"demoResc",
	//       "special_collection":null,
	//       "status_string":"",
	//       "sub_path":"",
	//       "version":"",
	//       "write_flag":0},
	//  "data_object_input":{
	//       "condition_input":[
	//            {"key":"resc_hier","value":"demoResc"},
	//            {"key":"selected_hierarchy","value":"demoResc"},
	//            {"key":"destRescName","value":"demoResc"},
	//            {"key":"openType","value":"3"}],
	//       "create_mode":1,
	//       "data_size":-1,
	//       "number_of_threads":0,
	//       "object_path":"/tempZone/home/ton/xx",
	//       "offset":0,
	//       "open_flags":64,
	//       "operation_type":1,
	//       "special_collection":null},
	//  "data_object_input_replica_flag":1,
	//  "data_size":-1,
	//  "in_pdmo":"",
	//  "in_use":true,
	//  "l3descInx":3,
	//  "lock_file_descriptor":0,
	//  "open_type":2,
	//  "operation_status":0,
	//  "operation_type":1,
	//  "other_data_object_info":null,
	//  "plugin_data":null,
	//  "purge_cache_flag":0,
	//  "remote_l1_descriptor_index":0,
	//  "remote_zone_host":null,
	//  "replica_status":0,
	//  "replica_token":"",
	//  "replication_data_object_info":null,
	//  "source_l1_descriptor_index":0,
	//  "stage_flag":0}
	
	@Override
	public String unpackInstruction() {
		return "BinBytesBuf_PI";
	}

}
