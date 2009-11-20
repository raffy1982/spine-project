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
 *  Coordinator of Virtual Node (Node Emulator instance). 
 * 
 * Note that this class is only used internally at the framework.
 *
 * @author Alessia Salmeri
 *
 * @version 1.3
 */

package spine.communication.emu;

import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.*;
import spine.Properties;
import spine.SPINEManager;
import spine.SPINESupportedPlatforms;

import java.io.*;
import java.net.*;
import java.util.*;

// There is a ClientWorker for each Virtual Node
class ClientWorker implements Runnable {

	private static Properties prop = Properties.getDefaultProperties();

	private static final String URL_PREFIX = prop.getProperty(SPINESupportedPlatforms.EMULATOR + "_" + Properties.URL_PREFIX_KEY);

	private Socket client;

	private JTextArea textArea;

	private SocketMessageListener emuLocalNodeAdapter;

	private SocketThrdServer serverSocket;

	ClientWorker(Socket client, JTextArea textArea, SocketMessageListener emuLocalNodeAdapter, SocketThrdServer serverSocket) {
		this.client = client;
		this.textArea = textArea;
		this.emuLocalNodeAdapter = emuLocalNodeAdapter;
		this.serverSocket = serverSocket;
	}

	public void run() {
		int srcID = 99;
		short sSPort = 0;
		int destNodeID = 0;
		String sourceURL = "";

		EMUMessage msg = new EMUMessage();

		ObjectInputStream ois = null;
		// NOT DELETE and NOT COMMENT oos (otherwise NodeEmulator not connect to WSN)
		ObjectOutputStream oos = null;
		try {
			ois = new ObjectInputStream(client.getInputStream());
			oos = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			System.out.println("In or out failed");
		}
		while (true) {
			try {
				msg = (EMUMessage) ois.readObject();
				// In msg type Node Information the ProfileId contains
				// Server Socket Node port number (otherwise 0)
				sSPort = msg.getProfileId();
				sourceURL = msg.getSourceURL();
				destNodeID = Integer.parseInt(sourceURL.substring(URL_PREFIX.length()));

				if (sSPort != 0) {
					serverSocket.connectToSocketServerNode(destNodeID, sSPort);
				}
				emuLocalNodeAdapter.messageReceived(srcID, msg);
				textArea.append(msg.toString() + "\n");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Read failed");
				break;
			}
		}
	}
}

class SocketThrdServer extends JFrame implements Runnable {
	
	private static final long serialVersionUID = 1L;

	SocketMessageListener emulAdap;

	JLabel label = new JLabel("Nodes information and data received over socket:");

	JPanel panel;

	JScrollPane jsp;

	JTextArea textArea = new JTextArea();

	ServerSocket server = null;

	private static final int nodeCoordinatorPort = Integer.parseInt(SPINEManager.getMoteCom());

	SocketThrdServer() {
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.white);
		panel.add("North", label);
		panel.add("Center", textArea);
		jsp = new JScrollPane(panel);
		jsp.setPreferredSize(new java.awt.Dimension(581, 47));
		getContentPane().add(jsp);
	}

	// EMULocalNodeAdapter is a SocketMessage listener
	public void registerListener(SocketMessageListener arg) {
		System.out.println("EMULLocalNodeAdapter registerListener (SocketMessageListener): " + arg);
		emulAdap = arg;
	}


	Hashtable<Integer, ObjectInputStream> oisClient = new Hashtable<Integer, ObjectInputStream>();
	Hashtable<Integer, ObjectOutputStream> oosClient = new Hashtable<Integer, ObjectOutputStream>();

	Socket socket = null;

	public void run() {
		try {
			server = new ServerSocket(nodeCoordinatorPort);
		} catch (IOException e) {
			System.out.println("Could not listen on port " + nodeCoordinatorPort);
		}
		while (true) {
			ClientWorker w;
			try {
				w = new ClientWorker(server.accept(), textArea, emulAdap, this);
				Thread t = new Thread(w);
				t.start();

			} catch (IOException e) {
				System.out.println("Accept failed");
			}
		}
	}

	// Create socket connection to Server Socket Node
	public void connectToSocketServerNode(int destNodeID, short sSPort) {
		try {
			socket = new Socket("localhost", sSPort);
			System.out.println("Connection successful to Server Socket - Node " + destNodeID + " on port " + sSPort);
			oosClient.put(new Integer(destNodeID), new ObjectOutputStream(socket.getOutputStream()));
			oisClient.put(new Integer(destNodeID), new ObjectInputStream(socket.getInputStream()));

		} catch (UnknownHostException e) {
			System.out.println("Unknown host");

		} catch (IOException e) {
			System.out.println("No I/O");
		}
	}

	public void sendCommand(int destNodeID, EMUMessage emumsg) throws IOException {
		try {
			System.out.println("Send cmd: " + emumsg.toString() + " to node: " + destNodeID);
			ObjectOutputStream oosC = (ObjectOutputStream) (oosClient.get(new Integer(destNodeID)));
			oosC.writeObject(emumsg);
			oosC.flush();
		} catch (IOException e) {
			throw e;
		}
	}

	protected void finalize() {
		// Objects created in run method are finalized when
		// program terminates and thread exits
		try {
			server.close();
		} catch (IOException e) {
			System.out.println("Could not close socket");
		}
	}

}
