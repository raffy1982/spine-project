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
 *
 * @author Trevor Pering <trevor.pering@intel.com>
 *
 * @version 1.2
 */

#include "Msp430Adc12.h"
#include "Mma_Accel.h"

module HilAccSensorP {
  provides interface AdcConfigure<const msp430adc12_channel_config_t*>;
  provides interface Init;
  provides interface Sensor;
  uses interface Mma_Accel as Mma7361;
  uses interface Boot;
  uses interface SensorsRegistry;
  uses interface Msp430Adc12MultiChannel as ADC;
  uses interface Resource;
}

implementation {
    uint16_t accData[3],dbuff[3];
    uint8_t blinky = 0;
    uint8_t valueTypesList[3] = { CH_1, CH_2, CH_3 };
    uint8_t acquireTypesList[1] = { ALL };
    
  const msp430adc12_channel_config_t config = {
      inch: INPUT_CHANNEL_A3,
      sref: REFERENCE_VREFplus_AVss,
      ref2_5v: REFVOLT_LEVEL_2_5,
      adc12ssel: SHT_SOURCE_ACLK,
      adc12div: SHT_CLOCK_DIV_1,
      sht: SAMPLE_HOLD_4_CYCLES,
      sampcon_ssel: SAMPCON_SOURCE_SMCLK,
      sampcon_id: SAMPCON_CLOCK_DIV_1
  };

  adc12memctl_t memctl[2] = { 
       { INPUT_CHANNEL_A4, REFVOLT_LEVEL_2_5, 0},
       { INPUT_CHANNEL_A5, REFVOLT_LEVEL_2_5, 1} };

  command error_t Init.init() {
    return SUCCESS;
  }
 
    event void Boot.booted() {
      call Resource.request();
      call Mma7361.setSensitivity(RANGE_6_0G);
    }

    async command const msp430adc12_channel_config_t* AdcConfigure.getConfiguration()
    {
      return &config;
    }

    event void Resource.granted() {
      call ADC.configure(&config,memctl,2,dbuff,3,0);
      call SensorsRegistry.registerSensor(ACC_SENSOR);
    }

    command uint8_t Sensor.getSignificantBits() {
        return 12;
    }

    task void dataReady() {
         signal Sensor.acquisitionDone(SUCCESS, acquireTypesList[0]);
    }	
    
    command error_t Sensor.acquireData(enum AcquireTypes acquireType) {
       if (acquireType != acquireTypesList[0])
          return FAIL;
       return call ADC.getData();
    }

    async event void ADC.dataReady(uint16_t *data,uint16_t numSamples) {
       atomic {
         if (numSamples > 3)
           numSamples = 3;
         memcpy(accData, data, sizeof(*data)*numSamples);
       }
       post dataReady();
     }
 
    command uint16_t Sensor.getValue(enum ValueTypes valueType) {
        switch (valueType) {
            case CH_1 : return accData[2];
            case CH_2 : return accData[1];
            case CH_3 : return accData[0];
            default : return 0xffff;
        }
    }

    command void Sensor.getAllValues(uint16_t* buffer, uint8_t* valuesNr) {
        *valuesNr = sizeof valueTypesList;
	atomic {
           //memcpy(buffer, accData, sizeof(*accData)*3);
           buffer[0] = accData[2];
           buffer[1] = accData[1];
           buffer[2] = accData[0];
        }
    }

    command enum SensorCode Sensor.getSensorCode() {
        return ACC_SENSOR;
    }

    command uint16_t Sensor.getSensorID() {
        return 0x2249; // the ID has been randomly choosen
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
