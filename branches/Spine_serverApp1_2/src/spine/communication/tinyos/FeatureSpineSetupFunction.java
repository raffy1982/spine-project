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

import spine.SPINEFunctionConstants;

public class FeatureSpineSetupFunction extends SpineSetupFunction {

	
	private final static int PARAM_LENGTH = 3; 
	
	private byte sensor = -1;
	private short windowSize = 0;
	private short shiftSize = 0;
	
	public byte[] encode() {
		byte[] data = new byte[5];
	
		data[0] = (byte)(SPINEFunctionConstants.FEATURE<<3);
		data[1] = PARAM_LENGTH;
		data[2] = (byte)(this.sensor<<4);
		data[3] = (byte)this.windowSize;
		data[4] = (byte)this.shiftSize;
		
		return data;	
	}
	
	public void setSensor(byte sensor) {
		this.sensor = sensor;
	}
	
	public void setWindowSize(short windowSize) {
		this.windowSize = windowSize;
	}
	
	public void setShiftSize(short shiftSize) {
		this.shiftSize = shiftSize;
	}
	
}
