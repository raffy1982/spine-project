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
 * @version 1.3
 * 
 * @see spine.SPINESensorConstants
 */

package spine.datamodel.functions;

import spine.SPINESensorConstants;

public class SpineSetupSensor  {
	
	private byte sensor = -1;
	private byte timeScale = -1;
	private int samplingTime = -1;


	/**
	 * Sets the sensor to setup
	 * 
	 * @param sensor the sensor to setup
	 */
	public void setSensor(byte sensor) {
		this.sensor = sensor;
	}
	

	public byte getSensor() {
		byte sensor;
		sensor=this.sensor;
		return sensor;
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
	

	public byte getTimeScale() {
		byte timeScale;
		timeScale=this.timeScale;
		return timeScale;
	}
	
	
	/**
	 * Set the absolute value of the sampling time (the actual sampling interval depends on the time scale)
	 * 
	 * @param samplingTime the value of the sampling time
	 */
	public void setSamplingTime(int samplingTime) {
		this.samplingTime = samplingTime;
	}
	
	
	public int getSamplingTime() {
		int samplingTime;
		samplingTime=this.samplingTime;
		return samplingTime;
	}
	
	
	/**
	 * 
	 * Returns a string representation of the SpineSetupSensor object.
	 * 
	 */
	public String toString() {
		String s = "Sensor Setup {";
		
		s += "sensor = " + SPINESensorConstants.sensorCodeToString(sensor) + ", ";
		s += "timeScale = " + SPINESensorConstants.timeScaleToString(timeScale) + ", ";
		s += "samplingTime = " + samplingTime + "}";
		
		return s;
	}
	
}
