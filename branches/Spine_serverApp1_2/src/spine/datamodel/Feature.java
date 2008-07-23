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
 *  
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

package spine.datamodel;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;

public class Feature {
	
	private int nodeID;
	
	private byte functionCode;
	private byte featureCode;
	
	private byte sensorCode;
	private byte channelBitmask;
	
	private int ch1Value;
	private int ch2Value;
	private int ch3Value;
	private int ch4Value;
	
	
	protected Feature(int nodeID, byte functionCode, byte featureCode, byte sensorCode, byte channelBitmask, int ch1Value, int ch2Value, int ch3Value, int ch4Value) {
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
	

	public byte getFeatureCode() {
		return featureCode;
	}
	
	public int getNodeID() {
		return nodeID;
	}

	public byte getSensorCode() {
		return sensorCode;
	}

	public byte getChannelBitmask() {
		return channelBitmask;
	}

	public int getCh1Value() {
		return ch1Value;
	}

	public int getCh2Value() {
		return ch2Value;
	}

	public int getCh3Value() {
		return ch3Value;
	}
	
	public int getCh4Value() {
		return ch4Value;
	}
	
	public String toString() {
		return "From node: " + this.nodeID + " - " + SPINEFunctionConstants.FEATURE_LABEL + ": " + SPINEFunctionConstants.functionalityCodeToString(this.functionCode, this.featureCode) + 
				" on " + SPINESensorConstants.sensorCodeToString(this.sensorCode) + 
				" (now on " + SPINESensorConstants.valueTypesBitmaskToString(this.channelBitmask) + ") " + 
				" - " + SPINESensorConstants.CH1_LABEL + ": "+ this.ch1Value + 
				"; " + SPINESensorConstants.CH2_LABEL + ": "+ this.ch2Value + 
				"; " + SPINESensorConstants.CH3_LABEL + ": "+ this.ch3Value + 
				"; " + SPINESensorConstants.CH4_LABEL + ": "+ this.ch4Value;
	}


	public byte getFunctionCode() {
		return functionCode;
	}
}