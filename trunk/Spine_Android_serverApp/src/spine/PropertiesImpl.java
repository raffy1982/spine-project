/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
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


import jade.util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import spine.utils.ResourceManagerApplication;

import android.content.res.AssetManager;
import android.content.res.Resources;

/**
 * 
 * @see spine.Properties
 */
public class PropertiesImpl extends spine.Properties {	
	
	@SuppressWarnings("unused")
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
	
	public void load() throws IOException {
		if (!loaded) 			
			loadPropFile();		
	}
	
	private void loadPropFile() throws IOException {
		Resources resources = ResourceManagerApplication.getContext().getResources();
		AssetManager assetManager = resources.getAssets();

		try {
		    InputStream inputStream = assetManager.open(propertiesFileName);
		    p.load(inputStream);
		    loaded = true;
		} catch (IOException e) {
		    System.err.println("Failed to open "+propertiesFileName);
		    e.printStackTrace();
		}
	}
	
	public void store() {
		//TODO
	}
	
	public String getProperty(String key) {
		try {
			load();
		} catch (IOException e) {
			e.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
				SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
			return null;
		}
		return p.getProperty(key);
	}
	
	public void setProperty(String key, String value) {
		p.setProperty(key, value);
	}
	
	public Object remove(String key) {
		return p.remove(key);
	}	
}
