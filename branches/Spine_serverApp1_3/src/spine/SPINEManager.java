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
 * This is the SPINE API implementation.
 * It is responsible for converting high level application requests into SPINE protocol messages; 
 * it also handles lower level network activity generating higher level events. 
 *
 * @author Raffaele Gravina
 * @author Alessia Salmeri
 *
 * @version 1.3
 */

package spine;

import spine.datamodel.functions.Exception.*;

import java.io.InterruptedIOException;
import java.util.Vector;
import java.util.Hashtable;

import com.tilab.gal.LocalNodeAdapter;
import com.tilab.gal.WSNConnection;

import spine.SPINEPacketsConstants;

import spine.datamodel.Address;
import spine.datamodel.Data;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;


import spine.datamodel.functions.*;
import spine.datamodel.serviceMessages.ServiceErrorMessage;
import spine.datamodel.serviceMessages.ServiceWarningMessage;



public class SPINEManager {
	
	private static Properties prop = Properties.getDefaultProperties();
	
	private static final String DEF_PROP_MISSING_MSG = 
		"ERROR: unable to load 'defaults.properties' file.";
	
	private static final String APP_PROP_MISSING_MSG = 
		"ERROR: 'app.properties' file is missing, not properly specified or 'MOTECOM' and/or 'PLATFORM' properties not defined!";
	
	private final static long DISCOVERY_TIMEOUT = 2000;	
	
	private static final byte DISC_COMPL_EVT_COD = 100;
	
		
	private Vector listeners = new Vector(); // <values:SPINEListener>
	
	private Vector activeNodes = new Vector(); // <values:Node>
	private boolean discoveryCompleted = false;
	private long discoveryTimeout = DISCOVERY_TIMEOUT;
	
	private boolean started = false;
	
	private WSNConnection connection;
	private LocalNodeAdapter nodeAdapter;	
	
	public static String MOTECOM = "";
	public static String PLATFORM = "";
	
	private static SPINEManager instance;
	
	// Hash Table class instance
	private Hashtable htInstance = new Hashtable();
	
	
	private SpineCodec spineCodec = null;
	//private SpineServiceAdvertisement spineServiceAdvertisement  = null;
	private com.tilab.gal.Message msg;
	
	private static final String SPINEDATA_FUNCT_CLASSNAME_SUFFIX = "SpineData";
	private static final String SPINE_SERVICE_MESSAGE_CLASSNAME_SUFFIX = "Message";
	
	private static byte MY_GROUP_ID = 0;
	private static String LOCALNODEADAPTER_CLASSNAME = null;
	private static String URL_PREFIX = null;
	private static final String SPINEDATACODEC_PACKAGE_PREFIX = "spine.payload.codec.";
	private static final String SPINE_SERVICE_MESSAGE_CODEC_PACKAGE_PREFIX = "spine.payload.codec.";
	private static String SPINEDATACODEC_PACKAGE = null;	
	private static String MESSAGE_CLASSNAME = null;	
	private static String SPINE_SERVICE_MESSAGE_CODEC_PACKAGE = null;
	
	private Node baseStation = null;
	
