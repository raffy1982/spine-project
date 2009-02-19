package spine.payload.codec.tinyos;

import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.datamodel.functions.Exception.MethodNotSupportedException;

public class ServiceAckMessage extends SpineCodec {

	public SpineObject decode(int nodeID, byte[] payload)throws MethodNotSupportedException {
		spine.datamodel.serviceMessages.ServiceAckMessage sam=new spine.datamodel.serviceMessages.ServiceAckMessage();
		sam.setNodeID(nodeID);
		sam.setMessageDetail(payload[1]);
		return sam;
	}

}
