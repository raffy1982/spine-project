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
 * This class represents the SPINE Setup Sensor request.
 *  
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 * 
 * @see spine.SPINESensorConstants
 */

package spine.communication.tinyos;

public class SpineSetupSensor extends spine.communication.tinyos.SpineTOSMessage {
	
	private final static int LENGTH = 3;
	
	private byte sensor = -1;
	private byte timeScale = -1;
	private int samplingTime = -1;
	
	/**
	 * This method is used internally by the framework and encodes the high level sensor setup 
	 * into an actual SPINE Ota message of the request, in terms of a byte[] array
	 * @return
	 */
	public byte[] encode() {
		byte[] data = new byte[LENGTH];
		
		data[0] = (byte)((this.sensor<<4) | (this.timeScale<<2 & 0x0C)); // 0x0C = 0000 1100
		data[1] = (byte)((this.samplingTime & 0x0000FFFF)>>8);
		data[2] = (byte)(this.samplingTime & 0x000000FF);
		
		return data;
	}
	
	/**
	 * Sets the sensor to setup
	 * 
	 * @param sensor the sensor to setup
	 */
	public void setSensor(byte sensor) {
		this.sensor = sensor;
	}
	
	/**
	 * Sets the time scale of the sampling time.
	 * 
	 * 
	 * @param timeScale the time scale
	 * 
	 * @see spine.SPINESensorConstants for the defined time-scales 
	 */
	public void setTimeScale(byte timeScale) {
		this.timeScale = timeScale;	
	}
	
	/**
	 * Set the absolute value of the sampling time (the actual sampling interval depends on the time scale)
	 * 
	 * @param samplingTime the value of the sampling time
	 */
	public void setSamplingTime(int samplingTime) {
		this.samplingTime = samplingTime;
	}
	
}
