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
 * Module component of the SPINE Application.
 * This module represent the SPINE entry point.
 *
 * @author Raffaele Gravina <raffale.gravina@gmail.com>
 *
 * @version 1.2
 */

#include "SpinePackets.h"
#include "SensorsConstants.h"
#include "Functions.h"

#ifndef SPINE_APP_UTILITY_BUFFER_SIZE
#define SPINE_APP_UTILITY_BUFFER_SIZE 100
#endif

#ifndef ANNCE_DELAY
#define ANNCE_DELAY 25
#endif

module SPINEApp_C
{
  uses {
    interface Boot;
    
    interface BufferedSend[spine_packet_type_t];
    interface Receive[spine_packet_type_t];
    interface SplitControl as AMControl;
    interface FrameConfiguration;

    interface SpineStartPkt;
    interface SpineSetupSensorPkt;
    interface SpineFunctionReqPkt;
    interface SpineSetupFunctionPkt;

    interface SensorsRegistry;
    interface SensorBoardController;

    interface FunctionManager;
    
    interface Timer<TMilli> as Annce_timer;
    interface Leds;
  }
}
implementation
{
  // just a temp buffer for general purpose needs
  uint8_t buffer[SPINE_APP_UTILITY_BUFFER_SIZE];

  void handle_Svc_Discovery(){
	  call Annce_timer.startOneShot(TOS_NODE_ID*ANNCE_DELAY);
  }

  void handle_Setup_Sensor() {
     call SensorBoardController.setSamplingTime(call SpineSetupSensorPkt.getSensorCode(),
                                                (call SpineSetupSensorPkt.getTimeScale() * call SpineSetupSensorPkt.getSamplingTime()) );
  }
  
  event void Annce_timer.fired() {
     uint16_t currBufferSize = 0;
     uint8_t i,j;
     uint8_t sensorsCount;
     uint8_t* sensorsList;
     uint8_t currSensorValueTypeCount;
     uint8_t* currSensorValueTypeList;
     uint8_t currSensorValueType;
     uint8_t functionsCount;
     uint8_t* functionList;

     sensorsList = call SensorsRegistry.getSensorList(&sensorsCount);
     buffer[currBufferSize++] = sensorsCount;

     for (i = 0; i<sensorsCount; i++) {
        buffer[currBufferSize++] = sensorsList[i];
        currSensorValueTypeList = call SensorBoardController.getValueTypesList(sensorsList[i], &currSensorValueTypeCount);

        memset(buffer+currBufferSize, 0x00, MAX_VALUE_TYPES);
        for(j = 0; j<currSensorValueTypeCount; j++) {
           currSensorValueType = currSensorValueTypeList[j];
           
           switch(currSensorValueType) {
              case CH_1 : buffer[currBufferSize] = TRUE; break;
              case CH_2 : buffer[currBufferSize+1] = TRUE; break;
              case CH_3 : buffer[currBufferSize+2] = TRUE; break;
              case CH_4 : buffer[currBufferSize+3] = TRUE; break;
              default: break;
           }
        }
        currBufferSize += MAX_VALUE_TYPES;
     }

     functionList = call FunctionManager.getFunctionList(&functionsCount);
     buffer[currBufferSize++] = functionsCount;

     for (i = 0; i<functionsCount; i++)
        buffer[currBufferSize++] = functionList[i];
     
     call BufferedSend.send[SERVICE_ADV](SPINE_BASE_STATION, &buffer, currBufferSize);

  }


  void handle_Setup_Function() {

     uint8_t functionCode = call SpineSetupFunctionPkt.getFunctionCode();
     uint8_t functionParamsSize;
     uint8_t* functionParams = call SpineSetupFunctionPkt.getFunctionParams(&functionParamsSize);
    
     call FunctionManager.setUpFunction(functionCode, functionParams, functionParamsSize);
  }

  void handle_Start() {
     // the sensorboard controller starts sampling its sensors;
     call SensorBoardController.startSensing();
     // the functions manager starts computing the aforeactivated functions.
     call FunctionManager.startComputing();
  }

  void handle_Reset() {
     // an HW reset is simulated resetting the complete global states of the components.
     call FunctionManager.reset();
     call SensorBoardController.reset();

     memset(buffer, 0x00, sizeof buffer);
  }

  void handle_Syncr() {
     // TODO
  }

  void handle_Function_Req() {

     uint8_t functionCode = call SpineFunctionReqPkt.getFunctionCode();
     bool enable = call SpineFunctionReqPkt.isEnableRequest();
     uint8_t functionParamsSize;
     uint8_t* functionParams = call SpineFunctionReqPkt.getFunctionParams(&functionParamsSize);

     if (enable)
        call FunctionManager.activateFunction(functionCode, functionParams, functionParamsSize);
     else
        call FunctionManager.disableFunction(functionCode, functionParams, functionParamsSize);
  }


  event message_t* Receive.receive[spine_packet_type_t pktType](message_t* msg, void* payload, uint8_t len) {
    call Leds.led1Toggle();
    switch(pktType) {
      case SERVICE_DISCOVERY: handle_Svc_Discovery(); break;
      case SETUP_SENSOR: handle_Setup_Sensor(); break;
      case SETUP_FUNCTION: handle_Setup_Function(); break;
      case START: handle_Start(); break;
      case RESET: handle_Reset(); break;
      case SYNCR: handle_Syncr(); break;
      case FUNCTION_REQ: handle_Function_Req(); break;
      default: break;
    }
    return msg;
  }


  event void Boot.booted() {
    call FrameConfiguration.setSlotLength(SPINE_SLOT_LENGTH);
    call FrameConfiguration.setFrameLength(SPINE_NUM_SLOTS);
    call AMControl.start();
  }
  event void AMControl.startDone(error_t error) {
  }
  event void AMControl.stopDone(error_t error) {
  }

  event void SensorBoardController.acquisitionStored(enum SensorCode sensorCode, error_t result, int8_t resultCode) {}
  event void FunctionManager.sensorWasSampledAndBuffered(enum SensorCode sensorCode) {}
}
