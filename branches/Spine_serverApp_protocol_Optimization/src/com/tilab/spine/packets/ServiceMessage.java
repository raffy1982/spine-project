/*****************************************************************
SPINE - Signal Processing In-Note Environment is a framework that 
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

package com.tilab.spine.packets;


/**
 *
 * This class represent a well formatted AMP message Packet.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class ServiceMessage {
	
	private Header header;
	private short messageType;
	private short messageDetail;
	
	/**
	 * Constructor for an message Packet.
	 *  
	 * @param header  the header of the packet
	 * @param messageType the message type code
	 * @param messageDetail message detail code
	 * 
	 * @see com.tilab.spine.constants.ServiceMessageCodes for the standard messages coding
	 */
	public ServiceMessage(Header header, short messageType, short messageDetail) {
		this.header = header;
		this.messageType = messageType;
		this.messageDetail = messageDetail;
	}
	
	/**
	 * Getter method
	 * 
	 * @return the AMP header
	 */
	public Header getHeader() {
		return header;
	}
	
	/**
	 * Setter method
	 * 
	 * @param header the AMP header
	 */
	public void setHeader(Header header) {
		this.header = header;
	}
	
	/**
	 * Getter method
	 * 
	 * @return the message type code
	 */
	public short getMessageType() {
		return messageType;
	}
	
	/**
	 * Setter method
	 * 
	 * @param messageType the message type code
	 */
	public void setMessageType(short messageType) {
		this.messageType = messageType;
	}
	
	/**
	 * Getter method
	 * 
	 * @return the message detail code
	 */
	public short getMessageDetail() {
		return messageDetail;
	}
	
	/**
	 * Setter method
	 * 
	 * @param messageDetail the message detail code
	 */
	public void setMessageDetail(short messageDetail) {
		this.messageDetail = messageDetail;
	}
	
}
