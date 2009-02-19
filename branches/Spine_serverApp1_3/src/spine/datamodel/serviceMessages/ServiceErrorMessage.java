package spine.datamodel.serviceMessages;

import spine.SPINEServiceMessageConstants;
import spine.datamodel.ServiceMessage;

public class ServiceErrorMessage extends ServiceMessage {
	public ServiceErrorMessage() {
		super();
		setMessageType(SPINEServiceMessageConstants.ERROR);
	}


}
