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
* @author Philip Kuryloski
*
* @version 1.3
*/

package spine.datamodel;

import java.util.Vector;

import spine.SPINEFunctionConstants;


public class FeatureData extends Data {

	Feature[] features;
			
	/**
	 * Constructor of a Data object. 
	 * Note that Data in just a generic container and the actual Data implementations must be provided and
	 * configured properly within the Properties set. That is done declaring a new "data_function_className_myFunctionCode" property
	 * equal to the full path name of the class that will be responsible of decoding the byte[] data payload.
	 * The usage of properties is to allow the dynamic loading of the classes involved.
	 * 
	 * @param nodeID the source node generating the data
	 * @param payload the data represented as a byte[] array. Its length and content are 'function specific' 
	 */
	public Data init(int nodeID, byte[] payload) {
		try {
			this.baseInit(nodeID, payload);
			
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
			
			features = (Feature[]) feats.toArray(new Feature[0]);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return this;
	}
	
	/**
	 * Getter for features.
	 * @return features
	 */
	public Feature[] getFeatures() {
	    return features;
	}
	public void setFeatures(Feature[] features) {
		this.features = features;
	}
		
	/**
	 * 
	 * Returns a string representation of the Feature object.
	 * 
	 */
	public String toString() {
		String s = "Feature set received from Node "+nodeID+":\n";
		
		for (int i=0; i<features.length; i++) {
			s += "\t" + features[i] + "\n";
		}
		
		return s;
	}
	
}
