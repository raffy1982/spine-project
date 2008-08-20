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
*
* This class contains the static method to parse (decompress) a 
* TinyOS SPINE 'OneShot' Data packet payload into a platform independent one.
* This class is invoked only by the SpineData class, thru the dynamic class loading.
* 
* Note that this class is only used internally at the framework.
*
* @author Raffaele Gravina
*
* @version 1.0
*/

package spine.communication.tinyos;

import spine.SPINESensorConstants;

public class OneShotSpineData extends SpineData {

	/**
	 * Decompress 'OneShot' Data packet payload into a platform independent packet payload
	 * 
	 * @param payload the low level byte array containing the payload of the 'OneShot' Data packet to parse (decompress)
	 * @return still a byte array representing the platform independent 'OneShot' Data packet payload.
	 */
	protected byte[] decode(byte[] payload) {
		byte[] dataTmp = new byte[579]; 
		short dtIndex = 0;
		short pldIndex = 0;
		
		byte functionCode = payload[pldIndex++];
		dataTmp[dtIndex++] = functionCode;
		
		//byte paramLen = (byte)payload[pldIndex++];
		pldIndex++;
		
		byte sensorCode = payload[pldIndex++];
		dataTmp[dtIndex++] = sensorCode;
		
		byte bitmask = payload[pldIndex++];
		dataTmp[dtIndex++] = bitmask;				
		
		for (int j = 1; j<=SPINESensorConstants.MAX_VALUE_TYPES; j++) {							
			if (SPINESensorConstants.chPresent(j, bitmask)) {						
					dataTmp[dtIndex++] = payload[pldIndex++]; 
					dataTmp[dtIndex++] = payload[pldIndex++]; 
			}
			else {
				dataTmp[dtIndex++] = 0; 
				dataTmp[dtIndex++] = 0;
			}
		}
				
		byte[] data = new byte[dtIndex];
		System.arraycopy(dataTmp, 0, data, 0, data.length);
		
		return data;
	}
	
}
