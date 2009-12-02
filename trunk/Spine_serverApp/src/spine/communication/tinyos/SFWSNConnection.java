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
 * This class, together with SFLocalNodeAdapter, implements the generic API to accessing
 * a TinyOS base-station from the upper layers thru a (TinyOS) SerialForwarder. 
 * 
 * Note that this class is only used internally at the framework.
 *
 * @author Raffaele Gravina
 * @author Philip Kuryloski
 *
 * @version 1.2
 */

package spine.communication.tinyos;

import jade.util.Logger;

import java.io.InterruptedIOException;

import com.tilab.gal.WSNConnection;

import spine.Properties;
import spine.SPINEManager;
import spine.SPINEPacketsConstants;
import spine.SPINESupportedPlatforms;

public class SFWSNConnection implements WSNConnection {

	private byte sequenceNumber = 0; 
	
	private WSNConnection.Listener listener = null;	
	
	private SFLocalNodeAdapter adapter = null;
	
	
	protected SFWSNConnection (SFLocalNodeAdapter adapter) {
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
		
		byte fragmentNr = 1;
		byte totalFragments = 1;
		
		byte[] compressedPayload = new byte[0];
		try {
			// create a SPINE TinyOS dependent message from a high level Message object
			int destNodeID = Integer.parseInt(msg.getDestinationURL().substring(Properties.getDefaultProperties().getProperty(SPINESupportedPlatforms.TINYOS_VIA_SERIALFWD + "_" + Properties.URL_PREFIX_KEY).length()));
			
			try {
				short[] compressedPayloadShort = msg.getPayload();
				compressedPayload = new byte[compressedPayloadShort.length];
				for (int i = 0; i<compressedPayloadShort.length; i++)
					compressedPayload[i] = (byte)compressedPayloadShort[i];
			} catch (Exception e) {}
			
			SpineTOSMessage tosmsg = new SpineTOSMessage((byte)msg.getClusterId(), (byte)msg.getProfileId(),
														 SPINEPacketsConstants.SPINE_BASE_STATION, destNodeID, 
														 this.sequenceNumber++, fragmentNr, totalFragments, compressedPayload);
			
			// sends the platform dependent message using the local node adapter
			adapter.send(destNodeID, tosmsg);
			
			if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
				StringBuffer str = new StringBuffer();
				str.append("SENT -> ");
				str.append(tosmsg);
				SPINEManager.getLogger().log(Logger.INFO, str.toString());
			}	
			
			if ((byte)msg.getClusterId() == SPINEPacketsConstants.RESET)
				this.sequenceNumber = 0;
			
		} catch (NumberFormatException e) {
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
				SPINEManager.getLogger().log(Logger.INFO, e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
				SPINEManager.getLogger().log(Logger.INFO, e.getMessage());
		} 
		
	}

	public void setListener(WSNConnection.Listener l) {
		this.listener = l;		
	}

}
