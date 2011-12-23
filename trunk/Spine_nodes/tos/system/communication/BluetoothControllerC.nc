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
 * Module component of the Bluetooth Controller. This components takes care of sending and receiving messages.
 * Clients of the Bluetooth Controller don't have to worry about radio turn-on/off.
 * This component uses a queue to avoid messages drops or corruptions if the bluetooth is currently sending/receiving data.
 * It is possible to tune the max size of the queue defining the pflag 'QUEUE_MAX_SIZE' in the application makefile.
 *
 * @author Raffaele Gravina, Michele Capobianco, Livio Bioglio
 *
 * @version 1.3
 */

#include "btMessage.h"

 #ifndef RADIO_QUEUE_MAX_SIZE
 #define RADIO_QUEUE_MAX_SIZE 20
 #endif

 configuration BluetoothControllerC {
      provides interface RadioController;
 }

 implementation {
   components  MainC, LedsC, RovingNetworksC, BluetoothControllerP,//ActiveMessageC,
   new QueueC(bt_message,RADIO_QUEUE_MAX_SIZE) as Queue;
   
      RadioController = BluetoothControllerP;

      BluetoothControllerP.Boot -> MainC;
      BluetoothControllerP.Leds -> LedsC;
      BluetoothControllerP.BTStdControl -> RovingNetworksC.StdControl;
      BluetoothControllerP.Bluetooth -> RovingNetworksC;

      BluetoothControllerP.Queue -> Queue;
      
      BluetoothControllerP.BluetoothInit -> RovingNetworksC.Init;

   //BluetoothControllerP.Packet -> ActiveMessageC;
 }
