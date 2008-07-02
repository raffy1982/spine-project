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
 * Test component of the Packet Manager.
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */
 
 #include "Functions.h"

 module PacketManagerTestC {
  uses interface Boot;
  uses interface Leds;

  uses {
     interface PacketManager;

     interface SpineHeader;
     interface InPacket;
     interface SpineSetupSensorPkt;
     interface SpineStartPkt;
     interface SpineFunctionReqPkt;
  }
}

implementation {

  uint8_t msg1[9];
  uint8_t msg2[2];
  
  uint8_t msg3[3];
  
  uint8_t msg4[2];
  
  uint8_t msg5[6];

  event void Boot.booted() {
    
     uint8_t* tmpBuf;
     uint8_t tmpByte;

     msg1[0] = 0x81;  // vers 2, ext 0, type 1
     msg1[1] = 0xAB;  // group AB
     msg1[2] = 0x00;  // src 0000
     msg1[3] = 0x00;  //
     msg1[4] = 0x00;  // dst 00D2
     msg1[5] = 0xD2;  //
     msg1[6] = 0x03;  // seq nr 3
     msg1[7] = 0x01;  // frag nr 1
     msg1[8] = 0x01;  // tot frags 1

     msg2[0] = 0xab;
     msg2[1] = 0xcd;
     
     msg3[0] = 0x38;  // sensorCode 3, timeScale, 2 (sec)          (0011 10 00)
     msg3[1] = 0x03;  // 03E9 = 1001
     msg3[2] = 0xE9;
     
     msg4[0] = 0x00;  // motes in net 10
     msg4[1] = 0x0A;
     
     msg5[0] = 0x0C;  // fnCode 2, enable T
     msg5[1] = 0x04;  // params Length 4
     msg5[2] = 0x01;  // featCode 1
     msg5[3] = 0x01;  // sensorNr 2
     msg5[4] = 0x1E;  // acc ch1,ch2,ch3
     msg5[5] = 0x28;  // volt ch1

     //call PacketManager.build(SVC_MSG, &msg2, sizeof msg2);
     //call SpineHeader.parse(&msg1);
     //call InPacket.parse(&msg3, sizeof msg3);
     //call InPacket.parse(&msg4, sizeof msg4);
     call InPacket.parse(&msg5, sizeof msg5);

     //if (call SpineHeader.getVersion() == 0x02) call Leds.led0Toggle();
     //if (!call SpineHeader.isExtended() && call SpineHeader.getPktType() == SERVICE_DISCOVERY)  call Leds.led1Toggle();

     //if (call SpineHeader.getGroupID() == 0xAB && call SpineHeader.getSourceID() == SPINE_BASE_STATION
     //      && call SpineHeader.getDestID() == TOS_NODE_ID) call Leds.led0Toggle();

     //if (call SpineHeader.getSequenceNumber() == 0x03 && call SpineHeader.getFragmentNumber() == 0x01
     //    && call SpineHeader.getTotalFragments() == 0x01) call Leds.led1Toggle();

     //if (call SpineSetupSensorPkt.getSensorCode() == 0x03 && call SpineSetupSensorPkt.getTimeScale() == 0x03E8
     //    && call SpineSetupSensorPkt.getSamplingTime() == 0x03E9) call Leds.led0Toggle();
     //if (call SpineStartPkt.getNetworkSize() == 0x000A) call Leds.led0Toggle();
     
     if (call SpineFunctionReqPkt.getFunctionCode()==0x01 && call SpineFunctionReqPkt.isEnableRequest()) call Leds.led0Toggle();
     tmpBuf = call SpineFunctionReqPkt.getFunctionParams(&tmpByte);
     if (tmpByte == 0x04 && tmpBuf[0]==0x01 && tmpBuf[1]==0x01 && tmpBuf[2]==0x1E && tmpBuf[3]==0x28)  call Leds.led1Toggle();
  }

  event void PacketManager.messageReceived(enum PacketTypes pktType) {
      call Leds.led1Toggle();
  }

}

