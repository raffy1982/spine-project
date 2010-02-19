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
 * Module component of the SPINE HMM Engine.
 *
 *
 * @author Raffaele Gravina
 * @author Vitali Loseu
 *
 * @version 1.2
 */

module HMMEngineP {
	
	provides {
		interface Function;
	}

	uses {
		interface Boot;
		interface HMMPrefilter;
		interface HMMPropertiesExtractor;
		interface HMMClassifier;
		interface FunctionManager;
		interface SensorBoardController;
		interface Leds;
	}
}

implementation {
	
	bool registered = FALSE;
	bool setup = FALSE;
	bool activated = FALSE;
	bool computingStarted = FALSE;

	bool sendFullMsg = FALSE;

	uint8_t result[SPINE_PKT_PAYLOAD_MAX_SIZE - 2];
	uint8_t counter = 0;

	event void Boot.booted() {
		if (!registered) {
			call FunctionManager.registerFunction(HMM);
			registered = TRUE;
		}
	}

	command bool Function.setUpFunction(uint8_t* functionParams, uint8_t functionParamsSize) {

                if (functionParamsSize != 1) {
			return FALSE;
		}
		
		sendFullMsg = functionParams[0];

                setup = TRUE;

		return TRUE;
	}
	
	command bool Function.activateFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
		
		activated = TRUE;
		
		return TRUE;
	}

	command bool Function.disableFunction(uint8_t* functionParams, uint8_t functionParamsSize) {

		if (functionParamsSize != 1)
		   return FALSE;

		activated = FALSE;
		
		return TRUE;
	}
	
	command uint8_t* Function.getSubFunctionList(uint8_t* functionCount) {
		*functionCount = 0;
		return NULL;
	}
	
	command void Function.startComputing() {
		computingStarted = TRUE;
	}
	
	command void Function.stopComputing() {
		computingStarted = FALSE;
	}
	
	event void FunctionManager.sensorWasSampledAndBuffered(enum SensorCode sensorCode) {}

	event void HMMPrefilter.prefilterDataReady(int16_t* res, uint8_t len) {
           call HMMPropertiesExtractor.extractProperties(res, len);
           /*result[0] = 6;

           result[1] = res[0] >> 8;
           result[2] = (uint8_t)res[0];
           result[3] = res[1] >> 8;
           result[4] = (uint8_t)res[1];
           result[5] = res[2] >> 8;
           result[6] = (uint8_t)res[2];

           call FunctionManager.send(HMM, result, 7);*/
        }

	event void HMMPropertiesExtractor.propertiesReady(int16_t* res, uint8_t len) {
           call HMMClassifier.classify(res, len);
        }

	event void HMMClassifier.classificationDone(uint8_t state) {
           if (!sendFullMsg) {
              result[0] = 1;
              result[1] = state;
              call FunctionManager.send(HMM, result, 2);
           }
           else {
              result[0] = ++counter;
              result[counter] = state;
              if( counter == (sizeof(result)-1) ) {
                 call FunctionManager.send(HMM, result, sizeof(result));
                 counter = 0;
              }
           }
           
        }
        
        event void SensorBoardController.acquisitionStored(enum SensorCode sensorCode, error_t res, int8_t resultCode) {
           int16_t data[3];
           uint8_t len;

           if (!setup || !activated || !computingStarted)
	      return;

	   if (sensorCode == ACC_SENSOR) {
	      call SensorBoardController.getAllValues(sensorCode, data, &len);
              call HMMPrefilter.computePrefilter(data, len);
	   }
        }

}

