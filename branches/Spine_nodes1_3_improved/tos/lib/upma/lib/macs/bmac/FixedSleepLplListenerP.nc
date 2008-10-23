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
 * @author Greg Hackmann
 * @version $Revision: 1.1 $
 * @date $Date: 2007/11/06 23:58:58 $
 */
module FixedSleepLplListenerP
{
	provides interface AsyncReceive as Receive;
	provides interface FixedSleepLplListener;
	provides interface Interval as RadioTimeout;
	
	uses interface ChannelPoller;
	uses interface StdControl as PollerControl;
	uses interface RadioPowerControl;
	uses interface AsyncReceive as SubReceive;
	uses interface State as SendState;
	uses interface AMPacket;
	uses interface Alarm<TMilli, uint16_t> as TimeoutAlarm;
	uses interface Leds;
}
implementation
{
	uint16_t timeout = 100;
	
	async command void RadioTimeout.set(uint16_t ms)
	{
		atomic timeout = ms;
	}
	
	async command uint16_t RadioTimeout.get()
	{
		atomic return timeout;
	}
	
	async command void FixedSleepLplListener.startTimeout()
	{
		call TimeoutAlarm.stop();
		call TimeoutAlarm.start(timeout);
	}
	
	async command void FixedSleepLplListener.cancelTimeout()
	{
		call TimeoutAlarm.stop();
	}
	
	async event void TimeoutAlarm.fired()
	{
#ifndef FORCE_RADIO_ON
		if(call SendState.isIdle())
			call RadioPowerControl.stop();
#endif
	}
	
	task void stopPoller()
	{
		call PollerControl.stop();
	}
	
	async event void ChannelPoller.activityDetected(bool detected)
	{
#ifndef FORCE_RADIO_ON
		if(detected)
		{
			post stopPoller();

			if(call SendState.isIdle())
				call FixedSleepLplListener.startTimeout();
		}

		// If the CCA check is positive, keep the radio on and pause
		// the periodic polls
		else if(call SendState.isIdle())
			call RadioPowerControl.stop();
		// Otherwise, stop the radio if we're currently not using it
#endif
	}
	
	event void RadioPowerControl.startDone(error_t error) { }
	event void RadioPowerControl.stopDone(error_t error)
	{
#ifndef FORCE_RADIO_ON
		call PollerControl.start();
		// Now that the radio's off, resume the LPL checks
#endif
	}
	
	command void Receive.updateBuffer(message_t * msg)
	{
		call SubReceive.updateBuffer(msg);
	}

	async event void SubReceive.receive(message_t * msg, void * payload, uint8_t len)
	{
		signal Receive.receive(msg, payload, len);
	}	
}
