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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.
*****************************************************************/

/**
 *
 * @author Kevin Klues <klueska@wsnlabberkeley.com>
 *
 */

 module AMQueuedSendTestC {
  uses {
    interface Boot;
    interface SplitControl as AMControl;
    interface BufferedSendWithHeader as BufferedSend;
    interface AMPacket;
    interface Timer<TMilli> as SendTimer;
    interface Leds;
  }
}

implementation {
  uint8_t msg1[1];
  uint8_t msg2[3];
  uint8_t msg3[5];
  uint16_t destination1 = 0xA1B2;
  uint16_t destination2 = 0xDCFD;
  uint16_t destination3 = AM_BROADCAST_ADDR;

  event void Boot.booted() {
     msg1[0] = 0x11;
     
     msg2[0] = 0xaa;
     msg2[1] = 0xbb;
     msg2[2] = 0xcc;

     msg3[0] = 0x22;
     msg3[1] = 0x33;
     msg3[2] = 0x44;
     msg3[3] = 0x55;
     msg3[4] = 0x66;
     call AMControl.start();
  }

  event void SendTimer.fired() {
    call Leds.led0Toggle();
    
    call BufferedSend.send(destination1, &msg3, sizeof(msg3), &msg1, sizeof msg1);
    call BufferedSend.send(destination2, NULL, 0, &msg2, sizeof msg2);
    call BufferedSend.send(destination3, NULL, 0, &msg3, sizeof msg3);
  }

  event void AMControl.startDone(error_t error) {
     call SendTimer.startPeriodic(3443);
  }
  event void AMControl.stopDone(error_t error) {
  }
}

