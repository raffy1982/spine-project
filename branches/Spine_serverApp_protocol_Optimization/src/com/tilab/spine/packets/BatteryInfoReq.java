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
* This class represent a well formatted Battery Info Request Packet.
*
* @author Raffaele Gravina
* @author Antonio Guerrieri
*
* @version 1.0
*/
public class BatteryInfoReq {
	
	public static final short MILLISEC = 1;
	
	public static final short SEC = 2;
	
	public static final short MIN = 3;
	
	public static final short ON_FLY_REQUEST = 0;
	
	public static final short CANCEL_PERIODIC_REQUEST = 0x1FFF;
	
	
	public static final String MILLISEC_CAPTION = "ms";
	public static final String SEC_CAPTION = "sec";
	public static final String MIN_CAPTION = "min";

	
	private Header header;
	private boolean isTime;
	private short timeScale;
	private short period;
	
	/** 
     * This constructor is used for generating 'on-fly' battery info requests 
     * 
     * @param header the header for this packet 
     */ 
	public BatteryInfoReq(Header header) {
		this.header = header;
		this.isTime = false;
		this.timeScale = 0;
		this.period = 0;
	}	
	
	public BatteryInfoReq(Header header, boolean isTime, short timeScale, short period) {
		this.header = header;
		this.isTime = isTime;
		this.timeScale = timeScale;
		this.period = period;
	}
	
	
	public Header getHeader() {
		return header;
	}
	
	public void setHeader(Header header) {
		this.header = header;
	}
	
	public boolean isTime() {
		return isTime;
	}
	
	public void setTime(boolean isTime) {
		this.isTime = isTime;
	}
	
	public short getTimeScale() {
		return timeScale;
	}
	
	public void setTimeScale(short timeScale) {
		this.timeScale = timeScale;
	}
	
	public short getPeriod() {
		return period;
	}
	
	public void setPeriod(short period) {
		this.period = period;
	}
}
