package spine.communication.tinyos;

public class SpineStart extends spine.communication.tinyos.SpineTOSMessage {
	
	private final static int LENGTH = 4;
	
	private int activeNodesCount = -1;
	private boolean radioAlwaysOn;
	private boolean enableTDMA;
	
	public byte[] encode() {
		byte[] data = new byte[LENGTH];
		
		data[0] = (byte)(this.activeNodesCount>>8);
		data[1] = (byte)this.activeNodesCount;
		data[2] = (this.radioAlwaysOn)? (byte)1: 0;
		data[3] = (this.enableTDMA)? (byte)1: 0;
		
		return data;
	}

	public void setActiveNodesCount(int activeNodesCount) {
		this.activeNodesCount = activeNodesCount;
	}

	public void setRadioAlwaysOn(boolean radioAlwaysOn) {
		this.radioAlwaysOn = radioAlwaysOn;
	}

	public void setEnableTDMA(boolean enableTDMA) {
		this.enableTDMA = enableTDMA;
	}
}
