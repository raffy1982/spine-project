/*****************************************************************
SPINE - Signal Processing In-Note Environment is a framework that 
allows dynamic configuration of feature extraction capabilities 
of WSN nodes via an OtA protocol

Copyright (C) 2007 Telecom Italia S.p.A. 
�
GNU Lesser General Public License
�
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
�
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the GNU
Lesser General Public License for more details.
�
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA� 02111-1307, USA.
*****************************************************************/

/**
 * This component parses and builds the AMP
 * (Activity Monitoring Feature Selection Protocol) Battery Info Request packet.
 * It also allows the retrieval of the packet fields.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
 
module AmpBatteryInfoReqC
{
   provides interface AmpBatteryInfoReq;
}
implementation
{
      bool timeFlag;
      uint8_t timeScale;

      uint16_t period;

      uint8_t partTemp;

      /*
      * Parses the given data structure into a meaningful AMP Battery Info Request packet
      *
      * @param payload : the pointer to the data structure containing the non-formatted packet
      *
      * @return void
      */
      command void AmpBatteryInfoReq.parse(uint8_t* payload) {
          memcpy(&partTemp, payload, 1);
          if( ((partTemp>>7) & 0x01) == 0x01)
               timeFlag = TRUE;

          timeScale = (partTemp & 0x60)>>5; // 0x60 = 1100000 binario

          period = partTemp & 0x1f; // 0x1f = 11111 binario
          period = period<<8;
          memcpy(&partTemp, (payload+1), 1);
          period = period | partTemp;
      }

      /*
      * Indicates whether the request periodicity is time-based or packet-based
      *
      * @return 'bool' : <code>TRUE</code> if the periodicity is time-based, 
      *                  <code>FALSE</code> otherwise
      */
      command bool AmpBatteryInfoReq.isTime() {
          return timeFlag;
      }

      /*
      * Gets the time scale of the time-based periodic request
      *
      * Multiplying this value with the period value results in the actual interval in ms
      * between two voltage sampling and sending.
      *
      * @return 'uint16_t' : the multiplier for the period specified in the packet
      */
      command uint16_t AmpBatteryInfoReq.getTimeScale() {
          switch (timeScale) {
              case 1 : return 1; // 1ms
              case 2 : return 1000; // (1000ms = 1sec)
              case 3 : return 0xEA60; // 0xEA60 = 60000 (1000ms x 60sec = 1min)
              default : return 0;
          }
          return 0;
      }

      /*
      * Gets the periodicity info about the request
      *
      * Note: at this time, the max allowed value (FFFF) for this fields
      * is interpreted as the cancel of a previous periodic request. 
      * Moreover, a 0 value is interpreted as a One-Shot (non periodic) request
      *
      * @return 'uint16_t' : the period info of the request
      */
      command uint16_t AmpBatteryInfoReq.getPeriod() {
          return period;
      }

}



