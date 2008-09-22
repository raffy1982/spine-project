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
* This class represents the OneShotData entity.
* It contains the decode method for converting low level One-Shot type data into an high level object.
*
* @author Raffaele Gravina
* @author Philip Kuryloski
*
* @version 1.2
*/

package spine.datamodel;

import spine.SPINEFunctionConstants;

public class OneShotData extends Data {
	
	Feature oneShot = null;		

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
			
			byte sensorCode = payload[1];
			byte bitmask = payload[2];
			
			int currCh1Value = convertTwoBytesToInt(payload, 3);
			int currCh2Value = convertTwoBytesToInt(payload, 5);
			int currCh3Value = convertTwoBytesToInt(payload, 7);
			int currCh4Value = convertTwoBytesToInt(payload, 9);
			
			oneShot = new Feature(nodeID, SPINEFunctionConstants.ONE_SHOT, SPINEFunctionConstants.RAW_DATA, sensorCode, bitmask, currCh1Value, currCh2Value, currCh3Value, currCh4Value);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return this;
	}

	/**
	 * 
	 * Returns the one shot feature contained into the OneShot Data message received.
	 * 
	 */
	public Feature getFeature() {
		return oneShot;
	}
	
	
	/**
	 * 
	 * Returns a string representation of the OneShotData object.
	 * 
	 */
	public String toString() {
		return "" + oneShot;
	}
}
