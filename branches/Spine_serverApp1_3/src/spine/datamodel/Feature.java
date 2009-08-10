/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

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
 *
 *  This class represents the Feature entity.
 *  It contains a constructor, a toString and getters methods.
 *
 * @author Raffaele Gravina
 * @author Alessia Salmeri
 *
 * @version 1.3
 */

package spine.datamodel;

import java.io.Serializable;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;

public class Feature implements Comparable, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Node node;
	
	/**
	 * @deprecated  
	 */
	private int nodeID;
	
	private byte functionCode;
	private byte featureCode;
	
	private byte sensorCode;
	private byte channelBitmask;
	
	private int ch1Value;
	private int ch2Value;
	private int ch3Value;
	private int ch4Value;
	
	/**
	 * Default Constructor of a Feature object.
	 *  
	 */
	public Feature() {
		this.functionCode = SPINEFunctionConstants.FEATURE;
	}
	
	/**
	 * Constructor of a Feature object. 
	 * This is used only for convenience when (de)activating features 
	 * through the FeatureSpineFunctionReq class
	 * 
	 * @param featureCode the code of the feature
	 * @param channelBitmask the channels bitmask specifying 
	 * 
	 * @see spine.SPINEFunctionConstants
	 * @see spine.SPINESensorConstants
	 */
	public Feature(byte featureCode, byte channelBitmask) { 
		this.functionCode = SPINEFunctionConstants.FEATURE;
		this.featureCode = featureCode;
		this.channelBitmask = channelBitmask;
	}
	
	/**
	 * Constructor of a Feature object.
	 * This is used by the lower level components of the framework for creating Feature objects
	 * from a low level Feature data packet received by remote nodes. 
	 * 
	 * @param nodeID the node id
	 * @param functionCode the function code 
	 * @param featureCode the feature code
	 * @param sensorCode the sensor code
	 * @param channelBitmask the sensor channels bitmask
	 * @param ch1Value the first feature channel value
	 * @param ch2Value the first feature channel value
	 * @param ch3Value the first feature channel value
	 * @param ch4Value the first feature channel value
	 * 
	 * @see spine.SPINEFunctionConstants
	 * @see spine.SPINESensorConstants	
	 * 
	 *  @deprecated
	 */
	public Feature(int nodeID, byte functionCode, byte featureCode, byte sensorCode, byte channelBitmask, int ch1Value, int ch2Value, int ch3Value, int ch4Value) {
		
		this.nodeID = nodeID;

		this.functionCode = functionCode;
		this.featureCode = featureCode;
		
		this.sensorCode = sensorCode;
		this.channelBitmask = channelBitmask;
		
		this.ch1Value = ch1Value;
		this.ch2Value = ch2Value;
		this.ch3Value = ch3Value;
		this.ch4Value = ch4Value;
	}
	
	/**
	 * Constructor of a Feature object.
	 * This is used by the lower level components of the framework for creating Feature objects
	 * from a low level Feature data packet received by remote nodes. 
	 * 
	 * @param nodeID the node id
	 * @param functionCode the function code 
	 * @param featureCode the feature code
	 * @param sensorCode the sensor code
	 * @param channelBitmask the sensor channels bitmask
	 * @param ch1Value the first feature channel value
	 * @param ch2Value the first feature channel value
	 * @param ch3Value the first feature channel value
	 * @param ch4Value the first feature channel value
	 * 
	 * @see spine.SPINEFunctionConstants
	 * @see spine.SPINESensorConstants
	 */
	public Feature(Node node, byte functionCode, byte featureCode, byte sensorCode, byte channelBitmask, int ch1Value, int ch2Value, int ch3Value, int ch4Value) {
		
		this.node = node;
		
		this.nodeID = node.getPhysicalID().getAsInt();

		this.functionCode = functionCode;
		this.featureCode = featureCode;
		
		this.sensorCode = sensorCode;
		this.channelBitmask = channelBitmask;
		
		this.ch1Value = ch1Value;
		this.ch2Value = ch2Value;
		this.ch3Value = ch3Value;
		this.ch4Value = ch4Value;
	}
	
	/**
	 * Getter method of the node id
	 * @return the node id
	 * 
	 * @deprecated
	 */
	public int getNodeID() {
		return nodeID;
	}
	
	/**
	 * Getter method of the node issuing this Feature 
	 * @return the node  
	 */
	public Node getNode() {
		return this.node;
	}
	
	/**
	 * Getter method of the node id
	 * @return the node id
	 */
	public byte getFunctionCode() {
		return functionCode;
	}
		
	/**
	 * Getter method of the feature code
	 * @return the feature code
	 */
	public byte getFeatureCode() {
		return featureCode;
	}	

	/**
	 * Getter method of the sensor code
	 * @return the sensor code
	 */
	public byte getSensorCode() {
		return sensorCode;
	}

	/**
	 * Getter method of the sensor channels bitmask
	 * @return the sensor channels bitmask
	 */
	public byte getChannelBitmask() {
		return channelBitmask;
	}

	/**
	 * Getter method of the value of the first feature channel
	 * @return the value of the first feature channel
	 */
	public int getCh1Value() {
		return ch1Value;
	}

	/**
	 * Getter method of the value of the second feature channel
	 * @return the value of the second feature channel
	 */
	public int getCh2Value() {
		return ch2Value;
	}

	/**
	 * Getter method of the value of the third feature channel
	 * @return the value of the third feature channel
	 */
	public int getCh3Value() {
		return ch3Value;
	}
	
	/**
	 * Getter method of the value of the fourth feature channel
	 * @return the value of the fourth feature channel
	 */
	public int getCh4Value() {
		return ch4Value;
	}
	
	/**
	 * 
	 * Setter method of the node id
	 * 
	 * @deprecated
	 */
	public void setNodeId(int nodeId) {
		this.nodeID = nodeId;		
	}
	
	/**
	 * 
	 * Setter method of the node 
	 * 
	 */
	public void setNode(Node node) {
		this.node = node;	
		this.nodeID = node.getPhysicalID().getAsInt();
	}

	/**
	 * 
	 * Setter method of the feature code
	 * 
	 */
	public void setFeatureCode(byte featureCode) {
		this.featureCode = featureCode;	
	}

	/**
	 * 
	 * Setter method of the sensor code
	 * 
	 */
	public void setSensorCode(byte sensorCode) {
		this.sensorCode = sensorCode;			
	}

	/**
	 * 
	 * Add the given channel to the channel bitmask.
	 * Available channels are CH1, CH2, CH3, CH4
	 * 
	 * @see spine.SPINESensorConstants
	 * 
	 */
	public void addChannelToBitmask(byte channel) {
		switch(channel) {
			case SPINESensorConstants.CH1: this.channelBitmask |= SPINESensorConstants.CH1_ONLY; break; 
			case SPINESensorConstants.CH2: this.channelBitmask |= SPINESensorConstants.CH2_ONLY; break;
			case SPINESensorConstants.CH3: this.channelBitmask |= SPINESensorConstants.CH3_ONLY; break;
			case SPINESensorConstants.CH4: this.channelBitmask |= SPINESensorConstants.CH4_ONLY; break;
		}		
	}
	
	/**
	 * 
	 * Returns a string representation of the Feature object.
	 * 
	 */
	public String toString() {
		return "From node: {" + this.node.toShortString() + "} - " + SPINEFunctionConstants.FEATURE_LABEL + ": " + SPINEFunctionConstants.functionalityCodeToString(this.functionCode, this.featureCode) + 
				" on " + SPINESensorConstants.sensorCodeToString(this.sensorCode) + 
				" (now on " + SPINESensorConstants.channelBitmaskToString(this.channelBitmask) + ") " + 
				" - " + SPINESensorConstants.CH1_LABEL + ": "+ this.ch1Value + 
				"; " + SPINESensorConstants.CH2_LABEL + ": "+ this.ch2Value + 
				"; " + SPINESensorConstants.CH3_LABEL + ": "+ this.ch3Value + 
				"; " + SPINESensorConstants.CH4_LABEL + ": "+ this.ch4Value;
	}

	/**
	 * @param nodeID the nodeID to set
	 * 
	 * @deprecated
	 */
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	/**
	 * @param functionCode the functionCode to set
	 */
	public void setFunctionCode(byte functionCode) {
		this.functionCode = functionCode;
	}

	/**
	 * @param channelBitmask the channelBitmask to set
	 */
	public void setChannelBitmask(byte channelBitmask) {
		this.channelBitmask = channelBitmask;
	}

	/**
	 * @param ch1Value the ch1Value to set
	 */
	public void setCh1Value(int ch1Value) {
		this.ch1Value = ch1Value;
	}

	/**
	 * @param ch2Value the ch2Value to set
	 */
	public void setCh2Value(int ch2Value) {
		this.ch2Value = ch2Value;
	}

	/**
	 * @param ch3Value the ch3Value to set
	 */
	public void setCh3Value(int ch3Value) {
		this.ch3Value = ch3Value;
	}

	/**
	 * @param ch4Value the ch4Value to set
	 */
	public void setCh4Value(int ch4Value) {
		this.ch4Value = ch4Value;
	}

	public int compareTo(Object o) {
		Feature f = (Feature)o;
		
		if (this.node.getPhysicalID().getAsInt() < f.node.getPhysicalID().getAsInt()) return -1;
		if (this.node.getPhysicalID().getAsInt() > f.node.getPhysicalID().getAsInt()) return 1;
		if (this.node.getPhysicalID().getAsInt() == f.node.getPhysicalID().getAsInt()) {
			if (this.sensorCode < f.sensorCode) return -1;
			if (this.sensorCode > f.sensorCode) return 1;
			if (this.sensorCode == f.sensorCode) {
				if (this.featureCode < f.featureCode) return -1;
				if (this.featureCode > f.featureCode) return 1;
				if (this.featureCode == f.featureCode) {
					if (this.channelBitmask < f.channelBitmask) return -1;
					if (this.channelBitmask > f.channelBitmask) return 1;
					if (this.channelBitmask == f.channelBitmask) return 0;
				}
			}
		}
		
		return 0;
	}
	
	public Object clone() {
		Feature clone = new Feature(this.node, this.functionCode, this.featureCode, this.sensorCode, this.channelBitmask, 
									this.ch1Value, this.ch2Value, this.ch3Value, this.ch4Value);
		return clone;
	}
	
}