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
 * @version 1.3
 */

package spine.datamodel;

import spine.Properties;
import spine.datamodel.functions.CodecInfo;
import spine.datamodel.functions.SpineObject;

public abstract class Data implements SpineObject{
	
	private static Properties prop = Properties.getProperties();
	
	private static final String SPINEDATACODEC_PACKAGE_PREFIX = "spine.payload.codec.";
	private static final String SPINEDATACODEC_PACKAGE = SPINEDATACODEC_PACKAGE_PREFIX + 
								prop.getProperty(Properties.SPINEDATACODEC_PACKAGE_SUFFIX_KEY);
	
	protected static CodecInfo codecInformation=null;
	
	protected long timestamp = 0;

	protected Node node = null;
	
	/**
	 * @deprecated  
	 */
	protected int nodeID = 0;
	
	protected byte functionCode = -1;
	
	protected Data() {}
		

	/**
	 * @param node
	 * @param payload
	 */
	public void baseInit(Node node, byte[] payload) {
		timestamp = System.currentTimeMillis();
		this.nodeID = node.getPhysicalID().getAsInt();
		this.node = node;
		
		//  Setting functionCode
		try {
			// dynamic class loading of the proper CodecInformation
			if (codecInformation==null){
				Class g = Class.forName(SPINEDATACODEC_PACKAGE + 
				       "CodecInformation");
				codecInformation = (CodecInfo)g.newInstance();
			} 
			functionCode=codecInformation.getFunctionCode(payload);
		} catch (Exception e) { 
			System.out.println(e); 
			return;
		} 	
	}
	
	/**
	 * 
	 * @param functionCode
	 */
	public void setFunctionCode(byte functionCode) {
		this.functionCode = functionCode;
	}
	/**
	 * 
	 * @param node
	 */
	public void setNode(Node node) {
		this.node = node;
		this.nodeID = node.getPhysicalID().getAsInt();
	}
	/**
	 * 
	 * @param timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;

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
	 * Getter method of the ID of the node generating the data
	 * 
	 * @return the node of the function generating the data
	 */
	public Node getNode() {
		return this.node;
	}
	
	/**
	 * Getter method of the ID of the node generating the data
	 * 
	 * @return the nodeID of the function generating of the data
	 * 
	 * @deprecated
	 */
	public int getNodeID() {
		return this.nodeID;
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
	public static int convertFourBytesToInt(byte[] bytes, int index) {    
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
	public static int convertTwoBytesToInt(byte[] bytes, int index) {
		if(bytes.length < 2) return 0;
		
		return   (bytes[index + 1] & 0xFF) |
		        ((bytes[index] & 0xFF) << 8);
	}
	
}

