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
 * Implementation of the GAL WSNConnection.
 * This class, together with BTLocalNodeAdapter, implements the generic API to accessing
 * a TinyOS-Bluetooth base-station from the upper layers. 
 * 
 * Note that this class is only used internally at the framework.
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

package spine.communication.bt;

import java.io.InterruptedIOException;

import com.tilab.gal.WSNConnection;

public class BTWSNConnection implements WSNConnection {

	private WSNConnection.Listener listener = null;	
	
	private BTLocalNodeAdapter adapter = null;
	
	
	protected BTWSNConnection (BTLocalNodeAdapter adapter) {
		this.adapter = adapter;
	}	
	
	public void received(com.tilab.gal.Message msg) {
		listener.messageReceived(msg); // just a pass-thru
	}
	
	public void send(com.tilab.gal.Message msg) throws InterruptedIOException, UnsupportedOperationException {
		adapter.send(msg); // just a pass-thru		
	}
	
	public void close() {}

	public com.tilab.gal.Message poll() {
		return null;
	}

	public com.tilab.gal.Message receive() {
		return null;
	}

	public void setListener(WSNConnection.Listener l) {
		this.listener = l;		
	}

}
