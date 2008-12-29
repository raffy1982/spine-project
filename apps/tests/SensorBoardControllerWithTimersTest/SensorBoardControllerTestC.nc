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

    interface SensorBoardController;
    interface BufferedSend[spine_packet_type_t];
    interface SplitControl as AMControl;
    
    interface Leds;
  }
}
implementation
{
  event void Boot.booted() {
    call AMControl.start();
  }

  event void AMControl.startDone(error_t error) {

    //call SensorBoardController.setSamplingTime(ACC_SENSOR, 0x0000F000); // 1 min
    call SensorBoardController.setSamplingTime(VOLTAGE_SENSOR, 0x0000EA60); // 1 min
    //call SensorBoardController.setSamplingTime(INTERNAL_TEMPERATURE_SENSOR, 0x0000F800); // 1 min e 2 sec

    call SensorBoardController.startSensing();
  }

  event void AMControl.stopDone(error_t error) {
  }

  event void SensorBoardController.acquisitionStored(enum SensorCode sensorCode, error_t result, int8_t resultCode) {
      
      uint16_t data[4];
      uint8_t msg[8];
      uint8_t resNr = 4;
      uint8_t i, j=0;

      if (result != SUCCESS)
          memset(data, 0xFFFF, 4);
      else {
          call SensorBoardController.getAllValues(sensorCode, data, &resNr);
          for (i = 0; i<resNr; i++) {
             msg[j++] = (data[i]>>8);
             msg[j++] = (uint8_t)data[i];
          }
      }
      
      call BufferedSend.send[DATA](AM_BROADCAST_ADDR, &msg, resNr*2); // resNr*2 because each reading is 16bit
  }
}
