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
* Objects of this class are used for expressing at high level function requests 
* (both activation and deactivation) of type 'Feature'.
* An application that needs to do a Feature request, must create a new FeatureSpineFunctionReq
* object, set on it the sensor involved and use the addFeature one or more times 
* (currently, up to 7 add are supported per each request) 
* for features activation, or the removeFeature (currently, up to 7 remove are supported per each request) 
* for features deactivation.
* 
* This class also implements the encode method of the abstract class SpineFunctionReq that is used internally
* to convert the high level request into an actual SPINE Ota message.     
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

	/**
	 * Converts an high level request object into an actual SPINE Ota message
	 * 
	 * @return the actual SPINE Ota message of the request, in terms of a byte[] array  
	 */
	public byte[] encode() {
		int featuresCount = this.features.size();
		
		byte[] data = new byte[1 + 1 + 1 + 1 + 1 + featuresCount*2];
		
		byte activationBinaryFlag = (this.isActivationRequest)? (byte)1 : 0;
		data[0] = SPINEFunctionConstants.FEATURE; 
		
		data[1] = activationBinaryFlag;
		
		data[2] = (byte)(1 + 1 + featuresCount*2);
		
		data[3] = this.sensor;
		
		data[4] = (byte)featuresCount;
				
		for (int i = 0; i < featuresCount; i++) {
			data[(5+i*2)] = ((Feature)features.elementAt(i)).getFeatureCode();
			data[(5+i*2)+1] = ((Feature)features.elementAt(i)).getChannelBitmask();
		}
		
		return data;		
	}

	/**
	 * Set the sensor involved in the request
	 * 
	 * @param sensor the code of the sensor
	 * 
	 * @see spine.SPINESensorConstants 
	 */
	public void setSensor(byte sensor) {
		this.sensor  = sensor;		
	}

	/**
	 * Add a new feature to the activation request.
	 * Note that on each request object calling addFeature is mutually exclusive with
	 * removeFeature calls.  
	 * 
	 * @param feature the code of the feature
	 * @param channelBitmask the channels over which activate the given feature
	 * 
	 * @see spine.SPINESensorConstants
	 * @see spine.SPINEFunctionConstants
	 */
	public void addFeature(byte feature, byte channelBitmask) {
		this.features.addElement(new Feature(feature, channelBitmask));		
	}
	
	/**
	 * Add a new feature to the deactivation request.
	 * 
	 * Note that on each request object calling removeFeature is mutually exclusive with
	 * addFeature calls.  
	 * 
	 * @param feature the code of the feature
	 * @param channelBitmask the channels over which deactivate the given feature
	 * 
	 * @see spine.SPINESensorConstants
	 * @see spine.SPINEFunctionConstants
	 */
	public void removeFeature(byte feature, byte channelBitmask) {
		this.features.addElement(new Feature(feature, (byte)(channelBitmask ^ 0x0F)));		
	}
	
}
