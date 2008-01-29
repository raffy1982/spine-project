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

package com.tilab.spine.logic;

import java.util.Hashtable;
import java.util.Vector;


/**
 *
 * This class represent a node of the BSN 
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class BsnNode {

	private short nodeID;
	private Hashtable sensor_Axis;
	private Vector availableFeatures;
	
	/**
	 * Constructor for a BSN Node object
	 * 
	 * @param nodeID the node id
	 * @param sensor_Axis the Hashtable contains the codes of the sensors of this node (as keys)
	 * 		  and their related number of axes (as values) 	 
	 * @see com.tilab.spine.constants.SensorCodes for details about the sensor and axes codes
	 *  
	 * @param availableFeatures the feature this node can compute
	 * @see com.tilab.spine.constants.FeatureCodes for details about the feature codes
	 */
	public BsnNode (short nodeID, Hashtable sensor_Axis, Vector availableFeatures) {
		this.nodeID = nodeID;
		this.sensor_Axis = sensor_Axis;
		this.availableFeatures = availableFeatures;
	}

	/**
	 * Getter method
	 * 
	 * @return the node ID
	 */
	public short getNodeID() {
		return nodeID;
	}

	/**
	 * Getter method
	 * 
	 * @return the sensor/sensorAxis list of this node
	 * @see com.tilab.spine.constants.SensorCodes for details about the sensor and axes codes
	 */
	public Hashtable getSensor_Axis() {
		return sensor_Axis;
	}

	/**
	 * Getter method
	 * 
	 * @return the available Features list of this node
	 * @see com.tilab.spine.constants.FeatureCodes for details about the feature codes
	 */
	public Vector getAvailableFeatures() {
		return availableFeatures;
	}
	
	/**
	 * Utility Method to compare two nodes by ID.
	 * 
	 * @return 0 if the comparing nodes have the same ID; 1 if the 'node2' has a lower ID; 
	 *         -1 if 'node2' has an higher ID
	 */
	public int compareTo(BsnNode node2) {
		if (this.nodeID < node2.getNodeID())
			return -1;
		else if (this.nodeID > node2.getNodeID())
			return 1;
		return 0;
	}
	
}
