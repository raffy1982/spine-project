package spine.datamodel.serviceMessages;

import spine.SPINEServiceMessageConstants;
import spine.datamodel.ServiceMessage;

public class ServiceNotSpecifiedMessage extends ServiceMessage {
	public ServiceNotSpecifiedMessage() {
		super();
		setMessageType(SPINEServiceMessageConstants.NOT_SPECIFIED);
	}
}
