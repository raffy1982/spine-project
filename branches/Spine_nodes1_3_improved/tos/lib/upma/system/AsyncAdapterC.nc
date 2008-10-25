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
 * Converts <tt>AsyncReceive</tt> and <tt>AsyncSend</tt> interfaces to
 * synchronous <tt>Receive</tt> and <tt>Send</tt> interfaces.
 *
 * @author Greg Hackmann
 */
configuration AsyncAdapterC
{
	provides interface Receive;
	provides interface Send;
	uses interface AsyncReceive;
	uses interface AsyncSend;
}
implementation
{
	components AsyncReceiveAdapterP as RecvAdapter, AsyncSendAdapterP as SendAdapter;

	Receive = RecvAdapter;	
	Send = SendAdapter;
	
	RecvAdapter.AsyncReceive = AsyncReceive;
	SendAdapter.AsyncSend = AsyncSend;

	components LedsC;
	SendAdapter.Leds -> LedsC;
}
