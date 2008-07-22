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
import com.tilab.zigbee.Message;
import com.tilab.zigbee.WSNConnection;

import spine.SPINEPacketsConstants;

import spine.datamodel.Data;
import spine.datamodel.Node;

public class SPINEManager implements WSNConnection.Listener {

	private final static long DISCOVERY_TIMEOUT = 500;
	
	public static final String URL_PREFIX_KEY = "url_prefix";
	private static final String URL_PREFIX = System.getProperty(URL_PREFIX_KEY);
	
	private static final byte DISC_COMPL = 100;
	
	public static final byte MY_GROUP_ID = (byte)0xAB;

	public static final String MESSAGE_CLASSNAME_KEY = "Message_ClassName";
	
	
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


	public void discoveryWsn() {		
		send(SPINEPacketsConstants.SPINE_BROADCAST, SPINEPacketsConstants.SERVICE_DISCOVERY, null);
		
		if(this.discoveryTimeout > 0)
			new DiscoveryTimer(this.discoveryTimeout).start();
	}
	
	public void bootUpWsn() {		
		
	}

	public void setupSensor(int nodeID, byte sensorCode, byte timeScale, int samplingTime) {
		byte[] payload = new byte[4];
		payload[0] = sensorCode;
		payload[1] = timeScale;				
		payload[2] = (byte)((samplingTime & 0x0000FFFF)>>8);
		payload[3] = (byte)(samplingTime & 0x000000FF);
		
		send(nodeID, SPINEPacketsConstants.SETUP_SENSOR, payload);
	}

	public void setupFunction(int nodeID, byte function, byte[] params) {
		byte[] payload = new byte[2 + params.length];
		payload[0] = function;
		payload[1] = (byte)params.length;	
		System.arraycopy(params, 0, payload, 2, params.length);
		send(nodeID, SPINEPacketsConstants.SETUP_FUNCTION, payload);
	}

	public void activateFunction(int nodeID, byte function, byte[] params) {
		byte[] payload = new byte[3 + params.length];
		payload[0] = function;
		payload[1] = 1; // stands as the activate/deactivate flag (1 = TRUE, activate)
		payload[2] = (byte)params.length;	
		System.arraycopy(params, 0, payload, 3, params.length);

		send(nodeID, SPINEPacketsConstants.FUNCTION_REQ, payload);
	}
	
	public void deactivateFunction(int nodeID, byte function, byte[] params) {
		byte[] payload = new byte[3 + params.length];
		payload[0] = function;
		payload[1] = 0; // stands as the activate/deactivate flag (0 = FALSE, deactivate)
		payload[2] = (byte)params.length;	
		System.arraycopy(params, 0, payload, 3, params.length);
		
		send(nodeID, SPINEPacketsConstants.FUNCTION_REQ, payload);
	}
	
	public void start(boolean radioAlwaysOn) {
		start(radioAlwaysOn, false);
	}
	
	public void start(boolean radioAlwaysOn, boolean enableTDMA) {
		byte[] payload = new byte[4];
		int nodesCount = activeNodes.size();
		payload[0] = (byte)(nodesCount>>8);
		payload[1] = (byte)nodesCount;
		payload[2] = (radioAlwaysOn)? (byte)1: 0;
		payload[3] = (enableTDMA)? (byte)1: 0;
		
		send(SPINEPacketsConstants.SPINE_BROADCAST, SPINEPacketsConstants.START, payload);
	}
	
	public void resetWsn() {		
		send(SPINEPacketsConstants.SPINE_BROADCAST, SPINEPacketsConstants.RESET, null);
	}
	
	public void syncrWsn() {		
		send(SPINEPacketsConstants.SPINE_BROADCAST, SPINEPacketsConstants.SYNCR, null);
	}

	private void send(int nodeID, byte pktType, byte[] payload) {
		try {
			Class c = Class.forName(System.getProperty(MESSAGE_CLASSNAME_KEY));
			com.tilab.zigbee.Message msg = (com.tilab.zigbee.Message)c.newInstance();
			
			msg.setDestinationURL(URL_PREFIX + nodeID);
			msg.setClusterId(pktType);
			msg.setProfileId(MY_GROUP_ID);
			if (payload != null)
				msg.setPayload(payload);
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
		
		printPayload(msg); // DEBUG CODE
		
		Object o = null;
		
		short pktType = msg.getClusterId(); 
		switch(pktType) {
			case SPINEPacketsConstants.SERVICE_ADV: 
				if (!this.discoveryCompleted) 
					this.activeNodes.addElement(new Node(nodeID, msg.getPayload())); 				
				break;
			case SPINEPacketsConstants.DATA: o = new Data(nodeID, msg.getPayload()); break;
			case SPINEPacketsConstants.SVC_MSG: break;
			default: break;
		}
		
		notifyListeners(nodeID, pktType, o);
		
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
					((SPINEListener)this.listeners.elementAt(i)).serviceMessageReceived(); 
					break;
				case DISC_COMPL:
					((SPINEListener)this.listeners.elementAt(i)).discoveryCompleted((Vector)o);
				default: break;
			}
		
	}

	public Vector getActiveNodes() {
		return activeNodes;
	}
	
	
	private void printPayload(Message msg) {  // DEBUG CODE
		System.out.print("in: ");
		for (int i = 0; i<msg.getPayload().length; i++) {
			short b =  msg.getPayload()[i];
			if (b<0) b += 256;
			System.out.print(Integer.toHexString(b) + " ");
		}
		System.out.println("\n");		
	}

	public void readNow(int nodeID, byte sensorCode) {
		this.setupSensor(nodeID, sensorCode, SPINESensorConstants.NOW, 0);		
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
	
	
	private class DiscoveryTimer extends Thread {
		
		private long delay = 0;
		
		DiscoveryTimer(long delay) {
			this.delay = delay;
		}
		
		public void run () {
			try {
				sleep(delay);
			} catch (InterruptedException e) {e.printStackTrace();}
			
			if (activeNodes.size()==0) 
				//listener.messageReceived(Constants.BASE_STATION_ADDRESS, ServiceMessageCodes.FATAL_ERROR, ServiceMessageCodes.CONNECTION_FAIL);
				notifyListeners(SPINEPacketsConstants.SPINE_BASE_STATION, SPINEPacketsConstants.SVC_MSG, null);
						
			discoveryCompleted = true;			
			notifyListeners(SPINEPacketsConstants.SPINE_BASE_STATION, DISC_COMPL, activeNodes);
		}
	}
}
