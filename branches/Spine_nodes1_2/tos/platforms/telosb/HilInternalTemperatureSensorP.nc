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
    
    bool registered = FALSE;
    

    event void Boot.booted() {
       if (!registered) {
          call SensorsRegistry.registerSensor(INTERNAL_TEMPERATURE_SENSOR);
          
          valueTypesList[0] = CH_1;
          acquireTypesList[0] = CH_1_ONLY;

          registered = TRUE;
       }
    }

    command uint8_t Sensor.getSignificantBits() {
        return 12;
    }

    command error_t Sensor.acquireData(enum AcquireTypes acquireType) {
        call InternalTemp.read();  // here the acquireType is not usefull
        return SUCCESS;
    }

    command uint16_t Sensor.getValue(enum ValueTypes valueType) {
        return internTemp; // here the valueType is not usefull
    }

    command void Sensor.getAllValues(uint16_t* buffer, uint8_t* valuesNr) {
        *valuesNr = sizeof valueTypesList;
        buffer[0] = internTemp;
    }

    event void InternalTemp.readDone(error_t result, uint16_t data) {
       internTemp = (result != SUCCESS)? 0 : data;
       signal Sensor.acquisitionDone(result, CH_1_ONLY);
    }

    command enum SensorCode Sensor.getSensorCode() {
        return INTERNAL_TEMPERATURE_SENSOR;
    }

    command uint16_t Sensor.getSensorID() {
        return 0xacfd; // the ID has been randomly choosen
    }

    command uint8_t* Sensor.getValueTypesList(uint8_t* valuesTypeNr) {
        *valuesTypeNr = sizeof valueTypesList;
        return valueTypesList;
    }

    command uint8_t* Sensor.getAcquireTypesList(uint8_t* acquireTypesNr) {
        *acquireTypesNr = sizeof acquireTypesList;
        return acquireTypesList;
    }
}


