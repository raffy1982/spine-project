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
 * This class represent a well formatted AMP Feature Activation Packet.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class FeatureActivation {
	
	private Header header;
	private short featureCode;
	private short windowFrame;
	private short shift;
	private int samplingTime;
	private short sensorCode;
	private boolean activeAxis0;
	private boolean activeAxis1;
	private boolean activeAxis2;
	
	/**
	 * Constructor for a FeatureActivation object
	 * 
	 * @param header the header of this packet
	 * @param featureCode the feature code
	 * @param windowFrame the window size in terms of samples numbers
	 * @param shift the shift size in terms of samples numbers
	 * @param samplingTime the sampling rate expressed in ms
	 * @param sensorCode the sensor code
	 * @param activeAxis0 <code>true</code> if the feature has to be activated 
	 * 						on the x-axis of the specified sensor
	 * @param activeAxis1 <code>true</code> if the feature has to be activated 
	 * 						on the y-axis of the specified sensor
	 * @param activeAxis2 <code>true</code> if the feature has to be activated 
	 * 						on the z-axis of the specified sensor
	 * 
	 * @see com.tilab.spine.constants.FeatureCodes for the standard feature coding
	 * @see com.tilab.spine.constants.SensorCodes for the standard sensor and axis coding
	 */
	public FeatureActivation(Header header, short featureCode, short windowFrame, short shift,
							 int samplingTime, short sensorCode, boolean activeAxis0, boolean activeAxis1,
							 boolean activeAxis2) {
		this.header = header;
		this.featureCode = featureCode;
		this.windowFrame = windowFrame;
		this.shift = shift;
		this.samplingTime = samplingTime;
		this.sensorCode = sensorCode;
		this.activeAxis0 = activeAxis0;
		this.activeAxis1 = activeAxis1;
		this.activeAxis2 = activeAxis2;
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
	 * @return the window size
	 */
	public short getWindowFrame() {
		return windowFrame;
	}
	
	/**
	 * Setter method
	 * 
	 * @param windowFrame the window size
	 */
	public void setWindowFrame(short windowFrame) {
		this.windowFrame = windowFrame;
	}
	
	/**
	 * Getter method
	 * 
	 * @return the shift value
	 */
	public short getShift() {
		return shift;
	}
	
	/**
	 * Setter method
	 * 
	 * @param shift the shift value
	 */
	public void setShift(short shift) {
		this.shift = shift;
	}
	
	/**
	 * Getter method
	 * 
	 * @return the sampling rate
	 */
	public int getSamplingTime() {
		return samplingTime;
	}
	
	/**
	 * Setter method
	 * 
	 * @param samplingTime the sampling rate
	 */
	public void setSamplingTime(int samplingTime) {
		this.samplingTime = samplingTime;
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
	 * @return <code>true</code> if the feature has to be activated 
	 * 		   on the x-axis of the specified sensor
	 * @see com.tilab.spine.constants.SensorCodes for the standard axis coding
	 */
	public boolean isActiveAxis0() {
		return activeAxis0;
	}
	
	/**
	 * Setter method
	 * 
	 * @param activeAxis0 <code>true</code> if the feature has to be activated 
	 * 		  on the x-axis of the specified sensor
	 * @see com.tilab.spine.constants.SensorCodes for the standard axis coding
	 */
	public void setActiveAxis0(boolean activeAxis0) {
		this.activeAxis0 = activeAxis0;
	}
	
	/**
	 * Getter method
	 * 
	 * @return <code>true</code> if the feature has to be activated 
	 * 		   on the y-axis of the specified sensor
	 * @see com.tilab.spine.constants.SensorCodes for the standard axis coding
	 */
	public boolean isActiveAxis1() {
		return activeAxis1;
	}
	
	/**
	 * Setter method
	 * 
	 * @param activeAxis1 <code>true</code> if the feature has to be activated 
	 * 		  on the y-axis of the specified sensor
	 * @see com.tilab.spine.constants.SensorCodes for the standard axis coding
	 */
	public void setActiveAxis1(boolean activeAxis1) {
		this.activeAxis1 = activeAxis1;
	}
	
	/**
	 * Getter method
	 * 
	 * @return <code>true</code> if the feature has to be activated 
	 * 		   on the z-axis of the specified sensor
	 * @see com.tilab.spine.constants.SensorCodes for the standard axis coding
	 */
	public boolean isActiveAxis2() {
		return activeAxis2;
	}
	
	/**
	 * Setter method
	 * 
	 * @param activeAxis2 <code>true</code> if the feature has to be activated 
	 * 		  on the z-axis of the specified sensor
	 * @see com.tilab.spine.constants.SensorCodes for the standard axis coding
	 */
	public void setActiveAxis2(boolean activeAxis2) {
		this.activeAxis2 = activeAxis2;
	}
	
}
