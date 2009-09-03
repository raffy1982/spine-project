package spine.payload.codec.emule;

import spine.datamodel.Node;
import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.exceptions.MethodNotSupportedException;

public class ServiceWarningMessage extends SpineCodec {
	
	public byte[] encode(SpineObject payload) throws MethodNotSupportedException{
		throw new MethodNotSupportedException("encode");
	};
	
	public SpineObject decode(Node node, byte[] payload)throws MethodNotSupportedException {
		spine.datamodel.serviceMessages.ServiceWarningMessage swm=new spine.datamodel.serviceMessages.ServiceWarningMessage();
		swm.setNode(node);
		swm.setMessageDetail(payload[1]);
		return swm;
	}

}
