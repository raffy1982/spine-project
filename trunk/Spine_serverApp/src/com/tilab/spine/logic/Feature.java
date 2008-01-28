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

package com.tilab.spine.logic;

import com.tilab.spine.constants.FeatureCodes;
import com.tilab.spine.constants.SensorCodes;


/**
 *
 * This class represents the triple that defines a feature and its value.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class Feature {
	
	private short featureCode;
	
	private short sensorCode;
	
	private byte axisCombination;
	
	private int featureAxisXValue;
	
	private int featureAxisYValue;
	
	private int featureAxisZValue;
	
	/**
	 * Default constructor for a Feature object.
	 */
	public Feature() {
	}
	
	/**
	 * Constructor for a Feature object, used when the caller has to make 
	 * a feature activation or removal request, therefore doesn't mind about the value
	 * 
	 * @param featureCode the code of the feature
	 * @see com.tilab.spine.constants.FeatureCodes for details about the feature codes
	 * 
	 * @param sensorCode the code of the sensor the features refers to 	  
	 * @param axisCombination the axis combination of the sensor the features refers to
	 * @see com.tilab.spine.constants.SensorCodes for details about the sensor and the axis codes
	 */
	public Feature(short featureCode, short sensorCode, byte axisCombination) {
		this.featureCode = featureCode;
		this.sensorCode = sensorCode;
		this.axisCombination = axisCombination;
	}
	
	/**
	 * Constructor for a Feature object, including the value of the feature.
	 * 
	 * @param featureCode the code of the feature
	 * @see com.tilab.spine.constants.FeatureCodes for details about the feature codes
	 * 
	 * @param sensorCode the code of the sensor the features refers to 
	 * @param axisCombination the axis combination of the sensor the features refers to
	 * @see com.tilab.spine.constants.SensorCodes for details about the sensor and the axis codes
	 * 
	 * @param featureAxisXValue the X value of the feature
	 * @param featureAxisYValue the Y value of the feature
	 * @param featureAxisZValue the Z value of the feature
	 */
	public Feature(short featureCode, short sensorCode, byte axisCombination, 
			       int featureAxisXValue, int featureAxisYValue, int featureAxisZValue) {
		this.featureCode = featureCode;
		this.sensorCode = sensorCode;
		this.axisCombination = axisCombination;
		this.featureAxisXValue = featureAxisXValue;
		this.featureAxisYValue = featureAxisYValue;
		this.featureAxisZValue = featureAxisZValue;
	}	

	/**
	 * Getter method
	 * 
	 * @return the feature code
	 */
	public short getFeatureCode() {
		return featureCode;
	}

	/**
	 * Getter method
	 * 
	 * @return the sensor code
	 */
	public short getSensorCode() {
		return sensorCode;
	}

	/**
	 * Getter method
	 * 
	 * @return the axis combination
	 */
	public byte getAxisCombination() {
		return axisCombination;
	}

	/**
	 * Getter method
	 * 
	 * @return the feature value on the x-axis 
	 */
	public int getFeatureAxisXValue() {
		return featureAxisXValue;
	}
	
	/**
	 * Getter method
	 * 
	 * @return the feature value on the y-axis 
	 */
	public int getFeatureAxisYValue() {
		return featureAxisYValue;
	}
	
	/**
	 * Getter method
	 * 
	 * @return the feature value on the z-axis 
	 */
	public int getFeatureAxisZValue() {
		return featureAxisZValue;
	}
	
	/**
	 * Merges the current Axis combination to the new one
	 * 
	 * @param newAC the Axis Combination to merge to the current one
	 */
	public void mergeAxisCombination(byte newAC){
		if(axisCombination == newAC) 
			return;
		boolean[] aC1 = SensorCodes.getAxisBitMask(axisCombination);
		boolean[] aC2 = SensorCodes.getAxisBitMask(newAC);
		
		axisCombination = SensorCodes.getAxisCombination(aC1[0] | aC2[0],
														 aC1[1] | aC2[1], 
														 aC1[2] | aC2[2]);
	}
	
	/**
	 * Deletes the Axis specified by newAC combination from the current AxisCombination 
	 * 
	 * @param newAC the Axis Combination to merge to the current one
	 * @return true if the feature can be deleted
	 */
	public boolean deleteAxisFromCurrAxisCombination(byte newAC){
		if(newAC == axisCombination || newAC == SensorCodes.X_Y_Z_AXIS) 
			return true; 
		
		boolean[] aC1 = SensorCodes.getAxisBitMask(axisCombination);
		boolean[] aC2 = SensorCodes.getAxisBitMask(newAC); 
		
		if (f(aC2[0], aC1[0]) & f(aC2[1], aC1[1]) & f(aC2[2], aC1[2]))
			return true;
			
		
		axisCombination = SensorCodes.getAxisCombination(!(aC1[0] & aC2[0]),
														 !(aC1[1] & aC2[1]), 
														 !(aC1[2] & aC2[2]));
		return false;
	}
	
	private boolean f(boolean a, boolean b) {
		if (a) return true;
		if (b) return false;
		return true;
	}
	
	public String toString() {
		return FeatureCodes.getFeatureName(featureCode) + " on: " 
		       + SensorCodes.getSensorName(sensorCode) + ", active on: " + 
		       SensorCodes.AXIS_X_CAPTION + ": " + SensorCodes.getAxisBitMask(axisCombination)[SensorCodes.AXIS_X] + ", " +
		       SensorCodes.AXIS_Y_CAPTION + ": " + SensorCodes.getAxisBitMask(axisCombination)[SensorCodes.AXIS_Y] + ", " +
		       SensorCodes.AXIS_Z_CAPTION + ": " + SensorCodes.getAxisBitMask(axisCombination)[SensorCodes.AXIS_Z] + ", " +
		       " with values: " + 
		       SensorCodes.AXIS_X_CAPTION + ": " + featureAxisXValue + ", " +
		       SensorCodes.AXIS_Y_CAPTION + ": " + featureAxisYValue + ", " +
		       SensorCodes.AXIS_Z_CAPTION + ": " + featureAxisZValue;
	}
}
