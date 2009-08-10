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
    
    interface RadioController;
    interface PacketManager;

    interface SpineStartPkt;
    interface SpineSetupSensorPkt;
    interface SpineFunctionReqPkt;
    interface SpineSetupFunctionPkt;

    interface SensorsRegistry;
    interface SensorBoardController;

    interface FunctionManager;
    
    interface Timer<TMilli> as Annce_timer;
  }
}
implementation
{
  // just a temp buffer for general purpose needs
  uint8_t buffer[SPINE_APP_UTILITY_BUFFER_SIZE];


  void init() {
     // initially the Radio must be kept ON; the low-power mode will be eventually enabled at the time of START
     call RadioController.setRadioAlwaysOn(TRUE);
  }

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
     uint8_t currSensorValueTypeCount = 0;
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
     
     call PacketManager.build(SERVICE_ADV, &buffer, currBufferSize);

  }


  void handle_Setup_Function() {

     uint8_t functionCode = call SpineSetupFunctionPkt.getFunctionCode();
     uint8_t functionParamsSize;
     uint8_t* functionParams = call SpineSetupFunctionPkt.getFunctionParams(&functionParamsSize);
    
     call FunctionManager.setUpFunction(functionCode, functionParams, functionParamsSize);
  }

  void handle_Start() {
     // the radio controller is set according to the user specified needs.
     call RadioController.setRadioAlwaysOn(call SpineStartPkt.radioAlwaysOnFlag());
     if (call SpineStartPkt.enableTDMAFlag())
        call RadioController.enableTDMA(call SpineStartPkt.getNetworkSize(), (TOS_NODE_ID-1)); // that currently forces to flash the nodes with sequential IDs starting from 1

     // the sensorboard controller starts sampling its sensors;
     call SensorBoardController.startSensing();
     // the functions manager starts computing the aforeactivated functions.
     call FunctionManager.startComputing();
  }

  void handle_Reset() {
     #if defined(PLATFORM_TELOSB) || defined(PLATFORM_SHIMMER)
        WDTCTL = 0;
     #elif defined(PLATFORM_MICAZ)
        cli();
	wdt_enable(0);
	while(TRUE)
           __asm__ __volatile__("nop" "\n\t" ::);
     #endif
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


  event void PacketManager.messageReceived(enum PacketTypes pktType) {
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
  }


  event void Boot.booted() {
      init();
  }

  event void RadioController.radioOn() {}
  event void RadioController.receive(uint16_t source, enum PacketTypes pktType, void* payload, uint8_t len) {}
  event void SensorBoardController.acquisitionStored(enum SensorCode sensorCode, error_t result, int8_t resultCode) {}
  event void FunctionManager.sensorWasSampledAndBuffered(enum SensorCode sensorCode) {}
}
