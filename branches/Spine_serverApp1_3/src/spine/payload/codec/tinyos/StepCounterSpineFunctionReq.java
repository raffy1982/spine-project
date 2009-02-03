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
* (both activation and deactivation) of type 'StepCounter'.
* An application that needs to do a StepCounter request, must create a new StepCounterSpineFunctionReq
* object for alarm activation, or deactivation.
* 
* This class also implements the encode method of the abstract class SpineFunctionReq that is used internally
* to convert the high level request into an actual SPINE Ota message.     
*
*
* @author Raffaele Gravina
* @author Alessia Salmeri
*
* @version 1.3
*/

package spine.payload.codec.tinyos;

import spine.SPINEFunctionConstants;

import spine.datamodel.functions.*;
import spine.datamodel.functions.Exception.*;


public class StepCounterSpineFunctionReq extends SpineCodec {

	public SpineObject decode(int nodeID, byte[] payload)throws MethodNotSupportedException{
		return super.decode(nodeID, payload);
	};
    

	public byte[] encode(SpineObject payload) {
		
		spine.datamodel.functions.StepCounterSpineFunctionReq workPayLoad = (spine.datamodel.functions.StepCounterSpineFunctionReq)payload;
		
		byte[] data = new byte[3];
		byte activationBinaryFlag = (workPayLoad.getActivationFlag())? (byte)1 : 0;
		data[0] = SPINEFunctionConstants.STEP_COUNTER; 	
		data[1] = activationBinaryFlag;
		data[2] = (byte)1;

		printPayload(data);
		return data;		
	}

	
	private void printPayload(byte[] payload) {  // DEBUG CODE
		if(payload == null || payload.length == 0)
			System.out.print("empty payload");
		else{
			for (int i = 0; i<payload.length; i++) {
				short b =  payload[i];
				if (b<0) b += 256;
				System.out.print(Integer.toHexString(b) + " ");
			}
		}
		System.out.println("");		
	}
	
}
