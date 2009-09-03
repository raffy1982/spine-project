package spine.payload.codec.emule;

import spine.datamodel.Node;
import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.exceptions.MethodNotSupportedException;

public class ServiceAckMessage extends SpineCodec {

	public byte[] encode(SpineObject payload) throws MethodNotSupportedException{
		throw new MethodNotSupportedException("encode");
	};
	
	public SpineObject decode(Node node, byte[] payload)throws MethodNotSupportedException {
		spine.datamodel.serviceMessages.ServiceAckMessage sam=new spine.datamodel.serviceMessages.ServiceAckMessage();
		sam.setNode(node);
		sam.setMessageDetail(payload[1]);
		return sam;
	}

}
