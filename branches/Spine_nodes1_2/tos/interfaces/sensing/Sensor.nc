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
 *  Interface for the generic sensor.
 *  Every actual sensor drivers should provide this interface in order to be SPINE v1.2 compliant
 *
 *
 * @author Raffaele Gravina <rgravina@wsnlabberkeley.com>
 *
 * @version 1.0
 */

#include "SensorsConstants.h"

interface Sensor {

    /**
    * Returns the number of significant bits in the readings.
    *
    *
    * @return 'uint8_t'   the number of significant bits in the readings
    */
    command uint8_t getSignificantBits();
    
    /**
    * Commands the sensor to start the specified data acquisition
    *
    * @param 'acquireType' refers to the type of acquisition the sensor has to make
    *        (i.e. if the actual sensor is a 3D accelerometer,
    *        the user might need just the current value on the X-axis, but not on all the three axis)
    *
    * @return SUCCESS if it's all ok, FAIL otherwise
    */
    command error_t acquireData(enum AcquireTypes acquireType);

    /**
    * Returns the last acquired value of the given valueType
    * Note that this command must be called within the 'acquisitionDone' event handler 
    * to be sure to get the latest valid data
    *
    * @param 'valueType' the sensor value type in which the caller is interested at
    *
    * @return 'uint16_t'
    */
    command uint16_t getValue(enum ValueTypes valueType);
    
    /**
    * Returns all the last acquired 'channels' values of the sensor
    * Note that this command must be called within the 'acquisitionDone' event handler
    * to be sure to get the latest valid data
    *
    * @param 'buffer'    the buffer array in where to store the values. Note the caller must pre-allocate
    *                    a buffer big enough to contain all the values
    * @param 'valuesNr'  the variable in where to store the number of values in the 'buffer' array
    *
    * @return 'void' (the result of the command is stored in the given buffer)
    */
    command void getAllValues(uint16_t* buffer, uint8_t* valuesNr);

    /**
    * Returns the sensor serial number or other unique ID
    *
    * @return 'uint16_t' the sensor ID
    */
    command uint16_t getSensorID();

    /**
    * Returns the sensor code
    *
    * @return 'enum SensorCode' the sensor code
    */
    command enum SensorCode getSensorCode();

    /**
    * Returns the number of channels of the sensor (i.e. if the sensor is a 3D accelerometer, it will have 3 channels;
    *                                                    instead, if is a temperature sensor it will have just 1 channel).
    *
    * @param 'valuesTypeNr'  the variable in where to store the number of values (sensor channels) available
    *
    * @return 'uint8_t'  the pointer to the array containing the values list
    */
    command uint8_t* getValueTypesList(uint8_t* valuesTypeNr);

    /**
    * Returns the acquire types code list
    *
    * @param 'acquireTypesNr'  the variable in where to store the number of acquire types
    *
    * @return 'uint8_t*' the acquire types code list.
    */
    command uint8_t* getAcquireTypesList(uint8_t* acquireTypesNr);

    /**
    * This events is thrown as soon as the current sensor data acquisition process is completed
    *
    * @param 'result' SUCCESS if the acquisition has been completed successfully, FAIL otherwise
    * @param 'resultCode' the specific success or fail code signaled by the sensor driver
    *
    * @return void
    */
    event void acquisitionDone(error_t result, int8_t resultCode);

}
