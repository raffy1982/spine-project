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
 * Interface of the Sensor Board Controller. This component has been introduces to abstract
 * the access to the different sensors of the specific sensorboard and 
 * to decouple the SPINE v1.2 core to the peripherals. 
 * This way, the only needed information to access a sensor, is to know its
 * code (as known by the Sensor Board Controller).
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */
 
 #include "SensorsConstants.h"
 #include "Functions.h"

 interface SensorBoardController {

       /**
       * Commands the specified sensor to make the given data acquisition
       *
       * @param 'sensorCode' the sensor to command
       * @param 'acquireType' refers to the type of acquisition the sensor has to make
       *        (i.e. if the actual sensor is a 3D accelerometer,
       *        the user might need just the current value on the X-axis, but not on all the three axis)
       *
       * @return SUCCESS if it's all ok, FAIL otherwise
       */
       command error_t acquireData(enum SensorCode sensorCode, enum AcquireTypes acquireType);

       /**
       * Returns the last acquired value of the given valueType of the specified sensor
       * Note that this command must be called within the 'acquisitionDone' event handler
       * to be sure to get the latest valid data
       * 
       * @param 'sensorCode' the sensor from which get the specified data value
       * @param 'valueType' the sensor value type in which the caller is interested at
       *
       * @return 'uint16_t'
       */
       command uint16_t getValue(enum SensorCode sensorCode, enum ValueTypes valueType);
       
       /**
       * Returns all the last acquired 'channels' values of the given sensor
       * Note that this command must be called within the 'acquisitionDone' event handler
       * to be sure to get the latest valid data
       * 
       * @param 'sensorCode'  the sensor from which get the values
       * @param 'buffer'      the buffer array in which to store the values. 
                              Note the caller must pre-allocate a buffer big enough to contain all the values
       * @param 'valuesNr'    the variable in where to store the number of values in the 'buffer' array
       *
       * @return void (the result of the command is stored in the given buffer)
       */
       command void getAllValues(enum SensorCode sensorCode, uint16_t* buffer, uint8_t* valuesNr);
       
       /**
       * Returns serial number or other unique ID for the given sensor
       *
       * @return 'uint16_t' the sensor ID
       */
       command uint16_t getSensorID(enum SensorCode sensorCode);

       /**
       * Returns the value types code list for the given sensor
       *
       * @param 'valuesTypeNr'  the variable in where to store the number of values (sensor channels) available
       *
       * @return 'uint8_t*' the value types code list.
       */
       command uint8_t* getValueTypesList(enum SensorCode sensorCode, uint8_t* valuesTypeNr);

       /**
       * Returns the acquire types code list for the given sensor
       *
       * @param 'acquireTypesNr'  the variable in where to store the number of acquire types
       *
       * @return 'uint8_t*' the acquire types code list.
       */
       command uint8_t* getAcquireTypesList(enum SensorCode sensorCode, uint8_t* acquireTypesNr);
       
       /**
       * Set the sampling time by which sample the given sensor. 
       * Note samplingTime=0 the sensor board controller doens't set any timer of the given sensor, but
       * request immediately the sensor for a reading and send back Ota the reading.
       *
       * @param 'samplingTime'  sampling Time (in ms) of the 'sensorCode' sensor
       *
       * @return 'uint8_t*' the acquire types code list.
       */
       command void setSamplingTime(enum SensorCode sensorCode, uint32_t samplingTime);

       /**
       *
       *
       * @return 'void'
       */
       command void startSensing();
       
       /**
       *
       *
       * @return 'void'
       */
       command void stopSensing();
       
       /**
       * Resets the state of the SensorBoardController.
       *
       *
       * @return 'void'
       */
       command void reset();

       /**
       * Returns the buffer ID reserved for the given channel and sensor code.
       *
       * @param 'sensorCode' the sensor we are interested in
       * @param 'valueType' the channel code we are interested in
       *
       * @return the buffer ID
       */
       command uint8_t getBufferID(enum SensorCode sensorCode, enum ValueTypes valueType);
       
       /**
       * Returns the channel and the sensor code mapped on the given buffer id.
       *
       * @param 'sensorCode' the sensor we are interested in.
       * @param 'sT' the value of the sampling time (in ms).
       *
       * @return SUCCESS if a match is found; FAIL otherwise.
       */
       command error_t getSensorAndChannelForBufferID(uint8_t bufferID, enum SensorCode *sensorCode, uint8_t *channel);

       /**
       * This events is thrown as soon as the given sensor completes its data acquisition process and its value(s)
       * are stored into the Buffer Pool
       *
       * @param 'sensorCode' the sensor that has thrown this event
       * @param 'result' SUCCESS if the acquisition has been completed successfully, FAIL otherwise
       * @param 'resultCode' the specific success or fail code signaled by the sensor driver
       *
       * @return void
       */
       event void acquisitionStored(enum SensorCode sensorCode, error_t result, int8_t resultCode);

 }

