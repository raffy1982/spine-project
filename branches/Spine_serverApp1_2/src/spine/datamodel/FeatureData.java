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

package spine.datamodel;

import java.util.Vector;


public class FeatureData extends Data {

	protected Object decode(int nodeID, byte[] payload) {
		this.functionCode = payload[0];
		
		this.data = new Vector();
		
		byte sensorCode = payload[1];
		byte featuresCount = payload[2];
		
		byte currFeatCode, currBitmask;		
		int currCh1Value, currCh2Value, currCh3Value, currCh4Value;
		
		for (int i = 0; i<featuresCount; i++) {
			currFeatCode = payload[3+i*18];
			currBitmask = payload[(3+i*18) + 1];
			
			currCh1Value = Data.convertToInt(payload, (3+i*18) + 2);
			currCh2Value = Data.convertToInt(payload, (3+i*18) + 6);
			currCh3Value = Data.convertToInt(payload, (3+i*18) + 10);
			currCh4Value = Data.convertToInt(payload, (3+i*18) + 14);
			
			((Vector)this.data).addElement(new Feature(nodeID, this.functionCode, currFeatCode, sensorCode, currBitmask, currCh1Value, currCh2Value, currCh3Value, currCh4Value));			
		}
		
		return this.data;
	}
	
}
