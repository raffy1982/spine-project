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

generic module AMQueuedSendWithHeaderP() {
  uses {
    interface AMSend as Sender;
    interface Packet;
    interface AMPacket;

    interface Queue<message_t*> as MsgQueue;
    interface Pool<message_t> as MsgPool;
  }
  provides {
    interface BufferedSendWithHeader as Send;
  }
}

implementation {
  /* Module variables */
  bool busySending = FALSE;   

  /* Function Prototypes */
  message_t* prepareToSend(am_addr_t dest, void* header, uint8_t header_len, void* data, uint8_t data_len);

  command error_t Send.send(am_addr_t dest, void* header, uint8_t header_len, void* data, uint8_t data_len) {
    message_t* p_msg;
    uint8_t total_len = header_len + data_len;

    if(total_len > TOSH_DATA_LENGTH)
      return ESIZE;

    if( (p_msg = prepareToSend(dest, header, header_len, data, data_len)) == NULL )
      return ERETRY;

    if (call MsgQueue.empty() && busySending == FALSE) {
      busySending = TRUE;
      return call Sender.send(dest, p_msg, total_len);
    }

    return call MsgQueue.enqueue(p_msg);
  }
  
  message_t* prepareToSend(am_addr_t dest, void* header, uint8_t header_len, void* data, uint8_t data_len) {
      message_t* p_msg;
      uint8_t* p_msg_payload;
      uint8_t total_len = header_len + data_len;

      if( call MsgPool.empty() == TRUE )
        return NULL;

      p_msg = call MsgPool.get();
      p_msg_payload = call Sender.getPayload(p_msg, total_len);
      memcpy(p_msg_payload, header, header_len);
      memcpy(p_msg_payload + header_len, data, data_len);
      call AMPacket.setDestination(p_msg, dest);
      call Packet.setPayloadLength(p_msg, total_len);			  

      return p_msg;
  }
       
  event void Sender.sendDone(message_t* msg, error_t error) {
    uint16_t destination; 
    uint8_t length;
    
    if(error == SUCCESS) {
      call MsgPool.put(msg);
      if( call MsgQueue.empty() == TRUE) {
        busySending = FALSE;
        return;
      }
      msg = call MsgQueue.dequeue();
    }
    destination = call AMPacket.destination(msg); 
    length = call Packet.payloadLength(msg); 

    call Sender.send(destination, msg, length); 
  }
}

