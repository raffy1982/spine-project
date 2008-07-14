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

public class SpineServiceAdvertisement {

	public static byte[] parse(byte[] payload) {
		byte sensorsNr = payload[0];
		byte librariesNr = payload[1+sensorsNr];		
		byte[] data = new byte[1 + sensorsNr*2 + 1 + librariesNr*2];
				
		data[0] = sensorsNr;
		
		for (int i = 0; i<sensorsNr; i++) {
			data[1+i*2] = (byte)((payload[1+i] & 0xFF)>>4); 
			data[(1+i*2) + 1] = (byte)(payload[1+i] & 0x0F);			
		}
		
		data[1+sensorsNr*2] = librariesNr;	
		
		for (int i = 0; i<librariesNr; i++) {
			data[(1+sensorsNr*2)+1+i*2] = (byte)((payload[1+sensorsNr+1+i] & 0xFF)>>5); 
			data[(1+sensorsNr*2)+1+i*2+1] = (byte)(payload[1+sensorsNr+1+i] & 0x1F); // 0x1F = 0001 1111 binary
		}
		
		return data;
	}

}
