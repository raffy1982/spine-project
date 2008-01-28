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
 * This class represent a well formatted AMP Remove Feature Packet.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class RemoveFeature {
	
	private Header header;
	private short featureCode;
	private short sensorCode;
	private boolean disableAxis0;
	private boolean disableAxis1;
	private boolean disableAxis2;
	
	/**
	 * Constructor of a Remove Feature Packet
	 * 
	 * @param header the header of this packet
	 * @param featureCode 
	 * @param sensorCode 
	 * @param disableAxis0 
	 * @param disableAxis1 
	 * @param disableAxis2 
	 */
	public RemoveFeature(Header header, short featureCode, short sensorCode, boolean disableAxis0, 
				boolean disableAxis1, boolean disableAxis2) {
		this.header = header;
		this.featureCode = featureCode;
		this.sensorCode = sensorCode;
		this.disableAxis0 = disableAxis0;
		this.disableAxis1 = disableAxis1;
		this.disableAxis2 = disableAxis2;	
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
	 * @return the AMP header
	 * @see com.tilab.spine.constants.FeatureCodes for the standard feature coding
	 */
	public short getFeatureCode() {
		return featureCode;
	}

	/**
	 * Setter method
	 * 
	 * @param featureCode the feature code
	 * @see com.tilab.spine.constants.FeatureCodes for the standard feature coding
	 */
	public void setFeatureCode(short featureCode) {
		this.featureCode = featureCode;
	}

	/**
	 * Getter method
	 * 
	 * @return the sensor code
	 * @see com.tilab.spine.constants.SensorCodes for the standard sensor and axis coding
	 */
	public short getSensorCode() {
		return sensorCode;
	}

	/**
	 * Setter method
	 * 
	 * @param sensorCode the sensor code
	 * @see com.tilab.spine.constants.SensorCodes for the standard sensor and axis coding
	 */
	public void setSensorCode(short sensorCode) {
		this.sensorCode = sensorCode;
	}

	/**
	 * Getter method
	 * 
	 * @return <code>true</code> if the feature has to be disabled 
	 * 		   on the x-axis of the specified sensor
	 * @see com.tilab.spine.constants.SensorCodes for the standard axis coding
	 */
	public boolean isDisableAxis0() {
		return disableAxis0;
	}

	/**
	 * Setter method
	 * 
	 * @param disableAxis0 <code>true</code> if the feature has to be disabled 
	 * 		  on the x-axis of the specified sensor
	 * @see com.tilab.spine.constants.SensorCodes for the standard axis coding
	 */
	public void setDisableAxis0(boolean disableAxis0) {
		this.disableAxis0 = disableAxis0;
	}

	/**
	 * Getter method
	 * 
	 * @return <code>true</code> if the feature has to be disabled 
	 * 		   on the y-axis of the specified sensor
	 * @see com.tilab.spine.constants.SensorCodes for the standard axis coding
	 */
	public boolean isDisableAxis1() {
		return disableAxis1;
	}

	/**
	 * Setter method
	 * 
	 * @param disableAxis1 <code>true</code> if the feature has to be disabled 
	 * 		  on the y-axis of the specified sensor
	 * @see com.tilab.spine.constants.SensorCodes for the standard axis coding
	 */
	public void setDisableAxis1(boolean disableAxis1) {
		this.disableAxis1 = disableAxis1;
	}

	/**
	 * Getter method
	 * 
	 * @return <code>true</code> if the feature has to be disabled 
	 * 		   on the z-axis of the specified sensor
	 * @see com.tilab.spine.constants.SensorCodes for the standard axis coding
	 */
	public boolean isDisableAxis2() {
		return disableAxis2;
	}

	/**
	 * Setter method
	 * 
	 * @param disableAxis2 <code>true</code> if the feature has to be disabled 
	 * 		  on the z-axis of the specified sensor
	 * @see com.tilab.spine.constants.SensorCodes for the standard axis coding
	 */
	public void setDisableAxis2(boolean disableAxis2) {
		this.disableAxis2 = disableAxis2;
	}
}
