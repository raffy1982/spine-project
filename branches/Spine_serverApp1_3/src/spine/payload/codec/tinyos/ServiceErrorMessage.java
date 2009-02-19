package spine.payload.codec.tinyos;

import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.datamodel.functions.Exception.MethodNotSupportedException;

public class ServiceErrorMessage extends SpineCodec {
	
	
	public SpineObject decode (int nodeID, byte[] payload)throws MethodNotSupportedException {
		spine.datamodel.serviceMessages.ServiceErrorMessage sem=new spine.datamodel.serviceMessages.ServiceErrorMessage();
		sem.setNodeID(nodeID);
		sem.setMessageDetail(payload[1]);
		
		return sem;
	}
	

}
