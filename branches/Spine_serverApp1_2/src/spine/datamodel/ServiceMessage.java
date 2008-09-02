/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic configuration of feature extraction capabilities 
of WSN nodes via an OtA protocol

Copyright (C) 2007 Telecom Italia S.p.A. 
 
GNU Lesser General Public License
 
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
 
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

/**
* This class represents the ServiceMessage entity.
* It contains a constructor, toString and getters methods.
*
* @author Raffaele Gravina
*
* @version 1.2
*/

package spine.datamodel;

public class ServiceMessage {


	// MESSAGE TYPES
	public static final byte ERROR = 0;
	public static final byte WARNING = 1;
	
	// MESSAGE DATAILS
	public static final byte CONNECTION_FAIL = 0;
	public static final byte UNKNOWN_PKT_RECEIVED = 10;
	
	// MESSAGE TYPES LABELS
	public static final String ERROR_LABEL = "Error";
	public static final String WARNING_LABEL = "Warning";
	
	// MESSAGE DATAILS LABELS
	public static final String CONNECTION_FAIL_LABEL = "Connection Fail";
	public static final String UNKNOWN_PKT_RECEIVED_LABEL = "Unknown Packet Received";
	
	
	private int nodeID = -1;
	private byte messageType = -1;
	private byte messageDetail = -1;
	

	/**
	 * Constructor of a ServiceMessage object.
	 * This is used by the lower level components of the framework for creating ServiceMessage objects
	 * from a low level ServiceMessage packet received by remote nodes.
	 * 
	 * @param nodeID the source nodeID of the ServiceMessage
	 * @param payload a two-length byte[] array containing ServiceMessage the structured as: [messageTypeCode, messageDetailCode]
	 */
	public ServiceMessage(int nodeID, byte[] payload) {
		this.nodeID = nodeID;
		this.messageType = payload[0];
		this.messageDetail = payload[1];
	}

	/**
	 * Constructor of a ServiceMessage object.
	 * This is basically used by the SPINEManager for creating ServiceMessage objects directed to the application.
	 * Refer to the public constants exposed by this class for the available message types and details
	 * 
	 * @param nodeID the source nodeID of the ServiceMessage
	 * @param messageType the message type code of the ServiceMessage
	 * @param messageDetail the message detail code of the ServiceMessage
	 */
	public ServiceMessage(int nodeID, byte messageType, byte messageDetail) {
		this.nodeID = nodeID;
		this.messageType = messageType;
		this.messageDetail = messageDetail;
	}
	
	/**
	 * Returns the string label mapped to the given message type code
	 * 
	 * @param messageType the message type to be returned as a string label
	 * 
	 * @return the message type string label mapped to the given message type code
	 */
	public static String messageTypeToString(byte messageType) {
		switch(messageType) {
			case ERROR: return ERROR_LABEL;
			case WARNING: return WARNING_LABEL;
			default: return "?";
		}
	}
	
	/**
	 * Returns the string label mapped to the given message detail code
	 * 
	 * @param messageDetail the message detail to be returned as a string label
	 * 
	 * @return the message detail string label mapped to the given message detail code 
	 */
	public static String messageDetailToString(byte messageDetail) {
		switch(messageDetail) {
			case CONNECTION_FAIL: return CONNECTION_FAIL_LABEL;
			case UNKNOWN_PKT_RECEIVED: return UNKNOWN_PKT_RECEIVED_LABEL;
			default: return "?";
		}
	}

	/**
	 * 
	 * Returns a string representation of the ServiceMessage object.
	 * 
	 */
	public String toString() {
		return "Service Message From Node: " + this.nodeID + " - " + 
				messageTypeToString(this.messageType) + ": " + messageDetailToString(this.messageDetail);
	}

	/**
	 * Getter method for the message type attribute 
	 * 
	 */
	public byte getMessageType() {
		return messageType;
	}

	/**
	 * Getter method for the message detail attribute 
	 * 
	 */
	public byte getMessageDetail() {
		return messageDetail;
	}

}
