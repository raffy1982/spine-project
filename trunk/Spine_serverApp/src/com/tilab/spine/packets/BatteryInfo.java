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
 * This class represent a well formatted Battery Info Packet.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class BatteryInfo {
	
	private Header header;
	private int voltage;
	
	/**
	 * Constructor of a Battery Info Packet
	 * 
	 * @param header the header of this packet
	 * @param voltage the voltage level
	 */
	public BatteryInfo(Header header, int voltage) {
		this.header = header;
		this.voltage = voltage;
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
	 * @return the voltage level as sent by the sender node
	 */
	public int getVoltage() {
		return voltage;
	}
	
	/**
	 * Utility method
	 * 
	 * @return the actual voltage level 
	 */
	public double getActualVoltage() {
		int intVolt = ( (voltage * 3000) / 4096 );
		return ( (double)intVolt / 1000 );
	}
	
}
