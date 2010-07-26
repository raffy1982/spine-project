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
 *
 *  This class represents a connection to a single Bluetooth node and contains the low level 'send' and 'receive' primitives.
 *  It is used only by the TinyOS-Bluetooth platform dependent classes of the framework.
 *  
 *  Note that this class is only used internally at the framework. 
 *
 * @author Raffaele Gravina
 * @author Michele Capobianco
 *
 * @version 1.3
 */

package spine.communication.bt;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import spine.datamodel.Address;

public class BTNodeConnection extends Thread {

	InputStream inStream = null;
	OutputStream outStream = null;
	
	String connectionURL = null;
	BTLocalNodeAdapter adapter = null;
	
	Address nodeID = null;
	
	public Address getNodeID() {
		return nodeID;
	}

	public void setNodeID(Address nodeID) {
		this.nodeID = nodeID;
	}

	public BTNodeConnection(String connectionURL, BTLocalNodeAdapter adapter) {
		this.connectionURL = connectionURL;
		this.adapter = adapter;
		
		try {
			StreamConnection stCon = (StreamConnection)Connector.open(connectionURL);
			
			//Thread.sleep(100); // wait for sync... appears not necessary 
			
			inStream = stCon.openInputStream();
			outStream = stCon.openOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		  }
	}

	public void run() {
		while(true)
			adapter.received(this, receive());
	}

	public boolean send(byte[] msg) {
		try {
			byte[] data = new byte[msg.length + 1];
			data[0] = (byte)msg.length;
			System.arraycopy(msg, 0, data, 1, msg.length);
			outStream.write(data);
			outStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private byte[] receive() {
		try {
			int len = inStream.read(); // first byte contains the message length 
			byte[] msg = new byte[len];
			
			while (inStream.available() < len);
			
			inStream.read(msg);
			
			return msg;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String toString() {
		return this.connectionURL.substring(16,20) + " ["+nodeID+"]";
	}

}