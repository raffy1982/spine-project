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
 * The present 'enum' contains the packets code and size of AMP 
 * (Activity Monitoring Features Selection Protocol)
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
 
#ifndef AMPPACKETSCONSTANTS_H
#define AMPPACKETSCONSTANTS_H

enum {

  BASE_STATION_ADDRESS = 0,
	
  BROADCAST_ADDRESS = 15,

  // packets codetypes
  SERVICE_DISCOVERY_PKT_CODE = 0x1,

  FEATURE_ACTIVATION_PKT_CODE = 0x2,

  REMOVE_FEATURE_PKT_CODE = 0x3,

  BATTERY_INFO_REQUEST_PKT_CODE = 0x4,


  SERVICE_ADVERTISEMENT_PKT_CODE = 0xc,

  DATA_PKT_CODE = 0xd,

  BATTERY_INFO_PKT_CODE = 0xe,

  SERVICE_MESSAGE_PKT_CODE = 0xf,
  
  
  // packets sizes
  PKT_HEADER_SIZE = 4,

  SERVICE_DISCOVERY_PKT_SIZE = 1 + PKT_HEADER_SIZE,

  FEATURE_ACTIVATION_PKT_SIZE = 6 + PKT_HEADER_SIZE,

  REMOVE_FEATURE_PKT_SIZE = 2 + PKT_HEADER_SIZE,

  BATTERY_INFO_REQUEST_PKT_SIZE = 2 + PKT_HEADER_SIZE,


  SERVICE_ADVERTISEMENT_PKT_SIZE = 22 + PKT_HEADER_SIZE,

  DATA_PKT_SIZE = 8 + PKT_HEADER_SIZE,

  BATTERY_INFO_PKT_SIZE = 2 + PKT_HEADER_SIZE,

  SERVICE_MESSAGE_PKT_SIZE = 1 + PKT_HEADER_SIZE
};

#endif

