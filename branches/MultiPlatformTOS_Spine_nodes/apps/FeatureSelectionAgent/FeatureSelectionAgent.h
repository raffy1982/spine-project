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
 * This file contains all the application constants needed and the AMP packet nx_struct
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */

#ifndef FeatureSelectionAgent_H
#define FeatureSelectionAgent_H

#ifndef BUFFER_SIZE
#define BUFFER_SIZE 200
#endif

#include "AmpPacketsConstants.h"

enum {
  AM_SERVICEDISCOVERYPKT = SERVICE_DISCOVERY_PKT_CODE,
  AM_FEATUREACTIVATIONPKT = FEATURE_ACTIVATION_PKT_CODE,
  AM_REMOVEFEATUREPKT = REMOVE_FEATURE_PKT_CODE,
  AM_BATTERYINFOREQPKT = BATTERY_INFO_REQUEST_PKT_CODE,
  AM_DATAPKT = DATA_PKT_CODE,
  AM_SERVICEMESSAGEPKT = SERVICE_MESSAGE_PKT_CODE,
  AM_BATTERYINFOPKT = BATTERY_INFO_PKT_CODE,
  AM_SERVICEADVERTISEMENTPKT = SERVICE_ADVERTISEMENT_PKT_CODE,

  SAMPLE_SIZE = 2,
  BUF_SIZE = BUFFER_SIZE * SAMPLE_SIZE,  // 500 if telosb
  MAX_FEATURES_PER_AXIS = 10,

  AMP_VERSION = 1,
  
  POST_TIMER_UNIT = 500,

  FALL_THRESHOLD = 0x4F5880,
  
  DETECTION_PAUSE_LENGHT = 10
};    

// AMP PACKETS

typedef nx_struct ServiceDiscoveryPkt {

  nx_uint8_t part[SERVICE_DISCOVERY_PKT_SIZE];

} ServiceDiscoveryPkt;

typedef nx_struct ServiceAdvertisementPkt {

  nx_uint8_t part[SERVICE_ADVERTISEMENT_PKT_SIZE];

} ServiceAdvertisementPkt;

typedef nx_struct FeatureActivationPkt {

  nx_uint8_t part[FEATURE_ACTIVATION_PKT_SIZE];

} FeatureActivationPkt;

typedef nx_struct DataPkt {

  nx_uint8_t part[DATA_PKT_SIZE - 6];
  nx_int16_t feature[3];

} DataPkt;

typedef nx_struct RemoveFeaturePkt {

  nx_uint8_t part[REMOVE_FEATURE_PKT_SIZE];

} RemoveFeaturePkt;

typedef nx_struct ServiceMessagePkt {

  nx_uint8_t part[SERVICE_MESSAGE_PKT_SIZE];

} ServiceMessagePkt;

typedef nx_struct BatteryInfoReqPkt {

  nx_uint8_t part[BATTERY_INFO_REQUEST_PKT_SIZE];

} BatteryInfoReqPkt;

typedef nx_struct BatteryInfoPkt {

  nx_uint8_t part[BATTERY_INFO_PKT_SIZE];

} BatteryInfoPkt;

#endif


