/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that
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
 * Module component of the SPINE Application.
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */

#include "SpinePackets.h"
#include "SensorsConstants.h"

#ifndef SPINE_APP_UTILITY_BUFFER_SIZE
#define SPINE_APP_UTILITY_BUFFER_SIZE 300
#endif

module SPINEApp_C
{
  uses {
    interface Boot;
    
    interface PacketManager;
    interface SpineSetupSensorPkt;

    interface SensorsRegistry;
    interface SensorBoardController;

    interface FunctionManager;

    interface Timer<TMilli> as DebugTimer;    // DEBUG CODE TO BE REMOVED
    interface RadioController;                // DEBUG CODE TO BE REMOVED
  }
  
  provides interface SPINEApp;

}
implementation
{
  uint8_t buffer[SPINE_APP_UTILITY_BUFFER_SIZE];

  
  
  void init() {
     call DebugTimer.startOneShot(1000);    // DEBUG CODE TO BE REMOVED
  }

  void handle_Svc_Discovery() {
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
        buffer[currBufferSize++] = *(sensorsList+i);
        currSensorValueTypeList = call SensorBoardController.getValueTypesList(*(sensorsList+i), &currSensorValueTypeCount);

        memset(buffer+currBufferSize, 0x00, MAX_VALUE_TYPES);
        for(j = 0; j<currSensorValueTypeCount; j++) {
           currSensorValueType = *(currSensorValueTypeList+j);
           
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
        buffer[currBufferSize++] = *(functionList+i);
     
     call PacketManager.build(SERVICE_ADV, &buffer, currBufferSize);
  }

  void handle_Setup_Sensor() {
     call SensorBoardController.setSamplingTime(call SpineSetupSensorPkt.getSensorCode(),
                                                (call SpineSetupSensorPkt.getTimeScale() * call SpineSetupSensorPkt.getSamplingTime()) );
  }

  void handle_Setup_Function() {

  }

  void handle_Start() {

  }

  void handle_Reset() {

  }

  void handle_Stop() {

  }

  void handle_Function_Req() {

  }

  event void Boot.booted() {
      init();
  }
  
  command void SPINEApp.send(enum PacketTypes pktType, void* payload, uint8_t len) {
      call PacketManager.build(pktType, payload, len);
  }

  
  event void DebugTimer.fired() { // DEBUG CODE TO BE REMOVED
      handle_Svc_Discovery();
      //handle_Setup_Sensor();
  }


  
  event void PacketManager.messageReceived(enum PacketTypes pktType) {
      switch(pktType) {
        case SERVICE_DISCOVERY: handle_Svc_Discovery(); break;
        case SETUP_SENSOR: handle_Setup_Sensor(); break;
        case SETUP_FUNCTION: handle_Setup_Function(); break;
        case START: handle_Start(); break;
        case RESET: handle_Reset(); break;
        case STOP: handle_Stop(); break;
        case FUNCTION_REQ: handle_Function_Req(); break;
        default: break;
      }
  }
  
  event void SensorBoardController.acquisitionDone(enum SensorCode sensorCode, error_t result, int8_t resultCode) {}

  event void RadioController.radioOn(){} // DEBUG CODE TO BE REMOVED
  event void RadioController.receive(uint16_t source, enum PacketTypes pktType, void* payload, uint8_t len){} // DEBUG CODE TO BE REMOVED
}