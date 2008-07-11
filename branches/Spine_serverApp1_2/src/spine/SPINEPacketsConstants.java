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

package spine;

public interface SPINEPacketsConstants {

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
	
}
