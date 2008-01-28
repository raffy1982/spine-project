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

#include "AmpPacketsConstants.h"

/**
 * This component builds the AMP (Activity Monitoring Feature Selection Protocol)
 * Service Advertisement packet.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
module AmpServiceAdvertisementC
{
   provides interface AmpServiceAdvertisement;
}
implementation
{
      // node with 1 accelerometer
      //uint8_t msg[SERVICE_ADVERTISEMENT_PKT_SIZE - PKT_HEADER_SIZE] = { 0x70, 0x0, 0x0, 0x44, 0x32, 0x14, 0xc7, 0x42,
      //                                       0x54, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
      /*
       msg[0] = 0x70; // 0x70 =   01    11    00    00
                     //        accel   xyz   nomore sensors
       msg[1] = 0x0; // nomore sensors on board
      */

      // node with 1 gyro and 1 accelerometer
      uint8_t msg[SERVICE_ADVERTISEMENT_PKT_SIZE - PKT_HEADER_SIZE] = { 0x7a, 0x0, 0x0, 0x44, 0x32, 0x14, 0xc7, 0x42,
                                              0x54, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};

      /*
      msg[0] = 0x7a; // 0x7a =   01    11    10    10
                     //        accel  xyz   gyro   xy
      msg[1] = 0x0; // nomore sensors on board

      // featureCode : ROW_DATA_CODE   MEAN_CODE   MEDIAN_CODE   CENTRAL_VALUE_CODE   AMPLITUDE_CODE
      // binaryCode  :    00000          00001        00010             00011              00100
      // byte merge  :    00000000 (0x0)    01000100 (0x44)              00110010 (0x32)       00010100 (0x14)

      // featureCode : RANGE_CODE      MIN_CODE   MAX_CODE   RMS_CODE     VAR_CODE    ST_DEV_CODE
      // binaryCode  :   00101           00110      00111     01000         01001       01010
      // byte merge  :                     11000111 (0xc7)    01000010 (0x42)  0101010 0 (0x54)

      msg[2] = 0x0;
      msg[3] = 0x44;
      msg[4] = 0x32;
      msg[5] = 0x14;
      msg[6] = 0xc7;
      msg[7] = 0x42;
      msg[8] = 0x54;
      
      // any other features available
      msg[9] = 0x0;
      msg[10] = 0x0;
      msg[11] = 0x0;
      msg[12] = 0x0;
      msg[13] = 0x0;
      msg[14] = 0x0;
      msg[15] = 0x0;
      msg[16] = 0x0;
      msg[17] = 0x0;
      msg[18] = 0x0;
      msg[19] = 0x0;
      msg[20] = 0x0;
      msg[21] = 0x0;
      */

      /*
      * Builds the AMP Service Advertisement packet starting from the feature capabilities and the
      * equipped sensors of the mote
      *
      * @param msgServAd : the data structure in which the mote properties info will be placed
      *
      * @return void
      */
      command void AmpServiceAdvertisement.build(uint8_t* msgServAd) {

         memcpy(msgServAd, msg, (SERVICE_ADVERTISEMENT_PKT_SIZE - PKT_HEADER_SIZE) );

      }

}




