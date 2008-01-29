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

package com.tilab.spine.constants;


/**
 *
 * This interface contains the packets code and size of AMP 
 * (Activity Monitoring Features Selection Protocol)
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public interface PacketConstants {
	// packets code types
	public final static short SERVICE_DISCOVERY_PKT = 0x1;

	public final static short FEATURE_ACTIVATION_PKT = 0x2;

	public final static short REMOVE_FEATURE_PKT = 0x3;

	public final static short BATTERY_INFO_REQUEST_PKT = 0x4;


	public final static short SERVICE_ADVERTISEMENT_PKT = 0xc;

	public final static short DATA_PKT = 0xd;

	public final static short BATTERY_INFO_PKT = 0xe;

	public final static short SERVICE_MESSAGE_PKT = 0xf;
	
	
	// packets names
	public final static String SERVICE_DISCOVERY_PKT_CAPTION = "Service Discovery";

	public final static String FEATURE_ACTIVATION_PKT_CAPTION = "Feature Activation";

	public final static String REMOVE_FEATURE_PKT_CAPTION = "Remove Feature";

	public final static String BATTERY_INFO_REQUEST_PKT_CAPTION = "Battery Info Request";


	public final static String SERVICE_ADVERTISEMENT_PKT_CAPTION = "Service Advertisement";

	public final static String DATA_PKT_CAPTION = "Data";

	public final static String BATTERY_INFO_PKT_CAPTION = "Battery Info";

	public final static String SERVICE_MESSAGE_PKT_CAPTION = "Service Message";
	  
	  
	  // packets sizes
	public final static byte PKT_HEADER_SIZE = 4;

	public final static byte SERVICE_DISCOVERY_PKT_SIZE = 1 + PKT_HEADER_SIZE;

	public final static byte FEATURE_ACTIVATION_PKT_SIZE = 6 + PKT_HEADER_SIZE;

	public final static byte REMOVE_FEATURE_PKT_SIZE = 2 + PKT_HEADER_SIZE;

	public final static byte BATTERY_INFO_REQUEST_PKT_SIZE = 2 + PKT_HEADER_SIZE;


	public final static byte SERVICE_ADVERTISEMENT_PKT_SIZE = 22 + PKT_HEADER_SIZE;

	public final static byte DATA_PKT_SIZE = 8 + PKT_HEADER_SIZE;

	public final static byte BATTERY_INFO_PKT_SIZE = 2 + PKT_HEADER_SIZE;

	public final static byte SERVICE_MESSAGE_PKT_SIZE = 1 + PKT_HEADER_SIZE;
}
