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

import com.tilab.spine.constants.Constants;

/**
 *
 * This class represent a well formatted AMP Header.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class Header {
	
	private short version;
	private boolean extended;
	private short pktType;
	private short groupID;
	private short sourceID;
	private short destID;
	private short timeStamp;
	
	/**
	 * This constructor is used to create a standard AMP header, specifying only 
	 * the minimal fields required
	 * 
	 * @param pktType the type of the packet the header will be associated with.
	 * @param groupID the group ID of the application instance
	 * @param destID the recipient address of the packet. 
	 * 
	 * @see	com.tilab.spine.constants.Constants	to use the well known addresses  
	 */
	public Header(short pktType, short groupID, short destID) {
		this.version = Constants.CURRENT_AMP_VERSION;
		this.extended = Constants.STANDARD_PACKET;
		this.pktType = pktType;
		this.groupID = groupID;
		this.sourceID = Constants.BASE_STATION_ADDRESS;
		this.destID = destID;
		this.timeStamp = 0;		
	}
	
	/**
	 * This constructor is used to create a generic AMP header.
	 * 
	 * @param ext indicates if the packet has an extension
	 * @param pktType the type of the packet the header will be associated with.
	 * @param groupID the group ID of the application instance
	 * @param destID the recipient address of the packet.
	 * @param timeStamp the timestamp of the packet 
	 */
	public Header(boolean ext, short pktType, short groupID, short destID, short timeStamp) {
		this.version = Constants.CURRENT_AMP_VERSION;
		this.extended = ext;
		this.pktType = pktType;
		this.groupID = groupID;
		this.sourceID = Constants.BASE_STATION_ADDRESS;
		this.destID = destID;
		this.timeStamp = timeStamp;		
	}
	
	/**
	 * This is a constructor that can be used only within the amp.packets package, 
	 * since it allows to specify freely some critical (and normally immutable at runtime) fields,
	 * such as the AMP version or the sender address.
	 * 
	 * @param version the AMP version used by the application
	 * @param ext indicates if the packet has an extension
	 * @param pktType the type of the packet the header will be associated with.
	 * @param groupID the group ID of the application instance
	 * @param sourceID the address of the sender
	 * @param destID the recipient address of the packet.
	 * @param timeStamp the timestamp of the packet 
	 */
	Header(short version, boolean ext, short pktType, short groupID, short sourceID, short destID, short timeStamp) {
		this.version = version;
		this.extended = ext;
		this.pktType = pktType;
		this.groupID = groupID;
		this.sourceID = sourceID;
		this.destID = destID;
		this.timeStamp = timeStamp;		
	}

	/**
	 * Getter method
	 * 
	 * @return the AMP version specified in the header
	 */
	public short getVersion() {
		return version;
	}

	/**
	 * Setter method
	 * 
	 * @param version the AMP version
	 */
	public void setVersion(short version) {
		this.version = version;
	}

	/**
	 * Getter method
	 * 
	 * @return <code>true</code> if the packet has an extension 
	 */
	public boolean isExtented() {
		return extended;
	}

	/**
	 * Setter method
	 * 
	 * @param ext <code>true</code> if the packet has an extension
	 */
	public void setExtented(boolean ext) {
		this.extended = ext;
	}

	/**
	 * Getter method
	 * 
	 * @return the packet type code specified in the header
	 * @see com.tilab.spine.constants.PacketConstants to refer to the proper codes
	 */
	public short getPktType() {
		return pktType;
	}

	/**
	 * Setter method
	 * 
	 * @param pktType the packet type code
	 * @see com.tilab.spine.constants.PacketConstants to refer to the proper codes
	 */
	public void setPktType(short pktType) {
		this.pktType = pktType;
	}

	/**
	 * Getter method
	 * 
	 * @return the group ID specified in the header
	 */
	public short getGroupID() {
		return groupID;
	}

	/**
	 * Setter method
	 * 
	 * @param groupID the group ID
	 */
	public void setGroupID(short groupID) {
		this.groupID = groupID;
	}

	/**
	 * Getter method
	 * 
	 * @return the sender address of the packet
	 */
	public short getSourceID() {
		return sourceID;
	}

	/**
	 * Setter method
	 * 
	 * @param sourceID the source ID
	 */
	public void setSourceID(short sourceID) {
		this.sourceID = sourceID;
	}

	/**
	 * Getter method
	 * 
	 * @return the destination address of the packet
	 */
	public short getDestID() {
		return destID;
	}

	/**
	 * Setter method
	 * 
	 * @param destID the destination ID
	 */
	public void setDestID(short destID) {
		this.destID = destID;
	}

	/**
	 * Getter method
	 * 
	 * @return the timestamp specified in the header
	 */
	public short getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Setter method
	 * 
	 * @param timeStamp the timestamp
	 */
	public void setTimeStamp(short timeStamp) {
		this.timeStamp = timeStamp;
	}
	
}
