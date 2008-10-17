// $Id: AMSend.nc,v 1.7 2008/06/11 00:46:24 razvanm Exp $
/*
 * "Copyright (c) 2005 The Regents of the University  of California.  
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice, the following
 * two paragraphs and the author appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS."
 *
 * Copyright (c) 2005 Intel Corporation
 * All rights reserved.
 *
 * This file is distributed under the terms in the attached INTEL-LICENSE     
 * file. If you do not find these files, copies can be found by writing to
 * Intel Research Berkeley, 2150 Shattuck Avenue, Suite 1300, Berkeley, CA, 
 * 94704.  Attention:  Intel License Inquiry.
 */

/** The basic active message message sending interface. Also see
  * Packet, Receive, and Send.
  *
  * @author Philip Levis
  * @author Kevin Klues
  * @date   January 5 2005
  * @see   Packet
  * @see   AMPacket
  * @see   Receive
  * @see   TEP 116: Packet Protocols
  */ 


#include <TinyError.h>
#include <message.h>
#include <AM.h>

interface BufferedSendWithHeader {

  /** 
    *
    * @param addr                address to which to send the packet
    * @param 'void* ONE header'  the header of the packet
    * @param 'void* ONE data'    the payload of the packet
    * @param header_len          the length of the header in the packet payload
    * @param data_len            the length of the data in the packet payload
    * @return                    SUCCESS if the request to send succeeded
    *                            ESIZE if the the length is to large to fit
    *                                  the data inside one packet
    *                            ERETRY if the buffer is full and cannot
    *                                  process the packet at the moment
    */ 
  command error_t send(am_addr_t addr, void* header, uint8_t header_len, void* data, uint8_t data_len);  
}
