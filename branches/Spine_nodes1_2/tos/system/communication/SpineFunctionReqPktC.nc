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
 *  Module component for the Function Request SPINE packet.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */ 
 module SpineFunctionReqPktC {
       
       provides {
         interface InPacket;
         interface SpineFunctionReqPkt;
       }
 }

 implementation {

    uint8_t fnCode = 0;
    bool isEnableReq;
    uint8_t* fnParams;
    uint8_t fnParamsSize = 0;
    
    uint8_t fnReqBuf[SPINE_FUNCTION_REQ_PKT_MAX_SIZE];


    command bool InPacket.parse(void* payload, uint8_t len) {
       memcpy(fnReqBuf, payload, len);
       
       fnCode = fnReqBuf[0];
       isEnableReq = ( fnReqBuf[1] & 0x01 ); // 0x01 = 00000001
       
       fnParamsSize = fnReqBuf[2];

       fnParams = (fnReqBuf+3);

       return TRUE;
    }
    
    command enum FunctionCodes SpineFunctionReqPkt.getFunctionCode() {
      return fnCode;
    }

    command bool SpineFunctionReqPkt.isEnableRequest() {
      return isEnableReq;
    }

    command uint8_t* SpineFunctionReqPkt.getFunctionParams(uint8_t* functionParamsSize) {
      *functionParamsSize = fnParamsSize;
      return  fnParams;
    }

}
