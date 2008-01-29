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
 * This component parses the AMP (Activity Monitoring Feature Selection Protocol) 
 * Service Discovery packet.
 * It also allows the retrieval of the packet fields.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
 
module AmpServiceDiscoveryC
{
   provides interface AmpServiceDiscovery;
}
implementation
{
      bool start;
      
      bool reset;

      uint8_t numMotes;


      uint8_t partTemp;


      /*
      * Parses the given data structure into a meaningful AMP Service Discovery packet
      *
      * @param payload : the pointer to the data structure containing the non-formatted payload
      *
      * @return void
      */
      command void AmpServiceDiscovery.parse(uint8_t* payload) {
          memcpy(&partTemp, payload, 1);
          numMotes = (partTemp & 0xf); // 0xf = 00001111 binario

          reset = (((partTemp>>6) & 0x01) == 0x01)? TRUE : FALSE;
          start = (((partTemp>>7) & 0x01) == 0x01)? TRUE : FALSE;
      }

      /*
      * It says whether this is a RESET packet or not
      *
      * @return 'bool' : TRUE if this is a Service Discovery RESET packet
      */
      command bool AmpServiceDiscovery.isReset() {
          return reset;
      }

      /*
      * It says whether this is a START packet or not
      *
      * @return 'bool' : TRUE if this is a Service Discovery START packet
      */
      command bool AmpServiceDiscovery.isStart() {
          return start;
      }

      /*
      * Gets the Number of Motes field of the parsed packet
      *
      * @return 'uint8_t' : the number of Motes the net is composed of
      */
      command uint8_t AmpServiceDiscovery.getNumMotes() {
          return numMotes;
      }


}




