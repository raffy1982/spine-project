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

import spine.Properties;

public class Data {

	private final static String DATA_FUNCT_CLASSNAME_KEY_PREFIX = "data_function_className_";
	
	protected Object data = null; 
	
	protected byte functionCode = -1;
	
	protected Data() {}
			
	public Data(int nodeID, byte[] payload) {
		this.functionCode = payload[0];		
		
		try {
			Class c = Class.forName(Properties.getProperties().getProperty(DATA_FUNCT_CLASSNAME_KEY_PREFIX + this.functionCode));
			this.data = ((Data)c.newInstance()).decode(nodeID, payload);
		} catch (ClassNotFoundException e) { System.out.println(e); } 
		  catch (InstantiationException e) { System.out.println(e); } 
		  catch (IllegalAccessException e) { System.out.println(e);	}
	}
	
	protected Object decode(int nodeID, byte[] payload){ return null; }

	public byte getFunctionCode() {
		return this.functionCode;
	}
	
	public Object getData() {
		return this.data;
	}
	
	protected static int convertToInt(byte[] bytes, int index) {        
		return ( bytes[index + 3] & 0xFF) 		 |
	           ((bytes[index + 2] & 0xFF) << 8)  |
	           ((bytes[index + 1] & 0xFF) << 16) |
	           ((bytes[index] & 0xFF) << 24);
	}
	
	protected static int convertTwoBytesToInt(byte[] bytes, int index) {
		return   (bytes[index + 1] & 0xFF) |
		        ((bytes[index] & 0xFF) << 8);
	}
	
	public String toString() {
		return "" + this.data;
	}

}

