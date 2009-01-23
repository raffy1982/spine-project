package spine.payload.codec.tinyos;

import spine.datamodel.functions.CodecInfo;

public class CodecInformation implements CodecInfo{

	public byte getFunctionCode(byte[] payload) {
		return payload[0];
	}

}
