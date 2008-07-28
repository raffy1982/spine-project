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

package spine;

import java.io.InterruptedIOException;
import java.util.Vector;

import com.tilab.zigbee.LocalNodeAdapter;
import com.tilab.zigbee.WSNConnection;

import spine.SPINEPacketsConstants;

import spine.communication.tinyos.SpineFunctionReq;
import spine.communication.tinyos.SpineSetupFunction;
import spine.communication.tinyos.SpineSetupSensor;
import spine.communication.tinyos.SpineStart;
import spine.datamodel.Data;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;

public class SPINEManager implements WSNConnection.Listener {

	private static Properties prop = Properties.getProperties();
	
	
	private final static long DISCOVERY_TIMEOUT = 500;	
	
	private static final String URL_PREFIX = prop.getProperty(Properties.URL_PREFIX_KEY);
	
	private static final byte DISC_COMPL_EVT_COD = 100;
	
	
	private static final byte MY_GROUP_ID = (byte)Short.parseShort(prop.getProperty(Properties.GROUP_ID_KEY), 16);

	
	private Vector listeners = new Vector(); // <values:SPINEListener>
	
	private Vector activeNodes = new Vector(); // <values:SpineNode>
	private boolean discoveryCompleted = false;
	private long discoveryTimeout = DISCOVERY_TIMEOUT;
	
	private WSNConnection connection;
	private LocalNodeAdapter nodeAdapter;	
	
	
	public static SPINEManager instance;
	
	
	private SPINEManager(String port, String speed) {
		try {
			nodeAdapter = LocalNodeAdapter.getLocalNodeAdapter();	
			
			Vector params = new Vector();
			params.add(port);
			params.add(speed);
			nodeAdapter.init(params);
			
			nodeAdapter.start();
			
			connection = nodeAdapter.createAPSConnection();			
			
			connection.setListener(this);
			
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (InstantiationException e) {
			System.out.println(e);
		} catch (IllegalAccessException e) {
			System.out.println(e);
		} 
	}
	
	
	public static SPINEManager getInstance(String port, String speed) {
		if (instance == null) 
			instance = new SPINEManager(port, speed);
		return instance;
	}
	
	public void registerListener(SPINEListener listener) {
		this.listeners.addElement(listener);
	}
	
	public void deregisterListener(SPINEListener listener) {
		this.listeners.removeElement(listener);
	}


	public void discoveryWsn() {		
		send(SPINEPacketsConstants.SPINE_BROADCAST, SPINEPacketsConstants.SERVICE_DISCOVERY, null);
		
		if(this.discoveryTimeout > 0)
			new DiscoveryTimer(this.discoveryTimeout).start();
	}
	
	public void bootUpWsn() {		
		
	}

	public void setupSensor(int nodeID, SpineSetupSensor sss) {
		send(nodeID, SPINEPacketsConstants.SETUP_SENSOR, sss);
	}

	public void setupFunction(int nodeID, SpineSetupFunction ssf) {
		send(nodeID, SPINEPacketsConstants.SETUP_FUNCTION, ssf);
	}

	public void activateFunction(int nodeID, SpineFunctionReq sfr) {
		sfr.setActivationFlag(true);

		send(nodeID, SPINEPacketsConstants.FUNCTION_REQ, sfr);
	}
	
	public void deactivateFunction(int nodeID, SpineFunctionReq sfr) {
		sfr.setActivationFlag(false);

		send(nodeID, SPINEPacketsConstants.FUNCTION_REQ, sfr);
	}
	
	public void start(boolean radioAlwaysOn) {
		start(radioAlwaysOn, false);
	}
	
	public void start(boolean radioAlwaysOn, boolean enableTDMA) {
		SpineStart ss = new SpineStart();
		ss.setActiveNodesCount(activeNodes.size());
		ss.setRadioAlwaysOn(radioAlwaysOn);
		ss.setEnableTDMA(enableTDMA);
		
		send(SPINEPacketsConstants.SPINE_BROADCAST, SPINEPacketsConstants.START, ss);
	}
	
	public void resetWsn() {		
		send(SPINEPacketsConstants.SPINE_BROADCAST, SPINEPacketsConstants.RESET, null);
	}
	
	public void syncrWsn() {		
		send(SPINEPacketsConstants.SPINE_BROADCAST, SPINEPacketsConstants.SYNCR, null);
	}

	private void send(int nodeID, byte pktType, Object payload) {
		try {
			Class c = Class.forName(prop.getProperty(Properties.MESSAGE_CLASSNAME_KEY));
			com.tilab.zigbee.Message msg = (com.tilab.zigbee.Message)c.newInstance();
			
			msg.setDestinationURL(URL_PREFIX + nodeID);
			msg.setClusterId(pktType); 
			msg.setProfileId(MY_GROUP_ID);
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

	public void messageReceived(com.tilab.zigbee.Message msg) {

		int nodeID = Integer.parseInt(msg.getSourceURL().substring(URL_PREFIX.length()));
		
		Object o = null;
		
		short pktType = msg.getClusterId(); 
		switch(pktType) {
			case SPINEPacketsConstants.SERVICE_ADV: 
				if (!this.discoveryCompleted) 
					this.activeNodes.addElement(new Node(nodeID, msg.getPayload())); 				
				break;
			case SPINEPacketsConstants.DATA: o = new Data(nodeID, msg.getPayload()); break;
			case SPINEPacketsConstants.SVC_MSG: o = new ServiceMessage(nodeID, msg.getPayload()); break;
			default: break;
		}
		
		notifyListeners(nodeID, pktType, o);
		
		//System.out.println("Memory available: " + Runtime.getRuntime().freeMemory() + " KB");
		System.gc();		
	}

	private void notifyListeners(int nodeID, short pktType, Object o) {
		for (int i = 0; i<this.listeners.size(); i++) 
			switch(pktType) {
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
				default: break;
			}
		
	}

	public Vector getActiveNodes() {
		return activeNodes;
	}
	
	
	public void readNow(int nodeID, byte sensorCode) {
		SpineSetupSensor sss = new SpineSetupSensor();
		sss.setSensor(sensorCode);
		sss.setTimeScale(SPINESensorConstants.NOW);
		sss.setSamplingTime(0);
		setupSensor(nodeID, sss);	
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
	
	public static Properties getProperties() {
		return prop;
	}
	
	
	private class DiscoveryTimer extends Thread {
		
		private long delay = 0;
		
		private DiscoveryTimer(long delay) {
			this.delay = delay;
		}
		
		public void run () {
			try {
				sleep(delay);
			} catch (InterruptedException e) {e.printStackTrace();}
			
			if (activeNodes.size()==0) 
				notifyListeners(SPINEPacketsConstants.SPINE_BASE_STATION, 
								SPINEPacketsConstants.SVC_MSG, 
								new ServiceMessage(SPINEPacketsConstants.SPINE_BASE_STATION, 
												   ServiceMessage.ERROR, ServiceMessage.CONNECTION_FAIL));
						
			discoveryCompleted = true;			
			notifyListeners(SPINEPacketsConstants.SPINE_BASE_STATION, DISC_COMPL_EVT_COD, activeNodes);
		}
	}
}
