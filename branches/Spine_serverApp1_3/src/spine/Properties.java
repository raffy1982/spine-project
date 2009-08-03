/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
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

/**
* Abstract class for storing/retrieval of Properties.
* It has been introduced to allow the abstraction of properties storing/retrieval in J2SE and J2ME
*
* @author Raffaele Gravina
*
* @version 1.3
*/

package spine;

public abstract class Properties {
	
	public static final String MOTECOM_KEY = "MOTECOM";
	public static final String URL_PREFIX_KEY = "url_prefix";
	public static final String GROUP_ID_KEY = "group_id";
	public static final String LINE_SEPARATOR_KEY = "line_separator";
	
	public static final String MESSAGE_CLASSNAME_KEY = "message_className";
	public static final String LOCALNODEADAPTER_CLASSNAME_KEY = "LocalNodeAdapter_ClassName";
	
	public static final String SPINEDATACODEC_PACKAGE_SUFFIX_KEY = "SpineDataCodec_Package_Suffix";
	
	public static final String PROPERTIES_FILE_PATH_PROPERTYKEY = "spine_PropertiesImpl_configFileName";
	
	public static final String NODE_EMULATOR_KEY = "nodeEmulator";

	
	/**
	 * Returns an implementation of the Properties abstract class
	 * 
	 * @return an implementation of the Properties abstract class
	 */
	public static Properties getProperties() {
		return PropertiesImpl.getInstance();
	}
	
	/**
	 * Loads the properties set from a properties source (i.e, from a predefined .properties file or a Manifest)
	 *  
	 */
	public abstract void load();
	
	/**
	 * Stores (permanently if possible) a loaded properties set
	 * 
	 */
	public abstract void store();
	
	/**
	 * Returns the value of the given property key 'key'
	 * 
	 * @param key the property key to be returned
	 * @return the property value or null if the property 'key' doesn't exist
	 */
	public abstract String getProperty(String key);
	
	/**
	 * Sets the property key 'key' with the value 'value'
	 * 
	 * @param key the property key to be set
	 * @param value the property value
	 */
	public abstract void setProperty(String key, String value);
	
	/**
	 * Remove the given property key 'key' from the set
	 * 
	 * @param key the property key to be removed
	 * @return the value mapped to 'key'
	 */
	public abstract Object remove(String key);
}
