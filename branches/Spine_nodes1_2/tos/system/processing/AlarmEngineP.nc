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
 * Module component of the SPINE Alarm Engine.
 *
 *
 * @author Roberta Giannantonio
 *
 * @version 1.2
 */
 
 #include "Functions.h"
 #include "SensorsConstants.h"

 #ifndef ACT_ALARM_LIST_SIZE
 #define ACT_ALARM_LIST_SIZE 12//512
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

 module AlarmEngineP {

       provides {
            interface Function;
       }
       
       uses {
            interface Boot;
            interface Feature as Features[uint8_t featureID];
            interface FunctionManager;
            interface SensorsRegistry;
            interface BufferPool;   interface Leds;
       }
 }

 implementation {
     	 
     bool start = FALSE;
     bool registered = FALSE;
     
     uint16_t newSamplesSinceLastFeatureAlarm[SENSORS_REGISTRY_SIZE];
     
     active_alarm_t actAlarmList[ACT_ALARM_LIST_SIZE];   // <sensorCode, sensorChannel, lower Threshold, upper Threshold, alarm type>
     uint8_t actAlarmIndex = 0;
     
    feat_params_t alarmParamsList[SENSORS_REGISTRY_SIZE];  // <sensorCode, windowSize, processingTime>
	uint8_t alarmParamsIndex = 0;
	
 	uint8_t evalFeatsList[128];
	uint8_t evalFeatsIndex = 0;
	uint8_t evalFeatsCount = 0;    


	uint8_t countOfChannelsInMask(uint8_t mask) {
            uint8_t i, sum = 0;
            
            for (i=0; i<MAX_VALUE_TYPES; i++)
               sum += (mask>>i & 0x01);
            
            return sum;
    }
    
     event void Boot.booted() {
          if (!registered) {
             call FunctionManager.registerFunction(ALARM);
             //alarmList[alarmCount++] = ALARM;
             registered = TRUE;
          }
     }


     command bool Function.setUpFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
		uint8_t i;
		uint8_t sensCode;
		uint8_t windowS;
		uint8_t shiftS; 

//check why this does not work		
				
//  		// get the job parameters
//  		if (functionParamsSize !=3) {
//  			return FALSE;
//  		}
	

		sensCode = functionParams[0];
		windowS = functionParams[1];
		shiftS = functionParams[2];
				
		if (shiftS == 0) {
			return FALSE;
		}
		
		sensCode = sensCode>>4;	// because the sensor code is stored in the MSB of the byte
		

		
        for (i = 0; i<alarmParamsIndex; i++)
           if (alarmParamsList[i].sensorCode == sensCode) {
              alarmParamsList[i].windowSize = windowS;
              alarmParamsList[i].processingTime = shiftS;
              break;
           }

        if (i == alarmParamsIndex) {
           alarmParamsList[alarmParamsIndex].sensorCode = sensCode;
           alarmParamsList[alarmParamsIndex].windowSize = windowS;
           alarmParamsList[alarmParamsIndex++].processingTime = shiftS;
        }
        return TRUE;
     }

     command bool Function.activateFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
        uint8_t j;
	    uint8_t dataType;			//raw data or feature code
        uint8_t sensCode;			//sensor code to monitor
        uint8_t channelMask;		//to indicate on which axis
        uint32_t lowerThreshold;	
        uint32_t upperThreshold; 	
        uint8_t alarmType;			//01= below threshold 10= above threshold 11= between thresholds

        
       if (functionParamsSize != 12)
            return FALSE;

		dataType = functionParams[0];
		sensCode = functionParams[1];
		channelMask = functionParams[2];
		lowerThreshold = (uint32_t)(((uint32_t)functionParams[3]) << 24 | (uint32_t)functionParams[4] << 16 | (uint32_t)functionParams[5] << 8 | (uint32_t)functionParams[6]);
		upperThreshold = (uint32_t)(((uint32_t)functionParams[7]) << 24 | (uint32_t)functionParams[8] << 16 | (uint32_t)functionParams[9] << 8 | (uint32_t)functionParams[10]);
		
		
		alarmType = functionParams[11];     
		

        for(j = 0; j<actAlarmIndex; j++)
        //refresh settings if the activate function is for a sensor-axis already active
         if (actAlarmList[j].sensorCode == sensCode && actAlarmList[j].channelMask == channelMask && actAlarmList[j].alarmType==alarmType && actAlarmList[j].dataType==dataType) 
	        {	
	         actAlarmList[j].lowerThreshold=lowerThreshold;
	         actAlarmList[j].upperThreshold=upperThreshold;
	         break;
	         }      

        if (j == actAlarmIndex) {
	       actAlarmList[actAlarmIndex].dataType=dataType;
	       actAlarmList[actAlarmIndex].sensorCode=sensCode;
	       actAlarmList[actAlarmIndex].channelMask=channelMask;
	       actAlarmList[actAlarmIndex].lowerThreshold=lowerThreshold;
	       actAlarmList[actAlarmIndex].upperThreshold=upperThreshold;
	       actAlarmList[actAlarmIndex].alarmType=alarmType;         
        	}
 	
     return TRUE;   
     }

     command bool Function.disableFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
        uint8_t j,k;
	    uint8_t sensCode;			//sensor code to monitor
        uint8_t channelMask;
        uint8_t alarmType;			

        if (functionParamsSize < 3)
          return FALSE;

        sensCode = functionParams[0];
        channelMask = functionParams[1];
        alarmType = functionParams[2];


       for(j = 0; j<actAlarmIndex; j++) {
        if (actAlarmList[j].sensorCode == sensCode && actAlarmList[j].channelMask == channelMask && actAlarmList[j].alarmType==alarmType) {
                for ( k = j; k<actAlarmIndex; k++) {
                   actAlarmList[k].sensorCode = actAlarmList[k+1].sensorCode;
                   actAlarmList[k].channelMask = actAlarmList[k+1].channelMask;
                   actAlarmList[k].lowerThreshold = actAlarmList[k+1].lowerThreshold;
                   actAlarmList[k].upperThreshold = actAlarmList[k+1].upperThreshold;
                   actAlarmList[k].alarmType = actAlarmList[k+1].alarmType;
                }
                actAlarmList[actAlarmIndex].sensorCode = actAlarmList[k+1].sensorCode;
                actAlarmList[actAlarmIndex].channelMask = actAlarmList[k+1].channelMask;
                actAlarmList[actAlarmIndex].lowerThreshold = actAlarmList[k+1].lowerThreshold;
                actAlarmList[actAlarmIndex].upperThreshold = actAlarmList[k+1].upperThreshold;
                actAlarmList[actAlarmIndex].alarmType = actAlarmList[k+1].alarmType;
				actAlarmIndex--;
             break;
          }
       }  
        return TRUE;
     }

     command uint8_t* Function.getSubFunctionList(uint8_t* functionCount) {
         *functionCount = 0;
         return NULL;
     }
     
     command void Function.startComputing() {
	    memset(newSamplesSinceLastFeatureAlarm, 0, sizeof(newSamplesSinceLastFeatureAlarm));
		//start flag to TRUE so that when a new data is sampled, the alarm engine will check it
		start = TRUE;
     }

     command void Function.stopComputing() {
	//TO DO
     }
     
     void calculateFeature(uint8_t featureCode, uint8_t sensorCode, uint8_t sensorChBitmask, uint8_t windowSize, uint16_t* bufferPoolCopy) {
		
		uint16_t **buffer = NULL;
		uint16_t *buf[4];
		
		uint8_t i,j;
		uint8_t mask = 0x08;
		uint8_t returnedChannelCount = 0;
		uint8_t returnSensorChBitmask = 0x00;
		uint8_t *maskAndSizePtr = NULL;
		uint32_t tmp = 0, currResult = 0;

		uint8_t resultWordLength = 0;

		evalFeatsList[evalFeatsIndex++] = featureCode;

		maskAndSizePtr = evalFeatsList+evalFeatsIndex++;

		for (i = 0; i<MAX_VALUE_TYPES; i++) {

			// prepare the buffer (buffer is an array of pointers to the real buffers for each sensor channel)
			if ( (sensorChBitmask & (mask>>i)) == (mask>>i)) {
				buf[i] = ( bufferPoolCopy + call BufferPool.getBufferSize(0) * call SensorsRegistry.getBufferID(sensorCode, i) );
			} else {
				buf[i] = NULL;
			}
	}
		
		// here we allow the  feature to write its result array directely into the evalFeatsList
		buffer = &(buf[0]);
		returnSensorChBitmask = call Features.calculate[featureCode]((int16_t **)buffer, sensorChBitmask, windowSize, evalFeatsList+evalFeatsIndex);

		resultWordLength = call Features.getResultSize[featureCode]();
        returnedChannelCount = countOfChannelsInMask(returnSensorChBitmask);

		// reverse result byte order
		if (resultWordLength > 1) {
			for (i = 0; i < returnedChannelCount; i++) {

				currResult = (resultWordLength == sizeof(uint32_t) )? 
                                                               *((uint32_t *)(evalFeatsList + evalFeatsIndex + (resultWordLength*i))) :
                                                               *((uint16_t *)(evalFeatsList + evalFeatsIndex + (resultWordLength*i)));
				for (j = 0; j<resultWordLength; j++) {
                                   tmp = ( currResult<<8*( sizeof(currResult) - resultWordLength + j) );
                                   *(evalFeatsList + evalFeatsIndex + (resultWordLength*i) + j) = (tmp>>8*( (sizeof currResult) - 1));
                               }
			}
		}

		*maskAndSizePtr = ((returnSensorChBitmask & 0x0F) << 4);
		*maskAndSizePtr |= (resultWordLength & 0x0F);
		// increment our index
		evalFeatsIndex += returnedChannelCount * resultWordLength;

		evalFeatsCount++;
	}
		
     event void FunctionManager.sensorWasSampledAndBuffered(enum SensorCode sensorCode){
	    uint8_t i,k,num_ch,res_size;
	    uint8_t notifyAlarmList[8];
		uint8_t notifyAlarmIndex=0;
		bool alarm = FALSE;
		uint8_t shift = 0;
		uint8_t window = 0;
		uint32_t elem = 0;
		uint16_t bufferPoolCopy[call BufferPool.getBufferSize(0) * call BufferPool.getBufferPoolSize()];
		
		if (! start) {
			return;
		}
							
		newSamplesSinceLastFeatureAlarm[sensorCode]++;

		call BufferPool.getBufferPoolCopy(bufferPoolCopy);
		
		// determine the shift for the given sensor
		for (i = 0; i < alarmParamsIndex; i++) {
			if (alarmParamsList[i].sensorCode == sensorCode) {
				shift = alarmParamsList[i].processingTime;
				window = alarmParamsList[i].windowSize;
			}
		}

		// check if it is time to calculate a feature
		if (newSamplesSinceLastFeatureAlarm[sensorCode] == shift) {
			// if so calculate all active features for that sensor
			evalFeatsIndex = 0;
			evalFeatsCount = 0;
			evalFeatsList[evalFeatsIndex++] = sensorCode;
			evalFeatsList[evalFeatsIndex++] = 0;
			for (i=0; i<actAlarmIndex; i++){
				if (actAlarmList[i].sensorCode == sensorCode)
					calculateFeature(actAlarmList[i].dataType, actAlarmList[i].sensorCode, actAlarmList[i].channelMask, window, bufferPoolCopy);					
					
				evalFeatsList[1] = evalFeatsCount;
				
				num_ch = countOfChannelsInMask(evalFeatsList[3] >> 4);
				res_size = evalFeatsList[3] & 0x0F; 
					
				for (k=0;k<num_ch;k++){		
					if (res_size == 1)
						elem = (uint32_t)((uint32_t)evalFeatsList[4+2*k]);																	
					else if (res_size == 2)
						elem = (uint32_t)((((uint32_t)evalFeatsList[4+2*k]) << 8) | ((uint32_t)evalFeatsList[5+2*k]));
					else if (res_size == 4)
						elem = (uint32_t)(((uint32_t)evalFeatsList[4+4*k]) << 24 | ((uint32_t)evalFeatsList[5+4*k]) << 16 | ((uint32_t)evalFeatsList[6+4*k]) << 8 | (uint32_t)evalFeatsList[7+4*k] );
								
		 			switch (actAlarmList[i].alarmType){
			 			case BELOW_Threshold:
			 				alarm = (elem < actAlarmList[i].lowerThreshold);
			 				break;
			 			case ABOVE_Threshold:
			 				alarm = (elem > actAlarmList[i].upperThreshold);
			 				break;
			 			case IN_BETWEEN_Thresholds:	
			 				alarm = (elem > actAlarmList[i].lowerThreshold && elem < actAlarmList[i].upperThreshold);
			 				break;
			 			case OUT_OF_Thresholds:	
			 				alarm = (elem < actAlarmList[i].lowerThreshold || elem > actAlarmList[i].upperThreshold);
			 				break;
			 			}
			 			if (alarm){
							notifyAlarmList[notifyAlarmIndex++]=actAlarmList[i].dataType;
			 				notifyAlarmList[notifyAlarmIndex++]=actAlarmList[i].sensorCode;
				 			notifyAlarmList[notifyAlarmIndex++]=actAlarmList[i].channelMask;
				 			notifyAlarmList[notifyAlarmIndex++]=actAlarmList[i].alarmType;
				 			
				 			notifyAlarmList[notifyAlarmIndex++]= (uint8_t)(elem >> 24);
				 			notifyAlarmList[notifyAlarmIndex++]= (uint8_t)(elem >> 16);
				 			notifyAlarmList[notifyAlarmIndex++]= (uint8_t)(elem >> 8);
				 			notifyAlarmList[notifyAlarmIndex++]= (uint8_t)(elem);

				 			call FunctionManager.send(ALARM, notifyAlarmList, notifyAlarmIndex);
				 			
				 			alarm = FALSE;
				 			notifyAlarmIndex = 0;
				 			elem = 0;
						}
					
			 		}		
	 		}	
		 	newSamplesSinceLastFeatureAlarm[sensorCode] = 0;									
		}			
	}


     
     event void BufferPool.newElem(uint8_t bufferID, uint16_t elem) {
     }
 

     command void Function.reset(){
	       //need to implement the reset
     }

         // Default commands needed due to the use of parametrized interfaces
	
    default command uint8_t Features.getResultSize[uint8_t featureID]() {
		dbg(DBG_USR1, "FeatureEngineP.getResultSize: Executed default operation. Chances are there's an operation miswiring.\n");
		return 0xFF;
	}
	
	default command error_t Features.calculate[uint8_t featureID](int16_t** data, uint8_t channelMask, uint16_t dataLen, int8_t* result) {
		dbg(DBG_USR1, "FeatureEngineP.calculate: Executed default operation. Chances are there's an operation miswiring.\n");
		return FAIL;
	}
}

