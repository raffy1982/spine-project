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
* Implementation of the Properties class for J2SE. 
* It is used to store and load permanent parameters and other variable configurations. 
* 
* Note that this class is only used internally at the framework. 
*
* @author Raffaele Gravina
* @author Antonio Guerrieri
*
* @version 1.3
*/

package spine;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * @see spine.Properties
 */
public class PropertiesImpl extends spine.Properties {	
	
	public final static String DEFAULT_PROPERTIES_FILE = "resources/defaults.properties";
	
	private final static String DEFAULT_COMMENT = "Created by the PropertiesImpl J2SE";
	
	
	private Properties p;
	
	private String propertiesFileName = null;
	
	private boolean loaded = false;
	
	
	
	PropertiesImpl() {
		this.propertiesFileName = DEFAULT_PROPERTIES_FILE;
		p = new Properties();				
	}
	
	PropertiesImpl(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
		p = new Properties();
	}
	
	
	public void load() {
		if (!loaded) 			
			loadPropFile();		
	}
	
	private void loadPropFile() {
		try {
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(propertiesFileName); 
			if (is == null)
				is = new FileInputStream(propertiesFileName);
			p.load(is);
			loaded = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void store() {
		try {
			FileOutputStream fos = new FileOutputStream(propertiesFileName);
			p.store(fos, DEFAULT_COMMENT);
			fos.close();
		} catch (IOException e) {
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
				SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
		}
	}
	
	public String getProperty(String key) {
		load();
		return p.getProperty(key);
	}
	
	public void setProperty(String key, String value) {
		p.setProperty(key, value);
	}
	
	public Object remove(String key) {
		return p.remove(key);
	}	
}
