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
 *
 * @author Raffaele Gravina        <rgravina@deis.unical.it>
 *
 * @version 1.3
 */
 
 module HilEmgSensorP{
 
	
 
	provides interface Sensor;
 
 
 
	uses{ 
 
		interface Boot;
 
		interface SensorsRegistry;
 
 		interface shimmerAnalogSetup;
 
		interface Msp430DmaChannel as DMA0; 
 
		interface Leds;}
 
	
 
}
 

 
implementation{
 
	
 
	uint8_t valueTypesList[1] = {CH_1};
 
	uint8_t acquireTypesList[1] = { ALL };
 
	 
 
	uint16_t emgData[1];
 
	
 
	norace uint8_t NBR_ADC_CHANS;
 
	bool firstTime = TRUE;
 
	
 

 
	event void Boot.booted() {
 
                call SensorsRegistry.registerSensor(EMG_SENSOR);
 
		call shimmerAnalogSetup.addEMGInput();
 
		NBR_ADC_CHANS = call shimmerAnalogSetup.getNumberOfChannels();
 
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
 

 
		if(firstTime){
 
			call shimmerAnalogSetup.finishADCSetup(emgData);
 
			firstTime = FALSE;
 
		}
 
	
 
		//else
 
		//	call DMA0.repeatTransfer((void*)ADC12MEM0_, (void*)emgData, NBR_ADC_CHANS);
 
	
 
		call shimmerAnalogSetup.triggerConversion();
 
	
 
		return SUCCESS;
 
		
 
	}
 
 
 
 
 
	async event void DMA0.transferDone(error_t success) {
 
		post dataReady();
 
	}
 
  
 
	command uint16_t Sensor.getValue(enum ValueTypes valueType) {
 
		switch (valueType) {
 
			case CH_1 : return emgData[0];
 
			default : return 0xffff;
 
		}
 
	}
 

 
	command void Sensor.getAllValues(uint16_t* buffer, uint8_t* valuesNr) {
 
		*valuesNr = sizeof valueTypesList;
 
		atomic {
 
			memcpy(buffer, emgData, sizeof(*emgData)*2);
 
		}
 
	}
 

 
	command enum SensorCode Sensor.getSensorCode() {
 
		return EMG_SENSOR;
 
	}
 

 
	command uint16_t Sensor.getSensorID() {
 
		return 0x2121; // the ID has been randomly choosen
 
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
