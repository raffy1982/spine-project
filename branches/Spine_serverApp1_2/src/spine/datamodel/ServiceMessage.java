package spine.datamodel;

public class ServiceMessage {


	// MESSAGE TYPES
	public static final byte ERROR = 0;
	
	// MESSAGE DATAILS
	public static final byte CONNECTION_FAIL = 0;
	
	// MESSAGE TYPES
	public static final String ERROR_LABEL = "Error";
	
	// MESSAGE DATAILS
	public static final String CONNECTION_FAIL_LABEL = "Connection Fail";
	
	
	private int nodeID = -1;
	private byte messageType = -1;
	private byte messageDetail = -1;
	

	public ServiceMessage(int nodeID, byte[] payload) {
		this.nodeID = nodeID;
		this.messageType = payload[0];
		this.messageDetail = payload[1];
	}

	public ServiceMessage(int nodeID, byte messageType, byte messageDetail) {
		this.nodeID = nodeID;
		this.messageType = messageType;
		this.messageDetail = messageDetail;
	}
	
	public static String messageTypeToString(byte messageType) {
		switch(messageType) {
			case ERROR: return ERROR_LABEL;
			default: return "?";
		}
	}
	
	public static String messageDetailToString(byte messageDetail) {
		switch(messageDetail) {
		case CONNECTION_FAIL: return CONNECTION_FAIL_LABEL;
		default: return "?";
	}
	}

	public String toString() {
		return "Service Message From Node: " + this.nodeID + " - " + 
				messageTypeToString(this.messageType) + ": " + messageDetailToString(this.messageDetail);
	}

}
