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
* Implementation of SpineSetupFunction responsible of handling setup of the function type 'Feature'
*
* @author Raffaele Gravina
*
* @version 1.0
*/

package spine.communication.tinyos;

import spine.SPINEFunctionConstants;

public class FeatureSpineSetupFunction extends SpineSetupFunction {

	
	private final static int PARAM_LENGTH = 3; 
	
	private byte sensor = -1;
	private short windowSize = 0;
	private short shiftSize = 0;
	
	/**
	 * This method is used internally by the framework and encodes the high Feature function setup request
	 * into an actual SPINE Ota message of the request, in terms of a byte[] array
	 */
	public byte[] encode() {
		byte[] data = new byte[5];
	
		data[0] = SPINEFunctionConstants.FEATURE;
		data[1] = PARAM_LENGTH;
		data[2] = (byte)(this.sensor<<4);
		data[3] = (byte)this.windowSize;
		data[4] = (byte)this.shiftSize;
		
		return data;	
	}
	
	/**
	 * Sets the sensor involved on the current Feature function setup request
	 * Note that a Feature function setup request is always made on a 'per sensor' basis.
	 * To activate features over different sensors, 
	 * it's necessary to do a Feature function setup request per each sensor and then
	 * to activate the required features on the involved sensors.
	 * 
	 * @param sensor the sensor code
	 */
	public void setSensor(byte sensor) {
		this.sensor = sensor;
	}
	
	/**
	 * Sets the size of the window over which computes the features 
	 * that will eventually activated thru a Feature Spine Function Req
	 * 
	 * @param windowSize the window size expressed in number of samples
	 */
	public void setWindowSize(short windowSize) {
		this.windowSize = windowSize;
	}
	
	/**
	 * Sets the shift size on the window over which computes the features 
	 * that will eventually activated thru a Feature Spine Function Req
	 * 
	 * @param shiftSize the overlap amount (ahead shift) over the previous window
	 */
	public void setShiftSize(short shiftSize) {
		this.shiftSize = shiftSize;
	}
	
}
