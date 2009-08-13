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
* TinyOS SPINE 'StepCounter' Data packet payload into a platform independent one.
* This class is invoked only by the SpineData class, thru the dynamic class loading.
* 
* @author Raffaele Gravina
* @author Alessia Salmeri
*
* @version 1.3
*/

package spine.payload.codec.emule;

import spine.datamodel.functions.*;
import spine.datamodel.functions.Exception.*;

import spine.datamodel.*;

public class StepCounterSpineData extends SpineCodec {
	
	public byte[] encode(SpineObject payload) throws MethodNotSupportedException{
		throw new MethodNotSupportedException("encode");
	};
	
	public SpineObject decode(Node node, byte[] payload) {
				
		StepCounterData data =  new StepCounterData();
		
		// set data.nodeID, data.functionCode e data.timestamp
		data.baseInit(node, payload);
		data.setStepsCount(byteArrayToInt(payload, 2));
		
		return data;
	}
	
	private static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 2; i++) {
            int shift = (2 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }
}
