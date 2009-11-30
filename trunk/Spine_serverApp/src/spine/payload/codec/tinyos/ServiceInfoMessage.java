package spine.payload.codec.tinyos;


import spine.datamodel.Node;
import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.exceptions.MethodNotSupportedException;


public class ServiceInfoMessage extends SpineCodec {
	
	public byte[] encode(SpineObject payload)throws MethodNotSupportedException {
		throw new MethodNotSupportedException("encode");
	};
	
	public SpineObject decode(Node node, byte[] payload) throws MethodNotSupportedException {
		spine.datamodel.serviceMessages.ServiceInfoMessage sim=new spine.datamodel.serviceMessages.ServiceInfoMessage();
		sim.setNode(node);
		sim.setMessageDetail(payload[1]);
		return sim;
	}
			
}

