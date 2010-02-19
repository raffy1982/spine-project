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

#ifndef STEP_X_THRESHOLD_WAIST
#define STEP_X_THRESHOLD_WAIST -1150
#endif

#ifndef STEP_Z_THRESHOLD_WAIST
#define STEP_Z_THRESHOLD_WAIST -190
#endif

#ifndef STEP_X_THRESHOLD_THIGH
#define STEP_X_THRESHOLD_THIGH -600
#endif

#ifndef STEP_Z_THRESHOLD_THIGH
#define STEP_Z_THRESHOLD_THIGH 1100
#endif

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
		setup = TRUE;
                return TRUE;
	}
	
	command bool Function.activateFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
                active = TRUE;
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
                int32_t x, z = 0;
                uint8_t msg[sizeof(steps)];
                if(active && start) {
                    if (sensorCode == ACC_SENSOR) {
                        
                        if (waitCounter == 0) {
                            x = call SensorBoardController.getValue(ACC_SENSOR, CH_1);
                            z = call SensorBoardController.getValue(ACC_SENSOR, CH_3);
                            if (x > 0x8000) x -= 0x10000;
                            if (z > 0x8000) z -= 0x10000;
                            //if (x < STEP_X_THRESHOLD_THIGH && z > STEP_Z_THRESHOLD_THIGH) {
                            if (x < STEP_X_THRESHOLD_WAIST) {
                            //if (z < STEP_Z_THRESHOLD_WAIST) {
                                waitCounter = DEFAULT_WAIT;
                                steps++;
                                msg[0] = (steps >> 8);
                                msg[1] = (uint8_t)steps;
                                call FunctionManager.send(STEP_COUNTER, msg, sizeof(msg));
                            }
                        }
                        else
                            waitCounter--;

                    }
                }
        }
        
        event void FunctionManager.sensorWasSampledAndBuffered(enum SensorCode sensorCode) {}
	
}

