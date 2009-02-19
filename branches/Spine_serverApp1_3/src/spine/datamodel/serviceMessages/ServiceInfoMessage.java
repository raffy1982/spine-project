package spine.datamodel.serviceMessages;

//ONLY FOR SPINE TO ZIGBEE
//modify payload->info object type 

import java.util.Date;

import spine.SPINEServiceMessageConstants;
//import spine.datamodel.Data;
import spine.datamodel.ServiceMessage;

public class ServiceInfoMessage extends ServiceMessage {
	protected long timestamp;
	public ServiceInfoMessage() {
		super();
		setMessageType(SPINEServiceMessageConstants.INFO);
		timestamp=System.currentTimeMillis();
	}


public String toString() {
	return super.toString()+"\n Time: "+new Date(timestamp).toLocaleString();
}
}
