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

/**
 * The present 'enum' contains the codes associated to the features expected and supported by AMP
 * (Activity Monitoring Features Selection Protocol)  
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.2
 */

#ifndef FUNCTIONS_H
#define FUNCTIONS_H

enum FunctionCodes {
  // if a new function is added to SPINE, its code must be included here

  FEATURE = 0x01,
  ALARM = 0x02,
  SIGNAL_PROCESSING = 0x03,
  ONE_SHOT = 0x04,
  STEP_COUNTER = 0x05,
  BUFFERED_RAWDATA = 0x06

};

enum AlarmTypes{
	BELOW_Threshold = 0x01,
	ABOVE_Threshold = 0x02,
	IN_BETWEEN_Thresholds = 0x03,
	OUT_OF_Thresholds = 0x04
};

enum FeatureCodes {
  // if a new feature is added to SPINE, its code must be included here

  RAW_DATA = 0x01,
  MAX = 0x02,
  MIN = 0x03,
  RANGE = 0x04,
  MEAN = 0x05,
  AMPLITUDE = 0x06,
  RMS = 0x07,
  ST_DEV = 0x08,
  TOTAL_ENERGY = 0x09,
  VARIANCE = 0x0A,
  MODE = 0x0B,
  MEDIAN = 0x0C,
  PITCH_ROLL = 0x0D,
  VECTOR_MAGNITUDE = 0x0E,
  
  ENTROPY = 0x10
};

typedef struct active_feature_t {
  uint8_t featureCode;
  uint8_t sensorCode;
  uint8_t sensorChBitmask;
} active_feature_t;

typedef struct active_alarm_t {
  uint8_t dataType;
  uint8_t sensorCode;
//  uint8_t valueType;
  uint8_t channelMask;
  uint32_t lowerThreshold;
  uint32_t upperThreshold;
  uint8_t alarmType;  
//  uint8_t bufferID;
} active_alarm_t;

typedef struct feat_params_t {
  uint8_t sensorCode;
  uint8_t windowSize;
  uint8_t processingTime; // actually contains just the shift_size
} feat_params_t;

typedef struct buffered_rawdata_params_t {
  uint8_t sensorCode;
  uint8_t chsBitmask;
  uint8_t bufferSize;
  uint8_t shiftSize;
  uint8_t samplesCount;
} buffered_rawdata_params_t;

#endif

