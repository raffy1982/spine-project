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

 #ifndef BOOT_RADIO_ON
 #define BOOT_RADIO_ON TRUE
 #endif

 #ifndef RADIO_LOW_POWER
 #define RADIO_LOW_POWER TRUE
 #endif

 module RadioControllerP {

       uses {
            interface Boot;

            interface SplitControl as Radio;
            interface Packet;
            interface AMPacket;
            
            interface AMSend as Sender;
            interface Receive as Receiver;

            interface Queue<message_t>;
            
            interface Leds;
            interface Timer<TMilli> as GuardTimer;
            interface Timer<TMilli> as ListenTimer;
       }

       provides interface RadioController;

 }

 implementation {

       uint32_t GUARD_TIMER = 5; // no shorter than 5ms (otherwise the radio isn't able to TX sequential pkts)
       uint32_t LISTEN_TIMER = 50;

       message_t msgTmp;

       uint8_t msgSentCount = 0;

       bool radioOn = FALSE;
       bool radioTurningOn = FALSE;
       bool lowPowerEnabled = RADIO_LOW_POWER;
       bool firstStart = TRUE;
       bool canSendNow = TRUE;
       bool sendMsgTmp = FALSE;


       event void Boot.booted() {
           if (BOOT_RADIO_ON)
               call Radio.start();
       }


       error_t sendOneMessage(uint16_t destination, uint8_t type, message_t* msg, uint8_t msgLen) {
           if (msgLen <= TOSH_DATA_LENGTH) {
               memcpy(call Sender.getPayload(&msgTmp), call Sender.getPayload(msg), msgLen);
               return call Sender.send(destination, &msgTmp, msgLen);
           }
           else {
                 // TODO implement the fragmenter ... actually is already implemented within the Packet Manager
           }
           return FAIL;
       }

       error_t sendMessages() {
           message_t mtmp = call Queue.dequeue();
           uint8_t length = call Packet.payloadLength(&mtmp);
           call AMPacket.setDestination(&msgTmp, call AMPacket.destination(&mtmp));
           call AMPacket.setType(&msgTmp, call AMPacket.type(&mtmp));
           call Packet.setPayloadLength(&msgTmp, length);
           memcpy(call Sender.getPayload(&msgTmp), call Sender.getPayload(&mtmp), length);
           return sendOneMessage(call AMPacket.destination(&msgTmp), call AMPacket.type(&msgTmp), &msgTmp, length);
       }
       
       event void ListenTimer.fired() {
           call Radio.stop();
       }

       void radioTurnOffPolicy() {
           if (lowPowerEnabled && call Queue.empty())
               call ListenTimer.startOneShot(LISTEN_TIMER);
       }


       event void GuardTimer.fired() {
           sendMessages();
       }

       void checkQueueToSend() {
           if (call Queue.empty()) {
               radioTurnOffPolicy();
               canSendNow = TRUE;
           }
           else
               call GuardTimer.startOneShot(GUARD_TIMER);
       }


       event void Radio.startDone(error_t res) {
           radioOn = (res == SUCCESS);
           radioTurningOn = FALSE;
           
           call Leds.led2Toggle();

           if (firstStart) {
              firstStart = FALSE;
              signal RadioController.radioOn();
           }

           //if(!canSendNow)
           //   checkQueueToSend();
           /*else*/ if (sendMsgTmp) {
              sendMsgTmp = FALSE;
              sendOneMessage(call AMPacket.destination(&msgTmp), call AMPacket.type(&msgTmp), &msgTmp, call Packet.payloadLength(&msgTmp));
           }
       }

       event void Radio.stopDone(error_t res) {
           radioOn = !(res == SUCCESS);
           
           call Leds.led2Toggle();
       }

       void radioTurnOnPolicy() {
           if(!radioOn && !radioTurningOn) {
              radioTurningOn = TRUE;
              call Radio.start();
           }
       }

       error_t bufferData(uint16_t destination, uint8_t type, void* payload, uint8_t len) {
          message_t buffer;
          if (len <= TOSH_DATA_LENGTH) {
             call AMPacket.setDestination(&buffer, destination);
             call AMPacket.setType(&buffer, type);
             call Packet.setPayloadLength(&buffer, len);          // CHECK HERE
             memcpy(call Sender.getPayload(&buffer), payload, len);
             return call Queue.enqueue(buffer);
          }
          else {
              // TODO implement the fragmenter ... actually is already implemented within the Packet Manager!
          }
          return FAIL;
       }



       error_t prepareToSend(uint16_t destination, uint8_t type, void* data, uint8_t len) {
           if (len <= TOSH_DATA_LENGTH) {
              sendMsgTmp = TRUE;
              call AMPacket.setDestination(&msgTmp, destination);
              call AMPacket.setType(&msgTmp, type);
              call Packet.setPayloadLength(&msgTmp, len);
              memcpy(call Sender.getPayload(&msgTmp), data, len);
           }
           else
              return bufferData(destination, type, data, len);

           return SUCCESS;
       }
       
       command void RadioController.setRadioAlwaysOn(bool enable) {
           lowPowerEnabled = !enable;
           if (lowPowerEnabled)
              radioTurnOffPolicy();
       }

       command error_t RadioController.send(uint16_t destination, enum PacketTypes pktType, void* payload, uint8_t len) {
           error_t status = SUCCESS;
           if (!radioOn) {
              if (canSendNow) {
                 canSendNow = FALSE;
                 status = prepareToSend(destination, pktType, payload, len);
              }
              else
                 status = bufferData(destination, pktType, payload, len);
              radioTurnOnPolicy();
           }
           else {
              if (lowPowerEnabled)
                 call ListenTimer.stop(); // if it's not running, nothing happens

              if (canSendNow) { // send the message immediately
                 canSendNow = FALSE;
                 status = ecombine(prepareToSend(destination, pktType, payload, len), sendOneMessage(destination, pktType, &msgTmp, len));
              }
              else
                 status = bufferData(destination, pktType, payload, len);
           }

           return status;
       }
       
       event void Sender.sendDone(message_t* msg, error_t error) {
           msgSentCount++;

           checkQueueToSend();
       }

       event message_t* Receiver.receive(message_t* msg, void* payload, uint8_t len) {
           
           radioTurnOffPolicy();

                                          // TODO if the coordinator can't set the source... let's impose here SPINE_BASE_STATION as the source
           signal RadioController.receive(call AMPacket.source(msg), call AMPacket.type(msg), payload, len);

           return msg;
       }

 }

