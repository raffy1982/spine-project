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
 * Interface of the SPINE Packets Manager.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */
 
 #include "SpinePackets.h"

 interface PacketManager {

       /**
       * This event is signaled when the Packet Manager receive a recognized Incoming SPINE packet from the Radio Controller
       *
       * @param pktType the type of the received SPINE packet
       *
       * @return        void.
       */
       event void messageReceived(enum PacketTypes pktType);
       
       /**
       * Compress the payload of the outgoing packet into a returning buffer.
       * This command add the SPINE header to the packet buffer and request its OtA transmission using the Radio Controller.
       *
       * @param pktType    the type of the SPINE packet to send
       * @param payload    the packet payload to build and eventually send.
       * @param len        the packet payload length  (in terms of number of bytes)
       *
       * @return           void.
       */
       command void build(enum PacketTypes pktType, void* payload, uint8_t len);

 }




