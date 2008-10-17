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

module PacketizerTestC {
  uses {
    interface Boot;
    interface SplitControl as AMControl;
    interface BufferedSend;
    interface AMPacket;
    interface Timer<TMilli> as SendTimer;
    interface Leds;
  }
}

implementation {
  uint8_t msg1[100];
  uint16_t destination1 = 0xA1B2;

  event void Boot.booted() {
    int i;
    for(i=0; i<100; i++)
      msg1[i] = i;
    call AMControl.start();
  }

  event void SendTimer.fired() {
    call Leds.led0Toggle();
    
    call BufferedSend.send(destination1, &msg1, sizeof msg1);
  }

  event void AMControl.startDone(error_t error) {
     call SendTimer.startPeriodic(3443);
  }
  event void AMControl.stopDone(error_t error) {
  }
}

