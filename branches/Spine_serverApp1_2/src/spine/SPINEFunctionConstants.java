/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

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
 * This class contains codes for functions and function libraries, shared with the SPINE node side, 
 * their corresponding labels and utility methods to obtain the labels from the codes. 
 *  
 *
 * @author Raffaele Gravina
 * @author Philip Kuryloski
 *
 * @version 1.2
 */

package spine;

public class SPINEFunctionConstants {
	
	// if new functions are added, declare their codes down here 
	public static final byte FEATURE = 0x01;
	public static final byte ALARM = 0x02;
	public static final byte SIGNAL_PROCESSING = 0x03;
	public static final byte ONE_SHOT = 0x04;
	
	// if new function libraries are added, declare their codes down here
	public static final byte RAW_DATA = 0x01;
	public static final byte MAX = 0x02;
	public static final byte MIN = 0x03;
	public static final byte RANGE = 0x04;
	public static final byte MEAN = 0x05;
	public static final byte AMPLITUDE = 0x06;
	public static final byte RMS = 0x07;
	public static final byte ST_DEV = 0x08;
	public static final byte TOTAL_ENERGY = 0x09;
	public static final byte VARIANCE = 0x0A;
	public static final byte MODE = 0x0B;
	public static final byte MEDIAN = 0x0C;	
	public static final byte PITCH_ROLL = 0x0D;
	public static final byte VECTOR_MAGNITUDE = 0x0E;
	
	// if new functions are added, declare their labels down here
	public static final String FEATURE_LABEL = "Feature";
	public static final String ALARM_LABEL = "Alarm";
	public static final String SIGNAL_PROCESSING_LABEL = "DSP";
	public static final String ONE_SHOT_LABEL = "One Shot";
	
	// if new function libraries are added, declare their labels down here
	public static final String RAW_DATA_LABEL = "Raw Data";
	public static final String MAX_LABEL = "Max";
	public static final String MIN_LABEL = "Min";
	public static final String RANGE_LABEL = "Range";
	public static final String MEAN_LABEL = "Mean";
	public static final String AMPLITUDE_LABEL = "Amplitude";
	public static final String RMS_LABEL = "RMS";
	public static final String ST_DEV_LABEL = "Standard Deviation";
	public static final String TOTAL_ENERGY_LABEL = "Total Energy";
	public static final String VARIANCE_LABEL = "Variance";
	public static final String MODE_LABEL = "Mode";
	public static final String MEDIAN_LABEL = "Median";
	public static final String PITCH_ROLL_LABEL = "Pitch & Roll";
	public static final String VECTOR_MAGNITUDE_LABEL = "Vector Magnitude";
	
	
	public static String functionCodeToString(byte code) {
		switch (code) {
			// if new functions are added, define the corresponding 'switch case' down here
			case FEATURE: return FEATURE_LABEL;
			case ALARM: return ALARM_LABEL;
			case SIGNAL_PROCESSING: return SIGNAL_PROCESSING_LABEL;
			case ONE_SHOT: return ONE_SHOT_LABEL;
			default: return "?";
		}
	}
	
	public static String functionalityCodeToString(byte functionCode, byte functionalityCode) {
		switch (functionCode) {
			// if new functions are added, define the corresponding 'switch case' down here
			case FEATURE:
				switch (functionalityCode) {
					// if new features are added, define the corresponding 'switch case' down here
					case RAW_DATA: return RAW_DATA_LABEL;
					case MAX: return MAX_LABEL;
					case MIN: return MIN_LABEL;
					case RANGE: return RANGE_LABEL;
					case MEAN: return MEAN_LABEL;
					case AMPLITUDE: return AMPLITUDE_LABEL;
					case RMS: return RMS_LABEL;
					case ST_DEV: return ST_DEV_LABEL;
					case TOTAL_ENERGY: return TOTAL_ENERGY_LABEL;
					case VARIANCE: return VARIANCE_LABEL;
					case MODE: return MODE_LABEL;
					case MEDIAN: return MEDIAN_LABEL;
					case PITCH_ROLL: return PITCH_ROLL_LABEL;
					case VECTOR_MAGNITUDE: return VECTOR_MAGNITUDE_LABEL;
					default: return "?";	
				}
			case ALARM: return "?";
			case ONE_SHOT: return "?";
			case SIGNAL_PROCESSING:	return "?"; 
			default: return "?";
		}
	}
}
