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
 * @author Philip Kuryloski
 *
 * @version 1.3
 */

package spine.datamodel;

import spine.SPINEFunctionConstants;

public class DataFactory {

	private final static String DATA_FUNCT_CLASSNAME_PREFIX = "spine.datamodel.";
	private final static String DATA_FUNCT_CLASSNAME_SUFFIX = "Data";
	
	private DataFactory() {}
	
	/**
	 * factory method allowing us to produce the appropriate child class
	 *
	 * @return Subclass of Data based on payload function code
	 */
	public static Data newData(int nodeID, byte[] payload) {
		Data result = null;
		
		byte functionCode = payload[0];
		
		try {
			Class c = Class.forName(DATA_FUNCT_CLASSNAME_PREFIX + 
									SPINEFunctionConstants.functionCodeToString(functionCode) + 
									DATA_FUNCT_CLASSNAME_SUFFIX);
									
			result = ((Data)c.newInstance()).init(nodeID, payload);
			
		} catch (Exception e) { e.printStackTrace(); } 
		
		return result;
	}
	
}

