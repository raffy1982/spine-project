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

import spine.SPINEPacketsConstants;

public class PacketManager {

	protected static byte[] build(com.tilab.zigbee.Message msg) throws UnknownFunctionException {
		byte[] payload = msg.getPayload();
		
		switch (msg.getClusterId()) {
			case SPINEPacketsConstants.SERVICE_DISCOVERY: 
				case SPINEPacketsConstants.RESET: case SPINEPacketsConstants.SYNCR:
				case SPINEPacketsConstants.START: return payload;
			case SPINEPacketsConstants.SETUP_SENSOR: return SpineSetupSensor.build(payload);
			case SPINEPacketsConstants.SETUP_FUNCTION: return new SpineSetupFunction().build(payload);
			case SPINEPacketsConstants.FUNCTION_REQ: return new SpineFunctionReq().build(payload);
			default: break;
		}
		
		return payload;
	}

	protected static byte[] parse(byte pktType, byte[] payloadBuf) {
		byte[] payload = payloadBuf;
		
		switch (pktType) {
			case SPINEPacketsConstants.SERVICE_ADV: payload = SpineServiceAdvertisement.parse(payload); break;
			case SPINEPacketsConstants.DATA: payload = new SpineData().parse(payload); break;
			case SPINEPacketsConstants.SVC_MSG: break;
			default: break;
		}
		
		return payload;
	}

}
