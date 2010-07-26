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
 * Module component of the Bluetooth Controller. This components takes care of sending and receiving messages.
 * Clients of the Bluetooth Controller don't have to worry about bluetooth turn-on/off.
 * This component uses a queue to avoid messages drops or corruptions if the bluetooth is currently sending/receiving data.
 * It is possible to tune the max size of the queue defining the pflag 'QUEUE_MAX_SIZE' in the application makefile.
 *
 *
 *
 * @author Raffaele Gravina, Michele Capobianco, Livio Bioglio
 *
 * @version 1.3
 */

#include "btMessage.h"

 module BluetoothControllerP {

       uses {
	    interface Boot;

	    interface Bluetooth;
	    interface StdControl as BTStdControl;

	    //interface Packet;

	    interface Queue<bt_message>;
            
            interface Leds;
       }

       provides interface RadioController;

 }

 implementation {

       //true if the bluetooth is busy
       bool busy_bt;
       //true if the byte received is the first of the message
       bool is_first;
       uint8_t length_to_read;
       uint8_t last_received;
       uint8_t received_msg[SPINE_PKT_MAX_SIZE];


       event void Boot.booted() {
	 call BTStdControl.start();
	 
       }


       async event void Bluetooth.connectionMade(uint8_t status) { 
	   call Leds.led2On();
	   is_first = TRUE;
	   atomic busy_bt = FALSE;
           //post signalRadioOn();
       }

       task void signalRadioOn(){
	 signal RadioController.radioOn();
       }

       async event void Bluetooth.commandModeEnded() { 
           call Leds.led1On();
	   post signalRadioOn();
       }
    
       async event void Bluetooth.connectionClosed(uint8_t reason){
           call Leds.led2Off();
       }


       error_t bufferData(bt_message buffer){	 
	 return call Queue.enqueue(buffer);
       }

       error_t sendOneMessage(bt_message* payload){
	 uint8_t len;
	 error_t status;
	 len = payload->buffer[0]+1;
	 status = call Bluetooth.write(payload->buffer, len);
	 if(status == SUCCESS)
	   call Leds.led0Toggle();
	 return status;
       }

       void sendMessages(){
	 bt_message msgtmp = call Queue.dequeue();
	 sendOneMessage(&msgtmp);
       }

       void checkQueueToSend(){	 
	 if(call Queue.empty())
	   atomic busy_bt = FALSE;
	 else
	   sendMessages();	   
       }

       event void Bluetooth.writeDone(){
	 call Leds.led0Toggle();
	 checkQueueToSend();
       }


   command error_t RadioController.send(uint16_t destination, enum PacketTypes pktType, void* payload, uint8_t len){
	 error_t status;
	 bt_message msg;
	 msg.buffer[0] = len;
	 memcpy(msg.buffer + 1, payload, len);
	 atomic if(!busy_bt){
	     busy_bt = TRUE;
	     status = sendOneMessage(&msg);
	 }
	 else
	   status = bufferData(msg);
	 return status;
       }


       task void packetAvaible(){
	 uint8_t len;
	 enum PacketTypes pktType =  AM_SPINE;
	 uint16_t source = 0;
	 atomic len = length_to_read;
	 signal RadioController.receive(source,pktType,&received_msg, len);
       }

       async event void Bluetooth.dataAvailable(uint8_t data){
	 if(is_first){
	   is_first = FALSE;
	   //message length (bytes)
	   length_to_read = data;
	   last_received = 0;
	 }
	 else{
	   received_msg[last_received] = data;
	   last_received ++;
	   if(last_received == length_to_read){
	     is_first = TRUE;	    
	     post packetAvaible();
	   }
	 }

       }

       command void RadioController.setRadioAlwaysOn(bool enable){}
       command void RadioController.enableTDMA(uint16_t networkSize, uint16_t myTimeSlotID){}
       command void RadioController.disableTDMA(){}

 }

