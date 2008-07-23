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

public class SpineSetupFunction {
	
	private final static String SPINESETUPFUNCTION_CLASSNAME_KEY_PREFIX = "spineSetupFunction_function_className_";
	
	protected SpineSetupFunction() {}
	
	protected byte[] build(byte[] payload) throws UnknownFunctionException {
		byte functionCode = payload[0];
		
		try {
			Class c = Class.forName(Properties.getProperties().getProperty(SPINESETUPFUNCTION_CLASSNAME_KEY_PREFIX + functionCode));
			return ((SpineSetupFunction)c.newInstance()).build(payload);
		} 	catch (ClassNotFoundException e) { 
				throw new UnknownFunctionException("unknown function '" + functionCode + "' while trying a function (de)activation."); 
		  	}
			catch (NullPointerException e) { 
				throw new UnknownFunctionException("unknown function '" + functionCode + "' while trying a function (de)activation."); 
			} 
			catch (InstantiationException e) { System.out.println(e); } 
			catch (IllegalAccessException e) { System.out.println(e); }
		
		return null;
	}
	
}
