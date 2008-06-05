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
 * Interface of the Sensor Board Manager. This component has been introduces to abstract
 * the access to the different sensors of the specific sensorboard and 
 * to decouple the SPINE v1.2 core to the peripherals. 
 * This way, the only needed information to access a sensor, is to know its
 * code (as known by the Sensor Manager).
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */
 interface SensorBoardManager {

       /**
       * Returns the number of significant bits in the reading for the given sensor.
       *
       * @param 'sensorCode' the sensor
       *
       * @return 'uint8_t'
       */
       command uint8_t getSignificantBits(enum SensorCode sensorCode);
       
       /**
       * Returns the number of channels for the given sensor (i.e. if the sensor is a 3D accelerometer, it will have 3 channels;
       *                                                           instead, if is a temperature sensor it will have just 1 channel).
       *
       * @param 'sensorCode' the sensor
       *
       * @return 'uint8_t'
       */
       command uint8_t getChannelsNumber(enum SensorCode sensorCode);

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
       * @param 'sensorCode' the sensor from which get the values
       * @param 'buffer' the buffer array in which to store the values. Note the caller must pre-allocate 
       *                 a buffer big enough to contain all the values
       *
       * @return 'uint16_t'
       */
       command void getAllValues(enum SensorCode sensorCode, uint16_t* buffer);
       
       /**
       * Returns serial number or other unique ID for the given sensor
       *
       * @return 'uint16_t' the sensor ID
       */
       command uint16_t getSensorID(enum SensorCode sensorCode);

       /**
       * Returns the number of value type available (usually are related to sensor channels, if more than one)
       * for the given sensor
       *
       * @return 'uint8_t' the number of value type available
       */
       command uint8_t getValueTypesNumber(enum SensorCode sensorCode);


       /**
       * Returns the value types code list for the given sensor
       *
       * @return 'uint8_t*' the value types code list. Note the caller must use 'getValueTypesNumber' to know how many value types are available
       */
       command uint8_t* getValueTypesList(enum SensorCode sensorCode);

       /**
       * Returns the number of acquire type for the given sensor
       *
       * @return 'uint8_t' the number of acquire type available
       */
       command uint8_t getAcquireTypesNumber(enum SensorCode sensorCode);

       /**
       * Returns the acquire types code list for the given sensor
       *
       * @return 'uint8_t*' the acquire types code list. Note the caller must use 'getAcquireTypesNumber' to know how many acquire types are available
       */
       command uint8_t* getAcquireTypesList(enum SensorCode sensorCode);
       
       
       /**
       * This events is thrown as soon as the given sensor completes its data acquisition process
       *
       * @param 'sensorCode' the sensor that has thrown this event
       * @param 'result' SUCCESS if the acquisition has been completed successfully, FAIL otherwise
       * @param 'resultCode' the specific success or fail code signaled by the sensor driver
       *
       * @return void
       */
       event void acquisitionDone(enum SensorCode sensorCode, error_t result, int8_t resultCode);

 }




