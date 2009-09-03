package spine.payload.codec.tinyos;

import spine.datamodel.Node;
import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.exceptions.MethodNotSupportedException;

public class ServiceErrorMessage extends SpineCodec {
	
	public byte[] encode(SpineObject payload)throws MethodNotSupportedException {
		throw new MethodNotSupportedException("encode");
	};
	
	public SpineObject decode (Node node, byte[] payload)throws MethodNotSupportedException {
		spine.datamodel.serviceMessages.ServiceErrorMessage sem = new spine.datamodel.serviceMessages.ServiceErrorMessage();
		sem.setNode(node);
		sem.setMessageDetail(payload[1]);		
		return sem;
	}
	

}
