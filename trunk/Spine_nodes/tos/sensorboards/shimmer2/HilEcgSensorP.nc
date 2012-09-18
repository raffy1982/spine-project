module HilEcgSensorP{
	
	provides interface Sensor;
 
	uses{ 
		interface Boot;
		interface SensorsRegistry;
 		interface shimmerAnalogSetup;
		interface Msp430DmaChannel as DMA0; 
		interface Leds;}
	
}

implementation{
	
	uint8_t valueTypesList[2] = {CH_1, CH_2};
	uint8_t acquireTypesList[1] = { ALL };
	 
	uint16_t ecgData[2];
	
	norace uint8_t NBR_ADC_CHANS;
	bool firstTime = TRUE;
	

	event void Boot.booted() {
                call SensorsRegistry.registerSensor(ECG_SENSOR);
		call shimmerAnalogSetup.addECGInputs();
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
			call shimmerAnalogSetup.finishADCSetup(ecgData);
			firstTime = FALSE;
		}
	
		//else
		//	call DMA0.repeatTransfer((void*)ADC12MEM0_, (void*)ecgData, NBR_ADC_CHANS);
	
		call shimmerAnalogSetup.triggerConversion();
	
		return SUCCESS;
		
	}
 
 
	async event void DMA0.transferDone(error_t success) {
		post dataReady();
	}
  
	command uint16_t Sensor.getValue(enum ValueTypes valueType) {
		switch (valueType) {
			case CH_1 : return ecgData[0];
			case CH_2 : return ecgData[1];
			default : return 0xffff;
		}
	}

	command void Sensor.getAllValues(uint16_t* buffer, uint8_t* valuesNr) {
		*valuesNr = sizeof valueTypesList;
		atomic {
			memcpy(buffer, ecgData, sizeof(*ecgData)*2);
		}
	}

	command enum SensorCode Sensor.getSensorCode() {
		return ECG_SENSOR;
	}

	command uint16_t Sensor.getSensorID() {
		return 0x2222; // the ID has been randomly choosen
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