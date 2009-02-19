package spine.datamodel.serviceMessages;

import spine.SPINEServiceMessageConstants;
import spine.datamodel.ServiceMessage;

public class ServiceWarningMessage extends ServiceMessage{
	
		public ServiceWarningMessage() {
			super();
			setMessageType(SPINEServiceMessageConstants.WARNING);
		}
		

}
