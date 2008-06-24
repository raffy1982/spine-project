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
 
 #ifndef FEATURE_LIST_SIZE
 #define FEATURE_LIST_SIZE 32
 #endif
 
 module FeatureEngineP {

       provides {
            interface Function;
            interface FeatureEngine;
       }
       
       uses {
            interface Boot;
            interface Feature as Features[uint8_t featureID];
            interface FunctionManager;
       }
 }

 implementation {
     
     uint8_t featureList[FEATURE_LIST_SIZE];
     uint8_t featCount = 0;
     bool registered = FALSE;

     event void Boot.booted() {
          if (!registered) {
             call FunctionManager.registerFunction(FEATURE);
             registered = TRUE;
          }
     }

     command bool Function.setUpFunction(enum FunctionCodes functionCode, uint8_t* functionParams, uint8_t functionParamsSize) {
        return TRUE;
     }

     command bool Function.activateFunction(enum FunctionCodes functionCode, uint8_t* functionParams, uint8_t functionParamsSize) {
        return TRUE;
     }

     command bool Function.disableFunction(enum FunctionCodes functionCode) {
        return TRUE;
     }
     
     command uint8_t* Function.getFunctionList(uint8_t* functionCount) {
         *functionCount = featCount;
         return featureList;
     }
     
     command error_t FeatureEngine.registerFeature(enum FeatureCodes featureID) {
         if (featCount < FEATURE_LIST_SIZE) { // to avoid memory leaks
            featureList[featCount++] = ((FEATURE<<5) | (featureID & 0x1F));  // The & 0x1F (00011111) is to avoid corruption in the first 'FunctionCode' 3 bits
            return SUCCESS;
         }
         return FAIL;
     }


     default command int32_t Features.calculate[uint8_t featureID](int16_t* data, uint16_t dataLen) {
        dbg(DBG_USR1, "FeatureEngineP.calculate: Executed default operation. Chances are there's an operation miswiring.\n");
        return 0xFFFFFFFF;
     }

 }

