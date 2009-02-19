package spine.datamodel.serviceMessages;

import spine.SPINEServiceMessageConstants;
import spine.datamodel.ServiceMessage;

public class ServiceAckMessage extends ServiceMessage {
	public ServiceAckMessage() {
		super();
		setMessageType(SPINEServiceMessageConstants.ACK);
	}
	

}
