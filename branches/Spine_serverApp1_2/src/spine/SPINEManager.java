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
 *
 * @version 1.2
 */

package spine;

import java.io.InterruptedIOException;
import java.util.Vector;

import com.tilab.gal.LocalNodeAdapter;
import com.tilab.gal.WSNConnection;

import spine.SPINEPacketsConstants;

import spine.communication.tinyos.SpineFunctionReq;
import spine.communication.tinyos.SpineSetupFunction;
import spine.communication.tinyos.SpineSetupSensor;
import spine.communication.tinyos.SpineStart;
import spine.datamodel.Data;
import spine.datamodel.DataFactory;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;

public class SPINEManager {

	private static Properties prop = Properties.getProperties();
	
	
	private final static long DISCOVERY_TIMEOUT = 500;	
	
	private static final String URL_PREFIX = prop.getProperty(Properties.URL_PREFIX_KEY);
	
	private static final byte DISC_COMPL_EVT_COD = 100;
	
	
	private static final byte MY_GROUP_ID = (byte)Short.parseShort(prop.getProperty(Properties.GROUP_ID_KEY), 16);

	
	private Vector listeners = new Vector(); // <values:SPINEListener>
	
	private Vector activeNodes = new Vector(); // <values:Node>
	private boolean discoveryCompleted = false;
	private long discoveryTimeout = DISCOVERY_TIMEOUT;
	
	private boolean started = false;
	
	private WSNConnection connection;
	private LocalNodeAdapter nodeAdapter;	
	
	
	private static SPINEManager instance;
	
