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
*
* @version 1.2
*/

package spine.datamodel;

public class AlarmData extends Data {
	
	/**
	 * Override of the spine.datamodel.Data decode method
	 * 
	 * @see spine.datamodel.Data
	 */
	protected Object decode(int nodeID, byte[] payload) {
		byte dataType = payload[1];
		byte sensorCode = payload[2];
		byte valueType = payload[3];
		byte alarmType = payload[4];
		int currentValue = Data.convertTwoBytesToInt(payload, 5);
		
		return new Alarm( nodeID, functionCode, dataType, sensorCode, valueType, alarmType, currentValue);
	}
	
}
