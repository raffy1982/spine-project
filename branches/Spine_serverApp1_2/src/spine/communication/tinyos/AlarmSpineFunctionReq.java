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
* @author Roberta Giannantonio
*
* @version 1.2
*/

package spine.communication.tinyos;

import spine.SPINEFunctionConstants;

public class AlarmSpineFunctionReq extends SpineFunctionReq {

	private byte sensor = -1;
	private byte dataType = -1;
	private byte valueType = -1;
	private int lowerThreshold;	
	private int upperThreshold;
	private byte alarmType;
		

	public byte[] encode() {
		
		byte[] data = new byte[1 + 1 + 1 + 1 + 1 + 2 + 2 + 1];
		
		byte activationBinaryFlag = (this.isActivationRequest)? (byte)1 : 0;
		data[0] = (byte)(SPINEFunctionConstants.ALARM<<3 | activationBinaryFlag<<2 ); 
		
		data[1] = (byte)(8);
		
		data[2] = this.dataType;
		data[3] = this.sensor;
		data[4] = this.valueType;
		//lower Threshold 
		data[5] = (byte) (this.lowerThreshold >> 8);
		data[6] = (byte) (this.lowerThreshold);
		//upper Threshold 
		data[7] = (byte) (this.upperThreshold >> 8);
		data[8] = (byte) (this.upperThreshold);
		
		data[9] = this.alarmType;
		
		return data;		
	}

	public void setDataType(byte dataType) {
		this.dataType  = dataType;		
	}
	
	public void setSensor(byte sensor) {
		this.sensor  = sensor;		
	}
	
	public void setValueType(byte ValueType) {
		this.valueType  = ValueType;		
	}	
	
	public void setLowerThreshold(int setLowerThreshold) {
		this.lowerThreshold  = setLowerThreshold;		
	}
	
	public void setUpperThreshold(int setUpperThreshold) {
		this.upperThreshold  = setUpperThreshold;		
	}	
	
	public void setAlarmType(byte AlarmType) {
		this.alarmType  = AlarmType;		
	}	
	
}
