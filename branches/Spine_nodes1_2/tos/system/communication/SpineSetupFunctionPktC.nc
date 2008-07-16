/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that
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
 *  Module component for the Setup-Function SPINE packet.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */ 
 module SpineSetupFunctionPktC {
       provides {
         interface InPacket;
         interface SpineSetupFunctionPkt;
       }
 }

 implementation {

    uint8_t fnCode = 0;
    uint8_t* fnParams;
    uint8_t fnParamsSize = 0;
    
    uint8_t setFnBuf[SPINE_SETUP_FUNCTION_PKT_MAX_SIZE];


    command bool InPacket.parse(void* payload, uint8_t len) {
       memcpy(setFnBuf, payload, len);
       
       fnCode = (setFnBuf[0]>>3);

       fnParamsSize = setFnBuf[1];

       fnParams = (setFnBuf+2);

       return TRUE;
    }
    
    command enum FunctionCodes SpineSetupFunctionPkt.getFunctionCode() {
      return fnCode;
    }

    command uint8_t* SpineSetupFunctionPkt.getFunctionParams(uint8_t* functionParamsSize) {
      *functionParamsSize = fnParamsSize;
      return  fnParams;
    }
}
