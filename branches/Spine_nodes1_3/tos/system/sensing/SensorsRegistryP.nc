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
 * Module Component of the Sensor Registry. Each sensor driver must register itself to the registry at boot time.
 * This component allows the retrieval of the sensor list.
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

#ifndef SENSORS_REGISTRY_SIZE
#define SENSORS_REGISTRY_SIZE 16
#endif

module SensorsRegistryP {

       provides interface SensorsRegistry;
       
       uses interface SensorBoardController;
}

implementation {

       uint8_t sensorList[SENSORS_REGISTRY_SIZE];
       uint32_t sensSamplingTimeList[SENSORS_REGISTRY_SIZE];
       uint8_t sensCount = 0;

       command error_t SensorsRegistry.registerSensor(enum SensorCode sensorID) {
           if (sensCount < SENSORS_REGISTRY_SIZE) { // to avoid memory leaks
              sensorList[sensCount] = sensorID;
              sensSamplingTimeList[sensCount++] = 0;
              return SUCCESS;
           }
           return FAIL;
       }

       command uint8_t* SensorsRegistry.getSensorList(uint8_t* sensorsCount) {
           *sensorsCount = sensCount;
           return sensorList;
       }
       
       command uint32_t SensorsRegistry.getSamplingTime(enum SensorCode sensorID) {
           uint8_t i;
           for (i = 0; i<sensCount; i++)
              if (sensorList[i] == sensorID)
                 return sensSamplingTimeList[i];
           
           return 0;
       }
       
       command void SensorsRegistry.setSamplingTime(enum SensorCode sensorID, uint32_t sT) {
           uint8_t i;
           for (i = 0; i<sensCount; i++)
              if (sensorList[i] == sensorID) {
                 sensSamplingTimeList[i] = sT;
                 break;
              }
       }
       
       command uint8_t SensorsRegistry.getBufferID(enum SensorCode sensorCode, enum ValueTypes valueType) {
          return call SensorBoardController.getBufferID(sensorCode, valueType);
       }
       
       command error_t SensorsRegistry.getSensorAndChannelForBufferID(uint8_t bufferID, enum SensorCode *sensorCode, uint8_t *channel) {
          return call SensorBoardController.getSensorAndChannelForBufferID(bufferID, sensorCode, channel);
       }

       event void SensorBoardController.acquisitionStored(enum SensorCode sensorCode, error_t result, int8_t resultCode){}

}




