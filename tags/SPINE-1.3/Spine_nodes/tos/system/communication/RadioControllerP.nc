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
 * @author Philip Kuryloski (Security Integration - implementation from http://hinrg.cs.jhu.edu/git/?p=jgko/tinyos-2.x.git;a=summary)
 *
 * @version 1.3
 */

 #ifndef BOOT_RADIO_ON
 #define BOOT_RADIO_ON TRUE
 #endif

 #ifndef ENABLE_TDMA
 #define ENABLE_TDMA FALSE
 #endif
 
 #ifndef TDMA_FRAME_PERIOD
 #define TDMA_FRAME_PERIOD 1024
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
            interface Timer<TMilli> as TDMATimer;
            
            #ifdef SECURE
               interface CC2420SecurityMode;
               interface CC2420Keys;
               interface Packet as SecPacket;
            #endif
       }

       provides interface RadioController;

 }

 implementation {

       uint32_t GUARD_TIMER = 5; // no shorter than 5ms (otherwise the radio isn't able to TX sequential pkts)
       uint32_t LISTEN_TIMER = 100;

       message_t msgTmp;

       bool radioOn = FALSE;
       bool radioTurningOn = FALSE;
       bool lowPowerEnabled = RADIO_LOW_POWER;
       bool firstStart = TRUE;
       bool canSendNow = TRUE;
       bool sendMsgTmp = FALSE;                            

       bool tdmaEnabled = ENABLE_TDMA;
       bool myTurn = !ENABLE_TDMA;
       uint8_t myTimeSlot = -1;
       uint8_t netSize = -1;
       uint8_t currentTimeSlot = -1;
       
       #ifdef SECURE
          #include "secure_key.h"
          #ifdef DEFAULT_KEY_USED
             #warning using default key from support/make/secure_key.h
          #endif
          uint8_t spine_secure_key[KEY_SIZE] = KEY;
       #endif
       
       
       event void Boot.booted() {
           if (BOOT_RADIO_ON)
               call Radio.start();
       }


       error_t sendOneMessage(uint16_t destination, uint8_t type, message_t* msg, uint8_t msgLen) {
           //uint8_t* t;
           if (msgLen <= TOSH_DATA_LENGTH) {
		      
	      #ifdef SECURE
                 // shift the message payload to allow for insertion of security header,
                 // as we used the Non-Secure packet interface to construct the payload.
                 uint8_t tMsg[TOSH_DATA_LENGTH];
                 memcpy(tMsg, call Packet.getPayload(msg, msgLen), msgLen);
                 memcpy(call SecPacket.getPayload(msg, msgLen), tMsg, msgLen);
                 call SecPacket.setPayloadLength(msg, msgLen);
                 call CC2420SecurityMode.setCcm(msg, 0, 0, MIC_LENGTH);   // encrypt the whole message, with a MIC_LENGTH bit auth code
              #endif
              
              /*t = call Packet.getPayload(msg, msgLen);
              printf("%02X ", t[msgLen-14]);
              printf("%02X, ", t[msgLen-13]);
              printf("%02X ", t[msgLen-12]);
              printf("%02X, ", t[msgLen-11]);
              printf("%02X ", t[msgLen-10]);
              printf("%02X, ", t[msgLen-9]);
              printf("%02X ", t[msgLen-8]);
              printf("%02X, ", t[msgLen-7]);
              printf("%02X ", t[msgLen-6]);
              printf("%02X, ", t[msgLen-5]);
              printf("%02X ", t[msgLen-4]);
              printf("%02X, ", t[msgLen-3]);
              printf("%02X ", t[msgLen-2]);
              printf("%02X ", t[msgLen-1]);
              printf("\n");
              printfflush();*/

              return call Sender.send(destination, msg, msgLen);
           }
           return FAIL;
       }

       error_t sendMessages() {
           message_t mtmp = call Queue.dequeue();
           uint8_t length = call Packet.payloadLength(&mtmp);
           call AMPacket.setDestination(&msgTmp, call AMPacket.destination(&mtmp));
           call AMPacket.setType(&msgTmp, call AMPacket.type(&mtmp));
           call Packet.setPayloadLength(&msgTmp, length);
		   
           #ifndef TINYOS_2_0_2
           memcpy(call Sender.getPayload(&msgTmp,length), call Sender.getPayload(&mtmp,length), length);	   
		   #else
           memcpy(call Sender.getPayload(&msgTmp), call Sender.getPayload(&mtmp), length);
           #endif
		   
           return sendOneMessage(call AMPacket.destination(&msgTmp), call AMPacket.type(&msgTmp), &msgTmp, length);
       }


       event void ListenTimer.fired() {
           call Radio.stop();
       }


       void radioTurnOnPolicy() {
           if(!radioOn && !radioTurningOn) {
              radioTurningOn = TRUE;
              call Radio.start();
           }
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

       event void TDMATimer.fired() {
           currentTimeSlot++;
           currentTimeSlot %= netSize;

           myTurn = (myTimeSlot == currentTimeSlot);
           if (myTurn)    {
              call Leds.led0On();
              if (!call Queue.empty()) {
                 if (!radioOn)
                    radioTurnOnPolicy();
                 else
                    checkQueueToSend();
              }      
           }
           else
              call Leds.led0Off();
       }

       event void Radio.startDone(error_t res) {
           radioOn = (res == SUCCESS);
           radioTurningOn = FALSE;
           
           call Leds.led2Toggle();

           if (firstStart) {
              firstStart = FALSE;
              #ifdef SECURE
                if(res == SUCCESS)
                    call CC2420Keys.setKey(0, spine_secure_key);
              #endif
              signal RadioController.radioOn();
           }

           if (sendMsgTmp) {
              sendMsgTmp = FALSE;
              sendOneMessage(call AMPacket.destination(&msgTmp), call AMPacket.type(&msgTmp), &msgTmp, call Packet.payloadLength(&msgTmp));
           }
           else if (myTurn && tdmaEnabled)
              checkQueueToSend();
       }

       #ifdef SECURE
       event void CC2420Keys.setKeyDone(uint8_t keyNo, uint8_t* skey) {
         // do nothing
       }
       #endif

       event void Radio.stopDone(error_t res) {
           radioOn = !(res == SUCCESS);
           
           call Leds.led2Toggle();
       }

       error_t bufferData(uint16_t destination, uint8_t type, void* payload, uint8_t len) {
          message_t buffer;
          if (len <= TOSH_DATA_LENGTH) {
             call AMPacket.setDestination(&buffer, destination);
             call AMPacket.setType(&buffer, type);
             call Packet.setPayloadLength(&buffer, len);          // CHECK HERE
			 
             #ifndef TINYOS_2_0_2
			 memcpy(call Sender.getPayload(&buffer,len), payload, len);	   
		     #else
             memcpy(call Sender.getPayload(&buffer), payload, len);
             #endif

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
			  
              #ifndef TINYOS_2_0_2
			  memcpy(call Sender.getPayload(&msgTmp,len), data, len);	   
		      #else
              memcpy(call Sender.getPayload(&msgTmp), data, len);
              #endif
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
       
       command void RadioController.enableTDMA(uint16_t networkSize, uint16_t myTimeSlotID) {
           tdmaEnabled = TRUE;
           myTurn = FALSE;
           netSize = networkSize;
           myTimeSlot = myTimeSlotID;

           call TDMATimer.startPeriodic(TDMA_FRAME_PERIOD / networkSize);
       }

       command void RadioController.disableTDMA() {
           tdmaEnabled = FALSE;
           myTurn = TRUE;

           call TDMATimer.stop();
       }

       command error_t RadioController.send(uint16_t destination, enum PacketTypes pktType, void* payload, uint8_t len) {
           error_t status = SUCCESS;
           error_t r1 = SUCCESS, r2 = SUCCESS;
           if (!radioOn) {
              if (myTurn) {
                 if (canSendNow) {
                    canSendNow = FALSE;
                    status = prepareToSend(destination, pktType, payload, len);
                 }
                 else
                    status = bufferData(destination, pktType, payload, len);
                 
                 radioTurnOnPolicy();   
              }
              else
                 status = bufferData(destination, pktType, payload, len);
           }
           else {
              if (myTurn) {
                 if (lowPowerEnabled)
                    call ListenTimer.stop(); // if it's not running, nothing happens

                 if (canSendNow) { // send the message immediately
                    canSendNow = FALSE;
                    r1 = prepareToSend(destination, pktType, payload, len);
                    r2 = sendOneMessage(destination, pktType, &msgTmp, len);
                    status = (r1 == r2) ? r1 : FAIL;
                    //status = ecombine(prepareToSend(destination, pktType, payload, len), sendOneMessage(destination, pktType, &msgTmp, len));
                 }
                 else
                    status = bufferData(destination, pktType, payload, len);
              }
              else
                 status = bufferData(destination, pktType, payload, len);
           }

           return status;
       }

       event void Sender.sendDone(message_t* msg, error_t error) {
           checkQueueToSend();
       }

       event message_t* Receiver.receive(message_t* msg, void* payload, uint8_t len) {
           
           radioTurnOffPolicy();

                                          // TODO if the coordinator can't set the source... let's impose here SPINE_BASE_STATION as the source
           signal RadioController.receive(call AMPacket.source(msg), call AMPacket.type(msg), payload, len);

           return msg;
       }

 }

