/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic configuration of feature extraction capabilities 
of WSN nodes via an OtA protocol

Copyright (C) 2007 Telecom Italia S.p.A. 

GNU Lesser General Public License

This library is free software, you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY, without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library, if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.
*****************************************************************/

/**
 * This file contains all constants needed by the Sensor Board Manager
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

#ifndef SensorsConstants_H
#define SensorsConstants_H

enum SensorCode {
  // if a new sensor is added to SPINE, its code must be included here

  ACC_SENSOR = 0x01,
  VOLTAGE_SENSOR = 0x02,
  GYRO_SENSOR = 0x03,
  INTERNAL_TEMPERATURE_SENSOR = 0x04,
  EIP_SENSOR = 0x05,
  ECG_SENSOR = 0x06

};

enum ValueTypes {
   CH_1 = 0x00,
   CH_2 = 0x01,
   CH_3 = 0x02,
   CH_4 = 0x03
};

enum AcquireTypes {
   CH_1_ONLY = 0x01,
   CH_2_ONLY = 0x02,
   CH_3_ONLY = 0x03,

   ALL = 0x0A
};

enum BitmaskTypes {
   	BM_CH1_CH2_CH3_CH4 = 0x0F,	        // 1111
	BM_NONE = 0x00,			// 0000

	BM_CH1_ONLY = 0x08,		// 1000
	BM_CH1_CH2_ONLY = 0x0C,		// 1100
	BM_CH1_CH2_CH3_ONLY = 0x0E,	// 1110
	BM_CH1_CH2_CH4_ONLY = 0x0D,	// 1101
	BM_CH1_CH3_ONLY = 0xA,		// 1010
	BM_CH1_CH3_CH4_ONLY = 0xB,	        // 1011
	BM_CH1_CH4_ONLY = 0x9,		// 1001
	
	
	BM_CH2_ONLY = 0x04,		// 0100
	BM_CH2_CH3_ONLY = 0x06,		// 0110
	BM_CH2_CH3_CH4_ONLY = 0x07,	// 0111
	BM_CH2_CH4_ONLY = 0x05,		// 0101
	
	BM_CH3_ONLY = 0x02,		// 0010
	BM_CH3_CH4_ONLY = 0x03,		// 0011

	BM_CH4_ONLY = 0x01			// 0001
};

enum {
   MAX_VALUE_TYPES = 0x04      // is equal to the max number of channel per physical sensor SPINE is able to handle
};

typedef struct sensor_buffer_map_t {
  uint8_t sensorCode;
  uint8_t channelCode;
  uint8_t bufferID;
} sensor_buffer_map_t;

#endif


