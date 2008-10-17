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
 *  Module component for the SPINE protocol Header.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */ 
 module SpineHeaderC {
    provides interface SpineHeader;
 }

 implementation {
    spine_header_t header_m;
    
    command void* SpineHeader.build(uint8_t version, bool extension, enum PacketTypes pktType, uint8_t groupID, uint16_t sourceID, uint16_t destID,
                                    uint8_t sequenceNumber, uint8_t fragmentNr, uint8_t totalFragments) { 
      header_m.version = vers;   
      header_m.ext = extension;  
      header_m.pktT = pktType;
      header_m.grpID = groupID;
      header_m.srcID = sourceID;
      header_m.dstID = destID;
      header_m.seqNr =  sequenceNumber;
      header_m.fragNr = fragmentNr;
      header_m.totFrags = totalFragments;
      return &header;
  }

  command bool SpineHeader.parse(void* header) {
    header_m = *((spine_header_t*)(header))
    return TRUE;
  }

  command uint8_t SpineHeader.getVersion() {
    return header_m.vers;
  }

  command bool SpineHeader.isExtended() {
    return (header_m.ext == 1);
  }

  command enum PacketTypes SpineHeader.getPktType() {
    return header_m.pktT;
  }

  command uint8_t SpineHeader.getGroupID() {
    return header_m.grpID;
  }

  command uint16_t SpineHeader.getSourceID() {
    return header_m.srcID;
  }

  command uint16_t SpineHeader.getDestID() {
    return header_m.dstID;
  }
    
  command uint8_t SpineHeader.getSequenceNumber() {
    return header_m.seqNr;
  }

  command uint8_t SpineHeader.getFragmentNumber() {
    return header_m.fragNr;
  }

  command uint8_t SpineHeader.getTotalFragments() {
    return header_m.totFrags;
  }
}
