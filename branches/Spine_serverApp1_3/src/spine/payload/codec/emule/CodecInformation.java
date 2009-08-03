package spine.payload.codec.emule;

import spine.datamodel.functions.CodecInfo;

public class CodecInformation implements CodecInfo{

	public byte getFunctionCode(byte[] payload) {
		return payload[0];
	}
	

	// New Refactoring ServiceMessage
	public byte getServiceMessageType(byte[] payload) {
		return payload[0];
	}
	
	
	

}
