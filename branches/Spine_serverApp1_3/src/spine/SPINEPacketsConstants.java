/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
�
GNU Lesser General Public License
�
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
�
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the GNU
Lesser General Public License for more details.
�
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA� 02111-1307, USA.
*****************************************************************/

/**
 * This class contains codes related to the SPINE communication protocol, shared with the SPINE node side, 
 * such as the reserved network addresses and the packets types. 
 *  
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

package spine;

public class SPINEPacketsConstants {

	public static final short AM_SPINE = 0x99;            // every SPINE packets will have the same AM Type

	public static final int SPINE_BASE_STATION = 0x0000;  // reserved address: remote SPINE nodes can't be assigned with this address
	public static final int SPINE_BROADCAST = 0xFFFF;     // reserved address: any SPINE node can't be assigned with this address
	
	
	public static final byte SERVICE_ADV = 0x02;
	public static final byte DATA = 0x04;
	public static final byte SVC_MSG = 0x06;              // to notify the coordinator of events, errors, warnings and other internal information.

	public static final byte SERVICE_DISCOVERY = 0x01;
	public static final byte SETUP_SENSOR = 0x03;
	public static final byte SETUP_FUNCTION = 0x05;
	public static final byte START = 0x09;
	public static final byte RESET = 0x0B;                // simulate an hardware reset
	public static final byte SYNCR = 0x0D;                // it is used as a BEACON (re-sync) message
	public static final byte FUNCTION_REQ = 0x07;         // contains a flag to specify if enable or disable the function
	
	public static final String SERVICE_ADV_LABEL = "Svc Adv";
	public static final String DATA_LABEL = "Data";
	public static final String SVC_MSG_LABEL = "Svc Msg";             

	public static final String SERVICE_DISCOVERY_LABEL = "Svc Disc";
	public static final String SETUP_SENSOR_LABEL = "Stp Sens";
	public static final String SETUP_FUNCTION_LABEL = "Stp Funct";
	public static final String START_LABEL = "Start";
	public static final String RESET_LABEL = "Reset";               
	public static final String SYNCR_LABEL = "Syncr";               
	public static final String FUNCTION_REQ_LABEL = "Funct Req";        
	

	public static final byte CURRENT_SPINE_VERSION = 2;

	
	/**
	 *  Returns a human friendly label of the given packet type code
	 * 
	 * @param code the packetType code to convert into a human friendly label
	 * @return human friendly label of the given packetType code
	 */
	public static String packetTypeToString(byte code) {
		switch (code) {
			case SERVICE_ADV: return SERVICE_ADV_LABEL;
			case DATA: return DATA_LABEL;
			case SVC_MSG: return SVC_MSG_LABEL;
			
			case SERVICE_DISCOVERY: return SERVICE_DISCOVERY_LABEL;
			case SETUP_SENSOR: return SETUP_SENSOR_LABEL;
			case SETUP_FUNCTION: return SETUP_FUNCTION_LABEL;
			case START: return START_LABEL;
			case RESET: return RESET_LABEL;
			case SYNCR: return SYNCR_LABEL;
			case FUNCTION_REQ: return FUNCTION_REQ_LABEL;
			default: return "?";
		}
	}
	
}
