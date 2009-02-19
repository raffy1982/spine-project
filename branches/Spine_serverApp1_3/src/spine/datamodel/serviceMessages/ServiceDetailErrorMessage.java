package spine.datamodel.serviceMessages;

import spine.SPINESensorConstants;
import spine.datamodel.serviceMessages.ServiceErrorMessage;

public class ServiceDetailErrorMessage extends ServiceErrorMessage {
	protected byte sensorCode=0,channelMask=0;
	protected String description="";
	
	public ServiceDetailErrorMessage() {
		super();
	}
	public void setChannelMask(byte channelMask) {
		this.channelMask = channelMask;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setSensorCode(byte sensorCode) {
		this.sensorCode = sensorCode;
	}
	public byte getChannelMask() {
		return channelMask;
	}
	public byte getSensorCode() {
		return sensorCode;
	}
	public String getDescription() {
		return description;
	}

	public String toString() {
		// TODO Auto-generated method stub
		String g=(sensorCode!=0)?(SPINESensorConstants.sensorCodeToString(sensorCode)+" "+((channelMask!=0)?SPINESensorConstants.channelBitmaskToString(channelMask):"")):"";
		return super.toString()+"detail: "+getDescription()+" "+g;
	}
}
