package spine.payload.codec.emule;


import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.datamodel.functions.Exception.MethodNotSupportedException;


public class ServiceInfoMessage extends SpineCodec {
	
		public SpineObject decode(int nodeID, byte[] payload)
				throws MethodNotSupportedException {
			spine.datamodel.serviceMessages.ServiceInfoMessage sim=new spine.datamodel.serviceMessages.ServiceInfoMessage();
			sim.setNodeID(nodeID);
			sim.setMessageDetail(payload[1]);
			return sim;
		}
			
	}

