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

package com.tilab.spine.packets;

import java.util.Hashtable;
import java.util.Vector;


/**
 *
 * This class represent a well formatted AMP Service Advertisement Packet.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class ServiceAdvertisement {
	
	private Header header;
	
	private Hashtable sensor_Axis;
	
	private Vector avFeatures;
	
	
	/**
	 * Constructor for a Service Advertisement Packet
	 * 
	 * @param header the header of this packet
	 * @param sensor_Axis the Hashtable containing the sensors-axis codes of the sender
	 * @param avFeatures the Vector containing the features codes the sender can compute 
	 * 
	 * @see com.tilab.spine.constants.FeatureCodes for the standard feature coding
	 * @see com.tilab.spine.constants.SensorCodes for the standard sensor and axis coding
	 */
	public ServiceAdvertisement(Header header, Hashtable sensor_Axis, Vector avFeatures) {
		this.header = header;
		this.sensor_Axis = sensor_Axis;
		this.avFeatures = avFeatures;
	}
	
	/**
	 * Getter method
	 * 
	 * @return the AMP header
	 */
	public Header getHeader() {
		return header;
	}
	
	/**
	 * Setter method
	 * 
	 * @param header the AMP header
	 */
	public void setHeader(Header header) {
		this.header = header;
	}
	
	/**
	 * Getter method
	 * 
	 * @return the Hashtable containing the sensors-axis codes of the sender
	 */
	public Hashtable getSensor_Axis() {
		return sensor_Axis;
	}
	
	/**
	 * Setter method
	 * 
	 * @param sensor_Axis the Hashtable containing the sensors-axis codes of the sender
	 */
	public void setSensor_Axis(Hashtable sensor_Axis) {
		this.sensor_Axis = sensor_Axis;
	}
	
	/**
	 * Getter method
	 * 
	 * @return the Vector containing the features codes the sender can compute
	 */
	public Vector getAvFeatures() {
		return avFeatures;
	}
	
	/**
	 * Setter method
	 * 
	 * @param avFeatures the Hashtable containing the features codes the sender can compute
	 */
	public void setAvFeatures(Vector avFeatures) {
		this.avFeatures = avFeatures;
	}
	
}
