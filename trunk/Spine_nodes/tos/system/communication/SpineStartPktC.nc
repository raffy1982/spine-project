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
 *  Module component for the Start SPINE packet.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */ 
 module SpineStartPktC {
       
       provides {
         interface InPacket;
         interface SpineStartPkt;
       }
 }

 implementation {

    uint16_t netSize = 1;
    bool radioAlwaysOnFlag;
    bool enableTDMAFlag;

    uint8_t startBuf[SPINE_START_PKT_SIZE];

    command bool InPacket.parse(void* payload, uint8_t len) {
       memcpy(startBuf, payload, SPINE_START_PKT_SIZE);
       netSize = startBuf[0];
       netSize = (netSize<<8) | startBuf[1];
       radioAlwaysOnFlag = ( startBuf[2] == 0)? FALSE: TRUE;
       enableTDMAFlag = ( startBuf[3] == 0)? FALSE: TRUE;

       return TRUE;
    }
    
    command uint16_t SpineStartPkt.getNetworkSize() {
       return netSize;
    }

    command bool SpineStartPkt.radioAlwaysOnFlag() {
       return radioAlwaysOnFlag;
    }
    
    command bool SpineStartPkt.enableTDMAFlag() {
       return enableTDMAFlag;
    }
}
