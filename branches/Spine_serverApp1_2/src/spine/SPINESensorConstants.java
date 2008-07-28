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
 *
 * @version 1.2
 */

package spine;

public class SPINESensorConstants {

	public static final byte ACC_SENSOR = 0x01;
	public static final byte VOLTAGE_SENSOR = 0x02;
	public static final byte GYRO_SENSOR = 0x03;
	public static final byte INTERNAL_TEMPERATURE_SENSOR = 0x04;
	
	
	public static final byte ALL = 0x0F;				// 1111
	public static final byte NONE = 0x00;				// 0000
	
	public static final byte CH1_ONLY = 0x08;			// 1000
	public static final byte CH1_CH2_ONLY = 0x0C;		// 1100
	public static final byte CH1_CH2_CH3_ONLY = 0x0E;	// 1110
	public static final byte CH1_CH2_CH4_ONLY = 0x0D;	// 1101
	public static final byte CH1_CH3_ONLY = 0xA;		// 1010
	public static final byte CH1_CH3_CH4_ONLY = 0xB;	// 1011
	public static final byte CH1_CH4_ONLY = 0x9;		// 1001
	
	
	public static final byte CH2_ONLY = 0x04;			// 0100
	public static final byte CH2_CH3_ONLY = 0x06;		// 0110
	public static final byte CH2_CH3_CH4_ONLY = 0x07;	// 0111
	public static final byte CH2_CH4_ONLY = 0x05;		// 0101
	
	public static final byte CH3_ONLY = 0x02;			// 0010
	public static final byte CH3_CH4_ONLY = 0x03;		// 0011
	
	public static final byte CH4_ONLY = 0x01;			// 0001
	
	public static final byte NOW = 0x00;				// 00
	public static final byte MILLISEC = 0x01;			// 01
	public static final byte SEC = 0x02;				// 10
	public static final byte MIN = 0x03;				// 11
	
	public static final int MAX_VALUE_TYPES = 4;
	
	public static final byte CH1 = 0x00;
	public static final byte CH2 = 0x01;
	public static final byte CH3 = 0x02;
	public static final byte CH4 = 0x03;
	
	public static final String CH1_LABEL = "ch1";
	public static final String CH2_LABEL = "ch2";
	public static final String CH3_LABEL = "ch3";
	public static final String CH4_LABEL = "ch4";
	
	public static String sensorCodeToString(byte code) {
		switch (code) {
			case ACC_SENSOR: return "accelerometer";
			case VOLTAGE_SENSOR: return "voltage";
			case GYRO_SENSOR: return "gyroscope";
			case INTERNAL_TEMPERATURE_SENSOR: return "cpu temperature";
			default: return "?";
		}
	}
	
	public static byte getValueTypesCodeByBitmask(boolean hasCh1, boolean hasCh2, boolean hasCh3, boolean hasCh4) {
		byte code = 0;
		
		if (hasCh1)
			code |= 0x8;
		if (hasCh2)
			code |= 0x4;
		if (hasCh3)
			code |= 0x2;
		if (hasCh4)
			code |= 0x1;
		
		return code;
	}
	
	public static String valueTypesBitmaskToString(byte code) {
		
		switch (code) {
			case ALL: return "ch1, ch2, ch3, ch4";
			case NONE: return "none";
			
			case CH1_ONLY: return "ch1";
			case CH1_CH2_ONLY: return "ch1, ch2";
			case CH1_CH2_CH3_ONLY: return "ch1, ch2, ch3";
			case CH1_CH2_CH4_ONLY: return "ch1, ch2, ch4";
			case CH1_CH3_ONLY: return "ch1, ch3";
			case CH1_CH3_CH4_ONLY: return "ch1, ch3, ch4";
			case CH1_CH4_ONLY: return "ch1, ch4";
			
			case CH2_ONLY: return "ch2";
			case CH2_CH3_ONLY: return "ch2, ch3";
			case CH2_CH3_CH4_ONLY: return "ch2, ch3, ch4";
			case CH2_CH4_ONLY: return "ch2, ch4";
			
			case CH3_ONLY: return "ch3";
			case CH3_CH4_ONLY: return "ch3, ch4";
			
			case CH4_ONLY: return "ch4";
			default: return "?";
		}
	}

	public static boolean chPresent(int chID, byte channelBitmask) {
		return (( (channelBitmask>>(MAX_VALUE_TYPES - chID)) & 0x01 ) == 1);
	}
	
}
