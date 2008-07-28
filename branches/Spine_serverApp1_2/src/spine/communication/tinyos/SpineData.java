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

package spine.communication.tinyos;

import spine.Properties;

public abstract class SpineData {

	private final static String SPINEDATA_FUNCT_CLASSNAME_KEY_PREFIX = "spineData_function_className_";
	
	protected static byte[] parse(byte[] payload) {
		byte functionCode = (byte)((payload[0] & 0xFF)>>3);
		
		try {
			Class c = Class.forName(Properties.getProperties().getProperty(SPINEDATA_FUNCT_CLASSNAME_KEY_PREFIX + functionCode));
			return ((SpineData)c.newInstance()).decode(payload);
		} catch (ClassNotFoundException e) { System.out.println(e); } 
		  catch (InstantiationException e) { System.out.println(e); } 
		  catch (IllegalAccessException e) { System.out.println(e);	}
		
		return null;  
	}
	
	protected abstract byte[] decode(byte[] payload);

}
