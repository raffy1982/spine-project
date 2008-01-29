/*****************************************************************
SPINE - Signal Processing In-Note Environment is a framework that 
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

package com.tilab.spine;

import java.util.Vector;

import com.tilab.spine.constants.SensorCodes;
import com.tilab.spine.logic.BsnNode;
import com.tilab.spine.logic.Feature;
import com.tilab.spine.packets.BatteryInfoReq;


/**
 *
 * This class contains AMP Protocol useful utility methods
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class SPINEUtils {


	/**
	 * This method converts a set of Feature objects into a 'long' array containing only their values
	 * 
	 * @param frame the set of features to be converted into a array of 'long'

	 * @return the array of 'long' with only the values of the features	  
	 */
	public static long[] featuresToIntegerArray(Feature[] frame) {
		double[] feats = featuresToDoubleArray(frame);
		long[] features = new long[feats.length];
		for (int i=0; i<features.length; i++) 
			features[i] = Math.round(feats[i]);
		return features;
	}
	
	/**
	 * This method converts a set of Feature objects into a double array containing only their values
	 * 
	 * @param frame the set of features to be converted into a double array

	 * @return the double array with only the values of the features	  
	 */
	public static double[] featuresToDoubleArray(Feature[] frame) {
		int size = 0;
		for (int i=0; i<frame.length; i++) {
			switch (frame[i].getAxisCombination()) {
				case SensorCodes.X_AXIS : case SensorCodes.Y_AXIS: case SensorCodes.Z_AXIS : size++; break;
				case SensorCodes.X_Y_AXIS : case SensorCodes.Y_Z_AXIS: case SensorCodes.X_Z_AXIS : size += 2; break;
				case SensorCodes.X_Y_Z_AXIS : size += 3; break;
			}
		}
		double[] feats = new double[size];

		int jFeats = 0; 
		for (int i=0; i<frame.length; i++) {
			switch (frame[i].getAxisCombination()) {
				case SensorCodes.X_AXIS : 
					feats[jFeats] = frame[i].getFeatureAxisXValue(); 
					jFeats++; 
					break; 
				case SensorCodes.Y_AXIS : feats[jFeats] = frame[i].getFeatureAxisYValue(); 
					jFeats++; 
					break;
				case SensorCodes.Z_AXIS : feats[jFeats] = frame[i].getFeatureAxisZValue(); 
					jFeats++; 
					break;
				case SensorCodes.X_Y_AXIS : 
					feats[jFeats] = frame[i].getFeatureAxisXValue();
					jFeats++;
					feats[jFeats] = frame[i].getFeatureAxisYValue();
					jFeats++; 
					break;
				case SensorCodes.Y_Z_AXIS : 
					feats[jFeats] = frame[i].getFeatureAxisYValue();
					jFeats++;
					feats[jFeats] = frame[i].getFeatureAxisZValue();
					jFeats++; 
					break;
				case SensorCodes.X_Z_AXIS : 
					feats[jFeats] = frame[i].getFeatureAxisXValue();
					jFeats++;
					feats[jFeats] = frame[i].getFeatureAxisZValue();
					jFeats++; 
					break;
				case SensorCodes.X_Y_Z_AXIS : 
					feats[jFeats] = frame[i].getFeatureAxisXValue();
					jFeats++;
					feats[jFeats] = frame[i].getFeatureAxisYValue();
					jFeats++; 
					feats[jFeats] = frame[i].getFeatureAxisZValue();
					jFeats++; 
					break;
			}			
		}		
		return feats;
	}
	
	public static Vector sortNodesByID(Vector nodes) {
		int iMin = 0;
		Vector vNew = new Vector();
		int loops = nodes.size();
		for (int i = 0; i < loops; i++){
			iMin = 0;
            for (int j = 1; j < nodes.size(); j++) {
            	if ( ((BsnNode)nodes.elementAt(j)).compareTo((BsnNode)nodes.elementAt(iMin)) < 0) 
            		iMin = j;
            }
            vNew.add(((BsnNode)nodes.elementAt(iMin)));
            nodes.remove(iMin);
		}
		
		return vNew;
	}
	
	public static String timeScaleToString(short scale) {
		switch (scale) {
			case BatteryInfoReq.MILLISEC: return BatteryInfoReq.MILLISEC_CAPTION;
			case BatteryInfoReq.SEC: return BatteryInfoReq.SEC_CAPTION;
			case BatteryInfoReq.MIN: return BatteryInfoReq.MIN_CAPTION;
		}
		return "?";
	}
	
}
