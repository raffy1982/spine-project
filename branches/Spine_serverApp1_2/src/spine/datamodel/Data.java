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
 *
 * @version 1.2
 */

package spine.datamodel;

import spine.SPINEFunctionConstants;

public class Data {

	private final static String DATA_FUNCT_CLASSNAME_PREFIX = "spine.datamodel.";
	private final static String DATA_FUNCT_CLASSNAME_SUFFIX = "Data";
	
	protected Object data = null; 
	
	protected byte functionCode = -1;
	
	protected Data() {}
	
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
	public Data(int nodeID, byte[] payload) {
		this.functionCode = payload[0];		
		
		try {
			Class c = Class.forName(DATA_FUNCT_CLASSNAME_PREFIX + 
									SPINEFunctionConstants.functionCodeToString(this.functionCode) + 
									DATA_FUNCT_CLASSNAME_SUFFIX);
			this.data = ((Data)c.newInstance()).decode(nodeID, payload);
		} catch (ClassNotFoundException e) { System.out.println(e); } 
		  catch (InstantiationException e) { System.out.println(e); } 
		  catch (IllegalAccessException e) { System.out.println(e);	}
	}
	
	/**
	 * This method MUST be overridden by any class that extends spine.datamodel.Data 
	 * and must return their own specific high level organization of the given data payload array.
	 * 
	 * @param nodeID the source node generating the data
	 * @param payload the data represented as a byte[] array. Its length and content are 'function specific'
	 *  
	 * @return the specific object resulting from the decoding of the byte[] payload
	 */
	protected Object decode(int nodeID, byte[] payload){ return null; }

	/**
	 * Getter method of the code of the function generating of the data 
	 * 
	 * @return the code of the function generating of the data
	 */
	public byte getFunctionCode() {
		return this.functionCode;
	}
	
	/**
	 * Return an Object that can be casted w.r.t. the specific 'function'Data class data type 
	 * 
	 * @return the Object returned by the specific 'function'Data class decode process 
	 */
	public Object getData() {
		return this.data;
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
	
	/**
	 * 
	 * Returns a string representation of the Data object.
	 * 
	 */
	public String toString() {
		return "" + this.data;
	}

}

