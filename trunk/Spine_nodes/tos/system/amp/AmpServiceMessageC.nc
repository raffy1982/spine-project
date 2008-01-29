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
 * This component builds the AMP (Activity Monitoring Feature Selection Protocol) ServiceMessage packet.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
 
module AmpServiceMessageC
{
   provides interface AmpServiceMessage;
}
implementation
{
      uint8_t partTemp;

      /*
      * Builds the ServiceMessage packet starting from the given fields
      *
      * @param msgServiceMessage : the data structure in which the non-formatted packet info will be placed
      * @param messageType : the ServiceMessage Type code
      * @param messageDetail : the ServiceMessage Detail code
      *
      * @return void
      */
      command void AmpServiceMessage.build(uint8_t* msgServiceMessage, uint8_t messageType, uint8_t messageDetail) {

         partTemp = (messageType<<5) | messageDetail;
         memcpy(msgServiceMessage, &partTemp, 1);

      }


}




