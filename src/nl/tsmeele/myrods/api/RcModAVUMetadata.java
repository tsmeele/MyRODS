package nl.tsmeele.myrods.api;

import nl.tsmeele.myrods.apiDataStructures.Api;
import nl.tsmeele.myrods.apiDataStructures.ModAVUMetadataInp;

public class RcModAVUMetadata extends RodsApiCall {

	public RcModAVUMetadata(ModAVUMetadataInp modAVUMetadataInp) {
		super(Api.MOD_AVU_METADATA_AN);
		msg.setMessage(modAVUMetadataInp);
	}

	@Override
	public String unpackInstruction() {
		return null;	// returns intInfo = 0 on success
	}

}
