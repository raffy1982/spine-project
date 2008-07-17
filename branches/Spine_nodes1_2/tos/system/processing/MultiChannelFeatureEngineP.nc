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
 * Module component of the SPINE Multi Channel Feature Engine.
 *
 *
 * @author Raffaele Gravina
 * @author Philip Kuryloski
 *
 * @version 1.0
 */

#include "Functions.h"
#include "SensorsConstants.h"

#ifndef MC_FEATURE_LIST_SIZE
#define MC_FEATURE_LIST_SIZE 32
#endif

#ifndef ACT_MC_FEATS_LIST_SIZE
#define ACT_MC_FEATS_LIST_SIZE 256
#endif

#ifndef SENSORS_REGISTRY_SIZE
#define SENSORS_REGISTRY_SIZE 16
#endif

#ifndef BUFFER_POOL_SIZE
#define BUFFER_POOL_SIZE 6
#endif

#ifndef BUFFER_LENGTH
#define BUFFER_LENGTH 80
#endif

module MultiChannelFeatureEngineP {
	
	provides {
		interface Function;
		interface MultiChannelFeatureEngine;
	}
	
	uses {
		interface Boot;
		interface MultiChannelFeature as MultiChannelFeatures[uint8_t featureID];
		interface FunctionManager;
		interface SensorsRegistry;
		interface BufferPool;
		interface Leds;
	}
}

