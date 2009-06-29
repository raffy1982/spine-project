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
 * Module component of the SPINE Packets Manager.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */
 
 module PacketManagerP {

       uses {
            interface RadioController;

            interface SpineHeader as Header;

            interface InPacket as InPackets[uint8_t inPktID];
            interface OutPacket as OutPackets[uint8_t outPktID];
       }

       provides interface PacketManager;
 }

 implementation {

     uint16_t DEFAULT_DEST = SPINE_BASE_STATION;

     uint8_t sequenceNr = 0;

     command void PacketManager.build(enum PacketTypes pktType, void* pld, uint8_t len) {

          bool extension = FALSE;
          uint8_t fragmentNr = 0;
          uint8_t totFragments;
          uint8_t builtLen = 1;
          uint8_t* header;
          uint8_t* payload = call OutPackets.build[pktType](pld, len, &builtLen);
          uint8_t msgTmp[SPINE_PKT_MAX_SIZE];
          uint8_t lenTmp = SPINE_PKT_PAYLOAD_MAX_SIZE;
          uint8_t i;

          // if necessary, we are going to split the message in multiple fragments

          totFragments = ( (builtLen%SPINE_PKT_PAYLOAD_MAX_SIZE) == 0)? (builtLen/SPINE_PKT_PAYLOAD_MAX_SIZE) :
                                                                         ((builtLen/SPINE_PKT_PAYLOAD_MAX_SIZE) + 1);
          for (i = 0; i<totFragments; i++) {
             fragmentNr = i+1;
             header = call Header.build(SPINE_VERSION, extension, pktType, GROUP_ID, TOS_NODE_ID, DEFAULT_DEST,
                                        sequenceNr, fragmentNr, totFragments);

             memcpy(msgTmp, header, SPINE_HEADER_PKT_SIZE);

             if(fragmentNr == totFragments)       // only the last payload fragment could be smaller than the max size
                lenTmp = builtLen - ((totFragments - 1) * SPINE_PKT_PAYLOAD_MAX_SIZE);

             memcpy(msgTmp + SPINE_HEADER_PKT_SIZE, payload + (i*SPINE_PKT_PAYLOAD_MAX_SIZE), lenTmp);

             call RadioController.send(DEFAULT_DEST, pktType, &msgTmp, SPINE_HEADER_PKT_SIZE + lenTmp);
          }
          
          sequenceNr++;     // seqNr is incremented on a per message basis (not each fragment)

     }

     event void RadioController.radioOn() {}

     event void RadioController.receive(uint16_t source, enum PacketTypes pktType, void* pkt, uint8_t len) {
         // TODO: if in the future the coordinator could send fragmented packets to the nodes,
         //       the logic for reconstructing the original message must be implemented here

         uint8_t* ackHeader;
         uint8_t* ackBuiltPld;
         uint8_t ackPld[SPINE_SVC_MSG_SIZE];
         uint8_t ackMsg[SPINE_HEADER_PKT_SIZE + SPINE_SVC_MSG_SIZE];
         uint8_t builtAckPldLen;

         if(pktType == AM_SPINE) {
            call Header.parse(pkt);
            // if the type of the incoming message is not a recognized spine packet, it won't be signaled
            if (call  InPackets.parse[call Header.getPktType()](pkt + SPINE_HEADER_PKT_SIZE, len - SPINE_HEADER_PKT_SIZE)) {
                if (call Header.getVersion() == SPINE_VERSION && call Header.getGroupID() == GROUP_ID &&
                       (call Header.getDestID() == TOS_NODE_ID || call Header.getDestID() == SPINE_BROADCAST) ) {
                    
                    // BEGIN ACKs transmission for received SPINE pkts
                    ackHeader = call Header.build(SPINE_VERSION, 0, SVC_MSG, GROUP_ID, TOS_NODE_ID, call Header.getSourceID(), sequenceNr++, 1, 1);
                    ackPld[0] = ACK;
                    ackPld[1] = call Header.getSequenceNumber();
                    ackBuiltPld = call OutPackets.build[SVC_MSG](ackPld, sizeof(ackPld), &builtAckPldLen);

                    memcpy(ackMsg, ackHeader, SPINE_HEADER_PKT_SIZE);
                    memcpy(ackMsg + SPINE_HEADER_PKT_SIZE, ackBuiltPld, builtAckPldLen);

                    call RadioController.send(call Header.getSourceID(), SVC_MSG, &ackMsg, sizeof(ackMsg));
                    // END ACKs transmission for received SPINE pkts

                    signal PacketManager.messageReceived(call Header.getPktType()); // it's supposed to be the same of the 'pktType' param
                }
            }
         }
     }


     // Default commands needed due to the use of parametrized interfaces

     default command bool InPackets.parse[uint8_t inPktID](void* payload, uint8_t len) {
           bool isSpinePktWithNoPayload = (inPktID == SERVICE_DISCOVERY || inPktID == RESET || inPktID == SYNCR);
           if (!isSpinePktWithNoPayload)
              dbg(DBG_USR1, "PacketManagerP.parse: Executed default operation. Chances are there's an operation miswiring.\n");
           return isSpinePktWithNoPayload;
     }

     default command void* OutPackets.build[uint8_t outPktID](void* payload, uint8_t len, uint8_t* builtLen) {
           dbg(DBG_USR1, "PacketManagerP.build: Executed default operation. Chances are there's an operation miswiring.\n");
           return NULL;
     }

 }

