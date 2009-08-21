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
 * This class, together with TOSLocalNodeAdapter, implements the generic API to accessing
 * a TinyOS base-station from the upper layers. 
 * 
 * Note that this class is only used internally at the framework.
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

package spine.communication.emule;

import java.io.InterruptedIOException;

import com.tilab.gal.WSNConnection;

import spine.Properties;
import spine.SPINEPacketsConstants;
import spine.SPINESupportedPlatforms;

public class EMUWSNConnection implements WSNConnection {

	//private byte sequenceNumber = 0; 
	
	private WSNConnection.Listener listener = null;	
	
	private EMULocalNodeAdapter adapter = null;
	
	
	protected EMUWSNConnection (EMULocalNodeAdapter adapter) {
		this.adapter = adapter;
	}
	
	
	public void messageReceived(com.tilab.gal.Message msg) {
		// just a pass-thru
		listener.messageReceived(msg);
	}
	
	
	public void close() {
		// TODO 
	}


	public com.tilab.gal.Message poll() {
		// TODO 
		return null;
	}

	public com.tilab.gal.Message receive() {
		// TODO 
		return null;
	}

	public void send(com.tilab.gal.Message msg) throws InterruptedIOException, UnsupportedOperationException {

		//byte fragmentNr = 1;
		//byte totalFragments = 1;
		
		try {
			// create a SPINE TinyOS dependent message from a high level Message object
			int destNodeID = Integer.parseInt(msg.getDestinationURL().substring(Properties.getDefaultProperties().getProperty(SPINESupportedPlatforms.EMULATOR + "_" + Properties.URL_PREFIX_KEY).length()));

			byte[] compressedPayload = new byte[0];
			try {
				short[] compressedPayloadShort = msg.getPayload();
				compressedPayload = new byte[compressedPayloadShort.length];
				for (int i = 0; i<compressedPayloadShort.length; i++)
					compressedPayload[i] = (byte)compressedPayloadShort[i];
			} catch (Exception e) {} 
			
			//SpineEMUMessage(byte pktType, byte groupID, int sourceID, int destID, byte sequenceNumber, byte fragmentNr, byte totalFragments, byte[] payload)
			//SpineEMUMessage emumsg = new SpineEMUMessage((byte)msg.getClusterId(), (byte)msg.getProfileId(), 
			//											 SPINEPacketsConstants.SPINE_BASE_STATION, destNodeID, 
			//			                                 this.sequenceNumber++, fragmentNr, totalFragments, compressedPayload);
			
			EMUMessage emumsg = new EMUMessage ();
			// pktType
			emumsg.setClusterId((byte)msg.getClusterId());
			// destID
			emumsg.setDestinationURL(Integer.toString(destNodeID));
			//sourceID
			emumsg.setSourceURL(Integer.toString(SPINEPacketsConstants.SPINE_BASE_STATION));
			//groupID
			//emumsg.setProfileId((byte)msg.getProfileId());
			// byte[] payload	
			short[] payloadShort = new short[compressedPayload.length];
			for (int h = 0; h<payloadShort.length; h++)
				payloadShort[h] = compressedPayload[h];					
			//msg.setPayload(payloadShort);
			emumsg.setPayload(payloadShort);
			
			
			// sends the platform dependent message using the local node adapter
			adapter.send(destNodeID, emumsg);
			System.out.println("Msg Sent -> " + emumsg);			
			
		} catch (NumberFormatException e) {
			System.out.println(e);
		}catch (IndexOutOfBoundsException e) {
			System.out.println(e);
		} 
		
	}

	public void setListener(WSNConnection.Listener l) {
		this.listener = l;		
	}

}
