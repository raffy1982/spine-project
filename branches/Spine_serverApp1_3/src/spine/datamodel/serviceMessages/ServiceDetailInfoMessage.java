package spine.datamodel.serviceMessages;



public class ServiceDetailInfoMessage extends ServiceInfoMessage {
	public static final int HIGTHLEVEL=2,LOWLEVEL=1;
	protected int computationType;
	protected double statistics;
	protected int codiceFeature;
	protected byte channelMask;
	protected int numElemeForChannel;
	protected String description="not avaible description for this type ";
	
	public ServiceDetailInfoMessage() {
		super();
	}

	public int getComputationType() {
		return computationType;
	}

	public void setComputationType(int computationType) {
		this.computationType = computationType;
	}

	public double getStatistics() {
		return statistics;
	}

	public void setStatistics(double statistics) {
		this.statistics = statistics;
	}

	public int getCodiceFeature() {
		return codiceFeature;
	}

	public void setCodiceFeature(int codiceFeature) {
		this.codiceFeature = codiceFeature;
	}

	public byte getChannelMask() {
		return channelMask;
	}

	public void setChannelMask(byte channelMask) {
		this.channelMask = channelMask;
	}

	public int getNumElemeForChannel() {
		return numElemeForChannel;
	}

	public void setNumElemeForChannel(int numElemeForChannel) {
		this.numElemeForChannel = numElemeForChannel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString() {
		return super.toString()+ description +"type :"+computationType+"  statValue="+statistics;
	}
	
	

	
	
	
	
	
	

}
