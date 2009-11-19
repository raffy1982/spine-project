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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.Â  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MAÂ  02111-1307, USA.
*****************************************************************/
package spine;

/**
 * This class is responsible for creating and configuring the SPINEManager
 * 
 * @author Fabio Bellifemine, Telecom Italia
 * @since 1.3
 */
public class SPINEFactory {


		   private static SPINEFactory instance = new SPINEFactory();

		   private SPINEManager managerInstance;
		   


		   /** private constructor to prevent the SPINEFactory class from being instantiated. **/
		   private SPINEFactory(){
		   }

		   /** returns an instance of the SPINEFactory **/
		   public static SPINEFactory getInstance(){
		  	 return instance;  
		   }
		    
		   /** returns the SPINEManager.
		    * Notice that before getting the SPINEManager, it must have been already initialized via the init method call.
		    * @see #init()
		    * @throws InstantiationException if the SPINEManager has not yet been initialized. **/
		   public SPINEManager getSPINEManager() throws InstantiationException {
			   if (managerInstance != null)
				   return managerInstance;
			   else
				   throw new InstantiationException("SPINEManager not yet initialized, call the SPINEFactory.init method");
		   }
		    

		   /** Initializes the SPINE Manager.
		    * The SPINEManager instance is connected to the base-station and platform
		    * obtained transparently from the app.properties file
		    * 
		    * @param appPropertiesFile the application properties file 
		    * where at least the 'MOTECOM' and 'PLATFORM' variables are defined
		    * 
		    * @see spine.SPINESupportedPlatforms
		    * @param appPropertiesFile
		    * @throws InstantiationException if the SPINEManager has already been initialized or MOTECOM and PLATFORM 
		    * variables have not been defined. 
		    **/
		   public void init(String appPropertiesFile) throws InstantiationException{
			   if (managerInstance != null) 
				   throw new InstantiationException("SPINEManager already initialized"); 
			   else {
						Properties appProp = Properties.getProperties(appPropertiesFile);	
						
						String MOTECOM = System.getProperty(Properties.MOTECOM_KEY);
						MOTECOM = (MOTECOM!=null)? MOTECOM : appProp.getProperty(Properties.MOTECOM_KEY);
						
						String PLATFORM = System.getProperty(Properties.PLATFORM_KEY);
						PLATFORM = (PLATFORM!=null)? PLATFORM : appProp.getProperty(Properties.PLATFORM_KEY);
						
						if (MOTECOM == null || PLATFORM == null)
							   throw new InstantiationException("MOTECOM and PLATFORM variables have not been defined"); 
						managerInstance = new SPINEManager(MOTECOM, PLATFORM, new String[]{MOTECOM});
					}
	
		   }



}
