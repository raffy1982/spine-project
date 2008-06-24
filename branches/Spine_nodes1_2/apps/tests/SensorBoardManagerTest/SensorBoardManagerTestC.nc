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
 * Test component of the SensorBoard Manager.
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */

#include "Timer.h"
#include "SensorsConstants.h"

module SensorBoardManagerTestC
{
  uses {
    interface Boot;
    interface Timer<TMilli>;

    interface SensorBoardManager;
    interface RadioController;
  }
}
implementation
{
  uint8_t reading = 0;
  uint16_t readings[5];


  event void Boot.booted() {}

  void startTimer() {
    call Timer.startPeriodic(200);
  }
          
  event void RadioController.radioOn() {
    startTimer();
  }

  event void RadioController.receive(uint16_t source, enum PacketTypes pktType, void* payload, uint8_t len) {}

  event void Timer.fired() {
    if (reading == 5) {
	call RadioController.send(AM_BROADCAST_ADDR, DATA, &readings, sizeof readings);
	reading = 0;
    }

    call SensorBoardManager.acquireData(VOLTAGE_SENSOR, CH_1_ONLY);
    // call SensorManager.acquireData(ACC_SENSOR, ALL);
    // call SensorManager.acquireData(INTERNAL_TEMPERATURE_SENSOR, CH_1_ONLY);
    // call SensorManager.acquireData(GYRO_SENSOR, ALL);

  }

  event void SensorBoardManager.acquisitionDone(enum SensorCode sensorCode, error_t result, int8_t resultCode) {
      if (result != SUCCESS)
          readings[reading++] = 0xffff;
      else
          readings[reading++] = call SensorBoardManager.getValue(sensorCode, CH_1);
          // 'CH_1' indicate the specific sensor driver to return the last acquired value of the first channel.
          // Tipically those parameters will be provided by the coordinator, then are transparent to the core node system
   }

}
