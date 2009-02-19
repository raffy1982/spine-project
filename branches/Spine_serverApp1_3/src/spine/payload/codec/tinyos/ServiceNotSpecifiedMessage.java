package spine.payload.codec.tinyos;

import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.datamodel.functions.Exception.MethodNotSupportedException;

public class ServiceNotSpecifiedMessage extends SpineCodec {
	public SpineObject decode(int nodeID, byte[] payload)throws MethodNotSupportedException {
		spine.datamodel.serviceMessages.ServiceNotSpecifiedMessage snsm=new spine.datamodel.serviceMessages.ServiceNotSpecifiedMessage();
		snsm.setNodeID(nodeID);
		snsm.setMessageDetail(payload[1]);
		return snsm;
	}

}
