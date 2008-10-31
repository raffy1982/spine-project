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

#include "spine_constants.h"

module PacketizerTestC {
  uses {
    interface Boot;
    interface SplitControl as AMControl;
    interface BufferedSend;
    interface AMPacket;
    interface Timer<TMilli> as SendTimer;
    interface Leds;
    interface LowPowerListening;
  }
}

implementation {
  uint8_t msg1[470];
  uint16_t destination1 = AM_BROADCAST_ADDR;

  event void Boot.booted() {
    int i;
    for(i=0; i<470; i++)
      msg1[i] = i;
    call AMControl.start();
  }

  event void AMControl.startDone(error_t error) {
     call LowPowerListening.setLocalSleepInterval(SPINE_SLEEP_INTERVAL);
     call SendTimer.startPeriodic(5000);
  }

  event void SendTimer.fired() {
    if(call BufferedSend.send(destination1, &msg1, sizeof msg1) == SUCCESS)
      call Leds.led0Toggle();
    else call Leds.led1Toggle();
  }

  event void AMControl.stopDone(error_t error) {
  }
}

