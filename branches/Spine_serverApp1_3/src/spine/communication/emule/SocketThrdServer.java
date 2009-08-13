package spine.communication.emule;

import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.*;

import spine.Properties;

import java.io.*;
import java.net.*;
import java.util.*;


class ClientWorker implements Runnable {

	private static Properties prop = Properties.getProperties();
	
	private static final String URL_PREFIX = prop.getProperty(Properties.URL_PREFIX_KEY);
	
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
		String sourceURL="";
		String node="";
		
		
		EMUMessage msg = new EMUMessage();

		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		try {
			ois = new ObjectInputStream(client.getInputStream());
			oos = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			System.out.println("In or out failed");
			// System.exit(-1);
		}
		while (true) {
			//try {
				try {
					msg = (EMUMessage) ois.readObject();
					// In msg type Node Information the ProfileId contains
					// Server Socket Node port number (otherwise 0)
					sSPort = msg.getProfileId();
					sourceURL=msg.getSourceURL();
					//System.out.println("sourceURL --> " + sourceURL );
					//destNodeID = Integer.parseInt(sourceURL.substring(sourceURL.lastIndexOf(":") + 1));
					destNodeID = Integer.parseInt(sourceURL.substring(URL_PREFIX.length()));
					//destNodeID = Integer.parseInt(sourceURL.substring(URL_PREFIX.length()+1));
					if (sSPort != 0) {
						serverSocket.connectToSocketServerNode(destNodeID,sSPort);
					}
					emuLocalNodeAdapter.messageReceived(srcID, msg);
					textArea.append(msg.toString() + "\n");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					System.out.println("Read failed");
					// System.exit(-1);
				}
				//emu.messageReceived(srcID, msg);
				//textArea.append(msg.toString() + "\n");
			//} catch (IOException e) {
				//System.out.println("Read failed");
				// System.exit(-1);
			//}
		}
	}

}


class SocketThrdServer extends JFrame implements Runnable {

	SocketMessageListener emulAdap;

	JLabel label = new JLabel("Nodes information and data received over socket:");

	JPanel panel;

	JScrollPane jsp;

	JTextArea textArea = new JTextArea();

	ServerSocket server = null;

	private static Properties prop = Properties.getProperties();
	
	private static final int nodeCoordinatorPort = Integer.parseInt(prop.getProperty(Properties.MOTECOM_KEY));
	
	SocketThrdServer() { // Begin Constructor
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.white);
		panel.add("North", label);
		panel.add("Center", textArea);
		jsp = new JScrollPane(panel);
		jsp.setPreferredSize(new java.awt.Dimension(581, 47));
		getContentPane().add(jsp);
	} // End Constructor

	// EMULocalNodeAdapter is a SocketMessage listener
	public void registerListener(SocketMessageListener arg) {
		System.out.println("EMULLocalNodeAdapter registerListener (SocketMessageListener): " + arg);
		emulAdap = arg;
	}

	//ObjectInputStream oisClient = null;
	Hashtable oisClient = new Hashtable();

	//ObjectOutputStream oosClient = null;
	Hashtable oosClient = new Hashtable();
	

	Socket socket = null;
	
	public void run() {
		//Socket socket = null;

		try {
			//server = new ServerSocket(4444);
			server = new ServerSocket(nodeCoordinatorPort);
		} catch (IOException e) {
			System.out.println("Could not listen on port " + nodeCoordinatorPort);
			// System.exit(-1);
		}
		while (true) {
			ClientWorker w;
			try {
				w = new ClientWorker(server.accept(), textArea, emulAdap, this);
				Thread t = new Thread(w);
				t.start();
				
			} catch (IOException e) {
				System.out.println("Accept failed: 4444");
				// System.exit(-1);
			}
		}
	}

	// Create socket connection to Server Socket Node
	public void connectToSocketServerNode(int destNodeID, short sSPort) {
		try {
			//socket = new Socket("localhost", 4445);
			socket = new Socket("localhost", sSPort);
			System.out.println("Connection successful to Server Socket - Node " + destNodeID + " on port " + sSPort );			
			oosClient.put(new Integer(destNodeID), new ObjectOutputStream(socket.getOutputStream()));
			//oosClient = new ObjectOutputStream(socket.getOutputStream());
			oisClient.put(new Integer(destNodeID), new ObjectInputStream(socket.getInputStream()));
			//oisClient = new ObjectInputStream(socket.getInputStream());
			System.out.println("Do oosClient.writeChars");
			//oosClient.writeObject("Connection successful to Server Socket - Node");
			//oosClient.flush();
			//ObjectOutputStream oosC =(ObjectOutputStream) (oosClient.get(new Integer(destNodeID)));
			//oosC.writeObject("Connection successful to Server Socket - Node");
			//oosC.flush();

		} catch (UnknownHostException e) {
			System.out.println("Unknown host");
			// System.exit(1);
		} catch (IOException e) {
			System.out.println("No I/O");
			// System.exit(1);
		}
		
		
		
	}

	//public void sendCommand(int destNodeID, String cmd) {
	public void sendCommand(int destNodeID, EMUMessage emumsg) {
		try {
			//oosClient.writeObject(cmd);
			//oosClient.flush();
			//System.out.println("Send cmd: " + cmd + " to node: " + destNodeID);
			System.out.println("Send cmd: " + emumsg.toString() + " to node: " + destNodeID);
			ObjectOutputStream oosC =(ObjectOutputStream) (oosClient.get(new Integer(destNodeID)));
			//oosC.writeObject(cmd);
			oosC.writeObject(emumsg);
			oosC.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	protected void finalize() {
		// Objects created in run method are finalized when
		// program terminates and thread exits
		try {
			server.close();
		} catch (IOException e) {
			System.out.println("Could not close socket");
			// System.exit(-1);
		}
	}

}
