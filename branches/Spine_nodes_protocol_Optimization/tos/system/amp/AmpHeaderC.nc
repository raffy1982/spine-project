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
 
module AmpHeaderC
{
   provides interface AmpHeader;
}
implementation
{
      uint8_t version;
      uint8_t ext;
      uint8_t pktType;

      uint8_t groupID;
      uint8_t sourceID;

      uint8_t destID;

      uint8_t timeStamp;

      
      uint8_t partTemp;


      /*
      * Parses the given data structure into a meaningful AMP header
      *
      * @param header : the pointer to the data structure containing the non-formatted packet header
      *
      * @return void
      */
      command void AmpHeader.parse(uint8_t* header) {
         memcpy(&partTemp, header, 1);
         version = (partTemp & 0xc0)>>6; // 0xc0 = 11000000 binario
         ext = (partTemp & 0x20)>>5; // 0x20 = 00100000 binario
         pktType = (partTemp & 0x1e)>>1; // 0x1e = 00011110 binario

         memcpy(&partTemp, (header+1), 1);
         groupID = (partTemp & 0xf0)>>4; // 0xf0 = 11110000 binario
         sourceID = (partTemp & 0x0f); // 0x0f = 00001111 binario

         memcpy(&partTemp, (header+2), 1);
         destID = (partTemp & 0xf0)>>4; // 0xf0 = 11110000 binario

         memcpy(&timeStamp, (header+3), 1);
      }
      
      /*
      * Gets the Version field of the parsed header
      *
      * @return 'uint8_t' : the AMP Version used by the AMP coordinator agent
      */
      command uint8_t AmpHeader.getVersion() {
         return version;
      }
      
      /*
      * Gets the Extension field of the parsed header
      *
      * @return 'bool' : <code>TRUE</code> if the packet has an extension, <code>FALSE</code> otherwise
      */
      command bool AmpHeader.isExtended() {
         return (ext==1);
      }
      
      /*
      * Gets the Packet Type field of the parsed header
      *
      * @return 'uint8_t' : the Packet Type code of the packet
      */
      command uint8_t AmpHeader.getPktType() {
         return pktType;
      }
      
      /*
      * Gets the Group ID field of the parsed header
      *
      * @return 'uint8_t' : the Group ID of the sender of the packet
      */
      command uint8_t AmpHeader.getGroupID() {
         return groupID;
      }
      
      /*
      * Gets the Source ID field of the parsed header
      *
      * @return 'uint8_t' : the ID of the sender of the packet
      */
      command uint8_t AmpHeader.getSourceID() {
         return sourceID;
      }
      
      /*
      * Gets the Dest ID field of the parsed header
      *
      * @return 'uint8_t' : the ID of the recipient of the packet
      */
      command uint8_t AmpHeader.getDestID() {
         return destID;
      }
      
      /*
      * Gets the TimeStamp field of the parsed header
      *
      * @return 'uint8_t' : the TimeStamp of the packet
      */
      command uint8_t AmpHeader.getTimeStamp() {
        return timeStamp;
      }

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
      command void AmpHeader.build(uint8_t* msgH, uint8_t ver, bool ex, uint8_t pktT, uint8_t grpID,
                                   uint8_t srcID, uint8_t dstID, uint8_t tS) {

         uint8_t e = ex? 1: 0;
         partTemp = (ver<<6) | (e<<5) | (pktT<<1);
         memcpy(msgH, &partTemp, 1);

         partTemp = (grpID<<4) | srcID;
         memcpy(msgH+1, &partTemp, 1);

         partTemp = (dstID<<4);
         memcpy(msgH+2, &partTemp, 1);

         memcpy(msgH+3, &tS, 1);
      }


}




