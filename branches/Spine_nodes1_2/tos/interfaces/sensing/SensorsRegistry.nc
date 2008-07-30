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
 * Interface of the Sensor Registry. Each sensor driver must register itself to the registry at boot time.
 * This component allows the retrieval of the sensor list.
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */
 
 #include "SensorsConstants.h"

 interface SensorsRegistry {

       /**
	* Registers a new sensor. This command must be called by each SPINE sensor driver at boot time to allow the inclusion of that sensor
	* among the service advertisement message.
	*
	* @param 'SensorCode' the code of the sensor to register
	*
	* @return 'error_t' SUCCESS if the registration has success; FAIL otherwise
	*/
       command error_t registerSensor(enum SensorCode sensorCode);

       /**
       * Returns the sensor list. The caller has to invoke the 'getSensorsCount' command in order to know the size of this list.
       *
       * @param 'sensorsCount' the variable in where to store the size of the sensor codes list.
       *
       * @return 'uint8_t*' the pointer to the sensor list
       */
       command uint8_t* getSensorList(uint8_t* sensorsCount);
       
       /**
       * Returns the sampling time for the given sensor.
       *
       * @param 'sensorCode' the sensor we are interested in.
       *
       * @return 'uint32_t' the value of the sampling time (in ms)
       */
       command uint32_t getSamplingTime(enum SensorCode sensorCode);
       
       /**
       * Sets the sampling time for the given sensor.
       *
       * @param 'sensorCode' the sensor we are interested in.
       * @param 'sT' the value of the sampling time (in ms).
       *
       * @return 'void'
       */
       command void setSamplingTime(enum SensorCode sensorCode, uint32_t sT);

       /**
       * Returns the buffer ID reserved for the given channel and sensor code.
       * This method is here for convenience and it's the same of the corresponding one into the SensorBoard Controller component.
       *
       * @param 'sensorCode' the sensor we are interested in
       * @param 'valueType' the channel code we are interested in
       *
       * @return the buffer ID
       */
       command uint8_t getBufferID(enum SensorCode sensorCode, enum ValueTypes valueType);
       
       /**
       * Returns the channel and the sensor code mapped on the given buffer id.
       * This method is here for convenience and it's the same of the corresponding one into the SensorBoard Controller component.
       *
       * @param 'sensorCode' the sensor we are interested in.
       * @param 'sT' the value of the sampling time (in ms).
       *
       * @return SUCCESS if a match is found; FAIL otherwise.
       */
       command error_t getSensorAndChannelForBufferID(uint8_t bufferID, enum SensorCode *sensorCode, uint8_t *channel);

       /**
       * Resets the state of the SensorRegistry.
       *
       *
       * @return 'void'
       */
       command void reset();

 }




