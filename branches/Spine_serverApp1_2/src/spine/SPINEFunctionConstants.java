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
 *
 *  
 *
 * @author Raffaele Gravina
 * @author Philip Kuryloski
 *
 * @version 1.2
 */

package spine;

public class SPINEFunctionConstants {
	
	public static final byte FEATURE = 0x01;
	public static final byte ALARM = 0x02;
	public static final byte SIGNAL_PROCESSING = 0x03;
	public static final byte ONE_SHOT = 0x04;
	public static final byte MULTI_CHANNEL_FEATURE = 0x05;
	
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
	
	public static final byte PITCH_ROLL = 0x01;
	public static final byte VECTOR_MAGNITUDE = 0x02;
	
	public static final String FEATURE_LABEL = "Feature";
	public static final String MULTI_CHANNEL_FEATURE_LABEL = "Multi-Channel Feature";
	
	
	public static String functionCodeToString(byte code) {
		switch (code) {
			case FEATURE: return FEATURE_LABEL;
			case ALARM: return "Alarm";
			case SIGNAL_PROCESSING: return "DSP";
			case ONE_SHOT: return "One Shot";
			case MULTI_CHANNEL_FEATURE: return MULTI_CHANNEL_FEATURE_LABEL;
			default: return "?";
		}
	}
	
	public static String functionalityCodeToString(byte functionCode, byte functionalityCode) {
		switch (functionCode) {
			case FEATURE:
				switch (functionalityCode) {
					case RAW_DATA: return "Raw Data";
					case MAX: return "Max";
					case MIN: return "Min";
					case RANGE: return "Range";
					case MEAN: return "Mean";
					case AMPLITUDE: return "Amplitude";
					case RMS: return "RMS";
					case ST_DEV: return "Standard Deviation";
					case TOTAL_ENERGY: return "Total Energy";
					case VARIANCE: return "Variance";
					case MODE: return "Mode";
					case MEDIAN: return "Median";
					default: return "?";
				}
				
			case MULTI_CHANNEL_FEATURE:
				switch (functionalityCode) {
					case PITCH_ROLL: return "Pitch & Roll";
					case VECTOR_MAGNITUDE: return "Vector Magnitude";
					default: return "?";
				}
				
			default:
				return "?";
		}
	}
}
