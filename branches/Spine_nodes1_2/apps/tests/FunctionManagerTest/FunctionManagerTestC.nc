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
 * Test component of the Function Manager.
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */
 #include "SensorsConstants.h"
 #include "Functions.h"
 module FunctionManagerTestC {
  uses interface Boot;
  uses interface Leds;
  uses interface Timer<TMilli>;
  uses interface FunctionManager;
  uses interface SensorBoardController;
}

implementation {
  
  event void Timer.fired() {
     //uint32_t i;
     uint8_t features[8];
     uint8_t features2[4];
     uint8_t function1Params[3];
     uint8_t function2Params[3];


     call SensorBoardController.setSamplingTime(INTERNAL_TEMPERATURE_SENSOR, 50);
     //call SensorBoardController.setSamplingTime(VOLTAGE_SENSOR, 50);


     function2Params[0] = 0x40; // sensCode = INTERNAL_TEMPERATURE
     function2Params[1] = 0x50; // window = 80samples
     function2Params[2] = 0x28; // shift = 40samples
     call FunctionManager.setUpFunction(FEATURE, function2Params, sizeof function2Params);

     function1Params[0] = 0x20; // sensCode = VOLT
     function1Params[1] = 0x50; // window = 80samples
     function1Params[2] = 0x28; // shift = 40samples
     //call FunctionManager.setUpFunction(FEATURE, function1Params, sizeof function1Params);


     features2[0] = INTERNAL_TEMPERATURE_SENSOR;
     features2[1] = 0x01; // featsNr = 1
     features2[2] = RANGE;
     features2[3] = 0x08; // bitmask1 = ch_1 // 0000 1000
     call FunctionManager.activateFunction(FEATURE, features2, sizeof features2);

     features[0] = VOLTAGE_SENSOR;
     features[1] = 0x01; // featsNr = 1
     features[2] = MEAN;
     features[3] = 0x08; // bitmask1 = ch_1 // 0000 1000
     //features[4] = MAX;
     //features[5] = 0x08; // bitmask2 = ch_1 // 0000 1000
     //features[6] = MIN;
     //features[7] = 0x08; // bitmask3 = ch_1 // 0000 1000
     //call FunctionManager.activateFunction(FEATURE, features, 4);


     call SensorBoardController.startSensing();
     //for(i=0;i<1000000;i++);
     call FunctionManager.startComputing();
  }

  event void Boot.booted() { call Timer.startOneShot(1024); }

  event void SensorBoardController.acquisitionDone(enum SensorCode sensorCode, error_t result, int8_t resultCode) {}
}

