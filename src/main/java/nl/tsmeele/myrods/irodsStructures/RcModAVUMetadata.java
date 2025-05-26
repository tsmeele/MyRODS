package nl.tsmeele.myrods.irodsStructures;

import nl.tsmeele.myrods.api.Api;
import nl.tsmeele.myrods.api.ModAVUMetadataInp;

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
