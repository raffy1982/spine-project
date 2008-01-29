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
 * This component builds the AMP (Activity Monitoring Feature Selection Protocol) Data packet.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */

module AmpDataC
{
   provides interface AmpData;
}
implementation
{
      uint8_t partTemp;

      uint8_t temp0 = 0;
      uint8_t temp1 = 0;
      uint8_t temp2 = 0;

      /*
      * Builds the Data packet starting from the given fields
      *
      * @param msgData : the data structure in which the non-formatted packet info will be placed
      * @param features : the data structure in which the feature values will be placed
      * @param featureCode : the feature code associated to this packet
      * @param sensorCode : the sensor code where the row data processed belongs to.
      * @param activeAxis0 : indicates whether the feature computation is active on the X-Axis or not
      * @param activeAxis1 : indicates whether the feature computation is active on the Y-Axis or not
      * @param activeAxis2 : indicates whether the feature computation is active on the Z-Axis or not
      * @param feature0 : the feature value on the X-Axis
      * @param feature1 : the feature value on the Y-Axis
      * @param feature2 : the feature value on the Z-Axis
      *
      * @return void
      */
      command void AmpData.build(uint8_t* msgData, int16_t* features, uint8_t featureCode, uint8_t sensorCode, bool activeAxis0, bool activeAxis1,
                                 bool activeAxis2, int16_t feature0, int16_t feature1, int16_t feature2) {

         partTemp = (featureCode<<3);
         memcpy(msgData, &partTemp, 1);

         if (activeAxis0) temp0 = 0x20; // 0x1<<5
         else temp0 = 0;
         if (activeAxis1) temp1 = 0x10; // 0x1<<4
         else temp1 = 0;
         if (activeAxis2) temp2 = 0x8;  // 0x1<<3
         else temp2 = 0;

         partTemp = (sensorCode<<6) | temp0 | temp1 | temp2 ;
         memcpy(msgData+1, &partTemp, 1);

         memcpy(features, &feature0, 2);

         memcpy(features+1, &feature1, 2);

         memcpy(features+2, &feature2, 2);
      }


}




