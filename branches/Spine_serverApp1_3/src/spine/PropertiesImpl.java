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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import spine.utils.JarUtils;

/**
 * 
 * @see spine.Properties
 */
public class PropertiesImpl extends spine.Properties {	
	
	public final static String DEFAULT_PROPERTIES_FILE_PATH = "defaults.properties";
	
	private final static String SPINE_JAR = "spine.jar";
	
	private final static String DEFAULT_COMMENT = "Created by the PropertiesImpl J2SE";
	
	
	private Properties p;
	
	private String propertiesFileName = null;
	
	private boolean loaded = false;
	
	
	PropertiesImpl() {
		this.propertiesFileName = DEFAULT_PROPERTIES_FILE_PATH;
		p = new Properties();				
	}
	
	PropertiesImpl(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
		p = new Properties();
	}
	
	
	public void load() {
		if (!loaded) {			
			if (!loadPropFile()) {	
				
				List l = new ArrayList();
				findFile(SPINE_JAR, new File("."), l);
				String spineJarFile = "";
				if(l.iterator().hasNext())
					spineJarFile = l.iterator().next().toString();
				byte[] res = JarUtils.getResource(spineJarFile, propertiesFileName);
				
				if (res != null && res.length != 0) {
					try {
						FileOutputStream fos = new FileOutputStream(propertiesFileName);
						fos.write(res);
						fos.flush();
						fos.close();
					} catch (FileNotFoundException e) {} 
					  catch (IOException e) {}
					
					loadPropFile();
					
					File f = new File(propertiesFileName);
					f.delete();
				}
			}			
		}
	}
	
	private boolean loadPropFile() {
		try {
			String fileName = System.getProperty(PROPERTIES_FILE_PATH_PROPERTYKEY);
			fileName = (fileName == null)? propertiesFileName : fileName;
			
			if (propertiesFileName.equalsIgnoreCase(DEFAULT_PROPERTIES_FILE_PATH)) {
				List l = new ArrayList();
				findFile(propertiesFileName, new File("."), l);
				if (l.iterator().hasNext())
					fileName = l.iterator().next().toString();
				else if (SPINEManager.SPINE_HOME != null) {
					if (SPINEManager.SPINE_HOME.endsWith("\\"))
						fileName = SPINEManager.SPINE_HOME + fileName;
					else
						fileName = SPINEManager.SPINE_HOME + "\\" + fileName;
				}
				
			}
			
			FileInputStream fis = new FileInputStream(fileName);
			p.load(fis);
			fis.close();
			loaded = true;
		} catch (FileNotFoundException e) {}
		  catch (IOException e) {}
		  
		return loaded;  
	}
	
	public void store() {
		try {
			FileOutputStream fos = new FileOutputStream(propertiesFileName);
			p.store(fos, DEFAULT_COMMENT);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
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
	
	
	private static void findFile(String fileToFind, File start, List r) {
		if (start.isDirectory()) {
			File[] files = start.listFiles();
			for (int i = 0; i < files.length; i++) 
				findFile(fileToFind, files[i], r);
		} 
		else if (start.getName().equalsIgnoreCase(fileToFind))
			r.add(start);
	}	
	
}
