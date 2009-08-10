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
 * Module component of the SPINE StepCounter Engine.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

module StepCounterEngineP {

	provides {
		interface Function;
	}

	uses {
		interface Boot;
		interface FunctionManager;
		interface SensorBoardController;
		interface SensorsRegistry;
	}
}

implementation {

	bool registered = FALSE;
	bool setup, active = FALSE;
        bool start = FALSE;
        
        int32_t pre = 0, curr = 0;
        bool haveHistory = FALSE;


        int16_t AVG_ACCEL;
        int16_t STEP_THRESHOLD;
	
	uint8_t waitCounter = 0;
	uint8_t DEFAULT_WAIT = 0;
	
        uint16_t steps = 0;


	event void Boot.booted() {
		if (!registered) {
			call FunctionManager.registerFunction(STEP_COUNTER);
			registered = TRUE;
		}
	}
	
	
	command bool Function.setUpFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
		
                if (functionParamsSize != 4)
		   return FALSE;	// fail on invalid number of parameters

                AVG_ACCEL = functionParams[0];
                AVG_ACCEL = (AVG_ACCEL)<<8 | functionParams[1];

                STEP_THRESHOLD = functionParams[2];
                STEP_THRESHOLD = (STEP_THRESHOLD)<<8 | functionParams[3];

                setup = TRUE;

                return TRUE;
	}

	command bool Function.activateFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
                
                active = setup;

                return TRUE;
	}
	
	command bool Function.disableFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
		active = FALSE;
                return TRUE;
	}

	command uint8_t* Function.getSubFunctionList(uint8_t* functionCount) {
		*functionCount = 0;
		return NULL;
	}
	
	command void Function.startComputing() {				
		DEFAULT_WAIT = 400/call SensorsRegistry.getSamplingTime(ACC_SENSOR);
                start = TRUE;
	}
	
	command void Function.stopComputing() {
		start = FALSE;
	}
	
	event void SensorBoardController.acquisitionStored(enum SensorCode sensorCode, error_t result, int8_t resultCode) {
                
                uint8_t msg[sizeof(steps)];
                if(active && start) {
                    if (sensorCode == ACC_SENSOR) {

                        curr = call SensorBoardController.getValue(ACC_SENSOR, CH_3);

                        if (waitCounter == 0) {

                            if (pre > 0x8000) pre -= 0x10000;
                            if (curr > 0x8000) curr -= 0x10000;

                            if (((pre - curr) > STEP_THRESHOLD || (pre - curr) < -STEP_THRESHOLD) && (pre < AVG_ACCEL || curr < AVG_ACCEL)) {

                                waitCounter = DEFAULT_WAIT;

                                if(haveHistory) {
                                   steps++;
                                   msg[0] = (steps >> 8);
                                   msg[1] = (uint8_t)steps;
                                   call FunctionManager.send(STEP_COUNTER, msg, sizeof(msg));
                                }
                            }
                        }
                        else
                            waitCounter--;
                            
                        pre = curr;

                        haveHistory = TRUE;
                    }
                }
        }
        
        event void FunctionManager.sensorWasSampledAndBuffered(enum SensorCode sensorCode) {}
	
}

