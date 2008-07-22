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
 * Module component of the Radio Controller. This components takes care of sending and receiving messages.
 * Clients of the Radio Controller don't have to worry about radio turn-on/off.
 * Radio Controller takes care of timestamping every actual sent messages just before passing them to the lower radio stack.
 * This component uses a queue to avoid messages drops or corruptions if the radio is currently sending/receiving data.
 * It is possible to tune the max size of the queue defining the pflag 'QUEUE_MAX_SIZE' in the application makefile.
 *
 * IMPORTANT: the Radio Controller is able (if needed) to auto turn on the radio when asked to send messages, so users don't have to take care of it.
 *            A radioOn event is signaled for convenience the first time the radio turns on
 *            (as the RADIO_LOW_POWER option is TRUE by default, the radio will turn on-off frequently);
 *            handling this event is useful because, for instance, the user could start timers for acquiring data,
 *            do processing AFTER the radio is ready to send them.
 *            If the node need a OtA "start" message in order to start its working cycle,
 *            make sure to avoid declaring the BOOT_RADIO_ON pflag to FALSE.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */

 #ifndef QUEUE_MAX_SIZE
 #define QUEUE_MAX_SIZE 20
 #endif

 configuration RadioControllerC {
      provides interface RadioController;
 }

 implementation {
      components RadioControllerP, MainC, ActiveMessageC, LedsC,
                 new AMSenderC(AM_SPINE) as SenderC,

                 new AMReceiverC(AM_SPINE) as ReceiverC,
                 
                 new QueueC(message_t, QUEUE_MAX_SIZE) as Queue,
                 new TimerMilliC() as GuardTimer, new TimerMilliC() as ListenTimer, new TimerMilliC() as TDMATimer;

      
      RadioController = RadioControllerP;

      RadioControllerP.Sender -> SenderC;
      RadioControllerP.Receiver -> ReceiverC;

      RadioControllerP.Boot -> MainC;
      RadioControllerP.Radio -> ActiveMessageC;

      RadioControllerP.Packet -> ActiveMessageC;
      RadioControllerP.AMPacket -> ActiveMessageC;

      RadioControllerP.Queue -> Queue;

      RadioControllerP.Leds -> LedsC;

      RadioControllerP.GuardTimer -> GuardTimer;
      RadioControllerP.ListenTimer -> ListenTimer;
      RadioControllerP.TDMATimer -> TDMATimer;
 }
