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
 * @version 1.0
 */

#ifndef FUNCTIONS_H
#define FUNCTIONS_H

enum FunctionCodes {

  FEATURE = 0x01,
  ALARM = 0x02,
  SIGNAL_PROCESSING = 0x03,
  ONE_SHOT = 0x04
};

enum FeatureCodes {

  ROW_DATA = 0x01,
  MAX = 0x02,
  MIN = 0x03,
  RANGE = 0x04,
  MEAN = 0x05,
  AMPLITUDE = 0x06,
  RMS = 0x07,
  ST_DEV = 0x08,
  TOTAL_ENERGY = 0x09,
  VARIANCE = 0x0A

};

typedef struct active_feature_t {
  uint8_t featureCode;
  uint8_t sensorCode;
  uint8_t sensorChBitmask;
} active_feature_t;

typedef struct feat_params_t {
  uint8_t sensorCode;
  uint8_t windowSize;
  uint32_t processingTime;
} feat_params_t;

typedef struct running_timers_t {
  uint8_t sensorCode;
  uint32_t time;
} running_timers_t;

#endif

