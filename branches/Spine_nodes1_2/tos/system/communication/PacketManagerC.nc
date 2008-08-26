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
 * Configuration component of the SPINE Packets Manager.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

 configuration PacketManagerC {
      provides interface PacketManager;

      uses interface InPacket as InPackets[uint8_t inPktID];
      uses interface OutPacket as OutPackets[uint8_t outPktID];
 }

 implementation {
      components PacketManagerP, RadioControllerC,
                 SpineHeaderC;

      // if new InPackets are added, declare their components down here
      components SpineSetupSensorPktC;
      components SpineSetupFunctionPktC;
      components SpineFunctionReqPktC;
      components SpineStartPktC;
      
      // if new OutPackets are added, declare their components down here
      components SpineServiceAdvertisementPktC;
      components SpineDataPktC;
      components SpineServiceMessagePktC;

      
      PacketManager = PacketManagerP;
      
      PacketManagerP.InPackets = InPackets;
      PacketManagerP.OutPackets = OutPackets;

      PacketManagerP.RadioController -> RadioControllerC;
      
      PacketManagerP.Header -> SpineHeaderC;
      // if new InPackets are added, wire the aforedeclared components down here
      PacketManagerP.InPackets[SETUP_SENSOR] -> SpineSetupSensorPktC;
      PacketManagerP.InPackets[SETUP_FUNCTION] -> SpineSetupFunctionPktC;
      PacketManagerP.InPackets[FUNCTION_REQ] -> SpineFunctionReqPktC;
      PacketManagerP.InPackets[START] -> SpineStartPktC;

      // if new OutPackets are added, wire the aforedeclared components down here
      PacketManagerP.OutPackets[SERVICE_ADV] -> SpineServiceAdvertisementPktC;
      PacketManagerP.OutPackets[DATA] -> SpineDataPktC;
      PacketManagerP.OutPackets[SVC_MSG] -> SpineServiceMessagePktC;
 }
