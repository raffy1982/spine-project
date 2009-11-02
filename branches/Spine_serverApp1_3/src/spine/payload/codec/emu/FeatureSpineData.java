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
* This class contains the static method to parse (decompress) a 
* TinyOS SPINE 'Feature' Data packet payload into a platform independent one.
* This class is invoked only by the SpineData class, thru the dynamic class loading.
* 
* Note that this class is only used internally at the framework.
*
* @author Raffaele Gravina
* @author Alessia Salmeri
*
* @version 1.3
*/

package spine.payload.codec.emu;

import java.util.Vector;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;

import spine.datamodel.Data;
import spine.datamodel.Feature;
import spine.datamodel.functions.*;
import spine.exceptions.*;

import spine.datamodel.*;

public class FeatureSpineData extends SpineCodec {
	
	public byte[] encode(SpineObject payload) throws MethodNotSupportedException{
		throw new MethodNotSupportedException("encode");
	};
	
	int MAX_MSG_LENGHT=2500;
	byte MAX_LABEL_LENGTH=127;
	
	public SpineObject decode(Node node, byte[] payload) {
		
		// dataTmp differisce da payload (Emule) perche` allocata 4 byte per ogni canale anche se non presenti nella Bitmask e 127 byte per ogni featureLabel
		
		//byte[] dataTmp = new byte[579];
		// 02 Novembre
		//int MAX_MSG_LENGHT=2500;
		//byte MAX_LABEL_LENGTH=127;
		
		byte[] dataTmp = new byte[MAX_MSG_LENGHT]; 
		short dtIndex = 0;
		short pldIndex = 0;
		
		// functionCode = payload[0];
		byte functionCode = payload[pldIndex++];
		dataTmp[dtIndex++] = functionCode;
		
		// sensorCode = payload[1];
		byte sensorCode = payload[pldIndex++];
		dataTmp[dtIndex++] = sensorCode;
		
		//featuresCount = payload[2];
		byte featuresCount = payload[pldIndex++];
		dataTmp[dtIndex++] = featuresCount;
		
		byte currFeatCode, currSensBitmask;
		// 02 Novembre
		byte currFeatLabLenght, currFeatLabLengthBlank;
		for (int i = 0; i<featuresCount; i++) {
			currFeatCode = payload[pldIndex++];
			dataTmp[dtIndex++] = currFeatCode;
			
			currSensBitmask = payload[pldIndex++];
			dataTmp[dtIndex++] = currSensBitmask;
			
			// allocata 4 byte per ogni canale anche se non presenti nella Bitmask
			for (int j = 0; j<SPINESensorConstants.MAX_VALUE_TYPES; j++) {							
				if (SPINESensorConstants.chPresent(j, currSensBitmask)) {						
						dataTmp[dtIndex++] = payload[pldIndex++];
						dataTmp[dtIndex++] = payload[pldIndex++]; 
						dataTmp[dtIndex++] = payload[pldIndex++]; 
						dataTmp[dtIndex++] = payload[pldIndex++]; 
						
				}
				else {
					dataTmp[dtIndex++] = 0;
					dataTmp[dtIndex++] = 0; 
					dataTmp[dtIndex++] = 0; 
					dataTmp[dtIndex++] = 0;
				}
			}
			
			// 02 Novembre
			// featureLabel
			currFeatLabLenght=payload[pldIndex++];
			for (int k = 0 ; k< currFeatLabLenght; k++){
				dataTmp[dtIndex++] = payload[pldIndex++];
			}
			// alloca 127 byte per ogni featureLabel
			currFeatLabLengthBlank=(byte)(MAX_LABEL_LENGTH - currFeatLabLenght);
			for (int z = 0 ; z<currFeatLabLengthBlank; z++){
				dataTmp[dtIndex++] = 0;
			}
			
		}
				
		FeatureData data =  new FeatureData();
		
				
		try {
			
			// set data.node, data.functionCode e data.timestamp
			data.baseInit(node, payload);
			
			Vector feats = new Vector();

			// 02 Novembre
			Feature featureWork;
			byte currBitmask;	
			int currCh1Value, currCh2Value, currCh3Value, currCh4Value;
			// 02 Novembre
			String currFeatureLabel;
			int blockLength=18+MAX_LABEL_LENGTH;
			
			for (int i = 0; i<featuresCount; i++) {
				/*
				currFeatCode = dataTmp[3+i*18];
				currBitmask = dataTmp[(3+i*18) + 1];
			
				currCh1Value = Data.convertFourBytesToInt(dataTmp, (3+i*18) + 2);
				currCh2Value = Data.convertFourBytesToInt(dataTmp, (3+i*18) + 6);
				currCh3Value = Data.convertFourBytesToInt(dataTmp, (3+i*18) + 10);
				currCh4Value = Data.convertFourBytesToInt(dataTmp, (3+i*18) + 14);
				*/
				currFeatCode = dataTmp[3+i*blockLength];
				currBitmask = dataTmp[(3+i*blockLength) + 1];
			
				currCh1Value = Data.convertFourBytesToInt(dataTmp, (3+i*blockLength) + 2);
				currCh2Value = Data.convertFourBytesToInt(dataTmp, (3+i*blockLength) + 6);
				currCh3Value = Data.convertFourBytesToInt(dataTmp, (3+i*blockLength) + 10);
				currCh4Value = Data.convertFourBytesToInt(dataTmp, (3+i*blockLength) + 14);
				
				// 02 Novembre
				currFeatureLabel = convertBytesToString(dataTmp, (3+i*blockLength) +18);
				featureWork = new Feature(node, SPINEFunctionConstants.FEATURE, currFeatCode, sensorCode, currBitmask, currCh1Value, currCh2Value, currCh3Value, currCh4Value);
				featureWork.setFeatureLabel(currFeatureLabel);
				System.out.println("Set in featureWork: " + featureWork.toString() + " (" + featureWork.getFeatureLabel() + ")");
				
				//feats.addElement(new Feature(node, SPINEFunctionConstants.FEATURE, currFeatCode, sensorCode, currBitmask, currCh1Value, currCh2Value, currCh3Value, currCh4Value));			
				feats.addElement(featureWork);			

			}
			
			data.setFeatures((Feature[]) feats.toArray(new Feature[0]));
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
				
		return data;
	}
	
	
	private String convertBytesToString(byte[] bytes, int index) {    
		
		String label="";
		
		for (int k=0; k<MAX_LABEL_LENGTH; k++){
			if (bytes[index + k]!=0){
			label=label + (char)bytes[index + k];
			}
		}
		
		return label;
	}
	
	
	
}
