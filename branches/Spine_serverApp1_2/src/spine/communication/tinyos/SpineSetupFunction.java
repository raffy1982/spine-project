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

public class SpineSetupFunction {
	
	public static byte[] build(byte[] payload) throws UnknownFunctionException {
		byte[] data = new byte[payload.length];
		
		byte functionCode = payload[0];
		switch(functionCode) {
			case SPINEFunctionConstants.FEATURE: case SPINEFunctionConstants.MULTI_CHANNEL_FEATURE: {
				if (payload.length != 5)
					return null;
				
				data = new byte[payload.length];
				
				data[0] = (byte)(payload[0]<<3);
				data[1] = payload[1];
				data[2] = (byte)(payload[2]<<4);
				data[3] = payload[3];
				data[4] = payload[4];
				break;
			}
			default: throw new UnknownFunctionException("unknown function '" + functionCode + "' while trying a setup function");
		}
		
		return data;
	}
	
}
