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
 * This class represents the basic SPINE Data object. 
 * 
 * Data is a generic container if actual, function specific, data coming from a particular node.
 * Regarding to the function that generated the data, this class is able, if a match in found into the Properties set, 
 * to dynamically load the proper 'function'Data class, that is called for decoding appropriately the low level data packet.
 * 
 * That means for each function in the node, a new class, 
 * that extends spine.datamodel.Data and overrides the decode method, must be written. 
 * Refer to spine.datamodel.FeatureData or spine.datamodel.OneShotData as good examples.    
 *
 * @author Raffaele Gravina
 * @author Philip Kuryloski
 *
 * @version 1.2
 */

package spine.datamodel;

import java.lang.reflect.Method;

import spine.SPINEFunctionConstants;

public abstract class Data {
	
	protected long timestamp = 0;

	protected int nodeID = 0;
	protected byte functionCode = -1;
	
	protected Data() {}
		
	/**
	 * Initialize a new data object with a message
	 *
	 * @param nodeID ID of the node from which the message originated
	 * @param payload raw payload of the message
	 * @return the newly initialized object or null if the init fails
	 */
	public abstract Data init(int nodeID, byte[] payload);
	
	
	/**
	 * Initialization which should be in Data.init and called by super in child
	 * init methods, but is instead put here because Data.init must be abstract
	 * to be appropriately used by DataFactory
	 *
	 * @see spine.datamodel.DataFactory
	 */
	protected void baseInit(int nodeID, byte[] payload) {
		timestamp = System.currentTimeMillis();
		this.nodeID = nodeID;
		functionCode = payload[0];
	}
	

	/**
	 * Getter method of the code of the function generating of the data 
	 * 
	 * @return the code of the function generating of the data
	 */
	public byte getFunctionCode() {
		return this.functionCode;
	}
	
	/**
	 * Getter method of the data creation timestamp
	 * 
	 * @return the data creation timestamp
	 */
	public long getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * Converts the four following bytes in the array 'bytes' starting from the index 'index' 
	 * into the corresponding integer
	 * 
	 * @param bytes the byte array from where to take the 4 bytes to be converted to an integer 
	 * @param index the starting index on the interested portion to convert
	 * 
	 * @return the converted integer
	 */
	protected static int convertFourBytesToInt(byte[] bytes, int index) {        
		if(bytes.length < 4) return 0;
		
		return ( bytes[index + 3] & 0xFF) 		 |
	           ((bytes[index + 2] & 0xFF) << 8)  |
	           ((bytes[index + 1] & 0xFF) << 16) |
	           ((bytes[index] & 0xFF) << 24);
	}
	
	/**
	 * Converts the two following bytes in the array 'bytes' starting from the index 'index' 
	 * into the corresponding integer
	 * 
	 * @param bytes the byte array from where to take the 2 bytes to be converted to an integer
	 * @param index the starting index on the interested portion to convert
	 * 
	 * @return the converted integer
	 */
	protected static int convertTwoBytesToInt(byte[] bytes, int index) {
		if(bytes.length < 2) return 0;
		
		return   (bytes[index + 1] & 0xFF) |
		        ((bytes[index] & 0xFF) << 8);
	}
	
}

