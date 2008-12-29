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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.
*****************************************************************/

/**
 *
 * @author Kevin Klues <klueska@wsnlabberkeley.com>
 *
 */
 
generic module PacketizerP() {
  provides {
    interface BufferedSend[spine_packet_type_t type];
    interface Receive[spine_packet_type_t type];
  }
  uses {
    interface BufferedSendWithHeader as SubBufferedSend;
    interface Receive as SubReceive;
    interface AMPacket;
    interface Leds;
  }
}

implementation {
  enum {
    PACKET_LENGTH = TOSH_DATA_LENGTH-SPINE_HEADER_PKT_SIZE,
  };
  uint8_t seqNr = 0;

  command error_t BufferedSend.send[spine_packet_type_t pktType](am_addr_t dest, void* data, uint16_t len) {
    spine_header_t header;
    error_t error;
    uint8_t* data_ = (uint8_t*)data;
    uint8_t len_ = PACKET_LENGTH;

    header.vers = SPINE_VERSION;
    header.ext = FALSE;
    header.pktT = pktType;
    header.grpID = SPINE_GROUP_ID;
    header.srcID = TOS_NODE_ID;
    header.dstID = dest;
    header.seqNr = seqNr;
    header.fragNr = 1;
    header.totFrags = ((len % PACKET_LENGTH) == 0) ? (len/PACKET_LENGTH) : ((len/PACKET_LENGTH) + 1);

    for(; header.fragNr<=header.totFrags; header.fragNr++) {
      if(header.fragNr == header.totFrags)
        len_ = ((len % PACKET_LENGTH) == 0) ? PACKET_LENGTH : (len % PACKET_LENGTH);
      if( (error = call SubBufferedSend.send(dest, &header, sizeof(header), data_, len_)) != SUCCESS)
        return error;
      data_ += PACKET_LENGTH;
    }
    seqNr++;  // seqNr is incremented on a per data message basis (not each packet)
    return SUCCESS;
  }

  event message_t* SubReceive.receive(message_t* msg, void* pkt, uint8_t len) {
    spine_header_t* header = (spine_header_t*)pkt;
    spine_svc_msg_t ack_msg;

    if(call AMPacket.type(msg) == AM_SPINE) {
      // if the type of the incoming message is not a recognized spine packet, it won't be signaled
      if (header->vers == SPINE_VERSION && header->grpID == SPINE_GROUP_ID &&
          (header->dstID == TOS_NODE_ID || header->dstID == SPINE_BROADCAST) ) {
                    
        // ACKs transmission for received SPINE pkt
        ack_msg.type = SPINE_ACK;
        ack_msg.data[0] = header->seqNr;
        call BufferedSend.send[SVC_MSG](header->srcID, &ack_msg, sizeof(ack_msg));

        return signal Receive.receive[header->pktT](msg, pkt, len);
      }
    }
    return msg;
  }

  default event message_t* Receive.receive[spine_packet_type_t type](message_t* msg, void* payload, uint8_t len) {
    return msg;
  }
}

