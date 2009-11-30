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
 * Module component of the 'Sensirion AG SHT11' environmental
 * temperature sensor driver for the motive tmote sky platform.
 *
 *
 * If returns a 14-bit value that can be converted to degrees Celsius: 
 * 
 * temperature = -39.60 + (0.01 * raw_sample)
 *
 *
 * @author Carlo Caione <carlo.caione@unibo.it>
 *
 * @version 1.3
 */

module HilTemperatureSensorP {
  uses {
     interface Read<uint16_t> as Temp;

     interface Boot;
     interface SensorsRegistry;
  }

  provides interface Sensor;
}
implementation {
  
    uint16_t temp = 0;
    
    uint8_t valueTypesList[1];

    uint8_t acquireTypesList[1];
    
    bool registered = FALSE;
    

    event void Boot.booted() {
       if (!registered) {
          // the driver self-registers to the sensor registry
          call SensorsRegistry.registerSensor(TEMPERATURE_SENSOR);
          
          valueTypesList[0] = CH_1;
          acquireTypesList[0] = CH_1_ONLY;

          registered = TRUE;
       }
    }

    command uint8_t Sensor.getSignificantBits() {
        return 14;
    }

    command error_t Sensor.acquireData(enum AcquireTypes acquireType) {
        call Temp.read(); // here the acquireType is not usefull
        return SUCCESS;
    }

    command uint16_t Sensor.getValue(enum ValueTypes valueType) {
        return temp; // here the valueType is not usefull
    }

    command void Sensor.getAllValues(uint16_t* buffer, uint8_t* valuesNr) {
        *valuesNr = sizeof valueTypesList;
        buffer[0] = temp;
    }

    event void Temp.readDone(error_t result, uint16_t data) {
       temp = (result != SUCCESS)? 0 : data;
       signal Sensor.acquisitionDone(result, CH_1_ONLY);
    }

    command enum SensorCode Sensor.getSensorCode() {
        return TEMPERATURE_SENSOR;
    }

    command uint16_t Sensor.getSensorID() {
        return 0xbeef; // the ID has been randomly choosen
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


