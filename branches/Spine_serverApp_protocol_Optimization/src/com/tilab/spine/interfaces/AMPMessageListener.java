package com.tilab.spine.interfaces;

import java.util.Vector;
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

import java.util.Hashtable;

import com.tilab.spine.logic.Feature;


/**
 *
 * This is the interface representing the AMP events listener 
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public interface AMPMessageListener {

	/**
	 * This method is invoked by the AmpBsnManager to its registered listener instance
	 * when receive a ServiceAdvertisement message from a BSN node
	 * 
	 * @param sourceNode the address of the node sender the packet
	 * @param sensor_Axis the Hashtable containing the sensors-axis codes of the sender
	 * @param avFeatures the Vector containing the features codes the sender can compute
	 * 
	 * @see com.tilab.spine.constants.FeatureCodes for the standard feature coding
	 * @see com.tilab.spine.constants.SensorCodes for the standard sensor and axis coding
	 */
	public void advertisementReceived(short sourceNode, Hashtable sensor_Axis, Vector avFeatures);
	
	/**
	 * This method is invoked by the AmpBsnManager to its registered listener instance
	 * when the discovery procedure timer fires. The nodes responding to a 
	 * service discovery before the timeout will represent the list of node the application can handle.  
	 * It is possible to change the timer period, before booting up the BSN, 
	 * with the AmpBsnManager method: setDiscoveryProcedureTimeout(long period). 
	 * 
	 * @param nodes the list of BsnNode objects representing the node have sent ServiceAdvertisement 
	 * 		  a in the specific discovery procedure timeout 
	 *        (these node will represent the BSN seen by the application).
	 * 
	 * @see com.tilab.spine.logic.BsnNode
	 */
	public void discoveryCompleted(Vector nodes);
	
	/**
	 * This method is invoked by the AmpBsnManager to its registered listener instance
	 * when a node sends to its base-station the features set computed in the time-frame identified by 
	 * the logical timestamp.
	 * This method allows the application to obtain an aggregation of all the features transmitted by a 
	 * particular node in a certain time-frame.
	 * The AmpBsnManager takes care of checking if some of the feature requested by the application 
	 * to a particular node are missing in a certain frame. In that case, it will discard the incomplete feature set.  
	 * 
	 * @param sourceNode the address of the node sender the packet
	 * @param frame the set of Feature objects representing the features computed 
	 *        and sent by the node 'sourceNode' in a certain time-frame identified by 
	 *        the logical timestamp 'timestamp'
	 * @param timestamp the logical timestamp representing the time-frame over which these frame of
	 * 		  features have computed
	 * 
	 * @see com.tilab.spine.logic.Feature
	 */
	public void frameReceived(short sourceNode, Feature[] frame, int timestamp);
	
	/**
	 * This method is invoked by the AmpBsnManager to its registered listener instance
	 * when EVERY nodes within the BSN have sent their feature values over a certain time-frame identified 
	 * by the logical timestamp. 
	 * This method allows the application to obtain an aggregation of all the features transmitted by all the 
	 * nodes within the BSN in a certain time-frame.   
	 * The AmpBsnManager takes care of checking if some of the feature requested 
	 * by the application to a particular node are missing in a certain frame and if some whole node frame 
	 * is missing. 
	 * In that case, it will discard the incomplete superframe. 
	 * 
	 * @param superFrame the set of features computed and sent by all the node within the BSN.
	 * 	 	  This Hashtable has the sourceNode ID as keys and 'Feature[]' (features frame of single nodes)
	 *  	  as values.	  
	 * @param timestamp the logical timestamp representing the time-frame over which 
	 * 		  these super-frame of features have computed
	 */
	public void superFrameReceived(Hashtable superFrame, int timestamp);
	
	/**
	 * This method is invoked by the AmpBsnManager to its registered listener instance
	 * when a ServiceMessage is received from a particular node.  
	 * The AmpBsnManager takes care of forwarding the service message (that could be the signal of errors or events)
	 * to the application.
	 * 
	 * @param sourceNode the address of the node sender the packet
	 * @param messageType the message type code
	 * @param messageDetail the message detail code
	 * 
	 * @see com.tilab.spine.constants.ServiceMessageCodes for the standard messages coding
	 */
	public void messageReceived(short sourceNode, short messageType, short messageDetail);
	
	/**
	 * This method is invoked by the AmpBsnManager to its registered listener instance
	 * when a BatteryInfo packet from a particular node is received.
	 * The AmpBsnManager takes care of forwarding the voltageLevel message to the application. 
	 * 
	 * @param sourceNode the address of the node sender the packet
	 * @param voltageLevel the actual voltage level (i.e. 2.87)
	 */
	public void batteryInfoReceived(short sourceNode, double voltageLevel);
}
