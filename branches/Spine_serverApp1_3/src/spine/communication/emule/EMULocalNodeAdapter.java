/*****************************************************************
 SPINE - Signal Processing In-Node Environment is a framework that 
 allows dynamic on node configuration for feature extraction and a 
 OtA protocol for the management for WSN

 Copyright (C) 2007 Telecom Italia S.p.A. 
 †
 GNU Lesser General Public License
 †
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation, 
 version 2.1 of the License. 
 †
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.† See the GNU
 Lesser General Public License for more details.
 †
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA† 02111-1307, USA.
 *****************************************************************/

/**
 * Implementation of the GAL LocalNodeAdapter.
 * This class is responsible to implement the specific logic of accessing a TinyOS base station 
 * in a way complying to the GAL APIs.
 * Hence, it's responsible of receiving packets from the serial port thru the TinyOS.jar APIs and to 
 * provide a standard way of transmitting packets to the attached base-station that will eventually be forwarded Ota. 
 * 
 * Note that this class is only used internally at the framework. 
 *
 * @author Alessia Salmeri
 *
 * @version 1.2
 * 
 * @see LocalNodeAdapter
 */

package spine.communication.emule;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import spine.Properties;
import spine.SPINEPacketsConstants;

import com.tilab.gal.ConfigurationDescriptor;
import com.tilab.gal.LocalNodeAdapter;
import com.tilab.gal.Message;
import com.tilab.gal.WSNConnection;
import java.util.Hashtable;
import java.util.Enumeration;

public class EMULocalNodeAdapter extends LocalNodeAdapter implements SocketMessageListener {

	private static Properties prop = Properties.getProperties();
	
	private static final String URL_PREFIX = prop.getProperty(Properties.URL_PREFIX_KEY);
	//
	// TODO
	// Gestire caso di WSN con piu` nodi
	//
	private static final int WSN_NODE = Integer.parseInt(prop.getProperty(Properties.NODE_EMULATOR_KEY));
	

	//boolean start;

	// Node Information Hash Table
	Hashtable nodeInfo = new Hashtable();

	int nodeId;

	int nodeConnected = 0;

	/**
	 * This method is called by the nodeCoordinator (SocketThrdServer) when a
	 * new message is received by the nodeCoordinator.
	 */
	public void messageReceived(int srcID, Message msg) {

		String sourceURL = "";
		System.out.println("Message received from nodeCoordinator (SocketThrdServer) msg= " + msg);

		//
		// Se il campo ApplicationId e` diverso da 0 si tratta di un msg che
		// fornisce le informazioni sul nodo ==> memorizzato in una
		// HashTable gestita da EMULocalNodeAdapter (non pass - thru to
		// connections)
		// 

		if (msg.getApplicationId() != 0) {
			// nodeId from sourceURL
			sourceURL = msg.getSourceURL();
			//nodeId = Integer.parseInt(sourceURL.substring(sourceURL.lastIndexOf(":") + 1));
			nodeId = Integer.parseInt(sourceURL.substring(URL_PREFIX.length()));

			// Add "nodeId"/"node info msg" to nodeInfo
			nodeInfo.put(new Integer(nodeId), msg);

			nodeConnected++;

			if (nodeConnected == WSN_NODE) {
				//start = true;
				// Per ogni nodo in nodeInfo pass-thru to connections
				for (Enumeration e = nodeInfo.keys(); e.hasMoreElements();) {
					Integer key = (Integer)e.nextElement();
					System.out.println(key + ":" + nodeInfo.get(key));
					for (int i = 0; i < connections.size(); i++)
						((EMUWSNConnection) connections.elementAt(i)).messageReceived((Message) nodeInfo.get(key));
				}
			}
			
		} else {

			// pass - thru connections
			for (int i = 0; i < connections.size(); i++)
				((EMUWSNConnection) connections.elementAt(i)).messageReceived(msg);
		}
	}

	private Vector connections = new Vector(); // <values: WSNConnection>

	protected String motecom = null;

	protected String port = null;

	protected String speed = null;

	public WSNConnection createAPSConnection() {
		WSNConnection newConnection = new EMUWSNConnection(this);
		connections.add(newConnection);
		return newConnection;
	}

	public ConfigurationDescriptor getConfigurationDescriptor() {
		return null;
	}

	public void init(Vector parms) {
		System.out.println("EMULocalNodeAdapter nodeAdapter.init(params)");
		// TODO
	}

	public void reset() {
		System.out.println("EMULocalNodeAdapter nodeAdapter.reset()");
		// TODO
	}

	SocketThrdServer nodeCoordinator = new SocketThrdServer();

