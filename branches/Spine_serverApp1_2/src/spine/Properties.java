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
*
*
* @author Raffaele Gravina
*
* @version 1.0
*/

package spine;

public abstract class Properties {
	
	public static final String BASE_STATION_PORT_KEY = "BS_PORT";
	public static final String BASE_STATION_SPEED_KEY = "BS_SPEED";
	public static final String URL_PREFIX_KEY = "url_prefix";
	public static final String GROUP_ID_KEY = "group_id";
	public static final String LINE_SEPARATOR_KEY = "line_separator";
	
	public static final String MESSAGE_CLASSNAME_KEY = "message_className";


	public static Properties getProperties() {
		return PropertiesImpl.getInstance();
	}
	
	
	public abstract void load();
	
	public abstract void store();
	
	public abstract String getProperty(String key);
	
	public abstract void setProperty(String key, String value);
	
	public abstract Object remove(String key);
}
