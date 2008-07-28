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
*
* @author Raffaele Gravina
*
* @version 1.0
*/

package spine.communication.tinyos;

import java.util.Vector;

import spine.SPINEFunctionConstants;
import spine.datamodel.Feature;

public class FeatureSpineFunctionReq extends SpineFunctionReq {

	private byte sensor = -1;
	private Vector features = new Vector();

	public byte[] encode() {
		int featuresCount = this.features.size();
		
		byte[] data = new byte[1 + 1 + 1 + 1 + featuresCount*2];
		
		byte activationBinaryFlag = (this.isActivationRequest)? (byte)1 : 0;
		data[0] = (byte)(SPINEFunctionConstants.FEATURE<<3 | activationBinaryFlag<<2 ); 
		
		data[1] = (byte)(1 + 1 + featuresCount*2);
		
		data[2] = this.sensor;
		
		data[3] = (byte)featuresCount;
				
		for (int i = 0; i < featuresCount; i++) {
			data[(4+i*2)] = ((Feature)features.elementAt(i)).getFeatureCode();
			data[(4+i*2)+1] = ((Feature)features.elementAt(i)).getChannelBitmask();
		}
		
		return data;		
	}

	public void setSensor(byte sensor) {
		this.sensor  = sensor;		
	}

	public void addFeature(byte feature, byte channelBitmask) {
		this.features.add(new Feature(feature, channelBitmask));		
	}
	
	public void removeFeature(byte feature, byte channelBitmask) {
		this.features.add(new Feature(feature, (byte)(channelBitmask ^ 0x0F)));		
	}
	
}