	private SPINEManager(String[] args) {
		try {
			nodeAdapter = LocalNodeAdapter.getLocalNodeAdapter();	

			Vector params = new Vector();
			for (int i = 0; i < args.length; i++) {
				params.addElement(args[i]);
			}
			nodeAdapter.init(params);

			nodeAdapter.start();

			connection = nodeAdapter.createAPSConnection();			

			connection.setListener(new WSNConnectionListenerImpl());

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
	 */
	public static SPINEManager getInstance(String[] args) {
		if (instance == null) 
			instance = new SPINEManager(args);
		return instance;
	}

	/**
	 * Registers a SPINEListener to the manager instance
	 * 
	 * @param listener the listener to register
	 */
	public void registerListener(SPINEListener listener) {
		this.listeners.addElement(listener);
	}
	
	/**
	 * Deregisters a SPINEListener to the manager instance
	 * 
	 * @param listener the listener to deregister
	 */
	public void deregisterListener(SPINEListener listener) {
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
	public void setDiscoveryProcedureTimeout(long discoveryTimeout) {
		this.discoveryTimeout = discoveryTimeout;
	}
	
	/**
	 * Returns an instance of a Properties implementation class which can be queried 
	 * for retrieving system and framework properties and parameters 
	 * 
	 * @return the Properties instance
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
	public boolean started() {
		return this.started;
	}
	
	/**
	 * Commands the SPINEManager to discovery the surrounding WSN nodes
	 */
	public void discoveryWsn() {		
		send(SPINEPacketsConstants.SPINE_BROADCAST, SPINEPacketsConstants.SERVICE_DISCOVERY, null);
		
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
	public void setupSensor(int nodeID, SpineSetupSensor setupSensor) {
		send(nodeID, SPINEPacketsConstants.SETUP_SENSOR, setupSensor);
	}

	/**
	 * Setups a specific function of the given node.
	 * The parameters involved are 'function dependent' and are specified by providing a proper
	 * SpineSetupFunction instantiation object
	 * 
	 * @param nodeID the destination of the request
	 * @param setupFunction the object containing the setup parameters
	 */
	public void setupFunction(int nodeID, SpineSetupFunction setupFunction) {
		send(nodeID, SPINEPacketsConstants.SETUP_FUNCTION, setupFunction);
	}

	/**
	 * Activates a function (or even only function sub-routines) on the given sensor.
	 * The content of the actual request is 'function dependent' and it's embedded into the
	 * 'SpineFunctionReq' instantiations.
	 * 
	 * @param nodeID the destination of the request
	 * @param functionReq the specific function activation request
	 */
	public void activateFunction(int nodeID, SpineFunctionReq functionReq) {
		// function activation requests are differentiated by the deactivation requests by setting the appropriate flag 
		functionReq.setActivationFlag(true);

		send(nodeID, SPINEPacketsConstants.FUNCTION_REQ, functionReq);
	}
	
	/**
	 * Deactivates a function (or even only function sub-routines) on the given sensor.
	 * The content of the actual request is 'function dependent' and it's embedded into the
	 * 'SpineFunctionReq' instantiations.
	 * 
	 * @param nodeID the destination of the request
	 * @param functionReq the specific function deactivation request
	 */
	public void deactivateFunction(int nodeID, SpineFunctionReq functionReq) {
		// function activation requests are differentiated by the deactivation requests by setting the appropriate flag
		functionReq.setActivationFlag(false);

		send(nodeID, SPINEPacketsConstants.FUNCTION_REQ, functionReq);
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
	public void readNow(int nodeID, byte sensorCode) {
		SpineSetupSensor sss = new SpineSetupSensor();
		sss.setSensor(sensorCode);
		sss.setTimeScale(SPINESensorConstants.NOW);
		sss.setSamplingTime(0);
		setupSensor(nodeID, sss);	
	}
	
	/**
	 * Starts the WSN sensing and computing the previously requested functions. 
	 * This is done thru a broadcast SPINE Synchr message.
	 * This simple start method will let the nodes use their default radio access scheme.
	 * 
	 * @param radioAlwaysOn low-power radio mode control flag; set it 'true' to disable the radio low-power mode;
	 * 'false' to allow radio module turn off during 'idle' periods 
	 */
	public void start(boolean radioAlwaysOn) {
		start(radioAlwaysOn, false);
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
	public void start(boolean radioAlwaysOn, boolean enableTDMA) {
		SpineStart ss = new SpineStart();
		ss.setActiveNodesCount(activeNodes.size());
		ss.setRadioAlwaysOn(radioAlwaysOn);
		ss.setEnableTDMA(enableTDMA);
		
		send(SPINEPacketsConstants.SPINE_BROADCAST, SPINEPacketsConstants.START, ss);
		
		started = true;
	}
	
	/**
	 * Commands a software 'on node local clock' synchronization of the whole WSN.
	 * This is done thru a broadcast SPINE Synchr message.
	 */
	public void synchrWsn() {		
		send(SPINEPacketsConstants.SPINE_BROADCAST, SPINEPacketsConstants.SYNCR, null);
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
			send(((Node)activeNodes.elementAt(i)).getNodeID(), SPINEPacketsConstants.RESET, null);
		
		started = false;
	}	
	
	
	/*
	 * Private utility method containing the actual message send code 
	 */
	private void send(int nodeID, byte pktType, Object payload) {
		try {
			// dynamic class loading of the proper Message implementation
			Class c = Class.forName(prop.getProperty(Properties.MESSAGE_CLASSNAME_KEY));
			com.tilab.gal.Message msg = (com.tilab.gal.Message)c.newInstance();
			
			// costruction of the message 
			msg.setDestinationURL(URL_PREFIX + nodeID);
			msg.setClusterId(pktType); // the clusterId is treated as the 'packet type' field
			msg.setProfileId(MY_GROUP_ID); // the profileId is treated as the 'group id' field
			if (payload != null) {
				switch(pktType) {
					case SPINEPacketsConstants.SETUP_FUNCTION:
							msg.setPayload(((SpineSetupFunction)payload).encode());
							break;
					case SPINEPacketsConstants.FUNCTION_REQ:
							msg.setPayload(((SpineFunctionReq)payload).encode());
							break;
					case SPINEPacketsConstants.SETUP_SENSOR:
						msg.setPayload(((SpineSetupSensor)payload).encode());
						break;
					case SPINEPacketsConstants.START:
						msg.setPayload(((SpineStart)payload).encode());
						break;	
					default: break;
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
	
	

	/*
	 * Regarding to the 'eventType', this method notify the SPINEListeners properly, by
	 * casting in the right way the Object 'o' 
	 */
	private void notifyListeners(int nodeID, short eventType, Object o) {
		for (int i = 0; i<this.listeners.size(); i++) 
			switch(eventType) {
				case SPINEPacketsConstants.SERVICE_ADV:
					if (!this.discoveryCompleted)
						((SPINEListener)this.listeners.elementAt(i)).newNodeDiscovered((Node)activeNodes.lastElement()); 
					break;
				case SPINEPacketsConstants.DATA: 
					((SPINEListener)this.listeners.elementAt(i)).dataReceived(nodeID, (Data)o); 
					break;	
				case SPINEPacketsConstants.SVC_MSG: 
					((SPINEListener)this.listeners.elementAt(i)).serviceMessageReceived(nodeID, (ServiceMessage)o); 
					break;
				case DISC_COMPL_EVT_COD:
					((SPINEListener)this.listeners.elementAt(i)).discoveryCompleted((Vector)o);
					break;
				default: 
					((SPINEListener)this.listeners.elementAt(i)).serviceMessageReceived(nodeID, 
															new ServiceMessage(nodeID, ServiceMessage.WARNING, 
																				ServiceMessage.UNKNOWN_PKT_RECEIVED));
					break;
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
			if (activeNodes.size()==0) 
				notifyListeners(SPINEPacketsConstants.SPINE_BASE_STATION, 
								SPINEPacketsConstants.SVC_MSG, 
								new ServiceMessage(SPINEPacketsConstants.SPINE_BASE_STATION, 
												   ServiceMessage.ERROR, ServiceMessage.CONNECTION_FAIL));
						
			discoveryCompleted = true;			
			notifyListeners(SPINEPacketsConstants.SPINE_BASE_STATION, DISC_COMPL_EVT_COD, activeNodes);
		}
	}
	
	private class WSNConnectionListenerImpl implements WSNConnection.Listener {
		
		/*
		 * This method is called to notify the SPINEManager of a new SPINE message reception. 
		 */
		public void messageReceived(com.tilab.gal.Message msg) {

			int nodeID = Integer.parseInt(msg.getSourceURL().substring(URL_PREFIX.length()));
			
			Object o = null;
			
			short pktType = msg.getClusterId(); 
			switch(pktType) {
				case SPINEPacketsConstants.SERVICE_ADV: 
					if (!discoveryCompleted) {
						boolean alreadyDiscovered = false;
						for(int i = 0; i<activeNodes.size(); i++) {
							if(((Node)activeNodes.elementAt(i)).getNodeID() == nodeID) {
								alreadyDiscovered = true;
								break;
							}
						}
						if (!alreadyDiscovered)
							activeNodes.addElement(new Node(nodeID, msg.getPayload()));
					}
					break;
				case SPINEPacketsConstants.DATA: o = DataFactory.newData(nodeID, msg.getPayload()); 
					break;
				case SPINEPacketsConstants.SVC_MSG: o = new ServiceMessage(nodeID, msg.getPayload()); 
					break;
				default: break;
			}
			
			// SPINEListeners are notified of the reception from the node 'nodeID' of some data  
			notifyListeners(nodeID, pktType, o);
			
			//System.out.println("Memory available: " + Runtime.getRuntime().freeMemory() + " KB");
			// call to the garbage collector to favour the recycling of unused memory
			System.gc();		
		}
	}
	
}
