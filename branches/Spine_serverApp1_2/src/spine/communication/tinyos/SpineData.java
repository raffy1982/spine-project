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
 *  
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

package spine.communication.tinyos;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;

public class SpineData {

	public static byte[] parse(byte[] payload) {
		byte[] dataTmp = new byte[579]; 
		short dtIndex = 0;
		short pldIndex = 0;
		
		byte functionCode = (byte)((payload[pldIndex++] & 0xFF)>>3);
		dataTmp[dtIndex++] = functionCode;
		
		//byte paramLen = (byte)payload[pldIndex++];
		pldIndex++;
		
		switch(functionCode) {
			case SPINEFunctionConstants.FEATURE: case SPINEFunctionConstants.MULTI_CHANNEL_FEATURE: {
				byte sensorCode = payload[pldIndex++];
				dataTmp[dtIndex++] = sensorCode;
				
				byte featuresCount = payload[pldIndex++];
				dataTmp[dtIndex++] = featuresCount;
				
				for (int i = 0; i<featuresCount; i++) {
					byte currFeatCode = payload[pldIndex++];
					dataTmp[dtIndex++] = currFeatCode;
					
					byte currSensBitmask = (byte)( (payload[pldIndex]>>4) & 0x0F );
					dataTmp[dtIndex++] = currSensBitmask;
					
					byte resultLen = (byte)(payload[pldIndex++] & 0x0F);					
					for (int j = 1; j<=SPINESensorConstants.MAX_VALUE_TYPES; j++) {							
						if (SPINESensorConstants.chPresent(j, currSensBitmask)) {						
							if (resultLen == 1) {
								dataTmp[dtIndex++] = 0;
								dataTmp[dtIndex++] = 0; 
								dataTmp[dtIndex++] = 0; 
								dataTmp[dtIndex++] = payload[pldIndex++]; 
							}
							else if (resultLen == 2) {
								dataTmp[dtIndex++] = 0;
								dataTmp[dtIndex++] = 0; 
								dataTmp[dtIndex++] = payload[pldIndex++]; 
								dataTmp[dtIndex++] = payload[pldIndex++]; 
							}
							else if (resultLen == 3) {
								dataTmp[dtIndex++] = 0;
								dataTmp[dtIndex++] = payload[pldIndex++]; 
								dataTmp[dtIndex++] = payload[pldIndex++]; 
								dataTmp[dtIndex++] = payload[pldIndex++]; 
							}
							else if (resultLen == 4) {
								dataTmp[dtIndex++] = payload[pldIndex++];
								dataTmp[dtIndex++] = payload[pldIndex++]; 
								dataTmp[dtIndex++] = payload[pldIndex++]; 
								dataTmp[dtIndex++] = payload[pldIndex++]; 
							}	
						}
						else {
							dataTmp[dtIndex++] = 0;
							dataTmp[dtIndex++] = 0; 
							dataTmp[dtIndex++] = 0; 
							dataTmp[dtIndex++] = 0;
						}
					}
				}				
				break;
			}
			case SPINEFunctionConstants.ONE_SHOT: {
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
				
				break;
			}
			default: break;
		}
		
		byte[] data = new byte[dtIndex];
		System.arraycopy(dataTmp, 0, data, 0, data.length);
		
		return data;
	}

}
