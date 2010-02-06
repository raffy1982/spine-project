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

package logic;

import java.io.*;
import java.net.*;

import spine.communication.emu.EMUMessage;

/**
 * SocketServer - Each node is a SocketServer: it receive SPINE Command from
 * SPINE FRAMEWORK and pass-thru to SocketCommandListener.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */

// Each node is a SocketServer: it receive SPINE Command from SPINE FRAMEWORK
// and pass-thru to SocketCommandListener
class SocketServer implements Runnable {

	private int nodeCoordinatorPort;

	private PropertiesController propertControll;

	SocketCommandListener nodeEmul;

	ServerSocket server = null;

	SocketServer() { // Begin Constructor
		propertControll = PropertiesController.getInstance();
		propertControll.load();
	} // End Constructor

	public void registerListener(SocketCommandListener arg) {
		System.out.println("*** CommandListener:" + arg);
		nodeEmul = arg;
	}

	public void run() {

		int srcID = 0;
		String cmd = "";
		EMUMessage emumsg = new EMUMessage();

		Socket client = null;

		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;

		boolean ssPortAvailable = false;
		short sSPort = 0;

		// get a available port for Server Socket Node
		while (!ssPortAvailable) {
			try {
				sSPort = (short) getSSPort();
				server = new ServerSocket(sSPort);
				ssPortAvailable = true;
				// Send sSPort to VirtualNode
				cmd = "sSPort:" + sSPort;
				// Send the Server Socket Node port to VirtualNode
				nodeEmul.portReceived(srcID, cmd);
			} catch (IOException e) {
				System.err.println("Could not listen on port " + sSPort);
			}
		}

		try {
			client = server.accept();
			ois = new ObjectInputStream(client.getInputStream());
			oos = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			System.err.println("In or out failed");
		}

		while (true) {
			try {
				try {
					emumsg = (EMUMessage) ois.readObject();
					System.out.println("SPINECommand receive and pass-throu to node:" + emumsg.toString());

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				nodeEmul.commandReceived(srcID, emumsg);
			} catch (IOException e) {
				System.err.println("Read failed");
			}
		}

	}

	// Return a random port number
	private int getSSPort() {
		int sSPort;

		nodeCoordinatorPort = Integer.parseInt(propertControll.getProperty("MOTECOM"));
		// port number between nodeCoordinatorPort+1 and
		// nodeCoordinatorPort+1001
		sSPort = (int) (Math.random() * 1000 + nodeCoordinatorPort + 1);
		return sSPort;
	}

	protected void finalize() {
		// Objects created in run method are finalized when
		// program terminates and thread exits
		try {
			server.close();
		} catch (IOException e) {
			System.err.println("Could not close socket");
		}
	}

}
