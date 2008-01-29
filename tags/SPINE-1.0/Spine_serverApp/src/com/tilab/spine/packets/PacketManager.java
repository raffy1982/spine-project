package com.tilab.spine.packets;

import java.util.Hashtable;
import java.util.Vector;

import com.tilab.spine.constants.Constants;
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

import com.tilab.spine.constants.PacketConstants;

/**
 *
 * This utility class contains the methods to build low level AMP packets and
 * to parse low level AMP packets to build the well formatted AMP packets.
 *  
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class PacketManager {
	
	/**
	 * This utility method is used only within this class and builds a low level AMP header
	 * 
	 * @param pkt the low level array 
	 * @param header the well formatted header
	 */
	private static void buildHeader(short[] pkt, Header header) {
		 short ext = (header.isExtented())? (short)1: (short)0; 
		 pkt[0] = (short)((header.getVersion()<<6) | (ext<<5) | (header.getPktType()<<1));
		 pkt[1] = (short)((header.getGroupID()<<4) | header.getSourceID());
		 pkt[2] = (short)((header.getDestID()<<4));
		 pkt[3] = (short)(header.getTimeStamp());	
	}
	
	/**
	 * This utility method is used only within this class and parses a low level AMP header 
	 * to build an Header object 
	 * 
	 * @param pkt the low level array
	 * 
	 * @return the resulting Header object
	 */
	private static Header parseHeader(short[] pkt) {
		short version = (short)((pkt[0] & 0xC0)>>6); // 0xC0 = 11000000 binary
		short ext = (short)((pkt[0] & 0x20)>>5); // 0x20 = 00100000 binary
		boolean extended = (ext==(short)0)? Constants.STANDARD_PACKET : Constants.EXTENDED_PACKET;
		short pktType = (short)((pkt[0] & 0x1E)>>1); // 0x1E = 00011110 binary
		short groupID = (short)((pkt[1] & 0xF0)>>4); // 0xF0 = 11110000 binary
		short sourceID = (short)((pkt[1] & 0x0F)); // 0x0F = 00001111 binary
		short destID = (short)((pkt[2] & 0xF0)>>4); // 0xF0 = 11110000 binary
		short timeStamp = pkt[3];
	    
		return new Header(version, extended, pktType, groupID, sourceID, destID, timeStamp);
	}
	
	/**
	 * This utility method builds a low level Battery Info Request Packet
	 * 
	 * @param bir the BatteryInfoReq Object 
	 * @return BatteryInfoReqPkt with inside the low level array.
	 */
	public static BatteryInfoReqPkt buildBatteryInfoReqPkt(BatteryInfoReq bir) {
		short[] pkt = new short[PacketConstants.BATTERY_INFO_REQUEST_PKT_SIZE];
		
		short tpTemp = bir.isTime()? (short)1 : (short)0;
		short periodPart0 = (short)((bir.getPeriod() & 0x1F00)>>8);
		short periodPart1 = (short)(bir.getPeriod() & 0xFF);
		
		buildHeader(pkt, bir.getHeader());
		pkt[PacketConstants.PKT_HEADER_SIZE] = (short)((tpTemp<<7) | bir.getTimeScale()<<5 | periodPart0);
		pkt[PacketConstants.PKT_HEADER_SIZE + 1] = periodPart1;
		
		BatteryInfoReqPkt birp = new BatteryInfoReqPkt();
		birp.set_part(pkt);
		return birp;
	}
	
	/**
	 * This utility method parses a low level BatteryInfoPkt 
	 * to build a BatteryInfo Object 
	 * 
	 * @param bip the low level BatteryInfoPkt
	 * @return BatteryInfo Object
	 */
	public static BatteryInfo parseBatteryInfoPkt(BatteryInfoPkt bip) {
		int voltage = ( ((int)bip.get_part()[PacketConstants.PKT_HEADER_SIZE])<<8 ) | 
						(((int)bip.get_part()[PacketConstants.PKT_HEADER_SIZE+1]));
		
		return new BatteryInfo(parseHeader(bip.get_part()), voltage);
	}
	
	/**
	 * This utility method parses a low level DataPkt to build a Data Object
	 * 
	 * @param dp the low level DataPkt
	 * 
	 * @return Data Object
	 */
	public static Data parseDataPkt(DataPkt dp) {
		short featureCode = (short)((dp.get_part()[PacketConstants.PKT_HEADER_SIZE] & 0xF8)>>3); // 0xF8 = 11111000 binary
		short sensorCode = (short)((dp.get_part()[PacketConstants.PKT_HEADER_SIZE + 1] & 0xC0)>>6); // 0xC0 = 11000000 binary
		boolean activeAxis0 = (short)((dp.get_part()[PacketConstants.PKT_HEADER_SIZE + 1] & 0x20)>>5) == 1; // 0xC0 = 00100000 binary
		boolean activeAxis1 = (short)((dp.get_part()[PacketConstants.PKT_HEADER_SIZE + 1] & 0x10)>>4) == 1; // 0x10 = 00010000 binary
		boolean activeAxis2 = (short)((dp.get_part()[PacketConstants.PKT_HEADER_SIZE + 1] & 0x8)>>3) == 1; // 0x8 = 00001000 binary
		int featureAxis0 = dp.get_feature()[0];
		int featureAxis1 = dp.get_feature()[1];
		int featureAxis2 = dp.get_feature()[2];
		
		return new Data(parseHeader(dp.get_part()), featureCode, sensorCode, activeAxis0, activeAxis1,
				  activeAxis2, featureAxis0, featureAxis1, featureAxis2);
	}
	
	/**
	 * This utility method parses a low level ErrorPkt to build a amp.packets.Error Object
	 * 
	 * @param ep the low level ErrorPkt
	 * 
	 * @return amp.packets.Error Object
	 */
	public static com.tilab.spine.packets.ServiceMessage parseErrorPkt(ServiceMessagePkt ep) {
		short errorType = (short)((ep.get_part()[PacketConstants.PKT_HEADER_SIZE] & 0xE0)>>5); // 0xE0 = 11100000 binary
		short errorDetail = (short)(ep.get_part()[PacketConstants.PKT_HEADER_SIZE] & 0x1F); // 0x1F = 00011111 binary
		
		return new com.tilab.spine.packets.ServiceMessage(parseHeader(ep.get_part()), errorType, errorDetail);
	}
	
	/**
	 * This utility method builds a low level FeatureActivationPkt
	 * 
	 * @param fa the FeatureActivation Object
	 * 
	 * @return FeatureActivationPkt with inside the low level array.
	 */
	public static FeatureActivationPkt buildFeatureActivationPkt(FeatureActivation fa) {
		short[] pkt = new short[PacketConstants.FEATURE_ACTIVATION_PKT_SIZE];
		
		byte actAxis0Temp = fa.isActiveAxis0()? (byte)1 : (byte)0;
		byte actAxis1Temp = fa.isActiveAxis1()? (byte)1 : (byte)0;
		byte actAxis2Temp = fa.isActiveAxis2()? (byte)1 : (byte)0;
		
		short samplingTimePart0 = (short)((fa.getSamplingTime() & 0xFF00)>>8);
		short samplingTimePart1 = (short)(fa.getSamplingTime() & 0xFF);
		
		buildHeader(pkt, fa.getHeader());
		pkt[PacketConstants.PKT_HEADER_SIZE] = (short)((fa.getFeatureCode()<<3));
		pkt[PacketConstants.PKT_HEADER_SIZE + 1] = (short)(fa.getWindowFrame());
		pkt[PacketConstants.PKT_HEADER_SIZE + 2] = (short)(fa.getShift());
		pkt[PacketConstants.PKT_HEADER_SIZE + 3] = samplingTimePart0;
		pkt[PacketConstants.PKT_HEADER_SIZE + 4] = samplingTimePart1;
		pkt[PacketConstants.PKT_HEADER_SIZE + 5] = (short)((fa.getSensorCode()<<6) | (actAxis0Temp<<5) 
																				   | (actAxis1Temp<<4)
																				   | (actAxis2Temp<<3));
		
		FeatureActivationPkt fap = new FeatureActivationPkt();
		fap.set_part(pkt);
		return fap;
	}
	
	/**
	 * This utility method builds a low level RemoveFeaturePkt
	 * 
	 * @param rf the RemoveFeature Object
	 * 
	 * @return RemoveFeaturePkt with inside the low level array.
	 */
	public static RemoveFeaturePkt buildRemoveFeaturePkt(RemoveFeature rf) {
		short[] pkt = new short[PacketConstants.REMOVE_FEATURE_PKT_SIZE];
		
		byte disableAxis0Temp = rf.isDisableAxis0()? (byte)1 : (byte)0;
		byte disableAxis1Temp = rf.isDisableAxis1()? (byte)1 : (byte)0;
		byte disableAxis2Temp = rf.isDisableAxis2()? (byte)1 : (byte)0;
		
		buildHeader(pkt, rf.getHeader());
		pkt[PacketConstants.PKT_HEADER_SIZE] = (short)((rf.getFeatureCode()<<3));
		pkt[PacketConstants.PKT_HEADER_SIZE + 1] = (short)((rf.getSensorCode()<<6) | (disableAxis0Temp<<5) 
																				   | (disableAxis1Temp<<4)
																				   | (disableAxis2Temp<<3));
		
		RemoveFeaturePkt rfp = new RemoveFeaturePkt();
		rfp.set_part(pkt);
		return rfp;
	}
	
	/**
	 * This utility method builds a low level ServiceDiscoveryPkt
	 * 
	 * @param sd the ServiceDiscovery Object
	 * 
	 * @return ServiceDiscoveryPkt with inside the low level array.
	 */
	public static ServiceDiscoveryPkt buildServiceDiscoveryPkt(ServiceDiscovery sd) {
		short[] pkt = new short[PacketConstants.SERVICE_DISCOVERY_PKT_SIZE];
		
		buildHeader(pkt, sd.getHeader());
		
		byte resetFlag = sd.isReset()? (byte)1 : (byte)0;
		byte startFlag = sd.isStart()? (byte)1 : (byte)0;
		
		pkt[PacketConstants.PKT_HEADER_SIZE] = (short)((startFlag<<7) | (resetFlag<<6) | (sd.getNumMotes()));
		
		ServiceDiscoveryPkt sdp = new ServiceDiscoveryPkt();
		sdp.set_part(pkt);
		return sdp;
	}
	
	/**
	 * This utility method parses a low level ServiceAdvertisementPkt to build a ServiceAdvertisement Object
	 * 
	 * @param sap the low level ServiceAdvertisementPkt
	 * 
	 * @return ServiceAdvertisement Object
	 */
	public static ServiceAdvertisement parseServiceAdvertisementPkt(ServiceAdvertisementPkt sap) {
		Hashtable sensor_Axis = new Hashtable();
		
		for (int i=0; i<2; i++) {
			short tempAxis = (short)((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + i] & 0x30)>>4);
			if(tempAxis > (short)0)
				sensor_Axis.put(new Short((short)((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + i] & 0xC0)>>6)), 
						new Short(tempAxis));
			tempAxis = (short)(sap.get_part()[PacketConstants.PKT_HEADER_SIZE + i] & 0x3);
			if(tempAxis > (short)0)
				sensor_Axis.put(new Short((short)((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + i] & 0xC)>>2)),
						new Short(tempAxis));
		}
		
		Vector avFeatures = new Vector();
		int iFeat = 0;
		int byteAF = 2;
		for (int i=0; i<32; i++) { // 32 is the max number of available features
			short currFeat = 0;			
			if (iFeat > 3) { // 2 bytes needed from sap.get_part()
				switch (iFeat) {
					case 4:
						currFeat = (short)(((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + byteAF] & 0x0F)<<1) |
										   ((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + byteAF+1] & 0x80)>>7));
						break;
					case 5: 
						currFeat = (short)(((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + byteAF] & 0x7)<<2) |
								   ((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + byteAF+1] & 0xC0)>>6));
						break;
					case 6: 
						currFeat = (short)(((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + byteAF] & 0x03)<<3) |
								   ((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + byteAF+1] & 0xE0)>>5));
						break;
					case 7: 
						currFeat = (short)(((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + byteAF] & 0x1)<<4) |
								   ((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + byteAF+1] & 0xF0)>>4));
						break;
					default: break;
				}
				byteAF++;
				iFeat -= 3;
			}
			else {
				switch (iFeat) {
					case 0:
						currFeat = (short)((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + byteAF] & 0xF8)>>3);
						break;
					case 1: 
						currFeat = (short)((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + byteAF] & 0x7C)>>2);
						break;
					case 2: 
						currFeat = (short)((sap.get_part()[PacketConstants.PKT_HEADER_SIZE + byteAF] & 0x3E)>>1);
						break;
					case 3: 
						currFeat = (short)(sap.get_part()[PacketConstants.PKT_HEADER_SIZE + byteAF] & 0x1F);
						break;
					default: break;
				}
				iFeat += 5;
				if (iFeat == 8) {
					iFeat = 0;
					byteAF++;
				}					
			} // else
			
			// START MOD RG-AG 2008-03-01
			Short currF = new Short(currFeat);
			if (!avFeatures.contains(currF))
				avFeatures.add(currF);
			// END MOD RG-AG 2008-03-01

		} // for		
		
		return new ServiceAdvertisement(parseHeader(sap.get_part()), sensor_Axis, avFeatures);
	}
    
}
