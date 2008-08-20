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
 * This class contains the static method to parse (decompress) a 
 * TinyOS SPINE Data packet payload into a platform independent one.
 * This class, using dynamic class loading, call the actual class 
 * responsible of decompressing the current Data packet. 
 * 
 * Note that this class is only used internally at the framework.
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

package spine.communication.tinyos;

import spine.SPINEFunctionConstants;

public abstract class SpineData {

	private final static String SPINEDATA_FUNCT_CLASSNAME_PREFIX = "spine.communication.tinyos.";
	private final static String SPINEDATA_FUNCT_CLASSNAME_SUFFIX = "SpineData";
	
	/**
	 * Decompress Data packet payload into a platform independent packet payload
	 * Every derivate class of the SpineData must implement this method to decompress properly the 
	 * specific type of data inside the Data packet.
	 * 
	 * @param payload the low level byte array containing the payload of the Data packet to parse (decompress)
	 * @return still a byte array representing the platform independent Data packet payload.
	 */
	protected abstract byte[] decode(byte[] payload);
	
	/**
	 * Decompress Data packet payload into a platform independent packet payload
	 * 
	 * @param payload the low level byte array containing the payload of the Data packet to parse (decompress)
	 * @return still a byte array 
	 */
	protected static byte[] parse(byte[] payload) {
		byte functionCode = payload[0];
		
		try {
			Class c = Class.forName(SPINEDATA_FUNCT_CLASSNAME_PREFIX + 
									SPINEFunctionConstants.functionCodeToString(functionCode) + 
									SPINEDATA_FUNCT_CLASSNAME_SUFFIX);
			return ((SpineData)c.newInstance()).decode(payload);
		} catch (ClassNotFoundException e) { System.out.println(e); } 
		  catch (InstantiationException e) { System.out.println(e); } 
		  catch (IllegalAccessException e) { System.out.println(e);	}
		
		return null;  
	}	
}
