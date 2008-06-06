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
 * @version 1.0
 */

#ifndef SENSORS_REGISTRY_SIZE
#define SENSORS_REGISTRY_SIZE 10
#endif

module SensorsRegistryP {

       provides interface SensorsRegistry;
}

implementation {

       uint8_t sensorList[SENSORS_REGISTRY_SIZE];
       uint8_t sensorsCount = 0;

       command error_t SensorsRegistry.registerSensor(enum SensorCode sensorID) {
           if (sensorsCount < SENSORS_REGISTRY_SIZE) { // to avoid memory leaks
              sensorList[sensorsCount++] = sensorID;
              return SUCCESS;
           }
           return FAIL;
       }
       
       /**
       * Returns the number of registered sensors
       *
       * @return 'error_t' SUCCESS if the registration has success; FAIL otherwise
       */
       command uint8_t SensorsRegistry.getSensorsCount() {
           return sensorsCount;
       }

       /**
       * Returns the sensor list. The caller has to invoke the 'getSensorsCount' command in order to know the size of this list.
       *
       * @return 'uint8_t*' the pointer to the sensor list
       */
       command uint8_t* SensorsRegistry.getSensorList() {
           return sensorList;
       }

}




