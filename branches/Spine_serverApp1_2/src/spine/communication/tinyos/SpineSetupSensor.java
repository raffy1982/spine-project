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

public class SpineSetupSensor extends spine.communication.tinyos.SpineTOSMessage {
	
	private final static int LENGTH = 3;
	
	private byte sensor = -1;
	private byte timeScale = -1;
	private int samplingTime = -1;
	
	
	public byte[] encode() {
		byte[] data = new byte[LENGTH];
		
		data[0] = (byte)((this.sensor<<4) | (this.timeScale<<2 & 0x0C)); // 0x0C = 0000 1100
		data[1] = (byte)((this.samplingTime & 0x0000FFFF)>>8);
		data[2] = (byte)(this.samplingTime & 0x000000FF);
		
		return data;
	}
	
	public void setSensor(byte sensor) {
		this.sensor = sensor;
	}
	
	public void setTimeScale(byte timeScale) {
		this.timeScale = timeScale;	
	}
	
	public void setSamplingTime(int samplingTime) {
		this.samplingTime = samplingTime;
	}
	
}
