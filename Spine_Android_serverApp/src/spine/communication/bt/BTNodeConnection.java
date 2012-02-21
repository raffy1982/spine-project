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
 * @author Giuseppe Cristofaro
 *
 * @version 1.3
 */

package spine.communication.bt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import spine.datamodel.Address;
import android.bluetooth.BluetoothSocket;

import com.tilab.gal.LocalNodeAdapter;

public class BTNodeConnection extends Thread{

	BluetoothSocket socket;
	InputStream inStream;
	OutputStream outStream;
	boolean started = false;
	BTLocalNodeAdapter adapter = null;
	Address nodeID = null;
	
	public Address getNodeID() {
		return nodeID;
	}

	public void setNodeID(Address nodeID) {
		this.nodeID = nodeID;
	}

	public BTNodeConnection(BluetoothSocket socket) {

		this.socket = socket;
		try {
			inStream = socket.getInputStream();
			outStream = socket.getOutputStream();
			this.adapter = (BTLocalNodeAdapter)LocalNodeAdapter.getLocalNodeAdapter();
		} catch (ClassNotFoundException e1) {} 
		catch (InstantiationException e1) {} 
		catch (IllegalAccessException e1) {} 
		catch (IOException e) {}
		
	}
	
	public BluetoothSocket getSocket() {
		return socket;
	}

	public void run() {
		started = true;		
		while(true)
			adapter.received(this, receive()); 
	}

	public boolean send(byte[] msg) {
		while(!started)
			try {
				Thread.sleep(200);
		} catch (InterruptedException e) {}
		
		try {
			byte[] data = new byte[msg.length + 1];
			data[0] = (byte)msg.length;
			System.arraycopy(msg, 0, data, 1, msg.length);
			outStream.write(data);
			outStream.flush();

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private byte[] receive() {
		try {
			int len = inStream.read(); // first byte contains the message length 
					
			byte[] msg = new byte[len];
			
			for(int i = 0; i<len; i++) {
				msg[i] = (byte) inStream.read();
			}
			
			return msg;
			
		} catch (IOException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public String toString() {
		return "BTNodeConnection ["+nodeID+"]";
	}
	
}