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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

/**
 * This component parses and builds the header for any AMP 
 * (Activity Monitoring Feature Selection Protocol) packets. 
 * It also allows the retrieval of the header fields.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */

interface AmpHeader {
      
      /*
      * Parses the given data structure into a meaningful AMP header
      *
      * @param header : the pointer to the data structure containing the non-formatted packet header
      *
      * @return void
      */
      command void parse(uint8_t* header);

      /*
      * Gets the Version field of the parsed header
      *
      * @return 'uint8_t' : the AMP Version used by the AMP coordinator agent
      */
      command uint8_t getVersion();

      /*
      * Gets the Extension field of the parsed header
      *
      * @return 'bool' : <code>TRUE</code> if the packet has an extension, <code>FALSE</code> otherwise
      */
      command bool isExtended();
      
      /*
      * Gets the Packet Type field of the parsed header
      *
      * @return 'uint8_t' : the Packet Type code of the packet
      */
      command uint8_t getPktType();
      
      /*
      * Gets the Group ID field of the parsed header
      *
      * @return 'uint8_t' : the Group ID of the sender of the packet
      */
      command uint8_t getGroupID();
      
      /*
      * Gets the Source ID field of the parsed header
      *
      * @return 'uint8_t' : the ID of the sender of the packet
      */
      command uint8_t getSourceID();
      
      /*
      * Gets the Dest ID field of the parsed header
      *
      * @return 'uint8_t' : the ID of the recipient of the packet
      */
      command uint8_t getDestID();

      /*
      * Gets the TimeStamp field of the parsed header
      *
      * @return 'uint8_t' : the TimeStamp of the packet
      */
      command uint8_t getTimeStamp();

      
      /*
      * Builds the AMP header starting from the given fields
      *
      * @param msgH : the data structure in which the non-formatted header info will be placed
      * @param ver : the AMP Version field
      * @param ex : the Extension field
      * @param pktT : the Packet Type field
      * @param grpID : the Group ID field
      * @param srcID : the Source ID field
      * @param dstID : the Dest ID field
      * @param tS : the TimeStamp field
      *
      * @return void
      */
      command void build(uint8_t* msgH, uint8_t ver, bool ex, uint8_t pktT, uint8_t grpID,
                         uint8_t srcID, uint8_t dstID, uint8_t tS);

 }




