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
 * Module component of the 'Sparkfun Integrated Dual-Axis Gyro IDG-300'
 * sensor driver for the telosb platform
 *
 * @author Marco Arena <marco.arena@guest.telecomitalia.it>
 *
 * @version 1.0
 */
 
#include "Msp430Adc12.h"

module HilGyroSensorP {
  provides interface AdcConfigure<const msp430adc12_channel_config_t*>;
  provides interface Init;
  provides interface Sensor;
  uses interface Boot;
  uses interface SensorsRegistry;
  uses interface Msp430Adc12MultiChannel as ADC;
  uses interface Resource;    
}

implementation {
    uint16_t gyroData[3],dbuff[3];
	uint8_t valueTypesList[3] = { CH_1, CH_2, CH_3 };
    uint8_t acquireTypesList[1] = { ALL };	

  const msp430adc12_channel_config_t config = {
      inch: INPUT_CHANNEL_A1,
      sref: REFERENCE_AVcc_AVss,
      ref2_5v: REFVOLT_LEVEL_1_5,
      adc12ssel: SHT_SOURCE_ACLK,
      adc12div: SHT_CLOCK_DIV_1,
      sht: SAMPLE_HOLD_4_CYCLES,
      sampcon_ssel: SAMPCON_SOURCE_SMCLK,
      sampcon_id: SAMPCON_CLOCK_DIV_1
  };
  
  adc12memctl_t memctl[2] = { 
		{ INPUT_CHANNEL_A6, REFVOLT_LEVEL_1_5, 0},
		{ INPUT_CHANNEL_A2, REFVOLT_LEVEL_1_5, 1} };
      

  command error_t Init.init() {
    // power, active low
    TOSH_MAKE_PROG_OUT_OUTPUT();   
    TOSH_SEL_PROG_OUT_IOFUNC();
    TOSH_SET_PROG_OUT_PIN();    // off

    // x
    TOSH_MAKE_ADC_1_INPUT();         
    TOSH_SEL_ADC_1_MODFUNC();

    // y
    TOSH_MAKE_ADC_6_INPUT();         
    TOSH_SEL_ADC_6_MODFUNC();

    // z
    TOSH_MAKE_ADC_2_INPUT();         
    TOSH_SEL_ADC_2_MODFUNC();
	
    // power up
    TOSH_CLR_PROG_OUT_PIN();
	
    return SUCCESS;
  }
  
  event void Boot.booted() {  
	// the driver self-registers to the sensor registry		
	call SensorsRegistry.registerSensor(GYRO_SENSOR);  
    }
  
  async command const msp430adc12_channel_config_t* AdcConfigure.getConfiguration()
    {
      return &config;
    }

   event void Resource.granted() {   
		call ADC.configure(&config,memctl,2,dbuff,3,0); 
		call ADC.getData();
    }
	
	command uint8_t Sensor.getSignificantBits() {
        return 12;
    }		

    task void dataReady() {
      signal Sensor.acquisitionDone(SUCCESS, acquireTypesList[0]);
	  call Resource.release();
    }	
    
    command error_t Sensor.acquireData(enum AcquireTypes acquireType) {
      if (acquireType != acquireTypesList[0])
         return FAIL;
	  call Resource.request();
      return SUCCESS;
    }

    async event void ADC.dataReady(uint16_t *data,uint16_t numSamples) {      
	  atomic {		
		if (numSamples > 3)
		  numSamples = 3;
		memcpy(gyroData, data, sizeof(*data)*numSamples);
	  }
	  post dataReady();
     }
 
    command uint16_t Sensor.getValue(enum ValueTypes valueType) {
	  switch (valueType) {
          case CH_1 : return gyroData[0];
          case CH_2 : return gyroData[1];
          case CH_3 : return gyroData[2];
          default : return 0xffff;
        }
    }

    command void Sensor.getAllValues(uint16_t* buffer, uint8_t* valuesNr) {      
	  *valuesNr = sizeof valueTypesList;
	  atomic {
           memcpy(buffer, gyroData, sizeof(*gyroData)*3);
      }
    }

    command enum SensorCode Sensor.getSensorCode() {
      return GYRO_SENSOR;
    }
	
	command uint16_t Sensor.getSensorID() {
      return 0x45ac; // the ID has been randomly choosen
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
