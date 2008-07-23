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
 *  Module component for the SPINE protocol Header.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */ 
 module SpineHeaderC {
    provides interface SpineHeader;
 }

 implementation {

    uint8_t headerBuf[SPINE_HEADER_PKT_SIZE];

    uint8_t vers;      // 2 bits
    uint8_t ext;       // 1 bit
    uint8_t pktT;      // 5 bits

    uint8_t grpID;     // 8 bits

    uint16_t srcID;    // 16 bits

    uint16_t dstID;    // 16 bits

    uint8_t seqNr;     // 8 bits

    uint8_t fragNr;    // 8 bits
    uint8_t totFrags;  // 8 bits
    
    command void* SpineHeader.build(uint8_t version, bool extension, enum PacketTypes pktType, uint8_t groupID, uint16_t sourceID, uint16_t destID,
                                    uint8_t sequenceNumber, uint8_t fragmentNr, uint8_t totalFragments) {
       
       headerBuf[0] = ((version<<6) | (extension<<5) | pktType);

       headerBuf[1] = groupID;

       headerBuf[2] = sourceID>>8;
       headerBuf[3] = (uint8_t)sourceID;

       headerBuf[4] = destID>>8;
       headerBuf[5] = (uint8_t)destID;

       headerBuf[6] = sequenceNumber;

       headerBuf[7] = fragmentNr;

       headerBuf[8] = totalFragments;

       return headerBuf;
    }

    command bool SpineHeader.parse(void* header) {
       memcpy(headerBuf, header, SPINE_HEADER_PKT_SIZE);
       
       vers = (headerBuf[0] & 0xC0)>>6;    // 0xC0 = 11000000 binary
       ext = (headerBuf[0] & 0x20)>>5;     // 0x20 = 00100000 binary
       pktT = (headerBuf[0] & 0x1F);       // 0x1F = 00011111 binary

       grpID = headerBuf[1];

       srcID = headerBuf[2];                  // check
       srcID = (srcID<<8) | headerBuf[3];

       dstID = headerBuf[4];                  // check
       dstID = (dstID<<8) | headerBuf[5];

       seqNr = headerBuf[6];

       fragNr = headerBuf[7];

       totFrags = headerBuf[8];

       return TRUE;
    }

    command uint8_t SpineHeader.getVersion() {
       return vers;
    }

    command bool SpineHeader.isExtended() {
       return (ext == 1);
    }

    command enum PacketTypes SpineHeader.getPktType() {
       return pktT;
    }

    command uint8_t SpineHeader.getGroupID() {
       return grpID;
    }

    command uint16_t SpineHeader.getSourceID() {
       return srcID;
    }

    command uint16_t SpineHeader.getDestID() {
       return dstID;
    }
    
    command uint8_t SpineHeader.getSequenceNumber() {
      return seqNr;
    }

    command uint8_t SpineHeader.getFragmentNumber() {
       return fragNr;
    }

    command uint8_t SpineHeader.getTotalFragments() {
       return totFrags;
    }



 }
