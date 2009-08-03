package spine.payload.codec.emule;

import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.datamodel.functions.Exception.MethodNotSupportedException;

public class ServiceWarningMessage extends SpineCodec {

	public SpineObject decode(int nodeID, byte[] payload)throws MethodNotSupportedException {
		spine.datamodel.serviceMessages.ServiceWarningMessage swm=new spine.datamodel.serviceMessages.ServiceWarningMessage();
		swm.setNodeID(nodeID);
		swm.setMessageDetail(payload[1]);
		return swm;
	}

}