	private SPINEManager(String[] args) {
		try {
			
			MY_GROUP_ID = (byte)Short.parseShort(prop.getProperty(Properties.GROUP_ID_KEY), 16);
			LOCALNODEADAPTER_CLASSNAME = prop.getProperty(PLATFORM + "_" + Properties.LOCALNODEADAPTER_CLASSNAME_KEY);
			URL_PREFIX = prop.getProperty(PLATFORM + "_" + Properties.URL_PREFIX_KEY);
			SPINEDATACODEC_PACKAGE = SPINEDATACODEC_PACKAGE_PREFIX + 
									prop.getProperty(PLATFORM + "_" + Properties.SPINEDATACODEC_PACKAGE_SUFFIX_KEY) + ".";
			MESSAGE_CLASSNAME = prop.getProperty(PLATFORM + "_" + Properties.MESSAGE_CLASSNAME_KEY);
			SPINE_SERVICE_MESSAGE_CODEC_PACKAGE = SPINE_SERVICE_MESSAGE_CODEC_PACKAGE_PREFIX + 
													prop.getProperty(PLATFORM + "_" + Properties.SPINEDATACODEC_PACKAGE_SUFFIX_KEY) + ".";
			
			System.setProperty(Properties.LOCALNODEADAPTER_CLASSNAME_KEY, LOCALNODEADAPTER_CLASSNAME);
			nodeAdapter = LocalNodeAdapter.getLocalNodeAdapter();	

			Vector params = new Vector();
			for (int i = 0; i < args.length; i++) {
				params.addElement(args[i]);
			}
			nodeAdapter.init(params);

			nodeAdapter.start();

			connection = nodeAdapter.createAPSConnection();			

			connection.setListener(new WSNConnectionListenerImpl());
			
			baseStation = new Node(new Address(""+SPINEPacketsConstants.SPINE_BASE_STATION));
			baseStation.setLogicalID(new Address(SPINEPacketsConstants.SPINE_BASE_STATION_LABEL));

		} catch (NumberFormatException e) {
			exit(DEF_PROP_MISSING_MSG);
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (InstantiationException e) {
			System.out.println(e);
		} catch (IllegalAccessException e) {
			System.out.println(e);
		} 
	}

	/**
	 * Returns the SPINEManager instance connected to the given base-station
	 * Those parameters should be retrieved using the Properties instance 
	 * obtained thru the static SPINEManager.getProperties method
	 * 
	 * @param args an array of Strings used to configure the selected LocalNodeAdapter
	 * 
	 * @return the SPINEManager instance
	 * 
	 * @see spine.Properties
	 * 
	 * @deprecated
	 */
	public static SPINEManager getInstance(String[] args) {
		if (instance == null) 
			instance = new SPINEManager(args);
		return instance;
	}

	/**
	 * Returns the SPINEManager instance connected to the base-station and platform
	 * obtained transparently from the app.properties file
	 * 
	 * @param appPropertiesFile the application properties file 
	 * where at least the 'MOTECOM' and 'PLATFORM' variables are defined
	 * 
	 * @return the SPINEManager instance
	 * 
	 * @see spine.SPINESupportedPlatforms
	 */
	public static SPINEManager getInstance(String appPropertiesFile) {
		if (instance == null) {
			Properties appProp = Properties.getProperties(appPropertiesFile);	
			
			String mCom = System.getProperty(Properties.MOTECOM_KEY);
			MOTECOM = (mCom!=null)? mCom : appProp.getProperty(Properties.MOTECOM_KEY);
			
			String pltf = System.getProperty(Properties.PLATFORM_KEY);
			PLATFORM = (pltf!=null)? pltf : appProp.getProperty(Properties.PLATFORM_KEY);
			
			if (MOTECOM == null || PLATFORM == null)
				exit(APP_PROP_MISSING_MSG);
			else
				instance = new SPINEManager(new String[]{MOTECOM});
		}
		return instance;
	}
		
	/**
	 * Registers a SPINEListener to the manager instance
	 * 
	 * @param listener the listener to register
	 */
	/**
	 *@deprecated Please now use addListener(SPINEListener)
	 *@see #addListener(SPINEListener)
	 */
	public void registerListener(SPINEListener listener) {
		this.listeners.addElement(listener);
	}

	
	/**
	 * Registers a SPINEListener to the manager instance
	 * 
	 * @param listener the listener to register
	 */
	public void addListener(SPINEListener listener) {
		this.listeners.addElement(listener);
	}
	
	
	/**
	 * Deregisters a SPINEListener to the manager instance
	 * 
	 * @param listener the listener to deregister
	 */
	/**
	 *@deprecated Please now use removeListener(SPINEListener)
	 *@see #removeListener(SPINEListener)
	 */
	public void deregisterListener(SPINEListener listener) {
		this.listeners.removeElement(listener);
	}
	
	
	/**
	 * Deregisters a SPINEListener to the manager instance
	 * 
	 * @param listener the listener to deregister
	 */
	public void removeListener(SPINEListener listener) {
		this.listeners.removeElement(listener);
	}
	

	/**
	 * This method sets the timeout for the discovery procedure.
	 * 
	 * This method has effect only if used before the 'discoveryWsn()'; if not used, a default timeout of 0.5s, is used.
	 * 
	 * A timeout <= 0 will disable the Discovery Timer; 
	 * this way a 'discovery complete' event will never be signaled and at any time an 
	 * announcing node is added to the active-nodes list and signaled to the SPINE listeners. 
	 * 
	 * @param discoveryTimeout the timeout for the discovery procedure
	 */	
	/**
	 *@deprecated Please now use discoveryWsn(long)
	 *@see #discoveryWsn(long)
	 */
	public void setDiscoveryProcedureTimeout(long discoveryTimeout) {
		this.discoveryTimeout = discoveryTimeout;
	}
	
	
	/**
	 * Returns an instance of a Properties implementation class which can be queried 
	 * for retrieving system and framework properties and parameters 
	 * 
	 * @return the Properties instance
	 * 
	 * @deprecated
	 */
	public static Properties getProperties() {
		return prop;
	}
	
	
	/**
	 * Returns the list of the discovered nodes as a Vector of spine.datamodel.Node objects
	 * 
	 * @return the discovered nodes
	 * 
	 * @see spine.datamodel.Node
	 */
	public Vector getActiveNodes() {
		return activeNodes;
	}
	
	
	/**
	 * Returns true if the manager has been asked to start the processing in the wsn; false otherwise
	 * 
	 * @return true if the manager has been asked to start the processing in the wsn; false otherwise  
	 */
	/**
	 *@deprecated Please now use isStarted()
	 *@see #isStarted()
	 */
	public boolean started() {
		return this.started;
	}
	
	
	/**
	 * Returns true if the manager has been asked to start the processing in the wsn; false otherwise
	 * 
	 * @return true if the manager has been asked to start the processing in the wsn; false otherwise  
	 */
	public boolean isStarted() {
		return this.started;
	}
	
	
	/**
	 * Commands the SPINEManager to discovery the surrounding WSN nodes
	 * A default timeout of 0.5s, is used
	 */
	public void discoveryWsn() {		
		send(new Address(""+SPINEPacketsConstants.SPINE_BROADCAST), SPINEPacketsConstants.SERVICE_DISCOVERY, null);
		
		if(this.discoveryTimeout > 0)
			new DiscoveryTimer(this.discoveryTimeout).start();
	}
	

	/**
	 * 
	 * Commands the SPINEManager to discovery the surrounding WSN nodes
	 * This method sets the timeout for the discovery procedure.
	 * 
	 * A timeout <= 0 will disable the Discovery Timer; 
	 * this way a 'discovery complete' event will never be signaled and at any time an 
	 * announcing node is added to the active-nodes list and signaled to the SPINE listeners. 
	 * 
	 * @param discoveryTimeout the timeout for the discovery procedure
	 */
	public void discoveryWsn(long discoveryTimeout) {
		this.discoveryTimeout = discoveryTimeout;
		send(new Address(""+SPINEPacketsConstants.SPINE_BROADCAST), SPINEPacketsConstants.SERVICE_DISCOVERY, null);
		
		if(this.discoveryTimeout > 0)
			new DiscoveryTimer(this.discoveryTimeout).start();
	}
	

	/**
	 * 
	 * Currently, it does nothing!
	 * 
	 */
	public void bootUpWsn() {			
	}

	
	/**
	 * Setups a specific sensor of the given node.
	 * Currently, a sensor is setup by providing a sampling time value and a time scale factor 
	 * 
	 * @param nodeID the destination of the request
	 * @param setupSensor the object containing the setup parameters
	 */
	/**
	 *@deprecated Please now use setup(Node, SpineSetupSensor)
	 *@see #setup(Node, SpineSetupSensor)
	 */
	public void setupSensor(int nodeID, SpineSetupSensor setupSensor) {
		send(new Address(""+nodeID), SPINEPacketsConstants.SETUP_SENSOR, setupSensor);
	}


	/**
	 * Setups a specific sensor of the given node.
	 * Currently, a sensor is setup by providing a sampling time value and a time scale factor 
	 * 
	 * @param node the destination of the request
	 * @param setupSensor the object containing the setup parameters
	 */
	public void setup(Node node, SpineSetupSensor setupSensor) {
		if (node == null) 
			throw new RuntimeException("Can't setup the sensor: node is null");
		if (setupSensor == null)
			throw new RuntimeException("Can't setup the sensor: setupSensor is null");
		
		send(node.getPhysicalID(), SPINEPacketsConstants.SETUP_SENSOR, setupSensor);
	}

	
	/**
	 * Setups a specific function of the given node.
	 * The parameters involved are 'function dependent' and are specified by providing a proper
	 * SpineSetupFunction instantiation object
	 * 
	 * @param nodeID the destination of the request
	 * @param setupFunction the object containing the setup parameters
	 */
	/**
	 *@deprecated Please now use setup(Node, SpineSetupFunction)
	 *@see #setup(Node, SpineSetupFunction)
	 */
	public void setupFunction(int nodeID, SpineSetupFunction setupFunction) {
		send(new Address(""+nodeID), SPINEPacketsConstants.SETUP_FUNCTION, setupFunction);
	}


	/**
	 * Setups a specific function of the given node.
	 * The parameters involved are 'function dependent' and are specified by providing a proper
	 * SpineSetupFunction instantiation object
	 * 
	 * @param node the destination of the request
	 * @param setupFunction the object containing the setup parameters
	 */
	public void setup(Node node, SpineSetupFunction setupFunction) {
		if (node == null) 
			throw new RuntimeException("Can't setup the function: node is null");
		if (setupFunction == null)
			throw new RuntimeException("Can't setup the function: setupFunction is null");
		
		send(node.getPhysicalID(), SPINEPacketsConstants.SETUP_FUNCTION, setupFunction);
	}
	
		
	/**
	 * Activates a function (or even only function sub-routines) on the given sensor.
	 * The content of the actual request is 'function dependent' and it's embedded into the
	 * 'SpineFunctionReq' instantiations.
	 * 
	 * @param nodeID the destination of the request
	 * @param functionReq the specific function activation request
	 */
	/**
	 *@deprecated Please now use activate(Node, SpineFunctionReq)
	 *@see #activate(Node, SpineFunctionReq)
	 */
	public void activateFunction(int nodeID, SpineFunctionReq functionReq) {
		// function activation requests are differentiated by the deactivation requests by setting the appropriate flag 
		functionReq.setActivationFlag(true);

		send(new Address(""+nodeID), SPINEPacketsConstants.FUNCTION_REQ, functionReq);
	}
	
	
	/**
	 * Activates a function (or even only function sub-routines) on the given sensor.
	 * The content of the actual request is 'function dependent' and it's embedded into the
	 * 'SpineFunctionReq' instantiations.
	 * 
	 * @param node the destination of the request
	 * @param functionReq the specific function activation request
	 */
	public void activate(Node node, SpineFunctionReq functionReq) {
		if (node == null) 
			throw new RuntimeException("Can't activate the function: node is null");
		if (functionReq == null)
			throw new RuntimeException("Can't activate the function: functionReq is null");
		
		// function activation requests are differentiated by the deactivation requests by setting the appropriate flag 
		functionReq.setActivationFlag(true);

		send(node.getPhysicalID(), SPINEPacketsConstants.FUNCTION_REQ, functionReq);
	}
	
	
	/**
	 * Deactivates a function (or even only function sub-routines) on the given sensor.
	 * The content of the actual request is 'function dependent' and it's embedded into the
	 * 'SpineFunctionReq' instantiations.
	 * 
	 * @param nodeID the destination of the request
	 * @param functionReq the specific function deactivation request
	 */
	/**
	 *@deprecated Please now use deactivate(Node, SpineFunctionReq)
	 *@see #deactivate(Node, SpineFunctionReq)
	 */
	public void deactivateFunction(int nodeID, SpineFunctionReq functionReq) {
		// function activation requests are differentiated by the deactivation requests by setting the appropriate flag
		functionReq.setActivationFlag(false);

		send(new Address(""+nodeID), SPINEPacketsConstants.FUNCTION_REQ, functionReq);
	}
	
	
	/**
	 * Deactivates a function (or even only function sub-routines) on the given sensor.
	 * The content of the actual request is 'function dependent' and it's embedded into the
	 * 'SpineFunctionReq' instantiations.
	 * 
	 * @param node the destination of the request
	 * @param functionReq the specific function deactivation request
	 */
	public void deactivate(Node node, SpineFunctionReq functionReq) {
		if (node == null) 
			throw new RuntimeException("Can't deactivate the function: node is null");
		if (functionReq == null)
			throw new RuntimeException("Can't deactivate the function: functionReq is null");
		
		// function activation requests are differentiated by the deactivation requests by setting the appropriate flag
		functionReq.setActivationFlag(false);

		send(node.getPhysicalID(), SPINEPacketsConstants.FUNCTION_REQ, functionReq);
	}
	
	
	/**
	 * Commands the given node to do a 'immediate one-shot' sampling on the given sensor.
	 * The method won't have any effects if the node is not provided with the given sensor.
	 * 
	 * @param nodeID the destination of the request
	 * @param sensorCode the sensor to be sampled
	 * 
	 * @see spine.SPINESensorConstants
	 */
	/**
	 *@deprecated Please now use getOneShotData(Node, byte sensorCode)
	 *@see #getOneShotData(Node, byte sensorCode)
	 */
	public void readNow(int nodeID, byte sensorCode) {
		SpineSetupSensor sss = new SpineSetupSensor();
		sss.setSensor(sensorCode);
		sss.setTimeScale(SPINESensorConstants.NOW);
		sss.setSamplingTime(0);
		setupSensor(nodeID, sss);	
	}
	
	
	/**
	 * Commands the given node to do a 'immediate one-shot' sampling on the given sensor.
	 * The method won't have any effects if the node is not provided with the given sensor.
	 * 
	 * @param node the destination of the request
	 * @param sensorCode the sensor to be sampled
	 * 
	 * @see spine.SPINESensorConstants
	 */
	public void getOneShotData(Node node, byte sensorCode) {
		SpineSetupSensor sss = new SpineSetupSensor();
		sss.setSensor(sensorCode);
		sss.setTimeScale(SPINESensorConstants.NOW);
		sss.setSamplingTime(0);
		setup(node, sss);	
	}
	
	
	/**
	 * Starts the WSN sensing and computing the previously requested functions. 
	 * This is done thru a broadcast SPINE Synchr message.
	 * This simple start method will let the nodes use their default radio access scheme.
	 * 
	 * @param radioAlwaysOn low-power radio mode control flag; set it 'true' to disable the radio low-power mode;
	 * 'false' to allow radio module turn off during 'idle' periods 
	 */
	/**
	 *@deprecated Please now use startWsn(boolean)
	 *@see #startWsn(boolean)
	 */
	public void start(boolean radioAlwaysOn) {
		start(radioAlwaysOn, false);
	}
	
	
	/**
	 * Starts the WSN sensing and computing the previously requested functions. 
	 * This is done thru a broadcast SPINE Synchr message.
	 * This simple start method will let the nodes use their default radio access scheme.
	 * 
	 * @param radioAlwaysOn low-power radio mode control flag; set it 'true' to disable the radio low-power mode;
	 * 'false' to allow radio module turn off during 'idle' periods 
	 */
	public void startWsn(boolean radioAlwaysOn) {
		startWsn(radioAlwaysOn, false);
	}
	

	/**
	 * Starts the WSN sensing and computing the previously requested functions. 
	 * This is done thru a broadcast SPINE Synchr message.
	 * 
	 * @param radioAlwaysOn low-power radio mode control flag; set it 'true' to disable the radio low-power mode;
	 * 'false' to allow radio module turn off during 'idle' periods  
	 * 
	 * @param enableTDMA TDMA transmission scheme control flag; set it 'true' to enable the TDMA on the nodes;
	 * 'false' to keep using the default radio access scheme. 
	 */
	/**
	 *@deprecated Please now use startWsn(boolean, boolean)
	 *@see #startWsn(boolean, boolean)
	 */
	public void start(boolean radioAlwaysOn, boolean enableTDMA) {
		SpineStart ss = new SpineStart();
		ss.setActiveNodesCount(activeNodes.size());
		ss.setRadioAlwaysOn(radioAlwaysOn);
		ss.setEnableTDMA(enableTDMA);
		
		send(new Address(""+SPINEPacketsConstants.SPINE_BROADCAST), SPINEPacketsConstants.START, ss);
		
		started = true;
	}
	
	
	/**
	 * Starts the WSN sensing and computing the previously requested functions. 
	 * This is done thru a broadcast SPINE Synchr message.
	 * 
	 * @param radioAlwaysOn low-power radio mode control flag; set it 'true' to disable the radio low-power mode;
	 * 'false' to allow radio module turn off during 'idle' periods  
	 * 
	 * @param enableTDMA TDMA transmission scheme control flag; set it 'true' to enable the TDMA on the nodes;
	 * 'false' to keep using the default radio access scheme. 
	 */
	public void startWsn(boolean radioAlwaysOn, boolean enableTDMA) {
		SpineStart ss = new SpineStart();
		ss.setActiveNodesCount(activeNodes.size());
		ss.setRadioAlwaysOn(radioAlwaysOn);
		ss.setEnableTDMA(enableTDMA);
		
		send(new Address(""+SPINEPacketsConstants.SPINE_BROADCAST), SPINEPacketsConstants.START, ss);
		
		started = true;
	}
	
	
	/**
	 * Commands a software 'on node local clock' synchronization of the whole WSN.
	 * This is done thru a broadcast SPINE Synchr message.
	 */
	/**
	 *@deprecated Please now use syncWsn()
	 *@see #syncWsn()
	 */
	public void synchrWsn() {		
		send(new Address(""+SPINEPacketsConstants.SPINE_BROADCAST), SPINEPacketsConstants.SYNCR, null);
	}
	
	
	/**
	 * Commands a software 'on node local clock' synchronization of the whole WSN.
	 * This is done thru a broadcast SPINE Synchr message.
	 */
	public void syncWsn() {		
		send(new Address(""+SPINEPacketsConstants.SPINE_BROADCAST), SPINEPacketsConstants.SYNCR, null);
	}
	
	
	/**
	 * Commands a software reset of the whole WSN.
	 * This is done thru a broadcast SPINE Reset message.
	 */
	public void resetWsn() {		
		//send(SPINEPacketsConstants.SPINE_BROADCAST, SPINEPacketsConstants.RESET, null);
		
		// broadcast reset is translated into multiple unicast reset as a workaround in the case node are 
		// communicating in radio low power mode. 
		for(int i = 0; i<activeNodes.size(); i++) 
			send(((Node)activeNodes.elementAt(i)).getPhysicalID(), SPINEPacketsConstants.RESET, null);
		
		started = false;
	}	
	
	
	/*
	 * Private utility method containing the actual message send code 
	 */
	private void send(Address destination, byte pktType, SpineObject payload) {
		try {
			
			//	dynamic class loading of the proper SpineCodec implementation
			if (payload != null){
				    spineCodec = (SpineCodec)htInstance.get (payload.getClass().getSimpleName());
				    if (spineCodec==null){
				    	Class p =  Class.forName(SPINEDATACODEC_PACKAGE +
			    		      payload.getClass().getSimpleName());
				    	spineCodec = (SpineCodec)p.newInstance();
				    	htInstance.put (payload.getClass().getSimpleName(), spineCodec);	
				    } 
			}
			
			
			// dynamic class loading of the proper Message implementation
			// String messageClassName = prop.getProperty(Properties.MESSAGE_CLASSNAME_KEY);
			Class c = (Class)htInstance.get (MESSAGE_CLASSNAME);
			if (c == null){
				c = Class.forName(MESSAGE_CLASSNAME);
				htInstance.put (MESSAGE_CLASSNAME, c);
			} 
			msg = (com.tilab.gal.Message)c.newInstance();
			// construction of the message 
			msg.setDestinationURL(URL_PREFIX + destination.getAsInt());
			msg.setClusterId(pktType); // the clusterId is treated as the 'packet type' field
			msg.setProfileId(MY_GROUP_ID); // the profileId is treated as the 'group id' field
			if (payload != null) {
				try {
					
					byte[] payloadArray = spineCodec.encode(payload);
					short[] payloadShort = new short[payloadArray.length];
        			for (int i = 0; i<payloadShort.length; i++)
        				payloadShort[i] = payloadArray[i];					
					msg.setPayload(payloadShort);
					
				}catch (MethodNotSupportedException e){
					System.out.println(e);
				}	      
			}
			
			// message sending
			connection.send(msg);
			
		} catch (InstantiationException e) {
			System.out.println(e);
		} catch (IllegalAccessException e) {
			System.out.println(e);
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (InterruptedIOException e) {
			System.out.println(e);
		} catch (UnsupportedOperationException e) {
			System.out.println(e);
		}			
	}
	
	/**
	 * Returns the already discovered Node corresponding to the given Address
	 * 
	 * @param id the physical address of the Node to obtain
	 * @return the already discovered Node corresponding to the given Address 
	 * or null if doesn't exist any Node with the given Address.  
	 */
	public Node getNodeByPhysicalID(Address id) {
		for (int i = 0; i<activeNodes.size(); i++) {
			Node curr = (Node)activeNodes.elementAt(i);
			if(curr.getPhysicalID().equals(id))
				return curr;
		}
		return null;
	}
	
	/**
	 * Returns the already discovered Node corresponding to the given Address
	 * 
	 * @param id the logical address of the Node to obtain
	 * @return the already discovered Node corresponding to the given Address 
	 * or null if doesn't exist any Node with the given Address.  
	 */
	public Node getNodeByLogicalID(Address id) {
		for (int i = 0; i<activeNodes.size(); i++) {
			Node curr = (Node)activeNodes.elementAt(i);
			if(curr.getLogicalID().equals(id))
				return curr;
		}
		return null;
	}
	
	/*
	 * Regarding to the 'eventType', this method notify the SPINEListeners properly, by
	 * casting in the right way the Object 'o' 
	 */
	private void notifyListeners(short eventType, Object o) {
		for (int i = 0; i<this.listeners.size(); i++) 
			switch(eventType) {
				case SPINEPacketsConstants.SERVICE_ADV:
					if (!this.discoveryCompleted)
						((SPINEListener)this.listeners.elementAt(i)).newNodeDiscovered((Node)activeNodes.lastElement()); 
					break;
				case SPINEPacketsConstants.DATA: 
					((SPINEListener)this.listeners.elementAt(i)).received((Data)o); 
					break;	
				case SPINEPacketsConstants.SVC_MSG: 
					if(((ServiceMessage)o).getNode() != null)
						((SPINEListener)this.listeners.elementAt(i)).received((ServiceMessage)o); 
					break;
				case DISC_COMPL_EVT_COD:
					((SPINEListener)this.listeners.elementAt(i)).discoveryCompleted((Vector)o);
					break;
				default: {
					ServiceMessage sm = new ServiceWarningMessage();
					sm.setMessageDetail(SPINEServiceMessageConstants.UNKNOWN_PKT_RECEIVED);
					sm.setNode(baseStation);
					((SPINEListener)this.listeners.elementAt(i)).received(sm);				
					break;
				}
			}
		
	}
	
	
	/*
	 * implementation of the discovery timer procedure can be simply seen as a 
	 * Thread that, after a certain sleep interval, declares the discovery procedure completed 
	 * (by setting a global boolean flag) and notifies the SPINEListeners of such event 
	 */
	private class DiscoveryTimer extends Thread {
		
		private long delay = 0;
		
		private DiscoveryTimer(long delay) {
			this.delay = delay;
		}
		
		public void run () {
			try {
				discoveryCompleted = false;
				sleep(delay);
			} catch (InterruptedException e) {e.printStackTrace();}
			
			// if no nodes has been discovered, it's the symptom of some radio connection problem;
			// the SPINEManager notifies the SPINEListener of that situation by issuing an appropriate service message
			if (activeNodes.size()==0) {
				ServiceErrorMessage serviceErrorMessage=new ServiceErrorMessage();
				serviceErrorMessage.setNode(baseStation);
				serviceErrorMessage.setMessageDetail(SPINEServiceMessageConstants.CONNECTION_FAIL);
				notifyListeners(SPINEPacketsConstants.SVC_MSG,serviceErrorMessage);
			}		
			discoveryCompleted = true;			
			notifyListeners(DISC_COMPL_EVT_COD, activeNodes);
		}
	}
	
	
	private class WSNConnectionListenerImpl implements WSNConnection.Listener {
		
		/*
		 * This method is called to notify the SPINEManager of a new SPINE message reception. 
		 */
		public void messageReceived(com.tilab.gal.Message msg) {
			Address nodeID = new Address(msg.getSourceURL().substring(URL_PREFIX.length()));
			
			SpineObject o = null;
			
			short pktType = msg.getClusterId();
			short[] payloadShort = msg.getPayload();
			byte[] payload = new byte[payloadShort.length];
			for (int i = 0; i<payloadShort.length; i++)
				payload[i] = (byte)payloadShort[i];
	
			switch(pktType) {
				case SPINEPacketsConstants.SERVICE_ADV: {
					try {
						// dynamic class loading of the proper SpineCodec implementation						
						spineCodec = (SpineCodec)htInstance.get("ServiceAdvertisement");
						
						 if (spineCodec == null) {
							 Class d = Class.forName(SPINEDATACODEC_PACKAGE + "ServiceAdvertisement");
							 spineCodec = (SpineCodec)d.newInstance();
						    htInstance.put("ServiceAdvertisement", spineCodec);
						 }
						 
						 // Invoking decode and setting SpineObject data
						 o = spineCodec.decode(new Node(nodeID), payload);
						 
					} catch (Exception e) { 
						e.printStackTrace();
						return;
					} 
										
					if (!discoveryCompleted) {
						boolean alreadyDiscovered = false;
						for(int i = 0; i<activeNodes.size(); i++) {
							if(((Node)activeNodes.elementAt(i)).getPhysicalID().equals(nodeID)) {
								alreadyDiscovered = true;
								break;
							}
						}
						if (!alreadyDiscovered)
							activeNodes.addElement((Node)o);
					}					
					break;
				}
				case SPINEPacketsConstants.DATA: {					
					byte functionCode;					
					//  Setting functionCode
					try {
						// dynamic class loading of the proper CodecInformation
						CodecInfo codecInformation = (CodecInfo)htInstance.get("CodecInformation");
						if (codecInformation == null) {
							Class g = Class.forName(SPINEDATACODEC_PACKAGE + "CodecInformation");
							codecInformation = (CodecInfo)g.newInstance();	
							htInstance.put("CodecInformation", codecInformation);
						} 
						functionCode = codecInformation.getFunctionCode(payload);
					} catch (Exception e) { 
						e.printStackTrace();
						return;
					} 										
					
					try {
						// dynamic class loading of the proper SpineCodec implementation
						String className = SPINEFunctionConstants.functionCodeToString(functionCode) + 
							               SPINEDATA_FUNCT_CLASSNAME_SUFFIX;
						spineCodec = (SpineCodec)htInstance.get (className);
						 if (spineCodec == null){
							 Class d = Class.forName(SPINEDATACODEC_PACKAGE + className);
							 spineCodec = (SpineCodec)d.newInstance();
							 htInstance.put(className, spineCodec);
						 }
						 
						 // Invoking decode and setting SpineObject data
						 o = spineCodec.decode(getNodeByPhysicalID(nodeID), payload);
						
					} catch (Exception e) { 
						e.printStackTrace();
						return;
					}
					break;
				}
				case SPINEPacketsConstants.SVC_MSG: {
					
					byte serviceMessageType;
					
					//  Setting functionCode
					try {
						// dynamic class loading of the proper CodecInformation
						CodecInfo codecInformation = (CodecInfo)htInstance.get("CodecInformation");
						if (codecInformation == null) {
							Class g = Class.forName(SPINEDATACODEC_PACKAGE + "CodecInformation");
							codecInformation = (CodecInfo)g.newInstance();	
							htInstance.put("CodecInformation", codecInformation);
						} 
						serviceMessageType = codecInformation.getServiceMessageType(payload);
					} catch (Exception e) { 
						e.printStackTrace();
						return;
					} 
					
					try {
						// dynamic class loading of the proper SpineCodec implementation
						String className = SPINEServiceMessageConstants.serviceMessageTypeToString(serviceMessageType) + 
			               SPINE_SERVICE_MESSAGE_CLASSNAME_SUFFIX;
						spineCodec = (SpineCodec)htInstance.get(className);
						
						if (spineCodec == null){
							Class d = Class.forName(SPINE_SERVICE_MESSAGE_CODEC_PACKAGE + className);
							spineCodec = (SpineCodec)d.newInstance();
						    htInstance.put(className, spineCodec);
						 }
						
						 // Invoking decode and setting SpineObject data
						 o = spineCodec.decode(getNodeByPhysicalID(nodeID), payload);
						
					} catch (Exception e) { 
						e.printStackTrace();
						return;
					}
					break;
				}	
				default: break;
			}
			
			// SPINEListeners are notified of the reception from the node 'nodeID' of some data  
			notifyListeners(pktType, o);
			
			//System.out.println("Memory available: " + Runtime.getRuntime().freeMemory() + " KB");
			// call to the garbage collector to favour the recycling of unused memory
			System.gc();		
		}
	}
	
	private static void exit(String msg) {
		System.out.println(msg); 
		System.out.println("Will exit now!");
		System.exit(-1);
	}
	
}
