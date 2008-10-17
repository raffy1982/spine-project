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

#include "SpinePackets.h"
 
configuration PacketizerTestAppC {
}

implementation {
  components MainC;
  components PacketizerTestC as App;
  App.Boot -> MainC.Boot;

  components ActiveMessageC;
  App.AMControl -> ActiveMessageC;
  
  components new PacketizerC(AM_SPINE);
  App.BufferedSend -> PacketizerC.BufferedSend[SVC_MSG];

  components new TimerMilliC() as SendTimer;
  App.SendTimer -> SendTimer;

  components LedsC;
  App.Leds -> LedsC;
}

