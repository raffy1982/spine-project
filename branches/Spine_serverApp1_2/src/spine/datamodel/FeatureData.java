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
* This class represents the FeatureData entity.
* It contains the decode method for converting low level Feature type data into an high level object.
*
* @author Raffaele Gravina
*
* @version 1.2
*/

package spine.datamodel;

import java.util.Vector;

import spine.SPINEFunctionConstants;


public class FeatureData extends Data {

	/**
	 * Override of the spine.datamodel.Data decode method
	 * 
	 * @see spine.datamodel.Data
	 */
	protected Object decode(int nodeID, byte[] payload) {
		Vector feats = new Vector();
		
		byte sensorCode = payload[1];
		byte featuresCount = payload[2];
		
		byte currFeatCode, currBitmask;		
		int currCh1Value, currCh2Value, currCh3Value, currCh4Value;
		
		for (int i = 0; i<featuresCount; i++) {
			currFeatCode = payload[3+i*18];
			currBitmask = payload[(3+i*18) + 1];
			
			currCh1Value = Data.convertFourBytesToInt(payload, (3+i*18) + 2);
			currCh2Value = Data.convertFourBytesToInt(payload, (3+i*18) + 6);
			currCh3Value = Data.convertFourBytesToInt(payload, (3+i*18) + 10);
			currCh4Value = Data.convertFourBytesToInt(payload, (3+i*18) + 14);
			
			feats.addElement(new Feature(nodeID, SPINEFunctionConstants.FEATURE, currFeatCode, sensorCode, currBitmask, currCh1Value, currCh2Value, currCh3Value, currCh4Value));			
		}
		
		return feats;
	}
	
}
