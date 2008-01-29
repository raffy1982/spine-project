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

import java.util.Vector;

/**
 *
 * This interface contains the codes associated to the features expected and supported by AMP
 * (Activity Monitoring Features Selection Protocol)
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class FeatureCodes {
	  // features codes
	  public final static byte RAW_DATA = 0x0;

	  public final static byte MEAN = 0x1;

	  public final static byte MEDIAN = 0x2;

	  public final static byte CENTRAL_VALUE = 0x3;

	  public final static byte AMPLITUDE = 0x4;

	  public final static byte RANGE = 0x5;

	  public final static byte MIN = 0x6;

	  public final static byte MAX = 0x7;
	  
	  public final static byte RMS = 0x8;

	  public final static byte VAR = 0x9;

	  public final static byte ST_DEV = 0xa;
	  
	  public final static byte TOTAL_ENERGY = 0xb;
	  
	  // features names
	  public final static String RAW_DATA_CAPTION = "Row Data";

	  public final static String MEAN_CAPTION = "Mean";

	  public final static String MEDIAN_CAPTION = "Median";

	  public final static String CENTRAL_VALUE_CAPTION = "Central Value";

	  public final static String AMPLITUDE_CAPTION = "Amplitude";

	  public final static String RANGE_CAPTION = "Range";

	  public final static String MIN_CAPTION = "Min Value";

	  public final static String MAX_CAPTION = "Max Value";
	  
	  public final static String RMS_CAPTION = "Root Mean Square";

	  public final static String VAR_CAPTION = "Variance";

	  public final static String ST_DEV_CAPTION = "Standard Deviation";
	  
	  public final static String TOTAL_ENERGY_CAPTION = "Total Energy";
	  
	  private final static String FEATURE_UNKNOWN_CAPTION = "Unknown Feature";
	  
	// features names
	  public final static String RAW_DATA_SHORT_CAPTION = "Row";

	  public final static String MEAN_SHORT_CAPTION = "Mean";

	  public final static String MEDIAN_SHORT_CAPTION = "Median";

	  public final static String CENTRAL_VALUE_SHORT_CAPTION = "CentrVal";

	  public final static String AMPLITUDE_SHORT_CAPTION = "Amplitude";

	  public final static String RANGE_SHORT_CAPTION = "Range";

	  public final static String MIN_SHORT_CAPTION = "Min";

	  public final static String MAX_SHORT_CAPTION = "Max";
	  
	  public final static String RMS_SHORT_CAPTION = "RMS";

	  public final static String VAR_SHORT_CAPTION = "Var";

	  public final static String ST_DEV_SHORT_CAPTION = "StDev";
	  
	  public final static String TOTAL_ENERGY_SHORT_CAPTION = "TotEn";
	  
	  private final static String FEATURE_UNKNOWN_SHORT_CAPTION = "UnkF";
	  
	  /**
		 * Utility method
		 * 
		 * @return the feature caption corresponding to the feature code
		 */
		public static String getFeatureName(short featureCode) {
			switch (featureCode) {
				case AMPLITUDE: return AMPLITUDE_CAPTION;
				case CENTRAL_VALUE: return CENTRAL_VALUE_CAPTION;
				case MAX: return MAX_CAPTION;
				case MEAN: return MEAN_CAPTION;
				case MEDIAN: return MEDIAN_CAPTION;
				case MIN: return MIN_CAPTION;
				case RANGE: return RANGE_CAPTION;
				case RMS: return RMS_CAPTION;
				case RAW_DATA: return RAW_DATA_CAPTION;
				case ST_DEV: return ST_DEV_CAPTION;
				case VAR: return VAR_CAPTION;
				case TOTAL_ENERGY: return TOTAL_ENERGY_CAPTION;
			}
			return FEATURE_UNKNOWN_CAPTION;
		}
		
		 /**
		 * Utility method
		 * 
		 * @return the feature abbreviated caption corresponding to the feature code
		 */
		public static String getFeatureShortName(short featureCode) {
			switch (featureCode) {
				case AMPLITUDE: return AMPLITUDE_SHORT_CAPTION;
				case CENTRAL_VALUE: return CENTRAL_VALUE_SHORT_CAPTION;
				case MAX: return MAX_SHORT_CAPTION;
				case MEAN: return MEAN_SHORT_CAPTION;
				case MEDIAN: return MEDIAN_SHORT_CAPTION;
				case MIN: return MIN_SHORT_CAPTION;
				case RANGE: return RANGE_SHORT_CAPTION;
				case RMS: return RMS_SHORT_CAPTION;
				case RAW_DATA: return RAW_DATA_SHORT_CAPTION;
				case ST_DEV: return ST_DEV_SHORT_CAPTION;
				case VAR: return VAR_SHORT_CAPTION;
				case TOTAL_ENERGY: return TOTAL_ENERGY_SHORT_CAPTION;
			}
			return FEATURE_UNKNOWN_SHORT_CAPTION;
		}
		
		private static Vector captions = null;
		
		/**
		 * Getter method
		 * 
		 * @return Vector containing the captions of all the features supplied
		 */
		public static Vector getFeatureCaptions() {
			if (captions == null) {
				captions = new Vector();
				
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.MEAN_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.MEAN_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Z_SHORT_CAPTION+"_"+FeatureCodes.MEAN_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.MEDIAN_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.MEDIAN_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Z_SHORT_CAPTION+"_"+FeatureCodes.MEDIAN_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.CENTRAL_VALUE_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.CENTRAL_VALUE_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Z_SHORT_CAPTION+"_"+FeatureCodes.CENTRAL_VALUE_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.AMPLITUDE_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.AMPLITUDE_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Z_SHORT_CAPTION+"_"+FeatureCodes.AMPLITUDE_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.RANGE_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.RANGE_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Z_SHORT_CAPTION+"_"+FeatureCodes.RANGE_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.MIN_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.MIN_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Z_SHORT_CAPTION+"_"+FeatureCodes.MIN_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.MAX_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.MAX_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Z_SHORT_CAPTION+"_"+FeatureCodes.MAX_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.RMS_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.RMS_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Z_SHORT_CAPTION+"_"+FeatureCodes.RMS_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.ST_DEV_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.ST_DEV_SHORT_CAPTION);
				captions.add(SensorCodes.ACCELEROMETER_SHORT_CAPTION+"_"+SensorCodes.AXIS_Z_SHORT_CAPTION+"_"+FeatureCodes.ST_DEV_SHORT_CAPTION);		
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.MEAN_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.MEAN_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.MEDIAN_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.MEDIAN_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.CENTRAL_VALUE_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.CENTRAL_VALUE_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.AMPLITUDE_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.AMPLITUDE_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.RANGE_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.RANGE_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.MIN_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.MIN_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.MAX_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.MAX_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.RMS_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.RMS_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_X_SHORT_CAPTION+"_"+FeatureCodes.ST_DEV_SHORT_CAPTION);
				captions.add(SensorCodes.GYROSCOPE_SHORT_CAPTION+"_"+SensorCodes.AXIS_Y_SHORT_CAPTION+"_"+FeatureCodes.ST_DEV_SHORT_CAPTION);
			}
			
			return captions;
		}
		
}
