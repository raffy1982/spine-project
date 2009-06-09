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
 * Module component of the SPINE Buffered Raw-Data Engine.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

#ifndef SENSORS_REGISTRY_SIZE
#define SENSORS_REGISTRY_SIZE 16   // we can have up to 16 sensors because they are addressed with 4bits into the SPINE comm. protocol
#endif

module BufferedRawDataEngineP {

	provides interface Function;

	uses {
		interface Boot;
		interface FunctionManager;
		interface SensorsRegistry;
		interface BufferPool;
	}
}

implementation {
	
	buffered_rawdata_params_t paramsList[SENSORS_REGISTRY_SIZE];  // <sensorCode, chsBitmask, bufferSize, shiftSize, samplesCount>
	uint8_t paramsIndex = 0;

	uint16_t msg[128];
	uint8_t msgByte[256];

	bool registered = FALSE;
        bool computingStarted = FALSE;
	
	event void Boot.booted() {
		if (!registered) {
			call FunctionManager.registerFunction(BUFFERED_RAWDATA);
			registered = TRUE;
		}
	}
	
	
	command bool Function.setUpFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
		return TRUE;
	}

	command bool Function.activateFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
                uint8_t i;

		uint8_t sensCode;
		uint8_t chsBitmask;
		uint8_t bufferSize;
		uint8_t shiftSize;
		
		if (functionParamsSize != 4)
		   return FALSE;

                sensCode = functionParams[0];
		chsBitmask = functionParams[1];
		bufferSize = functionParams[2];
		shiftSize = functionParams[3];

                for (i = 0; i<paramsIndex; i++)
                   if (paramsList[i].sensorCode == sensCode) {
                      paramsList[i].chsBitmask = chsBitmask;
                      paramsList[i].bufferSize = bufferSize;
                      paramsList[i].shiftSize = shiftSize;
                      break;
                   }

                if (i == paramsIndex) {
                   paramsList[paramsIndex].sensorCode = sensCode;
                   paramsList[paramsIndex].chsBitmask = chsBitmask;
                   paramsList[paramsIndex].bufferSize = bufferSize;
                   paramsList[paramsIndex].shiftSize = shiftSize;
                   paramsList[paramsIndex++].samplesCount = 0;
                }

                return TRUE;
	}
	
	command bool Function.disableFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
		uint8_t j, k;

		uint8_t sensorCode;
		uint8_t chsBitmask;
		
		if (functionParamsSize < 4)
		   return FALSE;

		sensorCode = functionParams[0];
		chsBitmask = functionParams[1];

		for(j = 0; j<paramsIndex; j++) {
			if (paramsList[j].sensorCode == sensorCode) {
				paramsList[j].chsBitmask &= chsBitmask;

				if (paramsList[j].chsBitmask == 0x0) {
					for ( k = j; k<paramsIndex; k++) {
						paramsList[k].sensorCode = paramsList[k+1].sensorCode;
						paramsList[k].chsBitmask = paramsList[k+1].chsBitmask;
						paramsList[k].bufferSize = paramsList[k+1].bufferSize;
						paramsList[k].shiftSize = paramsList[k+1].shiftSize;
						paramsList[k].samplesCount = paramsList[k+1].samplesCount;
					}
					paramsList[paramsIndex].sensorCode = 0;
					paramsList[paramsIndex].chsBitmask = 0;
					paramsList[paramsIndex].bufferSize = 0;
					paramsList[paramsIndex].shiftSize = 0;
					paramsList[paramsIndex].samplesCount = 0;
					paramsIndex--;
				}
				break;
			}
		}
		
		if (paramsIndex == 0xFF)  // CHECK
		   computingStarted = FALSE;

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
	
	event void BufferPool.newElem(uint8_t bufferID, uint16_t elem) {}

	event void FunctionManager.sensorWasSampledAndBuffered(enum SensorCode sensorCode) {
                
             uint16_t tmp;
             uint8_t mask = 0x08;
             uint8_t i, j;
             uint8_t chsCount = 0;
             
             if (computingStarted) {
                for (i = 0; i<paramsIndex; i++) {
                   if (paramsList[i].sensorCode == sensorCode) {
                      paramsList[i].samplesCount++;
                      if (paramsList[i].samplesCount == paramsList[i].shiftSize) {
                         tmp = (sensorCode<<4 | paramsList[i].chsBitmask);
                         msg[0] = sizeof(uint16_t)<<8 | tmp;
                         msg[1] = paramsList[i].bufferSize<<8;
                         
                         for (j = 0; j<MAX_VALUE_TYPES; j++) {
                            if ( (paramsList[i].chsBitmask & (mask>>j)) == (mask>>j)) {
                               call BufferPool.getData(call SensorsRegistry.getBufferID(sensorCode, j), paramsList[i].bufferSize, msg+(2+paramsList[i].bufferSize*chsCount));
                               chsCount++;
                            }
                         }
                         paramsList[i].samplesCount = 0;
                         
                         memcpy(msgByte, msg, (2+paramsList[i].bufferSize*chsCount)*sizeof(uint16_t) );
                         call FunctionManager.send(BUFFERED_RAWDATA, msgByte, (2+paramsList[i].bufferSize*chsCount)*sizeof(uint16_t));
                      }
                      break;
                   }
                }
             }
	}
	
	command void Function.reset() {
           memset(paramsList, 0x00, sizeof paramsList);
           paramsIndex = 0;

           memset(msg, 0x00, sizeof msg);
           memset(msgByte, 0x00, sizeof msgByte);

           computingStarted = FALSE;
        }
}

