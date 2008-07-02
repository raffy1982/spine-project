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
 * Module Component of the Function Manager. Each specific function implementation must register itself to the FunctionManager at boot time.
 * This component allows the retrieval of the Function list.
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */

#ifndef FUNCTION_LIST_SIZE
#define FUNCTION_LIST_SIZE 4             // max nr of functions supported by SPINE is 8
#endif

#ifndef FUNCTION_LIBRARIES_LIST_SIZE
#define FUNCTION_LIBRARIES_LIST_SIZE 128 // max nr of library per function is 32, so FUNCTION_LIBRARIES_LIST_SIZE = 32 * FUNCTION_LIST_SIZE
#endif

module FunctionManagerP {
       provides interface FunctionManager;
       
       uses {
         interface PacketManager;
         interface Function as Functions[uint8_t functionID];
       }
}

implementation {

       uint8_t functionList[FUNCTION_LIST_SIZE];
       uint8_t functCount = 0;
       
       uint8_t functionLibrariesList[FUNCTION_LIBRARIES_LIST_SIZE];
       uint8_t functLibCount = 0;
       
       uint8_t data[2+64];

       command error_t FunctionManager.registerFunction(enum FunctionCodes functionCode) {
          if (functCount < FUNCTION_LIST_SIZE) { // to avoid memory leaks
             functionList[functCount++] = functionCode;  
             return SUCCESS;
          }
          return FAIL;
       }

       command uint8_t* FunctionManager.getFunctionList(uint8_t* functionsCount) {
          uint8_t i;
          uint8_t* currFunctLibList;
          uint8_t currFunctLibCount;

          for (i = 0; i<functCount; i++) {
             currFunctLibList = call Functions.getFunctionList[ functionList[i] ](&currFunctLibCount);
             memcpy(functionLibrariesList+functLibCount, currFunctLibList, currFunctLibCount);
             functLibCount += currFunctLibCount;
          }

          *functionsCount = functLibCount;
          return functionLibrariesList;
       }

       command bool FunctionManager.setUpFunction(enum FunctionCodes functionCode, uint8_t* functionParams, uint8_t functionParamsSize) {
          return call Functions.setUpFunction[functionCode](functionParams, functionParamsSize);
       }


       command bool FunctionManager.activateFunction(enum FunctionCodes functionCode, uint8_t* functionParams, uint8_t functionParamsSize) {
          return call Functions.activateFunction[functionCode](functionParams, functionParamsSize);
       }

       command bool FunctionManager.disableFunction(enum FunctionCodes functionCode, uint8_t* functionParams, uint8_t functionParamsSize) {
          return call Functions.disableFunction[functionCode](functionParams, functionParamsSize);
       }
       
       /**
       *
       *
       * @return 'void'
       */
       command void FunctionManager.startComputing() {
          uint8_t i;
          for (i = 0; i<functCount; i++)
             call Functions.startComputing[ functionList[i] ]();
       }

       command void FunctionManager.stopComputing() {
          uint8_t i;
          for (i = 0; i<functCount; i++)
             call Functions.stopComputing[ functionList[i] ]();
       }

       command void FunctionManager.send(enum FunctionCodes functionCode, uint8_t* functionData, uint8_t len) {
          data[0] = functionCode;
          data[1] = len;
          memcpy((data+2), functionData, len);
          call PacketManager.build(DATA, data, (len+2));
       }

       
       event void PacketManager.messageReceived(enum PacketTypes pktType){}


       default command bool Functions.setUpFunction[uint8_t functionID](uint8_t* functionParams, uint8_t functionParamsSize) {
          dbg(DBG_USR1, "FunctionManagerP.setUpFunction: Executed default operation. Chances are there's an operation miswiring.\n");
          return FALSE;
       }

       default command bool Functions.activateFunction[uint8_t functionID](uint8_t* functionParams, uint8_t functionParamsSize) {
          dbg(DBG_USR1, "FunctionManagerP.activateFunction: Executed default operation. Chances are there's an operation miswiring.\n");
          return FALSE;
       }

       default command bool Functions.disableFunction[uint8_t functionID](uint8_t* functionParams, uint8_t functionParamsSize) {
          dbg(DBG_USR1, "FunctionManagerP.disableFunction: Executed default operation. Chances are there's an operation miswiring.\n");
          return FALSE;
       }

       default command uint8_t* Functions.getFunctionList[uint8_t functionID](uint8_t* functionCount) {
           dbg(DBG_USR1, "FunctionManagerP.getFunctionList: Executed default operation. Chances are there's an operation miswiring.\n");
           return NULL;
       }
       
       default command void Functions.startComputing[uint8_t functionID]() {
           dbg(DBG_USR1, "FunctionManagerP.startComputing: Executed default operation. Chances are there's an operation miswiring.\n");
       }
       
       default command void Functions.stopComputing[uint8_t functionID]() {
           dbg(DBG_USR1, "FunctionManagerP.stopComputing: Executed default operation. Chances are there's an operation miswiring.\n");
       }

}




