/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic configuration of feature extraction capabilities 
of WSN nodes via an OtA protocol

Copyright (C) 2007 Telecom Italia S.p.A. 
�
GNU Lesser General Public License
�
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
�
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the GNU
Lesser General Public License for more details.
�
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA� 02111-1307, USA.
*****************************************************************/

/**
* This class contains the static method to parse (decompress) a 
* TinyOS SPINE 'HMM' Data packet payload into a platform independent one.
* This class is invoked only by the SpineData class, thru the dynamic class loading.
* 
* @author Raffaele Gravina
* @author Vitali Loseu
*
* @version 1.3
*/

package spine.payload.codec.tinyos;

import spine.datamodel.functions.*;
import spine.exceptions.*;

import spine.datamodel.*;

public class HmmSpineData extends SpineCodec {
	
	public byte[] encode(SpineObject payload) throws MethodNotSupportedException{
		throw new MethodNotSupportedException("encode");
	};
	
	public SpineObject decode(Node node, byte[] payload) {
				
		HmmData data =  new HmmData();
		
		data.baseInit(node, payload);
		//data.setFunctionCode(SPINEFunctionConstants.HMM);
		
		short pldIndex = 2;
		int nStates = payload[pldIndex++];
		
		int[] states = new int[nStates];
		
		for(int i = 0; i<nStates; i++)
			states[i] = payload[pldIndex++];
		
		data.setStates(states);
		
		return data;
	}
}
