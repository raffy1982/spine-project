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
 * This class implements a dispatcher pattern and it's used internally for decoding
 * SPINE messages payloads.
 * 
 * Note that this class is only used internally at the framework. 
 *  
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

package spine.communication.tinyos;

import spine.SPINEPacketsConstants;

public class PacketManager {

	/**
	 * Allows the proper decoding of the given payload w.r.t. to its given packet type
	 * 
	 * @param pktType the SPINE packet type of the message 
	 * @param payloadBuf the payload of the message to be decoded
	 * @return the decoded payload
	 */
	protected static byte[] decode(byte pktType, byte[] payloadBuf) {
		byte[] payload = payloadBuf;
		
		switch (pktType) {
			case SPINEPacketsConstants.SERVICE_ADV: payload = SpineServiceAdvertisement.parse(payload); break;
			case SPINEPacketsConstants.DATA: payload = SpineData.parse(payload); break;
			case SPINEPacketsConstants.SVC_MSG: break;
			default: break;
		}
		
		return payload;
	}

}
