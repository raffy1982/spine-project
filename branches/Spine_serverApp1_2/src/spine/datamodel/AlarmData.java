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
* This class represents the AlarmData entity.
* It contains the decode method for converting low level Alarm type data into an high level object.
*
* @author Roberta Giannantonio
* @author Philip Kuryloski
*
* @version 1.2
*/

package spine.datamodel;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;

public class AlarmData extends Data {
	
	private byte dataType;
	private byte sensorCode;
	private byte valueType;
	private byte alarmType;
	private int currentValue;
	
	/**
	 * Constructor of a Data object. 
	 * Note that Data in just a generic container and the actual Data implementations must be provided and
	 * configured properly within the Properties set. That is done declaring a new "data_function_className_myFunctionCode" property
	 * equal to the full path name of the class that will be responsible of decoding the byte[] data payload.
	 * The usage of properties is to allow the dynamic loading of the classes involved.
	 * 
	 * @param nodeID the source node generating the data
	 * @param payload the data represented as a byte[] array. Its length and content are 'function specific' 
	 */
	public Data init(int nodeID, byte[] payload) {
		try {
			this.baseInit(nodeID, payload);
			
			dataType = payload[1];
			sensorCode = payload[2];
			valueType = payload[3];
			alarmType = payload[4];
			currentValue = Data.convertFourBytesToInt(payload, 5);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return this;
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
