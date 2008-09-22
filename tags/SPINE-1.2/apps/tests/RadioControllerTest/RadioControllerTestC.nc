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
 * Test component of the Radio Controller.
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */
 module RadioControllerTestC {
  uses interface Boot;
  uses interface Leds;

  uses interface RadioController;
  
  uses interface Timer<TMilli> as DeferredSend;
}

implementation {

  uint16_t netSize = 5;
  uint16_t myTimeSlotID = 4;
  
  uint8_t msg1[1];
  uint8_t msg2[3];
  uint8_t msg4[5];
  uint16_t destination1 = 0xA1B2;
  uint16_t destination2 = 0xDCFD;
  uint16_t destination4 = AM_BROADCAST_ADDR;

  event void Boot.booted() {
     //call RadioController.setRadioAlwaysOn(TRUE);
     call RadioController.setRadioAlwaysOn(FALSE);

     msg1[0] = 0x11;
     
     msg2[0] = 0xaa;
     msg2[1] = 0xbb;
     msg2[2] = 0xcc;

     msg4[0] = 0x22;
     msg4[1] = 0x33;
     msg4[2] = 0x44;
     msg4[3] = 0x55;
     msg4[4] = 0x66;
  }

  event void DeferredSend.fired() {
     call Leds.led1Toggle();

     call RadioController.send(destination1, SERVICE_ADV, &msg1, sizeof msg1);
     call RadioController.send(destination2, DATA, &msg2, sizeof msg2);
     call RadioController.send(destination4, SVC_MSG, &msg4, sizeof msg4);
  }

  event void RadioController.radioOn() {
     call RadioController.enableTDMA(netSize, myTimeSlotID); 
     //call RadioController.disableTDMA();

     call DeferredSend.startPeriodic(3443);
  }

  event void RadioController.receive(uint16_t source, enum PacketTypes pktType, void* payload, uint8_t len) {
  }

}

