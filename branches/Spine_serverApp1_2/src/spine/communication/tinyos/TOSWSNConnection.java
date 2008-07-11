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

package spine.communication.tinyos;

import java.io.InterruptedIOException;

import com.tilab.zigbee.WSNConnection;

import spine.SPINEPacketsConstants;

public class TOSWSNConnection implements WSNConnection {

	private static final String TINYOS_URL_PREFIX = "http://tinyos:";
	
	private static final int URL_PREFIX_LENGTH = TINYOS_URL_PREFIX.length();	
	
	private byte sequenceNumber = 0; 
	
	private WSNConnection.Listener listener = null;	
	
	private TOSLocalNodeAdapter adapter = null;
	
	
	TOSWSNConnection (TOSLocalNodeAdapter adapter) {
		this.adapter = adapter;
	}
	
	
	public void messageReceived(com.tilab.zigbee.Message msg) {
		listener.messageReceived(msg);
	}
	
	
	public void close() {
		// TODO 
	}


	public com.tilab.zigbee.Message poll() {
		// TODO 
		return null;
	}

	public com.tilab.zigbee.Message receive() {
		// TODO 
		return null;
	}

	public void send(com.tilab.zigbee.Message msg) throws InterruptedIOException, UnsupportedOperationException {
		
		byte fragmentNr = 1;
		byte totalFragments = 1;
		
		try {
			
			int destNodeID = Integer.parseInt(msg.getDestinationURL().substring(URL_PREFIX_LENGTH));
			byte[] compressedPayload = PacketManager.build(msg);
			SpineTOSMessage tosmsg = new SpineTOSMessage((byte)msg.getClusterId(), (byte)msg.getProfileId(), 
														 SPINEPacketsConstants.SPINE_BASE_STATION, destNodeID, 
														 this.sequenceNumber++, fragmentNr, totalFragments, compressedPayload);
			
			printPayload(compressedPayload); // DEBUG CODE		
			
			adapter.send(destNodeID, tosmsg);
			
		} catch (NumberFormatException e) {
			System.out.println(e);
		}catch (IndexOutOfBoundsException e) {
			System.out.println(e);
		}
		
	}

	public void setListener(WSNConnection.Listener l) {
		this.listener = l;		
	}
	
	private void printPayload(byte[] payload) {  // DEBUG CODE
		System.out.print("out: ");
		if(payload == null || payload.length == 0)
			System.out.print("empty payload");
		else{
			for (int i = 0; i<payload.length; i++) {
				short b =  payload[i];
				if (b<0) b += 256;
				System.out.print(Integer.toHexString(b) + " ");
			}
		}
		System.out.println("\n");		
	}

}
