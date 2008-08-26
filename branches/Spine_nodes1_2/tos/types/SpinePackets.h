/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
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
 * This file regards SPINE Packets
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

#ifndef SpinePackets_H
#define SpinePackets_H

#include "message.h"

enum PacketTypes {
  // if a new packet type is added to SPINE, its code must be included here

  SERVICE_ADV = 0x02,
  DATA = 0x04,
  SVC_MSG = 0x06,               // to notify the coordinator of events, errors, warnings and other internal information.

  SERVICE_DISCOVERY = 0x01,
  SETUP_SENSOR = 0x03,
  SETUP_FUNCTION = 0x05,
  START = 0x09,
  RESET = 0x0B,                 // simulate an hardware reset
  SYNCR = 0x0D,                  // it is used as a BEACON (re-sync) message
  FUNCTION_REQ = 0x07           // contains a flag to specify if enable or disable the function

};

enum {
  // if a new packet type is added to SPINE, its (max) size must be included here

  SPINE_HEADER_PKT_SIZE = 9,
  SPINE_PKT_PAYLOAD_MAX_SIZE = TOSH_DATA_LENGTH - SPINE_HEADER_PKT_SIZE,   // default will be 28 - 9 = 19 bytes
  SPINE_PKT_MAX_SIZE = SPINE_HEADER_PKT_SIZE + SPINE_PKT_PAYLOAD_MAX_SIZE,  // default will be 19 + 9 = 28 bytes

  SPINE_SVC_MSG_SIZE = 2,                   // the SPINE header size is not included
  SPINE_SVC_ADV_PKT_MAX_SIZE = 50,          // the SPINE header size is not included
  SPINE_SETUP_SENSOR_PKT_SIZE = 3,          // the SPINE header size is not included
  SPINE_START_PKT_SIZE = 4,                 // the SPINE header size is not included
  SPINE_FUNCTION_REQ_PKT_MAX_SIZE = 20,     // the SPINE header size is not included
  SPINE_SETUP_FUNCTION_PKT_MAX_SIZE = 20,   // the SPINE header size is not included
  SPINE_DATA_PKT_MAX_SIZE = 246             // the SPINE header size is not included  - note: max tinyos message payload is 255
};

enum {
  AM_SPINE = 0x99,              // every SPINE packets will have the same AM Type

  SPINE_BASE_STATION = 0x0000,  // reserved address: remote SPINE nodes can't be assigned with this address
  SPINE_BROADCAST = 0xFFFF      // reserved address: any SPINE node can't be assigned with this address
};

#endif