implementation {
	
	uint8_t featureList[MC_FEATURE_LIST_SIZE];
	uint8_t featCount = 0;
	bool registered = FALSE;
	
	active_feature_t actFeatsList[ACT_MC_FEATS_LIST_SIZE];   // <featureCode, sensorCode, sensorChBitmask>
	uint8_t actFeatsIndex = 0;
	
	feat_params_t featParamsList[SENSORS_REGISTRY_SIZE];  // <sensorCode, windowSize, processingTime>
	uint8_t featParamsIndex = 0;
	
	uint8_t evalFeatsList[64];
	uint8_t evalFeatsIndex = 0;
	uint8_t evalFeatsCount = 0;
	
	bool computingStarted = FALSE;
	
	uint16_t newSamplesSinceLastFeature[SENSORS_REGISTRY_SIZE];
	
	event void Boot.booted() {
		if (!registered) {
			call FunctionManager.registerFunction(MULTI_CHANNEL_FEATURE);
			registered = TRUE;
		}
	}
	
	
	command bool Function.setUpFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
		uint8_t i;
		uint8_t sensCode;
		uint8_t windowS;
		uint8_t shiftS; 
		
		// get the job parameters
		if (functionParamsSize !=3) {
			return FALSE;
		}
		
		sensCode = functionParams[0];
		windowS = functionParams[1];
		shiftS = functionParams[2];
		
		if (shiftS == 0) {
			return FALSE;
		}
		
		sensCode = sensCode>>4;	// because the sensor code is stored in the MSB of the byte
		
        for (i = 0; i<featParamsIndex; i++)
           if (featParamsList[i].sensorCode == sensCode) {
              featParamsList[i].windowSize = windowS;
              featParamsList[i].processingTime = shiftS;
              break;
           }

        if (i == featParamsIndex) {
           featParamsList[featParamsIndex].sensorCode = sensCode;
           featParamsList[featParamsIndex].windowSize = windowS;
           featParamsList[featParamsIndex++].processingTime = shiftS;
        }

        return TRUE;
	}
	
	command bool Function.activateFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
		
		uint8_t i, j;
		uint8_t sensorCode;
		uint8_t featureNumber;
		uint8_t currFeatureCode;
		uint8_t currSensorChBitmask;
		
		if (((functionParamsSize-2)%2) != 0) {
			return FALSE;	// fail on invalid number of parameters
		}
		
		sensorCode = functionParams[0];
		featureNumber = functionParams[1];
		
		for (i=0; i<featureNumber; i++) {
			currFeatureCode = functionParams[2+2*i];
			currSensorChBitmask = functionParams[3+2*i];
			currSensorChBitmask = (currSensorChBitmask & 0x0F);
			
			for(j = 0; j<actFeatsIndex; j++)
			if (actFeatsList[j].featureCode == currFeatureCode && actFeatsList[j].sensorCode == sensorCode) {
				actFeatsList[j].sensorChBitmask |= currSensorChBitmask;
				break;
			}
			
			if (j == actFeatsIndex) {
				actFeatsList[actFeatsIndex].featureCode = currFeatureCode;
				actFeatsList[actFeatsIndex].sensorCode = sensorCode;
				actFeatsList[actFeatsIndex].sensorChBitmask = currSensorChBitmask;
				actFeatsIndex++;
			}
		}
		
		return TRUE;
	}
	
	command bool Function.disableFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
		uint8_t i, j, k;
		uint8_t sensorCode;
		uint8_t featureNumber;
		uint8_t currFeatureCode;
		uint8_t currSensorChBitmask;
		
		if (functionParamsSize < 4)
		return FALSE;
		
		memcpy(&sensorCode, functionParams, 1);
		memcpy(&featureNumber, (functionParams+1), 1);
		
		for(i = 0; i<featureNumber; i++) {
			memcpy(&currFeatureCode, (functionParams+2+2*i), 1);
			memcpy(&currSensorChBitmask, (functionParams+3+2*i), 1);
			currSensorChBitmask = (currSensorChBitmask & 0x0F);
			
			for(j = 0; j<actFeatsIndex; j++) {
				if (actFeatsList[j].featureCode == currFeatureCode && actFeatsList[j].sensorCode == sensorCode) {
					actFeatsList[j].sensorChBitmask &= currSensorChBitmask;
					
					if (actFeatsList[j].sensorChBitmask == 0x0) {
						for ( k = j; k<actFeatsIndex; k++) {
							//actFeatsList[k] = actFeatsList[k+1];
							actFeatsList[k].featureCode = actFeatsList[k+1].featureCode;
							actFeatsList[k].sensorCode = actFeatsList[k+1].sensorCode;
							actFeatsList[k].sensorChBitmask = actFeatsList[k+1].sensorChBitmask;
						}
						actFeatsList[actFeatsIndex].featureCode = 0;
						actFeatsList[actFeatsIndex].sensorCode = 0;
						actFeatsList[actFeatsIndex].sensorChBitmask = 0;
						actFeatsIndex--;
					}
					break;
				}
			}
		}
		return TRUE;
	}
	
	command uint8_t* Function.getFunctionList(uint8_t* functionCount) {
		*functionCount = featCount;
		return featureList;
	}
	
	command void Function.startComputing() {				
		// initialize the newElementsSinceLastFeatureArray
		memset(newSamplesSinceLastFeature, 0, sizeof(newSamplesSinceLastFeature));

		computingStarted = TRUE;
	}
	
	command void Function.stopComputing() {		
		computingStarted = FALSE;
	}
	
	command error_t MultiChannelFeatureEngine.registerMultiChannelFeature(enum MultiChannelFeatureCodes featureCode) {
		if (featCount < MC_FEATURE_LIST_SIZE) { // to avoid memory leaks
			featureList[featCount++] = ((MULTI_CHANNEL_FEATURE<<5) | (featureCode & 0x1F));  // The & 0x1F (00011111) is to avoid corruption in the first 'FunctionCode' 3 bits
			return SUCCESS;
		}
		return FAIL;
	}
	
	void calculateFeature(uint8_t featureCode, uint8_t sensorCode, uint8_t sensorChBitmask, uint8_t windowSize, uint16_t* bufferPoolCopy) {
		
		//uint16_t *buffer[4];
		uint16_t **buffer = NULL;
		uint16_t *buf[4];
		
		uint8_t i,j;
		uint8_t mask = 0x08;
		error_t success;
		uint8_t returnSensorChBitmask = 0x00;
		uint8_t tmp[4];
		
		uint8_t resultCount = 0;
		uint8_t resultWordLength = 0;
		
		evalFeatsList[evalFeatsIndex++] = featureCode;
		
		// because this is a multichannel feature, we must generate a new
		// bitmask that indicates the count of values generated (which could be
		// different than which channels are set to be sampled)
		returnSensorChBitmask = (0x0F << (4 - call MultiChannelFeatures.getReturnedChannelCount[featureCode]())) & 0x0F;
		
		evalFeatsList[evalFeatsIndex] = ((returnSensorChBitmask & 0x0F) << 4);
		evalFeatsList[evalFeatsIndex++] |= ((call MultiChannelFeatures.getResultSize[featureCode]()) & 0x0F);
		
		for (i = 0; i<MAX_VALUE_TYPES; i++) {
			
			// prepare the buffer (buffer is an array of pointers to the real buffers for each sensor channel)
			if ( (sensorChBitmask & (mask>>i)) == (mask>>i)) {
				buf[i] = ( bufferPoolCopy + call BufferPool.getBufferSize(0) * call SensorsRegistry.getBufferID(sensorCode, i) );
			} else {
				buf[i] = NULL;
			}
		}
		
		// here we allow the multichannel feature to write its result array directely into the evalFeatsList
		buffer = &(buf[0]);
		success = call MultiChannelFeatures.calculate[featureCode]((int16_t **)buffer, sensorChBitmask, windowSize, evalFeatsList+evalFeatsIndex);
		
		resultCount = call MultiChannelFeatures.getReturnedChannelCount[featureCode]();
		resultWordLength = call MultiChannelFeatures.getResultSize[featureCode]();
		
		// reverse result byte order
		if (resultWordLength > 1) {
			for (i = 0; i < resultCount; i++) {
				memcpy(tmp, evalFeatsList+evalFeatsIndex+(i*resultWordLength), resultWordLength);
				for (j=0; j<resultWordLength; j++) {
					*(evalFeatsList+evalFeatsIndex+(i*resultWordLength)+j) = tmp[resultWordLength-1-j];
				}
			}
		}
		
		// increment our index
		evalFeatsIndex += resultCount * resultWordLength;
		
		evalFeatsCount++;
	}
		
	event void BufferPool.newElem(uint8_t bufferID, uint16_t elem) {}
	
	event void FunctionManager.sensorWasSampled(enum SensorCode sensorCode) {
		uint8_t i;
		uint8_t shift = 0;
		uint8_t window = 0;
		uint16_t bufferPoolCopy[call BufferPool.getBufferSize(0) * call BufferPool.getBufferPoolSize()];
		
		if (! computingStarted) {
			return;
		}
						
		newSamplesSinceLastFeature[sensorCode]++;

		call BufferPool.getBufferPoolCopy(bufferPoolCopy);
		
		// determine the shift for the given sensor
		for (i = 0; i < featParamsIndex; i++) {
			if (featParamsList[i].sensorCode == sensorCode) {
				shift = featParamsList[i].processingTime;
				window = featParamsList[i].windowSize;
			}
		}
		
		// check if it is time to calculate a feature
		if (newSamplesSinceLastFeature[sensorCode] == shift) {
			// if so calculate all active features for that sensor
			evalFeatsIndex = 0;
			evalFeatsCount = 0;
			evalFeatsList[evalFeatsIndex++] = sensorCode;
			evalFeatsList[evalFeatsIndex++] = 0;
			
			for (i = 0; i < actFeatsIndex; i++) {
				if (actFeatsList[i].sensorCode == sensorCode) {
					calculateFeature(actFeatsList[i].featureCode, actFeatsList[i].sensorCode, actFeatsList[i].sensorChBitmask, window, bufferPoolCopy);
				}
			}
			
			evalFeatsList[1] = evalFeatsCount;
						
			call FunctionManager.send(MULTI_CHANNEL_FEATURE, evalFeatsList, evalFeatsIndex);
			
			newSamplesSinceLastFeature[sensorCode] = 0;
		}
	}
	
	command void Function.reset() {
           memset(actFeatsList, 0x00, sizeof actFeatsList);
           actFeatsIndex = 0;

           memset(featParamsList, 0x00, sizeof featParamsList);
           featParamsIndex = 0;

           memset(evalFeatsList, 0x00, sizeof evalFeatsList);
           evalFeatsIndex = 0;
           evalFeatsCount = 0;
           
           computingStarted = FALSE;
           memset(newSamplesSinceLastFeature, 0, sizeof newSamplesSinceLastFeature);
        }
	
	default command uint8_t MultiChannelFeatures.getReturnedChannelCount[uint8_t featureID]() {
		dbg(DBG_USR1, "MultiChannelFeatureEngineP.getReturnedChannelCount: Executed default operation. Chances are there's an operation miswiring.\n");
		return 0xFF;
	}
	
	default command uint8_t MultiChannelFeatures.getResultSize[uint8_t featureID]() {
		dbg(DBG_USR1, "MultiChannelFeatureEngineP.getResultSize: Executed default operation. Chances are there's an operation miswiring.\n");
		return 0xFF;
	}
	
	default command error_t MultiChannelFeatures.calculate[uint8_t featureID](int16_t** data, uint8_t channelMask, uint16_t dataLen, int8_t* result) {
		dbg(DBG_USR1, "MultiChannelFeatureEngineP.calculate: Executed default operation. Chances are there's an operation miswiring.\n");
		return FAIL;
	}

}

