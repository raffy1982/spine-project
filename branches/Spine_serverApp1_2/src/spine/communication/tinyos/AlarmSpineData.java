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
* This class contains the static method to parse (decompress) a 
* TinyOS SPINE 'Alarm' Data packet payload into a platform independent one.
* This class is invoked only by the SpineData class, thru the dynamic class loading.
* 
* @author Roberta Giannantonio
*
* @version 1.2
*/

package spine.communication.tinyos;


public class AlarmSpineData extends SpineData {
	
	protected byte[] decode(byte[] payload) {
		byte[] dataTmp = new byte[579]; 
		short dtIndex = 0;
		short pldIndex = 0;
		
		byte functionCode = payload[pldIndex++];
		dataTmp[dtIndex++] = functionCode;
		
		pldIndex++;
		
		byte dataType = payload[pldIndex++];
		dataTmp[dtIndex++] = dataType;
		
		byte sensorCode = payload[pldIndex++];
		dataTmp[dtIndex++] = sensorCode;
		
		byte valueType = payload[pldIndex++];
		dataTmp[dtIndex++] = valueType;
		
		byte alarmType = payload[pldIndex++];
		dataTmp[dtIndex++] = alarmType;
		
		dataTmp[dtIndex++] = payload[pldIndex++];
		
		dataTmp[dtIndex++] = payload[pldIndex++];
		
		dataTmp[dtIndex++] = payload[pldIndex++];
		
		dataTmp[dtIndex++] = payload[pldIndex++];
		
		byte[] data = new byte[dtIndex];
		System.arraycopy(dataTmp, 0, data, 0, data.length);
		
		return data;
	}
}
