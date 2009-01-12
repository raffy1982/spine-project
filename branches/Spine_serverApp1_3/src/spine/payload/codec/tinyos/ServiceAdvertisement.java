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
 * This class contains the static method to parse (decompress) a 
 * TinyOS SPINE Service Advertisement packet payload into a platform independent one.
 *
 * Note that this class is only used internally at the framework. 
 *
 * @author Raffaele Gravina
 * @author Alessia Salmeri
 *
 * @version 1.3
 */

package spine.payload.codec.tinyos;

import spine.datamodel.functions.*;


public class ServiceAdvertisement implements SpineServiceAdvertisement {

	/**
	 * Decompress Service Advertisement packet payload into a platform independent packet payload
	 * 
	 * @param payload the low level byte array containing the payload of the Spine Service Advertisement packet to parse (decompress)
	 * @return still a byte array representing the platform independent Spine Service Advertisement packet payload.
	 */
	public byte[] decode(byte[] payload) {
		byte sensorsNr = payload[0];
		byte librariesListSize = payload[1+sensorsNr];		
		byte[] data = new byte[1 + sensorsNr*2 + 1 + librariesListSize];
				
		data[0] = sensorsNr;
		
		for (int i = 0; i<sensorsNr; i++) {
			data[1+i*2] = (byte)((payload[1+i] & 0xFF)>>4); 
			data[(1+i*2) + 1] = (byte)(payload[1+i] & 0x0F);			
		}
		
		data[1+sensorsNr*2] = librariesListSize;	
		
		for (int i = 0; i<librariesListSize; i++) 
			data[(1+sensorsNr*2)+1+i] = payload[1+sensorsNr+1+i];
			
		return data;
	}

}
