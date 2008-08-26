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
 *  Interface for the SPINE protocol Header.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */ 

#include "SpinePackets.h"

interface SpineHeader {

    /**
    * Compress the SPINE protocol header into a returning buffer.
    *
    * @param version          the SPINE Protocol Version field
    * @param extension        the Extension flag field
    * @param pktType          the Packet Type field
    * @param groupID          the Group ID field
    * @param sourceID         the Source ID field
    * @param destID           the Dest ID field
    * @param sequenceNumber   the SequenceNumber field
    * @param fragmentNr       the Fragment Number field
    * @param totalFragments   the Total Fragments field
    *
    * @return the pointer to the compressed SPINE header
    */
    command void* build(uint8_t version, bool extension, enum PacketTypes pktType, uint8_t groupID, uint16_t sourceID, uint16_t destID,
                        uint8_t sequenceNumber, uint8_t fragmentNr, uint8_t totalFragments);

    /**
    * Decompress the SPINE header assigning meaningful values to its state fields.
    * Note that the SPINE Header has a fixed length, so is not necessary to pass it to the 'parse' command!
    *
    * @param header   the pointer to the SPINE header to parse
    *
    * @return bool TRUE if the parsing can be done; FALSE otherwise
    */
    command bool parse(void* header);

    /**
    * Gets the Version field of the parsed header
    *
    * @return the AMP Version used by the AMP coordinator agent
    */
    command uint8_t getVersion();

    /**
    * Gets the Extension field of the parsed header
    *
    * @return <code>TRUE</code> if the packet has an extension, <code>FALSE</code> otherwise
    */
    command bool isExtended();

    /**
    * Gets the Packet Type field of the parsed header
    *
    * @return the Packet Type code of the packet
    */
    command enum PacketTypes getPktType();

    /**
    * Gets the Group ID field of the parsed header
    *
    * @return the Group ID of the sender of the packet
    */
    command uint8_t getGroupID();

    /**
    * Gets the Source ID field of the parsed header
    *
    * @return the ID of the sender of the packet
    */
    command uint16_t getSourceID();

    /*
    * Gets the Dest ID field of the parsed header
    *
    * @return the ID of the recipient of the packet
    */
    command uint16_t getDestID();

    /**
    * Gets the Sequence Number field of the parsed header
    *
    * @return the SequenceNumber of the packet
    */
    command uint8_t getSequenceNumber();
    
    /**
    * Gets the Fragment Number field of the parsed header
    *
    * @return the fragment number of this packet
    *             (if the Total Fragments field is = 1, also this field must be = 1.
    *             That means the original packet hasn't been fragmented)
    */
    command uint8_t getFragmentNumber();

    /**
    * Gets the Total Fragments field of the parsed header
    *
    * @return the total fragments of the original packet
    */
    command uint8_t getTotalFragments();


}
