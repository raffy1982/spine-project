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
 * Module component of the internal temperature sensor driver
 * for the telosb platform
 *
 * @author Raffaele Gravina <rgravina@wsnlabberkeley.com>
 *
 * @version 1.0
 */

module HilInternalTemperatureSensorP {

  uses {
    interface Read<uint16_t> as InternalTemp;

    interface Boot;
    interface SensorsRegistry;
  }

  provides interface Sensor;
}

implementation {
  
    uint16_t internTemp = 0;
    
    uint8_t valueTypesList[1];

    uint8_t acquireTypesList[1];
    
    event void Boot.booted() {
        valueTypesList[0] = CH_1;
        acquireTypesList[0] = CH_1_ONLY;

        call SensorsRegistry.registerSensor(INTERNAL_TEMPERATURE_SENSOR);
    }


    /**
    * Returns the number of significant bits in the reading.
    *
    *
    * @return 'uint8_t'
    */
    command uint8_t Sensor.getSignificantBits() {
        return 12;
    }
    
    /**
    * Returns the number of channels of the sensor (i.e. if the sensor is a 3D accelerometer, it will have 3 channels;
    *                                                    instead, if is a temperature sensor it will have just 1 channel).
    *
    * @return 'uint8_t'
    */
    command uint8_t Sensor.getChannelsNumber() {
        return 1;
    }

    /**
    * Commands the reading of the current voltage level.
    *
    * @return SUCCESS if it's all ok, FAIL otherwise
    */
    command error_t Sensor.acquireData(enum AcquireTypes acquireType) {
        call InternalTemp.read();
        return SUCCESS;
    }
    
    /**
    * Gets the data value of the given 'valueType'
    *
    * @return 'uint16_t' the data value of the given 'valueType'
    */
    command uint16_t Sensor.getValue(enum ValueTypes valueType) {
        return internTemp; // here the valueType is not usefull
    }

    /**
    * Returns all the last acquired 'channels' values of the sensor
    * Note that this command must be called within the 'acquisitionDone' event handler
    * to be sure to get the latest valid data
    *
    * @param 'buffer' the buffer array in which to store the values. Note the caller must pre-allocate
    *                 a buffer big enough to contain all the values
    *
    * @return 'void' (the result of the command is stored in the given buffer)
    */
    command void Sensor.getAllValues(uint16_t* buffer) {
        memcpy(buffer, &internTemp, 2);
    }
    
    event void InternalTemp.readDone(error_t result, uint16_t data) {
       if (result != SUCCESS)
	   internTemp = 0;
       else
           internTemp = data;
       
       signal Sensor.acquisitionDone(result, 0); // here the acquireType is not usefull
    }
    
    /**
    * Returns the sensor code
    *
    * @return 'enum SensorCode' the sensor code
    */
    command enum SensorCode Sensor.getSensorCode() {
        return INTERNAL_TEMPERATURE_SENSOR;
    }
    
    /**
    * Returns the sensor serial number or other unique ID
    *
    * @return 'uint16_t' the sensor ID
    */
    command uint16_t Sensor.getSensorID() {
        return 0xacfd; // the ID has been randomly choosen
    }
    
    /**
    * Returns the number of value type available (usually are related to sensor channels, if more than one)
    *
    * @return 'uint8_t' the number of value type available
    */
    command uint8_t Sensor.getValueTypesNumber() {
        return 1;
    }

    /**
    * Returns the value types code list
    *
    * @return 'uint8_t*' the value types code list. Note the caller must use 'getValueTypesNumber' to know how many value types are available
    */
    command uint8_t* Sensor.getValueTypesList() {
        return valueTypesList;
    }
    
    /**
    * Returns the number of acquire type
    *
    * @return 'uint8_t' the number of acquire type available
    */
    command uint8_t Sensor.getAcquireTypesNumber() {
        return 1;
    }

    /**
    * Returns the acquire types code list
    *
    * @return 'uint8_t*' the acquire types code list. Note the caller must use 'getAcquireTypesNumber' to know how many acquire types are available
    */
    command uint8_t* Sensor.getAcquireTypesList() {
        return acquireTypesList;
    }
}


