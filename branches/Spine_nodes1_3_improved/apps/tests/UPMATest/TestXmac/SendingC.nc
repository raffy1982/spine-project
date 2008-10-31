/*
 * "Copyright (c) 2007 Washington University in St. Louis.
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice, the following
 * two paragraphs and the author appear in all copies of this software.
 *
 * IN NO EVENT SHALL WASHINGTON UNIVERSITY IN ST. LOUIS BE LIABLE TO ANY PARTY
 * FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING
 * OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF WASHINGTON
 * UNIVERSITY IN ST. LOUIS HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * WASHINGTON UNIVERSITY IN ST. LOUIS SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND WASHINGTON UNIVERSITY IN ST. LOUIS HAS NO
 * OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR
 * MODIFICATIONS."
 */
 
/**
 *
 * @author Greg Hackmann
 * @version $Revision: 1.1 $
 * @date $Date: 2007/11/06 23:58:57 $
 */

#include "spine_constants.h"
generic module SendingC()
{
	uses
	{
		interface Boot;
		interface Leds;
		interface LowPowerListening;
		interface AMSend;
		interface Receive;
		interface Packet;
		interface SplitControl as RadioControl;
	}
}

implementation
{
	message_t msg;
	task void sendTask();

	void send() {
		if(call AMSend.send(!TOS_NODE_ID, &msg, sizeof(TestMsg)) == SUCCESS)
			call Leds.led0Toggle();
		else {
			call Leds.led2Toggle();
			post sendTask();
		}
	}
	
	task void sendTask() {
		send();
	}

	event void Boot.booted()
	{
		TestMsg * payload = (TestMsg *)call AMSend.getPayload(&msg, sizeof(TestMsg));
		payload->payload[0] = 'T';
		payload->payload[1] = 'e';
		payload->payload[2] = 's';
		payload->payload[3] = 't';
		payload->payload[4] = '\0';
		call RadioControl.start();
	}
	
	event void RadioControl.startDone(error_t err)
	{
		call LowPowerListening.setLocalSleepInterval(SPINE_SLEEP_INTERVAL);
		send();
	}
	
	event void RadioControl.stopDone(error_t err)
	{
	}
	
	event void AMSend.sendDone(message_t * m, error_t error)
	{
		send();
	}
	
	event message_t * Receive.receive(message_t * m, void * payload, uint8_t len)
	{
		call Leds.led1Toggle();
		return m;
	}
}
