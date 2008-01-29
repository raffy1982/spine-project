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
 * This component parses the AMP
 * (Activity Monitoring Feature Selection Protocol) Remove Feature packet.
 * It also allows the retrieval of the packet fields.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */

module AmpRemoveFeatureC
{
   provides interface AmpRemoveFeature;
}
implementation
{
      uint8_t featureCode;
      
      uint8_t sensorCode;
      bool disableAxis0;
      bool disableAxis1;
      bool disableAxis2;


      uint8_t partTemp;


      /*
      * Parses the given data structure into a meaningful AMP Remove Feature packet
      *
      * @param payload : the pointer to the data structure containing the non-formatted payload
      *
      * @return void
      */
      command void AmpRemoveFeature.parse(uint8_t* payload) {
          memcpy(&partTemp, payload, 1);
          featureCode = (partTemp & 0xf8)>>3; // 0xf8 = 11111000 binario

          memcpy(&partTemp, (payload+1) , 1);
          sensorCode = (partTemp & 0xc0)>>6; // 0xc0 = 11000000 binario
          if( ((partTemp>>5)& 0x01) == 0x01)
               disableAxis0 = TRUE;
          else disableAxis0 = FALSE;
          if( ((partTemp>>4) & 0x01) == 0x01)
               disableAxis1 = TRUE;
          else disableAxis1 = FALSE;
          if( ((partTemp>>3) & 0x01) == 0x01)
               disableAxis2 = TRUE;
          else disableAxis2 = FALSE;
      }

      /*
      * Gets the Feature Code field of the parsed packet
      *
      * @return 'uint8_t' : the Feature Code representing the feature computation task to be removed
      */
      command uint8_t AmpRemoveFeature.getFeatureCode() {
          return featureCode;
      }

      /*
      * Gets the Sensor Code field of the parsed packet
      *
      * @return 'uint8_t' : the Sensor Code representing the sensor associated to the specified feature
      */
      command uint8_t AmpRemoveFeature.getSensorCode() {
          return sensorCode;
      }

      /*
      * Gets the X-Axis feature disabling flag
      *
      * @return 'uint8_t' : <code>TRUE</code> if the feature calculus has to be stopped on the 
      *                      X-Axis on the specified sensor, <code>FALSE</code> otherwise
      */
      command bool AmpRemoveFeature.disableAxis0() {
          return disableAxis0;
      }

      /*
      * Gets the Y-Axis feature disabling flag
      *
      * @return 'uint8_t' : <code>TRUE</code> if the feature has to be stopped on the
      *                      Y-Axis on the specified sensor, <code>FALSE</code> otherwise
      */
      command bool AmpRemoveFeature.disableAxis1() {
          return disableAxis1;
      }

      /*
      * Gets the Z-Axis feature disabling flag
      *
      * @return 'uint8_t' : <code>TRUE</code> if the feature has to be stopped on the
      *                      Z-Axis on the specified sensor, <code>FALSE</code> otherwise
      */
      command bool AmpRemoveFeature.disableAxis2() {
          return disableAxis2;
      }


}




