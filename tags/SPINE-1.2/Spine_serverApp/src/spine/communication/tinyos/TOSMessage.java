/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

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
 *
 * Implementation for TinyOS platforms of the GAL Message interface.
 * 
 * Note that this class is only used internally at the framework. 
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

package spine.communication.tinyos;

public class TOSMessage implements com.tilab.gal.Message {
	
	private short pktType;
	
	private String destID;
	
	private byte[] payload = new byte[0];
	
	private short groupID;
	
	private String sourceID;
	
	private boolean isBroadcast;
	
	//private int maxHopsNumber;
	
	//private byte txSettingsBitMask;
	
	
	public short getMessageId() {
		return pktType;
	}

	public String getDestinationURL() {
		return destID;
	}

	public byte getLinkQuality() {
		return 127;
	}

	public byte[] getPayload() {
		return payload;
	}

	public short getApplicationId() {
		return groupID;
	}

	public int getSecurityStatus() {
		return com.tilab.gal.Message.SECURITYSTATUS_UNSECURE;
	}

	public String getSourceURL() {
		return sourceID;
	}

	public boolean isBroadcast() {
		return isBroadcast;
	}
	

	public void setBroadcastDestination() {
		isBroadcast = true;		
	}

	public void setMessageId(short messageId) {
		this.pktType = messageId;		
	}

	public void setDestinationURL(String serviceConnectionURL) {
		this.destID = serviceConnectionURL;		
	}

	public void setPayload(byte[] payload) {
		this.payload = payload; 
	}

	public void setApplicationId(short applicationId) {
		this.groupID = applicationId;
	}
	
	public void setMaxHopsNumber(int maxHopsNumber) {
		//this.maxHopsNumber = maxHopsNumber;		
	}

	public void setTxSettings(byte txSettings) {
		//this.txSettingsBitMask = txSettings;
	}
	
	protected void setSourceURL(String sourceID) {
		this.sourceID = sourceID;
	}
}
