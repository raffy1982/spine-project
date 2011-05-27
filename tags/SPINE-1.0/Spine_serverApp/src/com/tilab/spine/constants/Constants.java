/*****************************************************************
SPINE - Signal Processing In-Note Environment is a framework that 
allows dynamic configuration of feature extraction capabilities 
of WSN nodes via an OtA protocol

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

package com.tilab.spine.constants;


/**
 *
 * This interface contains all the constants of the AMP 
 * (Activity Monitoring Feature Selection Protocol).
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public interface Constants {
	
	public final static short CURRENT_AMP_VERSION = 1;
	
	public final static short BASE_STATION_ADDRESS = 0;
	
	public final static short BROADCAST_ADDRESS = 15;
	
	public final static short MAX_ALLOWED_NODES = 14;
	
	public final static boolean STANDARD_PACKET = false;
	
	public final static boolean EXTENDED_PACKET = true;	

}