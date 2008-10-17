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

#include "AM.h"

generic configuration AMQueuedSendC(am_id_t AM_ID, uint8_t QUEUE_SIZE) {
  provides {
    interface BufferedSend as Send;
    interface Packet;
    interface AMPacket;
  }
}

implementation {          

  components new AMQueuedSendP();
  Send = AMQueuedSendP;

  components new AMSenderC(AM_ID) as SenderC;
  AMQueuedSendP.Sender -> SenderC;
  
  components ActiveMessageC;
  Packet= ActiveMessageC;
  AMPacket= ActiveMessageC;
  AMQueuedSendP.Packet -> ActiveMessageC;
  AMQueuedSendP.AMPacket -> ActiveMessageC;
      
  components new QueueC(message_t*, QUEUE_SIZE) as Queue;
  components new PoolC(message_t, QUEUE_SIZE) as Pool;
  AMQueuedSendP.MsgQueue -> Queue;
  AMQueuedSendP.MsgPool -> Pool;
}
