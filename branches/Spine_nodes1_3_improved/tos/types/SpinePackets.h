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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.
*****************************************************************/

/**
 * This file regards SPINE Packets
 *
 * @author Raffaele Gravina
 * @author Kevin Klues
 *
 * @version 1.3
 */

#ifndef SpinePackets_H
#define SpinePackets_H

#include "message.h"

#ifndef SPINE_GROUP_ID
#define SPINE_GROUP_ID	0x7D
#endif

#ifndef SPINE_MSQ_QUEUE_SIZE
#define SPINE_MSQ_QUEUE_SIZE	25
#endif

enum spine_packet_type {
  SERVICE_DISCOVERY = 0x01,
  SERVICE_ADV       = 0x02,
  SETUP_SENSOR      = 0x03,
  DATA              = 0x04,
  SETUP_FUNCTION    = 0x05,
  SVC_MSG           = 0x06,  // to notify the coordinator of events, errors, warnings and other internal information.
  FUNCTION_REQ      = 0x07,  // contains a flag to specify if enable or disable the function
  START             = 0x09,
                  //= 0x0A
  RESET             = 0x0B,  // simulate a hardware reset
                  //= 0x0C
  SYNCR             = 0x0D,  // it is used as a BEACON (re-sync) message
                  //= 0x0E
                  //= 0x0F
};
typedef uint8_t spine_packet_type_t;

typedef nx_struct spine_header {
  nx_uint8_t vers       :2;      // 2 bits
  nx_uint8_t ext        :1;      // 1 bit
  nx_uint8_t pktT       :5;      // 5 bits
  nx_uint8_t grpID      :8;      // 8 bits
  nx_uint16_t srcID     :16;     // 16 bits
  nx_uint16_t dstID     :16;     // 16 bits
  nx_uint8_t seqNr      :8;      // 8 bits
  nx_uint8_t fragNr     :8;      // 8 bits
  nx_uint8_t totFrags   :8;      // 8 bits
} spine_header_t;
#define SPINE_HEADER_PKT_SIZE sizeof(spine_header_t) //9 bytes

typedef struct spine_svc_disc {
} spine_svc_disc_t;
#define SPINE_SVC_DISC_SIZE      sizeof(spine_svc_disc_t) //0 bytes

typedef struct spine_svc_adv {
  uint8_t sensCount;
  uint8_t functionsCount;
  uint8_t data[48];
} spine_svc_adv_t; 
#define SPINE_SVC_ADV_SIZE     sizeof(spine_svc_adv_t) //50 bytes

typedef struct spine_setup_sensor {
} spine_setup_sensor_t;
#define SPINE_SETUP_SENSOR_SIZE     sizeof(spine_setup_sensor_t) //0 bytes

typedef struct spine_data {
  uint8_t data[255 - SPINE_HEADER_PKT_SIZE]; //note: max tinyos message payload is 255
} spine_data_t;
#define SPINE_DATA_SIZE     sizeof(spine_data_t) //0 bytes

typedef struct spine_setup_function {
} spine_setup_func_t;
#define SPINE_SETUP_FUNCTION_SIZE     sizeof(spine_setup_function_t) //0 bytes

typedef struct spine_svc_msg {
} spine_svc_msg_t;
#define SPINE_SVC_MSG_FUNCTION_SIZE     sizeof(spine_svc_msg_t) //0 bytes
enum ServiceMessageTypes {
  ERROR = 0x00,
  WARNING = 0x01,
  ACK = 0x02
};

typedef struct spine_func_req {
} spine_func_req_t;
#define SPINE_FUNC_REQ_SIZE     sizeof(spine_func_req_t) //0 bytes

typedef struct spine_start {
  uint16_t netSize          :14;     // 16 bits
  bool radioAlwaysOnFlag    :1;      // 1 bit
  bool enableTDMAFlag       :1;      // 1 bit
} spine_start_t;
#define SPINE_START_SIZE     sizeof(spine_start_t) //2 bytes

typedef struct spine_reset {
} spine_reset_t;
#define SPINE_RESET_SIZE     sizeof(spine_reset_t) //0 bytes

typedef struct spine_syncr {
} spine_syncr_t;
#define SPINE_SYNCR_SIZE     sizeof(spine_syncr_t) //0 bytes

typedef union spine_pkt {
  spine_svc_disc_t     svc_disc;
  spine_svc_adv_t      svc_adv;
  spine_setup_sensor_t setup_sensor;
  spine_data_t         data;
  spine_setup_func_t   setup_func;
  spine_svc_msg_t      svc_msg;
  spine_start_t        start;
  spine_reset_t        reset;
  spine_syncr_t        syncr;
} spine_pkt_t;
#define SPINE_PKT_SIZE sizeof(spine_pkt_t)

typedef struct spine_msg {
  spine_header_t header;
  spine_pkt_t packet;
} spine_msg_t;
#define SPINE_MSG_SIZE sizeof(spine_msg_t)

enum {
  AM_SPINE = 0x99,              // every SPINE packets will have the same AM Type
  SPINE_BASE_STATION = 0x0000,  // reserved address: remote SPINE nodes can't be assigned with this address
  SPINE_BROADCAST = 0xFFFF      // reserved address: no SPINE node can be assigned with this address
};

#endif


