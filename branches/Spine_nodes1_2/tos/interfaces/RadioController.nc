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
 * Interface of the Radio Controller. This components takes care of sending and receiving messages.
 * Clients of the Radio Controller don't have to worry about radio turn-on/off, payload limit constraints 
 * (messages too long will be automatically splitted in several smaller fragments; the complete message will be 
 * rebuilt by the SPINE server on the gateway).
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
 
 #include "SpinePackets.h"
 #include "message.h"

 interface RadioController {

       /**
       * Signals the (only the first) RadioON event.
       * This event can be used as a "start engine" input for the application logic.
       *
       * @return    void.
       */
       event void radioOn();
       
       /**
       * Controls the low-power radio mode of the Radio Controller. 
       * Low-power mode is active by default and can also be tuned at compile time 
       * using the defining the pflag 'RADIO_LOW_POWER' at TRUE or FALSE.
       *
       * @param  enable    TRUE to enable the low-power radio mode; FALSE to disable it.
       *
       * @return    void.
       */
       command void enableLowPower(bool enable);

       /**
       * Send a packet with a data payload of <tt>data</tt> and type <tt>pktType</tt> to address <tt>destination</tt>.
       * If send returns SUCCESS, then the message is going to be transmitted; 
       * if send returns an error, then the message won't be transmitted.
       *
       * @param destination   address to which to send the message
       * @param pktType       the AM type of the message
       * @param payload       the message
       * @param len           the length of the data (in terms of number of bytes)
       *
       * @return              SUCCESS if the request to send succeeded.
       */
       command error_t send(uint16_t destination, enum PacketTypes pktType, void* payload, uint8_t len);

       /**
       * Signals the reception of a message.
       * IMPORTANT: is up to the handling component to copy out the data it needs.
       *     
       * @param  source    the source node ID of this message
       * @param  pktType   the AM type of the message
       * @param  payload   a pointer to the message's payload
       * @param  len       the length of the data region pointed to by payload (in terms of number of bytes)
       *
       * @return           void.
       */
       event void receive(uint16_t source, enum PacketTypes pktType, void* payload, uint8_t len);

 }