	public void start() {

		System.out.println("EMULocalNodeAdapter nodeAdapter.start()");
		nodeCoordinator.setTitle("WSN Enulator: Collector node");
		WindowListener l = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		nodeCoordinator.addWindowListener(l);
		// EMULocalNodeAdapter is a SocketMessage listener
		nodeCoordinator.registerListener(this);
		nodeCoordinator.pack();
		nodeCoordinator.setVisible(true);
		Thread t = new Thread(nodeCoordinator);
		t.start();
		
//*** NON CANCELLARE ***
// SPINEManager DISCOVERY_TIMEOUT alto oppure while (!start) in EMULocalNodeAdapter ==> BLOCCA L'APPLICAZIONE
	
		System.out.println("EMULLocalNodeAdapter in wainting ...");
		//
		// TODO
		// Problema dello start bloccante del EMULocalNodeAdapter ... Si sblocca
		// al primo messaggio ricevuto da un nodo
		//
		/*
		while (!start) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
			}
		}
		*/	
	}

	public void stop() {
		System.out.println("EMULocalNodeAdapter nodeAdapter.stop()");
		// TODO
	}

	// send from EMULocalNodeAdapter to SocketThrdServer .... from
	// SocketThrdServer to Server Socket Node
	protected synchronized void send(int destNodeID, SpineEMUMessage emumsg) {

		System.out.println("EMULocalNodeAdapter nodeAdapter.send()");

		EMUMessage msg = new EMUMessage();

		try {
			switch (emumsg.getHeader().getPktType()) {
			case SPINEPacketsConstants.START:
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> Cmd manager.start or manager.startWsn");
				// nodeCoordinator.sendCommand(destNodeID,"START");
				
				for (Enumeration e = nodeInfo.keys(); e.hasMoreElements();) {
					Integer key = (Integer)e.nextElement();
					System.out.println(key + ":" + nodeInfo.get(key));
					nodeCoordinator.sendCommand(key.intValue(), "START");
				}
				//nodeCoordinator.sendCommand(1, "START");
				break;
			case SPINEPacketsConstants.RESET:
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> Cmd manager.resetWsn");
				// nodeCoordinator.sendCommand(destNodeID, "RESET");
				for (Enumeration e = nodeInfo.keys(); e.hasMoreElements();) {
					Integer key = (Integer)e.nextElement();
					System.out.println(key + ":" + nodeInfo.get(key));
					nodeCoordinator.sendCommand(key.intValue(), "RESET");
				}
				// nodeCoordinator.sendCommand(1, "RESET");
				break;
			case SPINEPacketsConstants.SYNCR:
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> Cmd manager.syncWsn or manager.synchrWsn");
				// nodeCoordinator.sendCommand(destNodeID, "SYNCR");
				//nodeCoordinator.sendCommand(1, "SYNCR");
				break;
			case SPINEPacketsConstants.SERVICE_DISCOVERY:
				// Cmd discoveryWsn: gestito da EMULocalNodeAdapter
				// Per ogni nodo in nodeInfo pass-thru to connections
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> Cmd manager.discoveryWsn");
				/*
				for (Enumeration e = nodeInfo.keys(); e.hasMoreElements();) {
					int key = (Integer) e.nextElement();
					System.out.println(key + ":" + nodeInfo.get(key));
					for (int i = 0; i < connections.size(); i++)
						((EMUWSNConnection) connections.elementAt(i)).messageReceived((Message) nodeInfo.get(key));
				}
                */
				break;
			case SPINEPacketsConstants.SETUP_SENSOR:
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> Cmd manager.setupSensor() or manager.setup(..., SpineSetupSensor)");
				// nodeCoordinator.sendCommand(destNodeID, "SETUP_SENSOR");
				//nodeCoordinator.sendCommand(1, "SETUP_SENSOR");
				break;
			case SPINEPacketsConstants.SETUP_FUNCTION:
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> Cmd manager.setupFunction() or manager.setup(..., SpineSetupFunction)");
				// nodeCoordinator.sendCommand(destNodeID, "SETUP_FUNCTION");
				//nodeCoordinator.sendCommand(1, "SETUP_FUNCTION");
				break;
			case SPINEPacketsConstants.FUNCTION_REQ:
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> FUNCTION_REQ");
				// nodeCoordinator.sendCommand(destNodeID, "FUNCTION_REQ");
				//nodeCoordinator.sendCommand(1, "FUNCTION_REQ");
				break;
			default:
				System.out.println("ERROR PktType");
			}
		} catch (IllegalSpineHeaderSizeException e) {
			e.printStackTrace();
		}

	}

	private void printPayload(byte[] payload) { // DEBUG CODE
		System.out.print("in.lowLevel: ");
		if (payload == null || payload.length == 0)
			System.out.print("empty payload");
		else {
			for (int i = 0; i < payload.length; i++) {
				short b = payload[i];
				if (b < 0)
					b += 256;
				System.out.print(Integer.toHexString(b) + " ");
			}
		}
		System.out.println("");
	}

}