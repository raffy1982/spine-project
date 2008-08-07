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
 *  This class represents the Alarm entity.
 *  It contains a constructor, a toString and getters methods.
 *
 * @author Roberta Giannatonio
 *
 * @version 1.2
 */

package spine.datamodel;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;

public class Alarm {
	
	private int nodeID;
	private byte functionCode;
	private byte dataType;
	private byte sensorCode;
	private byte valueType;
	private byte alarmType;
	private int currentValue;
	

	/**
	 * Constructor of a Alarm object.
	 * This is used by the lower level components of the framework for creating Alarm objects
	 * from a low level Alarm data packet received by remote nodes. 
	 * 
	 * @param nodeID the node id
	 * @param functionCode the function code = ALARM
	 * @param sensorCode the sensor code
	 * @param dataType the type of data on which the alarm is activated
	 * @param valueType the sensor channel
	 * @param alarmType the type of alarm
	 * @param currentValue the value reported by the alarm
	 */
	protected Alarm(int nodeID, byte functionCode, byte dataType, byte sensorCode, byte valueType, byte alarmType, int currentValue) {
		this.nodeID = nodeID;

		this.functionCode = functionCode;
		this.dataType = dataType;
		
		this.sensorCode = sensorCode;
		this.valueType = valueType;
		this.alarmType = alarmType;
		
		this.currentValue = currentValue;
	}
	
	/**
	 * Getter method of the node id
	 * @return the node id
	 */
	public int getNodeID() {
		return nodeID;
	}
	
	/**
	 * Getter method of the function code
	 * @return the function code
	 */
	public byte getFunctionCode() {
		return functionCode;
	}
		
	/**
	 * Getter method of the data type
	 * @return the data type
	 */
	public byte getDataType() {
		return dataType;
	}	

	/**
	 * Getter method of the sensor code
	 * @return the sensor code
	 */
	public byte getSensorCode() {
		return sensorCode;
	}

	/**
	 * Getter method of the value type
	 * @return the value type
	 */
	public byte getValueType() {
		return valueType;
	}	
	
	/**
	 * Getter method of the alarm type
	 * @return the alarm type
	 */
	public byte getAlarmType() {
		return alarmType;
	}

	/**
	 * Getter method of the current value
	 * @return the current value
	 */
	public int getCurrentValue() {
		return currentValue;
	}
	
	/**
	 * 
	 * Returns a string representation of the Alarm object.
	 * 
	 */
	public String toString() {
		return "From node: " + this.nodeID + " - " + SPINEFunctionConstants.ALARM_LABEL  + 
				" on " + SPINEFunctionConstants.functionalityCodeToString(SPINEFunctionConstants.FEATURE,this.dataType) + " on sensor " + SPINESensorConstants.sensorCodeToString(this.sensorCode) + 
				" VALUE " + this.currentValue ;
	}
}