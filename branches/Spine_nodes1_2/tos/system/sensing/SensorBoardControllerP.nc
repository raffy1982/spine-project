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
 * Module component of the Sensor Board Controller. This component has been introduces to abstract
 * the access to the different sensors of the specific sensorboard and 
 * to decouple the SPINE v1.2 core to the peripherals. 
 * This way, the only needed information to access a sensor, is to know its
 * code (as known by the Sensor Board Controller).
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */ 

#ifndef SENSORS_REGISTRY_SIZE
#define SENSORS_REGISTRY_SIZE 16
#endif

module SensorBoardControllerP {

       provides interface SensorBoardController;

       uses {
          interface Sensor as SensorImpls[uint8_t sensorCode];
          interface Timer<TMilli> as SamplingTimers[uint8_t sensorCode];
          
          interface PacketManager;
          interface SensorsRegistry;
          
          interface BufferPool;
       }
}

implementation {

       uint8_t sensorOneShotList[SENSORS_REGISTRY_SIZE];
       uint8_t sensOneShotCount = 0;

       sensor_buffer_map_t sensorBufferMap[SENSORS_REGISTRY_SIZE * MAX_VALUE_TYPES];    // <sensorCode, channelCode, bufferID>
       uint8_t count4BufList = 0;


       command error_t SensorBoardController.acquireData(enum SensorCode sensorCode, enum AcquireTypes acquireType) {
           return call SensorImpls.acquireData[sensorCode](acquireType);
       }

       command uint16_t SensorBoardController.getValue(enum SensorCode sensorCode, enum ValueTypes valueType) {
           return call SensorImpls.getValue[sensorCode](valueType);
       }

       command void SensorBoardController.getAllValues(enum SensorCode sensorCode, uint16_t* buffer, uint8_t* valuesNr) {
           return call SensorImpls.getAllValues[sensorCode](buffer, valuesNr);
       }

       command uint16_t SensorBoardController.getSensorID(enum SensorCode sensorCode) {
           return call SensorImpls.getSensorID[sensorCode]();
       }

       command uint8_t* SensorBoardController.getValueTypesList(enum SensorCode sensorCode, uint8_t* valuesTypeNr) {
           return call SensorImpls.getValueTypesList[sensorCode](valuesTypeNr);
       }

       command uint8_t* SensorBoardController.getAcquireTypesList(enum SensorCode sensorCode, uint8_t* acquireTypesNr) {
          return call SensorImpls.getAcquireTypesList[sensorCode](acquireTypesNr);
       }

       bool buffers4SensorAllocated(uint8_t sensCode) {
          uint8_t i;
          for (i = 0; i<(SENSORS_REGISTRY_SIZE * MAX_VALUE_TYPES); i++)
            if (sensorBufferMap[i].sensorCode == sensCode) return TRUE;

          return FALSE;
       }

       command void SensorBoardController.setSamplingTime(enum SensorCode sensorCode, uint32_t samplingTime){
          uint8_t j;
          uint8_t* valueTypesList;
          uint8_t valueTypesCount;

          if (samplingTime == 0)
             call SensorImpls.acquireData[sensorCode](ALL);
          else {
             call SensorsRegistry.setSamplingTime(sensorCode, samplingTime);
             if (!buffers4SensorAllocated(sensorCode)) {
                valueTypesList = call SensorImpls.getValueTypesList[sensorCode](&valueTypesCount);
                for(j = 0; j<valueTypesCount; j++) {
                    sensorBufferMap[count4BufList+j].sensorCode = sensorCode;
                    sensorBufferMap[count4BufList+j].channelCode = valueTypesList[j];
                    sensorBufferMap[count4BufList+j].bufferID = call BufferPool.getAvailableBuffer();
                }
                count4BufList += valueTypesCount;
             }
             else 
                call SamplingTimers.startPeriodic[sensorCode](samplingTime); // check
          }
       }
       
       command void SensorBoardController.startSensing() {
          uint8_t i;
          uint8_t sensorsCount;
          uint8_t* sensorsList = call SensorsRegistry.getSensorList(&sensorsCount);
          uint8_t currSensCode;
          uint32_t currSamplingTime;

          for (i = 0; i<sensorsCount; i++) {
             currSensCode = sensorsList[i];
             currSamplingTime = call SensorsRegistry.getSamplingTime(currSensCode);
             if (currSamplingTime > 0)
                call SamplingTimers.startPeriodic[currSensCode](currSamplingTime);
          }
       }
       
       void stopSensing() {
          uint8_t i;
          uint8_t sensorsCount;
          uint8_t* sensorsList = call SensorsRegistry.getSensorList(&sensorsCount);

          for (i = 0; i<sensorsCount; i++)
             call SamplingTimers.stop[ sensorsList[i] ]();
       }

       command void SensorBoardController.stopSensing() {
          stopSensing();
       }

       command uint8_t SensorBoardController.getBufferID(enum SensorCode sensorCode, enum ValueTypes valueType) {
          uint8_t i;

          for (i = 0; i<count4BufList; i++)
            if (sensorBufferMap[i].sensorCode == sensorCode && sensorBufferMap[i].channelCode == valueType)
               return sensorBufferMap[i].bufferID;

          return 0xFF;
       }

       command error_t SensorBoardController.getSensorAndChannelForBufferID(uint8_t bufferID, enum SensorCode *sensorCode, uint8_t *channel) {
   		uint8_t i;

   		for (i = 0; i<count4BufList; i++) {
   			if (sensorBufferMap[i].bufferID == bufferID) {
   				*sensorCode = sensorBufferMap[i].sensorCode;
   				*channel = sensorBufferMap[i].channelCode;
   				return SUCCESS;
   			}
   		}
		
   		return FAIL;
       }


       event void SensorImpls.acquisitionDone[uint8_t sensorCode](error_t result, int8_t resultCode) {
           uint8_t j;

           uint8_t msg[12];  // 15: feature code 1 byte + params lenght 1byte + sensorCode 1 byte + channel bitmask + values (max 4*2 bytes = 8)
           uint8_t msgSize = 0;

           uint16_t readings[MAX_VALUE_TYPES];
           uint8_t readingsCount;

           uint8_t* valueTypesList;
           uint8_t valueTypesCount;
           uint8_t currSensorValueType;

           call SensorImpls.getAllValues[sensorCode](readings, &readingsCount);
           valueTypesList = call SensorImpls.getValueTypesList[sensorCode](&valueTypesCount);

           if (call SensorsRegistry.getSamplingTime(sensorCode) == 0) {

              msg[msgSize++] = ONE_SHOT;
              msg[msgSize++] = 1 + 1 + 2*readingsCount;
              msg[msgSize++] = sensorCode;

              msg[msgSize] = 0x0;
              for(j = 0; j<valueTypesCount; j++) {
                 currSensorValueType = valueTypesList[j];

                 switch(currSensorValueType) {
                    case CH_1 : msg[msgSize] |= 0x08; break;
                    case CH_2 : msg[msgSize] |= 0x04; break;
                    case CH_3 : msg[msgSize] |= 0x02; break;
                    case CH_4 : msg[msgSize] |= 0x01; break;
                    default: break;
                 }
              }
              msgSize++;

              for (j = 0; j<readingsCount; j++) {
                 msg[msgSize++] = readings[j]>>8;
                 msg[msgSize++] = (uint8_t)readings[j];
              }

              call PacketManager.build(DATA, &msg, msgSize);
           }
           else {
              for (j = 0; j<readingsCount; j++)
                 call BufferPool.putElem(call SensorBoardController.getBufferID(sensorCode, valueTypesList[j] ),
                                         readings[j]);   // assuming the valueTypesList and the readings buffer is populated in the same order
           }
           
           signal SensorBoardController.acquisitionDone(sensorCode, result, resultCode);
       }
       
       command void SensorBoardController.reset() {
          stopSensing();

          memset(sensorOneShotList, 0x00, sizeof sensorOneShotList);
          sensOneShotCount = 0;

          memset(sensorBufferMap, 0x00, sizeof sensorBufferMap);
          count4BufList = 0;
          
          call BufferPool.clear();
          
          call SensorsRegistry.reset();
       }

       event void SamplingTimers.fired[uint8_t sensorCode]() {
           call SensorImpls.acquireData[sensorCode](ALL);   //check
       }

       event void BufferPool.newElem(uint8_t bufferID, uint16_t elem) {

       }

       event void PacketManager.messageReceived(enum PacketTypes pktType) {}


       default command error_t SensorImpls.acquireData[uint8_t sensorCode](enum AcquireTypes acquireType) {
           dbg(DBG_USR1, "SensorBoardControllerP.acquireData: Executed default operation. Chances are there's an operation miswiring.\n");
           return FAIL;
       }

       default command uint16_t SensorImpls.getValue[uint8_t sensorCode](enum ValueTypes valueType) {
           dbg(DBG_USR1, "SensorBoardControllerP.getValue: Executed default operation. Chances are there's an operation miswiring.\n");
           return 0;
       }

       default command void SensorImpls.getAllValues[uint8_t sensorCode](uint16_t* buffer, uint8_t* valuesNr) {
           dbg(DBG_USR1, "SensorBoardControllerP.getAllValues: Executed default operation. Chances are there's an operation miswiring.\n");
       }

       default command uint16_t SensorImpls.getSensorID[uint8_t sensorCode]() {
           dbg(DBG_USR1, "SensorBoardControllerP.getSensorID: Executed default operation. Chances are there's an operation miswiring.\n");
           return 0;
       }

       default command uint8_t* SensorImpls.getValueTypesList[uint8_t sensorCode](uint8_t* valuesTypeNr) {
           dbg(DBG_USR1, "SensorBoardControllerP.getValueTypesList: Executed default operation. Chances are there's an operation miswiring.\n");
           return NULL;
       }

       default command uint8_t* SensorImpls.getAcquireTypesList[uint8_t sensorCode](uint8_t* acquireTypesNr) {
           dbg(DBG_USR1, "SensorBoardControllerP.getAcquireTypesList: Executed default operation. Chances are there's an operation miswiring.\n");
           return NULL;
       }
       
       default command void SamplingTimers.startPeriodic[uint8_t sensorCode](uint32_t dt) {
           dbg(DBG_USR1, "SensorBoardControllerP.startPeriodic: Executed default operation. Chances are there's an operation miswiring.\n");
       }
       
       default command void SamplingTimers.stop[uint8_t sensorCode]() {
           dbg(DBG_USR1, "SensorBoardControllerP.stop: Executed default operation. Chances are there's an operation miswiring.\n");
       }
}




