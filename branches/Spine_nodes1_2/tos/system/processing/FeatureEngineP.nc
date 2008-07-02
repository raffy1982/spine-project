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
 * Module component of the SPINE Feature Engine.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */
 
 #include "Functions.h"
 #include "SensorsConstants.h"

 #ifndef FEATURE_LIST_SIZE
 #define FEATURE_LIST_SIZE 32
 #endif
 
 #ifndef ACT_FEATS_LIST_SIZE
 #define ACT_FEATS_LIST_SIZE 512
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

 module FeatureEngineP {

       provides {
            interface Function;
            interface FeatureEngine;
       }
       
       uses {
            interface Boot;
            interface Feature as Features[uint8_t featureID];
            interface Timer<TMilli> as ComputingTimers[uint8_t id];
            interface FunctionManager;
            interface SensorsRegistry;
            interface BufferPool;   interface Leds;
       }
 }

 implementation {
     
     uint8_t featureList[FEATURE_LIST_SIZE];
     uint8_t featCount = 0;
     bool registered = FALSE;
     
     active_feature_t actFeatsList[ACT_FEATS_LIST_SIZE];   // <featureCode, sensorCode, sensorChBitmask>
     uint8_t actFeatsIndex = 0;

     feat_params_t featParamsList[SENSORS_REGISTRY_SIZE];  // <sensorCode, windowSize, processingTime>
     uint8_t featParamsIndex = 0;
     
     running_timers_t runningTimersList[SENSORS_REGISTRY_SIZE];
     uint8_t runningTimersIndex = 0;
     
     uint8_t evalFeatsList[64];
     uint8_t evalFeatsIndex;
     uint8_t evalFeatsCount;


     event void Boot.booted() {
          if (!registered) {
             call FunctionManager.registerFunction(FEATURE);
             registered = TRUE;
          }
     }


     command bool Function.setUpFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
        uint8_t i;
        uint8_t sensCode;
        uint8_t windowS;
        uint8_t shiftS; 
        uint32_t shiftBig; 
        uint32_t currSTime;

        if (functionParamsSize != 3)
           return FALSE;

        memcpy(&sensCode, functionParams, 1);
        memcpy(&windowS, (functionParams+1), 1);
        memcpy(&shiftS, (functionParams+2), 1);
        shiftBig = shiftS;

        if (shiftS == 0)
           return FALSE;

        sensCode = sensCode>>4;

        for (i = 0; i<featParamsIndex; i++)
           if (featParamsList[i].sensorCode == sensCode) {
              featParamsList[i].windowSize = windowS;
              currSTime = call SensorsRegistry.getSamplingTime(sensCode);
              featParamsList[i].processingTime = ( currSTime * shiftBig );
              break;
           }

        if (i == featParamsIndex) {
           featParamsList[featParamsIndex].sensorCode = sensCode;
           featParamsList[featParamsIndex].windowSize = windowS;
           currSTime = call SensorsRegistry.getSamplingTime(sensCode);
           featParamsList[featParamsIndex++].processingTime = ( currSTime * shiftBig );
        }

        return TRUE;
     }

     command bool Function.activateFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
        uint8_t i, j;
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

           for(j = 0; j<actFeatsIndex; j++)
             if (actFeatsList[j].featureCode == currFeatureCode && actFeatsList[j].sensorCode == sensorCode) {
                actFeatsList[j].sensorChBitmask |= currSensorChBitmask;
                break;
             }

           if (j == actFeatsIndex) {
              actFeatsList[actFeatsIndex].featureCode = currFeatureCode;
              actFeatsList[actFeatsIndex].sensorCode = sensorCode;
              actFeatsList[actFeatsIndex++].sensorChBitmask = currSensorChBitmask;
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
        /*uint8_t i, j, k;
        uint8_t currCode;
        uint32_t currTime;
        bool start = FALSE;

        for (i = 0; i<featParamsIndex; i++) {
           currCode = featParamsList[i].sensorCode;
           start = FALSE;
           for (k = 0; k<actFeatsIndex; k++) {
              if (actFeatsList[k].sensorCode == currCode) {
                 start = TRUE;
                 break;
              }
           }

           if (start) {
              currTime = featParamsList[i].processingTime;
              for (j = 0; j<runningTimersIndex; j++)
                 if (runningTimersList[j].time == currTime)
                    break;

              if (j == runningTimersIndex) {
                 runningTimersList[runningTimersIndex].sensorCode = currCode;
                 runningTimersList[runningTimersIndex++].time = currTime;
              }
           }
        }

        for (i = 0; i<runningTimersIndex; i++)
           call ComputingTimers.startPeriodic[ runningTimersList[i].sensorCode ](runningTimersList[i].time); */
           
        uint8_t i, j;
        uint8_t currCode;
        for (i = 0; i<featParamsIndex; i++) {
           currCode = featParamsList[i].sensorCode;
           for (j = 0; j<actFeatsIndex; j++) {
              if (actFeatsList[j].sensorCode == currCode) {
                 call ComputingTimers.startPeriodic[ currCode ](featParamsList[i].processingTime);
                 break;
              }
           }
        }
     }

     command void Function.stopComputing() {
        uint8_t i;
        for (i = 0; i<featParamsIndex; i++)
           call ComputingTimers.stop[ featParamsList[i].sensorCode ]();
     }
     
     command error_t FeatureEngine.registerFeature(enum FeatureCodes featureID) {
         if (featCount < FEATURE_LIST_SIZE) { // to avoid memory leaks
            featureList[featCount++] = ((FEATURE<<5) | (featureID & 0x1F));  // The & 0x1F (00011111) is to avoid corruption in the first 'FunctionCode' 3 bits
            return SUCCESS;
         }
         return FAIL;
     }

     void calculateFeature(uint8_t featureCode, uint8_t sensorCode, uint8_t sensorChBitmask, uint8_t windowSize, uint16_t* bufferPoolCopy) {
         uint8_t i, j;
         uint8_t mask = 0x08;
         uint16_t* buffer;
         int32_t currResult;
         uint8_t currResultByteSize;
         uint32_t tmp;

         evalFeatsList[evalFeatsIndex++] = featureCode;
         evalFeatsList[evalFeatsIndex] = (sensorChBitmask & 0x0F);
         evalFeatsList[evalFeatsIndex] = (evalFeatsList[evalFeatsIndex]<<4);
         currResultByteSize = call Features.getResultSize[featureCode]();
         evalFeatsList[evalFeatsIndex++] |= (currResultByteSize & 0x0F);

         for (i = 0; i<MAX_VALUE_TYPES; i++) {
            if ( (sensorChBitmask & (mask>>i)) == (mask>>i)) {
               buffer = ( bufferPoolCopy + call BufferPool.getBufferSize(0) * call SensorsRegistry.getBufferID(sensorCode, i) );
               currResult = call Features.calculate[featureCode](buffer, windowSize);
               
               for (j = 0; j<currResultByteSize; j++) {
                  tmp = ( currResult<<8*( (sizeof currResult) - currResultByteSize + j) );
                  evalFeatsList[evalFeatsIndex++] = (tmp>>8*( (sizeof currResult) - 1));
               }

               evalFeatsCount++;
            }
         }
     }
     uint8_t idOld=4;
     event void ComputingTimers.fired[uint8_t id]() {
         /*uint8_t i, j;
         uint32_t thisTime = call ComputingTimers.getdt[id]();
         uint8_t currSensorCode;

         uint16_t bufferPoolCopy[call BufferPool.getBufferSize(0) * call BufferPool.getBufferPoolSize()];
         call BufferPool.getBufferPoolCopy(bufferPoolCopy);

         for (i = 0; i<featParamsIndex; i++) {
            if (featParamsList[i].processingTime == thisTime) {
               currSensorCode = featParamsList[i].sensorCode;

               evalFeatsIndex = 0;
               evalFeatsCount = 0;
               evalFeatsList[evalFeatsIndex++] = currSensorCode;
               evalFeatsList[evalFeatsIndex++] = 0;

               for (j = 0; j<actFeatsIndex; j++)
                  if (actFeatsList[j].sensorCode == currSensorCode)
                     calculateFeature(actFeatsList[j].featureCode, currSensorCode, actFeatsList[j].sensorChBitmask, featParamsList[i].windowSize, bufferPoolCopy);

               evalFeatsList[1] = evalFeatsCount;
               
               call FunctionManager.send(FEATURE, evalFeatsList, evalFeatsIndex);
            }
         }*/
         
         uint8_t i, j;
         uint8_t currWindowSize = 1;
         uint16_t bufferPoolCopy[call BufferPool.getBufferSize(0) * call BufferPool.getBufferPoolSize()];
         call BufferPool.getBufferPoolCopy(bufferPoolCopy);

         evalFeatsIndex = 0;
         evalFeatsCount = 0;
         evalFeatsList[evalFeatsIndex++] = id;
         evalFeatsList[evalFeatsIndex++] = 0;
         
         for (j = 0; j<featParamsIndex; j++)
            if (featParamsList[j].sensorCode == id) {
               currWindowSize = featParamsList[j].windowSize;
               break;
            }

         for (i = 0; i<actFeatsIndex; i++)
            if (actFeatsList[i].sensorCode == id)
               calculateFeature(actFeatsList[i].featureCode, id, actFeatsList[i].sensorChBitmask, currWindowSize, bufferPoolCopy);

         evalFeatsList[1] = evalFeatsCount;

         call FunctionManager.send(FEATURE, evalFeatsList, evalFeatsIndex);
     }

     event void BufferPool.newElem(uint8_t bufferID, uint16_t elem) {/*if (bufferID==0) call Leds.led0Toggle(); else call Leds.led1Toggle();*/}


     default command uint8_t Features.getResultSize[uint8_t featureID]() {
        dbg(DBG_USR1, "FeatureEngineP.getResultSize: Executed default operation. Chances are there's an operation miswiring.\n");
        return 0xFF;
     }
     
     default command int32_t Features.calculate[uint8_t featureID](int16_t* data, uint16_t dataLen) {
        dbg(DBG_USR1, "FeatureEngineP.calculate: Executed default operation. Chances are there's an operation miswiring.\n");
        return 0xFFFFFFFF;
     }
     
     default command void ComputingTimers.startPeriodic[uint8_t id](uint32_t dt) {
        dbg(DBG_USR1, "FeatureEngineP.startPeriodic: Executed default operation. Chances are there's an operation miswiring.\n");
     }

      default command void ComputingTimers.stop[uint8_t id]() {
        dbg(DBG_USR1, "FeatureEngineP.stop: Executed default operation. Chances are there's an operation miswiring.\n");
     }

     default command uint32_t ComputingTimers.getdt[uint8_t id]() {
        dbg(DBG_USR1, "FeatureEngineP.getdt: Executed default operation. Chances are there's an operation miswiring.\n");
        return 0;
     }
 }

