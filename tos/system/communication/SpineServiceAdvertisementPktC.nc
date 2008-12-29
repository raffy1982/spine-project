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
 *  Module component for the Service Advertisement SPINE packet.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */ 

 module SpineServiceAdvertisementPktC {
       provides interface OutPacket;
 }

 implementation {
 
    uint8_t svcAdvBuf[SPINE_SVC_ADV_PKT_MAX_SIZE];

    command void* OutPacket.build(void* payload, uint8_t len, uint8_t* builtLen) {
       uint8_t tmp;
       uint8_t i;
       uint8_t sensCount;
       uint8_t currSensCode;
       uint8_t currSensChBitmask;
       uint8_t functionsCount;
       uint8_t currFunctionCode;

       uint8_t msgSize = 0;
       memcpy(&sensCount, payload, 1);
       sensCount &= 0x0F; // The & 0x0F (00001111) is to avoid overflows

       svcAdvBuf[msgSize++] = sensCount; // first byte of the svc_adv pkt is 4bit 'Reserved' 4bit 'SensorsCount'.

       for (i=0; i<sensCount; i++) {
          memcpy(&currSensCode, ((payload+1)+(i*5)), 1);
          currSensCode = currSensCode<<4;

          memcpy(&tmp, ((payload+1)+(i*5+1)), 1);
          currSensChBitmask = (tmp==TRUE) ? 0x01 : 0x00;
          
          memcpy(&tmp, ((payload+1)+(i*5+2)), 1);
          currSensChBitmask = (tmp==TRUE) ? (currSensChBitmask | 0x02) : (currSensChBitmask<<1);

          memcpy(&tmp, ((payload+1)+(i*5+3)), 1);
          currSensChBitmask = (tmp==TRUE) ? (currSensChBitmask | 0x04) : (currSensChBitmask<<1);

          memcpy(&tmp, ((payload+1)+(i*5+4)), 1);
          currSensChBitmask = (tmp==TRUE) ? (currSensChBitmask | 0x08) : (currSensChBitmask<<1);

          svcAdvBuf[msgSize++] = (currSensCode | currSensChBitmask);
       }

       memcpy(&functionsCount, ((payload+1)+(sensCount*5)), 1);
       svcAdvBuf[msgSize++] = functionsCount;

       for (i=0; i<functionsCount; i++) {
          memcpy(&currFunctionCode, (((payload+1)+(sensCount*5))+(i+1)), 1);
          svcAdvBuf[msgSize++] = currFunctionCode;
       }

       *builtLen = msgSize;
       return svcAdvBuf;
    }

}
