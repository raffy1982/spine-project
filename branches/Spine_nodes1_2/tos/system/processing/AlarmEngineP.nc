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
            //interface AlarmEngine;
       }
       
       uses {
            interface Boot;
            interface FunctionManager;
            interface SensorsRegistry;
            interface BufferPool;   interface Leds;
       }
 }

 implementation {
     
 	 uint8_t alarmList[1];
	 uint8_t alarmCount = 0;
	 
     bool start = FALSE;
     bool registered = FALSE;
     
     active_alarm_t actAlarmList[ACT_ALARM_LIST_SIZE];   // <sensorCode, sensorChannel, lower Threshold, upper Threshold, alarm type>
     uint8_t actAlarmIndex = 0;
     

     event void Boot.booted() {
          if (!registered) {
             call FunctionManager.registerFunction(ALARM);
             alarmList[alarmCount++] = ALARM << 5;
             registered = TRUE;
          }
     }


     command bool Function.setUpFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
		//no setup params are needed by the alarm engine
        return TRUE;
     }

     command bool Function.activateFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
        uint8_t j;
	    uint8_t dataType;			//raw data or feature code
        uint8_t sensCode;			//sensor code to monitor
        uint8_t valueType;			//to indicate on which axis
        uint16_t lowerThreshold;	//this must always contain a valid value
        uint16_t upperThreshold; 	//this is not considered if the alarmType is not "between thresholds"
        uint8_t alarmType;			//01= below threshold 10= above threshold 11= between thresholds

        
       if (functionParamsSize != 8)
            return FALSE;

		dataType = functionParams[0];
		sensCode = functionParams[1];
		valueType = functionParams[2];
		lowerThreshold = (uint16_t)(((uint16_t)functionParams[3]) << 8 | (uint16_t)functionParams[4]);
		upperThreshold = (uint16_t)(((uint16_t)functionParams[5]) << 8 | (uint16_t)functionParams[6]);
		alarmType = functionParams[7];       
		 

        for(j = 0; j<actAlarmIndex; j++)
        //refresh settings if the activate function is for a sensor-axis already active
         if (actAlarmList[j].sensorCode == sensCode && actAlarmList[j].valueType == valueType && actAlarmList[j].alarmType==alarmType && actAlarmList[j].dataType==dataType) 
	        {	
	         actAlarmList[j].lowerThreshold=lowerThreshold;
	         actAlarmList[j].upperThreshold=upperThreshold;
	         break;
	         }   
            
      

        if (j == actAlarmIndex) {
	       actAlarmList[actAlarmIndex].dataType=dataType;
	       actAlarmList[actAlarmIndex].sensorCode=sensCode;
	       actAlarmList[actAlarmIndex].valueType=valueType;
	       actAlarmList[actAlarmIndex].lowerThreshold=lowerThreshold;
	       actAlarmList[actAlarmIndex].upperThreshold=upperThreshold;
	       actAlarmList[actAlarmIndex].alarmType=alarmType;         
	       actAlarmList[actAlarmIndex++].bufferID=call SensorsRegistry.getBufferID(sensCode,valueType);  
        	}
 	
     return TRUE;   
     }

     command bool Function.disableFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
        uint8_t j,k;
	    uint8_t sensCode;			//sensor code to monitor
        uint8_t valueType;
        uint8_t alarmType;			//1= below threshold 2= above threshold 3= between thresholds

        if (functionParamsSize < 3)
          return FALSE;

        memcpy(&sensCode, functionParams, 1);
        memcpy(&valueType, (functionParams+1),1);
        memcpy(&alarmType, (functionParams+2),1);


       for(j = 0; j<actAlarmIndex; j++) {
        if (actAlarmList[j].sensorCode == sensCode && actAlarmList[j].valueType == valueType && actAlarmList[j].alarmType==alarmType) {
                for ( k = j; k<actAlarmIndex; k++) {
                   actAlarmList[k].sensorCode = actAlarmList[k+1].sensorCode;
                   actAlarmList[k].valueType = actAlarmList[k+1].valueType;
                   actAlarmList[k].lowerThreshold = actAlarmList[k+1].lowerThreshold;
                   actAlarmList[k].upperThreshold = actAlarmList[k+1].upperThreshold;
                   actAlarmList[k].alarmType = actAlarmList[k+1].alarmType;
                   actAlarmList[k].bufferID = actAlarmList[k+1].bufferID;
                }
                actAlarmList[actAlarmIndex].sensorCode = actAlarmList[k+1].sensorCode;
                actAlarmList[actAlarmIndex].valueType = actAlarmList[k+1].valueType;
                actAlarmList[actAlarmIndex].lowerThreshold = actAlarmList[k+1].lowerThreshold;
                actAlarmList[actAlarmIndex].upperThreshold = actAlarmList[k+1].upperThreshold;
                actAlarmList[actAlarmIndex].alarmType = actAlarmList[k+1].alarmType;
                actAlarmList[actAlarmIndex].bufferID = actAlarmList[k+1].bufferID;
				actAlarmIndex--;
             break;
          }
       }  
        return TRUE;
     }

     command uint8_t* Function.getFunctionList(uint8_t* functionCount) {
         *functionCount = alarmCount;
         return alarmList;
     }
     
     command void Function.startComputing() {
		//start flag to TRUE so that when a new data is sampled, the alarm engine will check it
		start = TRUE;
     }

     command void Function.stopComputing() {
	//TO DO
     }
     event void FunctionManager.sensorWasSampledAndBuffered(enum SensorCode sensorCode){
	     //RRR meglio qui!!!!!
     	}
     
     event void BufferPool.newElem(uint8_t bufferID, uint16_t elem) {
		 uint8_t i;
		 uint8_t notifyAlarmList[6];
		 uint8_t notifyAlarmIndex=0;
		 bool alarm = FALSE;
		
		 if (start){	
			 for(i = 0; i<actAlarmIndex; i++) {
	 			if (actAlarmList[i].bufferID == bufferID && actAlarmList[i].dataType == RAW_DATA){
		 			switch (actAlarmList[i].alarmType){
			 			case 0x01:
			 				alarm = (elem < actAlarmList[i].lowerThreshold);
			 				break;
			 			case 0x02:
			 				alarm = (elem > actAlarmList[i].lowerThreshold);
			 				break;
			 			case 0x03:	
			 				alarm = (elem > actAlarmList[i].lowerThreshold && elem < actAlarmList[i].upperThreshold);
			 				break;
		 			}
		 			if (alarm){
						notifyAlarmList[notifyAlarmIndex++]=actAlarmList[i].dataType;
		 				notifyAlarmList[notifyAlarmIndex++]=actAlarmList[i].sensorCode;
			 			notifyAlarmList[notifyAlarmIndex++]=actAlarmList[i].valueType;
			 			notifyAlarmList[notifyAlarmIndex++]=actAlarmList[i].alarmType;
			 			
			 			notifyAlarmList[notifyAlarmIndex++]= elem >> 8;
			 			notifyAlarmList[notifyAlarmIndex++]=(uint8_t)elem;
			 			//memcpy(&notifyAlarmList[notifyAlarmIndex], elem, 2);
			 			//notifyAlarmIndex+=2;
			 			call FunctionManager.send(ALARM, notifyAlarmList, notifyAlarmIndex);
					}
				}
		 	}        
		 }
 	}
 

       command void Function.reset(){
	       //need to implement the reset
       }

 }

