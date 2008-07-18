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

package spine.datamodel;

import java.util.Vector;

import spine.SPINEFunctionConstants;

public class Data {

	private Object data; 
	
	private byte functionCode;
	
	public Data(int nodeID, byte[] payload) {
		this.functionCode = payload[0];
		
		switch (this.functionCode) {
			case SPINEFunctionConstants.FEATURE: {
				data = new Vector();
				
				byte sensorCode = payload[1];
				byte featuresCount = payload[2];
				
				for (int i = 0; i<featuresCount; i++) {
					byte currFeatCode = payload[3+i*18];
					byte currBitmask = payload[(3+i*18) + 1];
					
					/*int currCh1Value = (payload[(3+i*18) + 2]<<24 | payload[(3+i*18) + 3]<<16 | payload[(3+i*18) + 4]<<8 | payload[(3+i*18) + 5]);				
					int currCh2Value = (payload[(3+i*18) + 6]<<24 | payload[(3+i*18) + 7]<<16 | payload[(3+i*18) + 8]<<8 | payload[(3+i*18) + 9]);
					int currCh3Value = (payload[(3+i*18) + 10]<<24 | payload[(3+i*18) + 11]<<16 | payload[(3+i*18) + 12]<<8 | payload[(3+i*18) + 13]);
					int currCh4Value = (payload[(3+i*18) + 14]<<24 | payload[(3+i*18) + 15]<<16 | payload[(3+i*18) + 16]<<8 | payload[(3+i*18) + 17]);*/
					int currCh1Value = convertToInt(payload, (3+i*18) + 2);
					int currCh2Value = convertToInt(payload, (3+i*18) + 6);
					int currCh3Value = convertToInt(payload, (3+i*18) + 10);
					int currCh4Value = convertToInt(payload, (3+i*18) + 14);
					
					((Vector)data).addElement(new Feature(nodeID, this.functionCode, currFeatCode, sensorCode, currBitmask, currCh1Value, currCh2Value, currCh3Value, currCh4Value));
				}		
				break;
			}
			case SPINEFunctionConstants.ONE_SHOT: {
				byte sensorCode = payload[1];
				byte bitmask = payload[2];
				
				int currCh1Value = convertTwoBytesToInt(payload, 3);
				int currCh2Value = convertTwoBytesToInt(payload, 5);
				int currCh3Value = convertTwoBytesToInt(payload, 7);
				int currCh4Value = convertTwoBytesToInt(payload, 9);
				data = new Feature(nodeID, this.functionCode, SPINEFunctionConstants.RAW_DATA, sensorCode, bitmask, currCh1Value, currCh2Value, currCh3Value, currCh4Value);
				break;
			}
			default: break;
		}		
	}
	
	public byte getFunctionCode() {
		return this.functionCode;
	}
	
	public Object getData() {
		return this.data;
	}
	
	private static int convertToInt(byte[] bytes, int index) {        
		return ( bytes[index + 3] & 0xFF) 		 |
	           ((bytes[index + 2] & 0xFF) << 8)  |
	           ((bytes[index + 1] & 0xFF) << 16) |
	           ((bytes[index] & 0xFF) << 24);
	}
	
	private static int convertTwoBytesToInt(byte[] bytes, int index) {
		return   (bytes[index + 1] & 0xFF) |
		        ((bytes[index] & 0xFF) << 8);
	}
	
	public String toString() {
		return "" + this.data;
	}

}

