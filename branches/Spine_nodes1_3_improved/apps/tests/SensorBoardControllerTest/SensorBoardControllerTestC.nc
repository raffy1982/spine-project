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
 * Test component of the SensorBoard Controller.
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */

#include "Timer.h"
#include "SensorsConstants.h"
#include "SpinePackets.h"

module SensorBoardControllerTestC
{
  uses {
    interface Boot;
    interface Timer<TMilli>;

    interface SensorBoardController;
    interface BufferedSend[spine_packet_type_t];
    interface SplitControl as AMControl;
  }
}
implementation
{
  uint16_t readings[3];
  uint16_t bufTmp[6];


  event void Boot.booted() {
    call AMControl.start();
  }

  void startTimer() {
    call Timer.startPeriodic(20);
  }
          
  event void AMControl.startDone(error_t error) {
    startTimer();
  }

  event void AMControl.stopDone(error_t error) {
  }

  event void Timer.fired() {
    call SensorBoardController.acquireData(ACC_SENSOR, ALL);
    //call SensorBoardController.acquireData(VOLTAGE_SENSOR, CH_1_ONLY);
    //call SensorBoardController.acquireData(INTERNAL_TEMPERATURE_SENSOR, CH_1_ONLY);
    //call SensorBoardController.acquireData(GYRO_SENSOR, ALL);
  }

  event void SensorBoardController.acquisitionStored(enum SensorCode sensorCode, error_t result, int8_t resultCode) {
      uint8_t resNr;
      uint8_t i;
      if (result != SUCCESS) {
          readings[0] = 0xffff;
          readings[1] = 0xffff;
          readings[2] = 0xffff;
          //memset(readings, 0xffff, 3);
      }
      else {
          call SensorBoardController.getAllValues(sensorCode, bufTmp, &resNr);
          for (i=0; i<resNr; i++)
             readings[i] = bufTmp[i];

          //readings[0] = call SensorBoardController.getValue(sensorCode, CH_1);
          //readings[1] = call SensorBoardController.getValue(sensorCode, CH_2);
          //readings[2] = call SensorBoardController.getValue(sensorCode, CH_3);
          // 'CH_1' indicate the specific sensor driver to return the last acquired value of the first channel.
          // Tipically those parameters will be provided by the coordinator, then are transparent to the core node system
      }
      call BufferedSend.send[DATA](AM_BROADCAST_ADDR, &readings, sizeof readings);

   }

}
