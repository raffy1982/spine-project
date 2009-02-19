package spine.datamodel.serviceMessages;

import spine.datamodel.serviceMessages.ServiceWarningMessage;

public class ServiceDetailWarningMessage extends ServiceWarningMessage {
	protected String description;
	public ServiceDetailWarningMessage() {
		super();
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}

	public String toString() {
		return super.toString()+" \n detail:"+getDescription();
	}
	
}
