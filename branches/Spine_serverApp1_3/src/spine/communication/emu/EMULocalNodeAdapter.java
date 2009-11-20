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
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.† See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA� 02111-1307, USA.
 *****************************************************************/

/**
 * Implementation of the GAL LocalNodeAdapter.
 * 
 * This class is responsible to receive and transmit packets from-to the Node Emulator. 
 * 
 * Note that this class is only used internally at the framework. 
 *
 * @author Alessia Salmeri
 *
 * @version 1.3
 * 
 * @see LocalNodeAdapter
 * @see SocketMessageListener
 */

package spine.communication.emu;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;
import java.io.*;

import spine.Properties;
import spine.SPINEPacketsConstants;
import spine.SPINESupportedPlatforms;

import com.tilab.gal.ConfigurationDescriptor;
import com.tilab.gal.LocalNodeAdapter;
import com.tilab.gal.Message;
import com.tilab.gal.WSNConnection;
import java.util.Hashtable;
import java.util.Enumeration;

public class EMULocalNodeAdapter extends LocalNodeAdapter implements SocketMessageListener {

	private static Properties prop = Properties.getDefaultProperties();

	private static final String URL_PREFIX = prop.getProperty(SPINESupportedPlatforms.EMULATOR + "_" + Properties.URL_PREFIX_KEY);

	// Node Information Hash Table
	Hashtable<Integer, Message> nodeInfo = new Hashtable<Integer, Message>();

	int nodeId;

	int nodeConnected = 0;

	/**
	 * This method is called by the nodeCoordinator (SocketThrdServer) when a
	 * new message is received by the nodeCoordinator.
	 */
	public void messageReceived(int srcID, Message msg) {

		String sourceURL = "";
		System.out.println("Msg Received from nodeCoordinator (SocketThrdServer) -> " + msg);

		// Case "Service Advertisement Message" (ProfileId != 0) => set nodeInfo
		// HashTable and pass - thru to connections
		if ((msg.getProfileId() != 0)) {
			// nodeId from sourceURL
			sourceURL = msg.getSourceURL();
			nodeId = Integer.parseInt(sourceURL.substring(URL_PREFIX.length()));

			// Add "nodeId"/"node info msg" to nodeInfo
			nodeInfo.put(new Integer(nodeId), msg);

			// pass - thru connections
			for (int i = 0; i < connections.size(); i++)
				((EMUWSNConnection) connections.elementAt(i)).messageReceived(msg);
		} else {
			// Case "Data Message" (ProfileId = 0) => pass - thru to connections
			for (int i = 0; i < connections.size(); i++)
				((EMUWSNConnection) connections.elementAt(i)).messageReceived(msg);
		}
	}

	private Vector<WSNConnection> connections = new Vector<WSNConnection>();

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
	}

	public void reset() {
	}

	SocketThrdServer nodeCoordinator = new SocketThrdServer();

	public void start() {
		System.out.println("EMULocalNodeAdapter nodeAdapter.start()");
		nodeCoordinator.setTitle("WSN Emulator: Collector node");
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
		System.out.println("EMULLocalNodeAdapter in wainting ...");
	}

	public void stop() {
	}

	// send from EMULocalNodeAdapter to SocketThrdServer .... from
	// SocketThrdServer to Server Socket Node
	protected synchronized void send(int destNodeID, EMUMessage emumsg) {
		int nodeId = -1;

		try {
			switch ((byte) emumsg.getClusterId()) {
			case SPINEPacketsConstants.START:
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> Cmd manager.start or manager.startWsn");
				// nodeCoordinator.sendCommand(destNodeID,"START");
				for (Enumeration e = nodeInfo.keys(); e.hasMoreElements();) {
					Integer key = (Integer) e.nextElement();
					System.out.println(key + ":" + nodeInfo.get(key));
					System.out.println("Case START --> emumsg:" + emumsg.toString() + " pktType=" + emumsg.getClusterId());
					nodeId = key.intValue();
					nodeCoordinator.sendCommand(key.intValue(), emumsg);
				}
				break;
			case SPINEPacketsConstants.RESET:
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> Cmd manager.resetWsn");
				// nodeCoordinator.sendCommand(destNodeID, "RESET");
				nodeId = Integer.parseInt(emumsg.getDestinationURL());
				nodeCoordinator.sendCommand(Integer.parseInt(emumsg.getDestinationURL()), emumsg);
				break;
			case SPINEPacketsConstants.SYNCR:
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> Cmd manager.syncWsn or manager.synchrWsn");
				// nodeCoordinator.sendCommand(destNodeID, "SYNCR");
				System.out.println("Case SYNCR --> emumsg:" + emumsg.toString());
				break;
			case SPINEPacketsConstants.SERVICE_DISCOVERY:
				// Cmd discoveryWsn:execute from EMULocalNodeAdapter
				// for each nodo in nodeInfo pass-thru to connections
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> Cmd manager.discoveryWsn");
				for (Enumeration e = nodeInfo.keys(); e.hasMoreElements();) {
					Integer key = (Integer) e.nextElement();
					System.out.println(key + ":" + nodeInfo.get(key));
					System.out.println("Case SERVICE_DISCOVERY --> emumsg:" + emumsg.toString());
					nodeId = key.intValue();
					nodeCoordinator.sendCommand(key.intValue(), emumsg);
				}
				break;
			case SPINEPacketsConstants.SETUP_SENSOR:
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> Cmd manager.setupSensor() or manager.setup(..., SpineSetupSensor)");
				System.out.println("Case SETUP_SENSOR --> emumsg:" + emumsg.toString());
				// nodeCoordinator.sendCommand(destNodeID, "SETUP_SENSOR");
				nodeId = Integer.parseInt(emumsg.getDestinationURL());
				nodeCoordinator.sendCommand(Integer.parseInt(emumsg.getDestinationURL()), emumsg);
				break;
			case SPINEPacketsConstants.SETUP_FUNCTION:
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> Cmd manager.setupFunction() or manager.setup(..., SpineSetupFunction)");
				System.out.println("Case SETUP_FUNCTION --> emumsg:" + emumsg.toString());
				// nodeCoordinator.sendCommand(destNodeID, "SETUP_FUNCTION");
				nodeId = Integer.parseInt(emumsg.getDestinationURL());
				nodeCoordinator.sendCommand(Integer.parseInt(emumsg.getDestinationURL()), emumsg);
				break;
			case SPINEPacketsConstants.FUNCTION_REQ:
				System.out.println("EMULocalNodeAdapter nodeAdapter.send() --> FUNCTION_REQ");
				System.out.println("Case FUNTION_REQ --> emumsg:" + emumsg.toString());
				// nodeCoordinator.sendCommand(destNodeID, "FUNCTION_REQ");
				nodeId = Integer.parseInt(emumsg.getDestinationURL());
				nodeCoordinator.sendCommand(Integer.parseInt(emumsg.getDestinationURL()), emumsg);
				break;
			default:
				System.out.println("ERROR PktType");
			}
		} catch (IOException e1) {
			nodeInfo.remove(new Integer(nodeId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
