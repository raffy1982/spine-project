package spine.datamodel.functions;

public interface CodecInfo {
	
	public byte getFunctionCode(byte[] payload);

	public byte getServiceMessageType(byte[] payload);

}
