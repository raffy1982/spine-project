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

package com.tilab.spine.constants;


/**
 *
 * This interface contains the sensor and the axis code standardized by AMP
 * (Activity Monitoring Features Selection Protocol)
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class SensorCodes {
	// sensors codes
	public final static byte GENERAL_PURPOSE = 0x0;

	public final static byte  ACCELEROMETER = 0x1;

	public final static byte  GYROSCOPE = 0x2;

	public final static byte  TEMPERATURE = 0x3;
	
	// sensors names
	public final static String GENERAL_PURPOSE_CAPTION = "General Purpose";

	public final static String  ACCELEROMETER_CAPTION = "Accelerometer";

	public final static String  GYROSCOPE_CAPTION = "Gyroscope";

	public final static String  TEMPERATURE_CAPTION = "Temperature";

	private final static String SENSOR_UNKNOWN_CAPTION = "Unknown Sensor";
	
	private final static String COMBINATION_UNKNOWN_CAPTION = "?";
	
	// sensors short names
	public final static String GENERAL_PURPOSE_SHORT_CAPTION = "Gen";

	public final static String  ACCELEROMETER_SHORT_CAPTION = "Acc";

	public final static String  GYROSCOPE_SHORT_CAPTION = "Gyro";

	public final static String  TEMPERATURE_SHORT_CAPTION = "Temp";

	private final static String SENSOR_UNKNOWN_SHORT_CAPTION = "UnkS";
	
	//axis codes	  
	public final static byte  AXIS_X = 0;
	  
	public final static byte  AXIS_Y = 1;

	public final static byte  AXIS_Z = 2;
	
	//axis names	  
	public final static String  AXIS_X_CAPTION = "Axis X";
	  
	public final static String  AXIS_Y_CAPTION = "Axis Y";

	public final static String  AXIS_Z_CAPTION = "Axis Z";
	
	//axis short names	  
	public final static String  AXIS_X_SHORT_CAPTION = "X";
	  
	public final static String  AXIS_Y_SHORT_CAPTION = "Y";

	public final static String  AXIS_Z_SHORT_CAPTION = "Z";
	
	//axis combination	  
	public final static byte  X_AXIS = 0;
	  
	public final static byte  X_Y_AXIS = 1;

	public final static byte  X_Z_AXIS = 2;
	
	public final static byte  Y_AXIS = 3;
	  
	public final static byte  Y_Z_AXIS = 4;

	public final static byte  Z_AXIS = 5;
	
	public final static byte  X_Y_Z_AXIS = 6;
	
	//axis combination caption	  
	public final static String  X_CAPTION = "x";
	  
	public final static String  X_Y_CAPTION = "xy";

	public final static String  X_Z_CAPTION = "xz";
	
	public final static String  Y_CAPTION = "y";
	  
	public final static String  Y_Z_CAPTION = "yz";

	public final static String  Z_CAPTION = "z";
	
	public final static String  X_Y_Z_CAPTION = "xyz";
	
	/**
	 * 
	 * @param axisCombination the axis combination code to be translated
	 * @return the corresponding bitmask for the given combination code. 
	 * The bitmask is represented by a 3-length boolean array, in which the first element is associated
	 * to the x-axis, the second to the y-axis and the last to the z-axis.
	 * Returns 'null' if an invalid combination code is passed
	 */
	public static boolean[] getAxisBitMask(byte axisCombination) {
		switch (axisCombination) {
		case X_AXIS : return new boolean[]{true, false, false};
		case X_Y_AXIS : return new boolean[]{true, true, false};
		case X_Z_AXIS : return new boolean[]{true, false, true};
		case Y_AXIS : return new boolean[]{false, true, false};
		case Y_Z_AXIS : return new boolean[]{false, true, true};
		case Z_AXIS : return new boolean[]{false, false, true};
		case X_Y_Z_AXIS : return new boolean[]{true, true, true};
		default: return null;
		}
	}
	
	/**
	 * Returns the axis combination code corresponding to the given axis bitmask
	 * 
	 * @param xAxis
	 * @param yAxis
	 * @param zAxis
	 * @return the translated axis combination code
	 */
	public static byte getAxisCombination(boolean xAxis, boolean yAxis, boolean zAxis) {
		if (xAxis) {
			if (yAxis) {
				if (zAxis) 
					return X_Y_Z_AXIS;
				else 
					return X_Y_AXIS;
			}
			else if (zAxis) 
				return X_Z_AXIS;
			else 
				return X_AXIS;
		}
		else if (yAxis) {
			if (zAxis)
				return Y_Z_AXIS;
			else 
				return Y_AXIS;
		}
		else if (zAxis)
			return Z_AXIS;
		return -1;
	}
	
	/**
	 * Utility method
	 * 
	 * @return the axis combination caption corresponding to the axis combination code
	 */
	public static String getAxisCombinationName(byte axisCombination) {
		switch (axisCombination) {
			case X_AXIS : return X_CAPTION;
			case X_Y_AXIS : return X_Y_CAPTION;
			case X_Z_AXIS : return X_Z_CAPTION;
			case Y_AXIS : return Y_CAPTION;
			case Y_Z_AXIS : return Y_Z_CAPTION;
			case Z_AXIS : return Z_CAPTION;
			case X_Y_Z_AXIS : return X_Y_Z_CAPTION;
		}
		return COMBINATION_UNKNOWN_CAPTION;
	}
	
	/**
	 * Utility method
	 * 
	 * @return the sensor caption corresponding to the sensor code
	 */
	public static String getSensorName(short sensorCode) {
		switch (sensorCode) {
			case ACCELEROMETER: return ACCELEROMETER_CAPTION;
			case GYROSCOPE: return GYROSCOPE_CAPTION;
			case TEMPERATURE: return TEMPERATURE_CAPTION;
			case GENERAL_PURPOSE: return GENERAL_PURPOSE_CAPTION;
		}
		return SENSOR_UNKNOWN_CAPTION;
	}
	
	/**
	 * Utility method
	 * 
	 * @return the sensor abbreviated caption corresponding to the sensor code
	 */
	public static String getSensorShortName(short sensorCode) {
		switch (sensorCode) {
			case ACCELEROMETER: return ACCELEROMETER_SHORT_CAPTION;
			case GYROSCOPE: return GYROSCOPE_SHORT_CAPTION;
			case TEMPERATURE: return TEMPERATURE_SHORT_CAPTION;
			case GENERAL_PURPOSE: return GENERAL_PURPOSE_SHORT_CAPTION;
		}
		return SENSOR_UNKNOWN_SHORT_CAPTION;
	}
}
