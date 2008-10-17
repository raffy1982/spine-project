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

generic module AMQueuedSendP() {
  uses {
    interface AMSend as Sender;
    interface Packet;
    interface AMPacket;

    interface Queue<message_t*> as MsgQueue;
    interface Pool<message_t> as MsgPool;
  }
  provides {
    interface BufferedSend;
  }
}

implementation {
  /* Module variables */
  bool busySending = FALSE;   

  /* Function Prototypes */
  message_t* prepareToSend(am_addr_t dest, void* data, uint16_t len);

  command error_t BufferedSend.send(am_addr_t destination, void* data, uint16_t len) {
    message_t* p_msg;

    if(len > TOSH_DATA_LENGTH)
      return ESIZE;

    if( (p_msg = prepareToSend(destination, data, len)) == NULL )
      return ERETRY;

    if (call MsgQueue.empty() && busySending == FALSE) {
      busySending = TRUE;
      return call Sender.send(destination, p_msg, len);
    }

    return call MsgQueue.enqueue(p_msg);
  }
  
  message_t* prepareToSend(uint16_t dest, void* data, uint16_t len) {
      message_t* p_msg;
      if( call MsgPool.empty() == TRUE )
        return NULL;
      p_msg = call MsgPool.get();
      call AMPacket.setDestination(p_msg, dest);
      call Packet.setPayloadLength(p_msg, len);
			  
      memcpy(call Sender.getPayload(p_msg, len), data, len);
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

