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
 * This file defines the constants and structures associated with
 * sending and receiving SPINE packets
 *
 * @author Raffaele Gravina
 * @author Kevin Klues
 *
 * @version 1.3
 */

#ifndef SpinePackets_H
#define SpinePackets_H

// This header file defines the basic TinyOS message structure
#include "message.h"

/****************************************
 * Messaging constants defined by SPINE *
 ****************************************/
// Default message queue size for buffered sending
// Modify in your makefile with -DSPINE_MSG_QUEUE_SIZE=XXX
#ifndef SPINE_MSG_QUEUE_SIZE
#define SPINE_MSG_QUEUE_SIZE	5
#endif
// The following constants are defined as enums so that mig can grab them 
// and automatically generate java class files using them 
enum {
  AM_SPINE = 0x99,              // every SPINE packets will have the same AM Type
  SPINE_GROUP_ID = 0xAB,	// SPINE group ID
  SPINE_BASE_STATION = 0x0000,  // reserved address: remote SPINE nodes can't be assigned with this address
  SPINE_BROADCAST = 0xFFFF      // reserved address: no SPINE node can be assigned with this address
};

/***********************************************
 * Sleep scheduling and Mac protocol constants *
 ***********************************************/
// Default sync interval for the SCP mac using the UPMA framework
// Modify in your makefile with -DSPINE_SYNC_INTERVAL=XXX
#ifndef SPINE_SYNC_INTERVAL
#define SPINE_SYNC_INTERVAL	10000
#endif
// Default sleep interval for bmac/xmac/scp using the UPMA framework
// Modify in your makefile with -DSPINE_SLEEP_INTERVAL=XXX
#ifndef SPINE_SLEEP_INTERVAL
#define SPINE_SLEEP_INTERVAL	3000
#endif
// Default slot length for pure-tdma and ss-tdma using the UPMA framework
// Modify in your makefile with -DSPINE_SLOT_LENGTH=XXX
#ifndef SPINE_SLOT_LENGTH
#define SPINE_SLOT_LENGTH	400
#endif
// Default number of slots for pure-tdma and ss-tdma using the UPMA framework
// Modify in your makefile with -DSPINE_NUM_SLOTS=XXX
#ifndef SPINE_NUM_SLOTS
#define SPINE_NUM_SLOTS		1
#endif

/**********************************
 * SPINE message type definitions *
 **********************************/
// Add additional message types as needed when more message types are defined
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

/******************************************
 * SPINE service message type definitions *
 ******************************************/
typedef enum service_message_type {
  SPINE_ERROR = 0x00,
  SPINE_WARNING = 0x01,
  SPINE_ACK = 0x02
} service_message_type_t;

/****************************
 * SPINE message structures *
 ****************************/
// Follow from the spine_packet_type enum defined above

// This structure defines the format of a spine header
typedef nx_struct spine_header {
  nx_uint8_t vers       :2;      // 2 bits	-- The SPINE version number
  nx_uint8_t ext        :1;      // 1 bit	-- A SPINE version extension number
  nx_uint8_t pktT       :5;      // 5 bits	-- SPINE packet type (see definitions above)
  nx_uint8_t grpID      :8;      // 8 bits	-- SPINE group ID
  nx_uint16_t srcID     :16;     // 16 bits	-- Source ID
  nx_uint16_t dstID     :16;     // 16 bits	-- Destination ID
  nx_uint8_t seqNr      :8;      // 8 bits	-- Sequence number for fragmented packets
  nx_uint8_t fragNr     :8;      // 8 bits	-- Fragment number for fragmented packets
  nx_uint8_t totFrags   :8;      // 8 bits	-- Total number of fragments
} spine_header_t;
#define SPINE_HEADER_PKT_SIZE sizeof(spine_header_t) //9 bytes

/////////////////////////////////
// Incoming message structures //
/////////////////////////////////
typedef struct spine_start {
  uint16_t netSize          :14;     // 14 bits
  bool radioAlwaysOnFlag    :1;      // 1 bit
  bool enableTDMAFlag       :1;      // 1 bit
} spine_start_t;
#define SPINE_START_SIZE sizeof(spine_start_t) //2 bytes
typedef struct spine_setup_sensor {
  uint8_t sensCode        :4;  // 4 bits
  uint8_t timeScale       :2;  // 2 bits
  uint16_t samplingTime   :16; // 16 bits
} spine_setup_sensor_t;
#define SPINE_SETUP_SENSOR_SIZE sizeof(spine_setup_sensor_t) //3 bytes
typedef struct spine_setup_func {
  uint8_t fnCode;
  uint8_t fnParamsSize;
  uint8_t fnBuf[20];
} spine_setup_func_t;
#define SPINE_SETUP_FUNC_SIZE sizeof(spine_setup_func_t) //22 bytes
typedef struct spine_func_req {
  uint8_t fnCode;
  bool isEnableReq;
  uint8_t fnParamsSize;
  uint8_t fnReqBuf[20];
} spine_func_req_t;
#define SPINE_FUNC_REQ_SIZE sizeof(spine_func_req_t) //23 bytes
typedef struct spine_svc_disc {
} spine_svc_disc_t;
#define SPINE_SVC_DISC_SIZE      sizeof(spine_svc_disc_t) //0 bytes
typedef struct spine_reset {
} spine_reset_t;
#define SPINE_RESET_SIZE     sizeof(spine_reset_t) //0 bytes
typedef struct spine_syncr {
} spine_syncr_t;
#define SPINE_SYNCR_SIZE     sizeof(spine_syncr_t) //0 bytes

/////////////////////////////////
// Outgoing message structures //
/////////////////////////////////
// TODO: Need to fix all outgoing messages to conform to same structure 
// approach as incoming messages
// Currently only have svc_msgs defined (since needed by the acks sent for any incoming message)
typedef nx_struct spine_svc_msg {
  nx_uint8_t type;
  nx_uint8_t data[1];
} spine_svc_msg_t;
#define SPINE_SVC_MSG_FUNCTION_SIZE     sizeof(spine_svc_msg_t) //2 bytes
typedef nx_struct spine_svc_adv {
} spine_svc_adv_t; 
#define SPINE_SVC_ADV_SIZE     sizeof(spine_svc_adv_t) //50 bytes
typedef struct spine_data {
} spine_data_t;
#define SPINE_DATA_SIZE     sizeof(spine_data_t) //0 bytes

// Union of all possible payloads for a SPINE packet
// Follows from the spine_packet_type enum defined above
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

// Struct defining a generic spine message as it comes in over the network
typedef struct spine_msg {
  spine_header_t header;
  spine_pkt_t packet;
} spine_msg_t;
#define SPINE_MSG_SIZE sizeof(spine_msg_t)

#endif


