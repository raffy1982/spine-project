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
 * This class, together with EMULocalNodeAdapter, implements the generic API to accessing
 * a Node Emulator from the upper layers. 
 * 
 * Note that this class is only used internally at the framework.
 *
 * @author Alessia Salmeri
 *
 * @version 1.3
 */

package spine.communication.emu;

import jade.util.Logger;

import java.io.InterruptedIOException;

import com.tilab.gal.WSNConnection;

import spine.Properties;
import spine.SPINEManager;
import spine.SPINEPacketsConstants;
import spine.SPINESupportedPlatforms;

public class EMUWSNConnection implements WSNConnection {

	private WSNConnection.Listener listener = null;

	private EMULocalNodeAdapter adapter = null;

	protected EMUWSNConnection(EMULocalNodeAdapter adapter) {
		this.adapter = adapter;
	}

	public void messageReceived(com.tilab.gal.Message msg) {
		// just a pass-thru
		listener.messageReceived(msg);
	}

	public void close() {
	}

	public com.tilab.gal.Message poll() {
		return null;
	}

	public com.tilab.gal.Message receive() {
		return null;
	}

	public void send(com.tilab.gal.Message msg) throws InterruptedIOException, UnsupportedOperationException {

		try {
			// create a SPINE EMUMessage dependent message from a high level
			// Message object
			int destNodeID = Integer.parseInt(msg.getDestinationURL().substring(
					Properties.getDefaultProperties().getProperty(SPINESupportedPlatforms.EMULATOR + "_" + Properties.URL_PREFIX_KEY).length()));

			byte[] compressedPayload = new byte[0];
			try {
				short[] compressedPayloadShort = msg.getPayload();
				compressedPayload = new byte[compressedPayloadShort.length];
				for (int i = 0; i < compressedPayloadShort.length; i++)
					compressedPayload[i] = (byte) compressedPayloadShort[i];
			} catch (Exception e) {}

			EMUMessage emumsg = new EMUMessage();
			// pktType
			emumsg.setClusterId((byte) msg.getClusterId());
			// destID
			emumsg.setDestinationURL(Integer.toString(destNodeID));
			// sourceID
			emumsg.setSourceURL(Integer.toString(SPINEPacketsConstants.SPINE_BASE_STATION));
			short[] payloadShort = new short[compressedPayload.length];
			for (int h = 0; h < payloadShort.length; h++)
				payloadShort[h] = compressedPayload[h];
			// msg.setPayload(payloadShort);
			emumsg.setPayload(payloadShort);

			// sends the platform dependent message using the local node adapter
			adapter.send(destNodeID, emumsg);
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) {
				StringBuffer str = new StringBuffer();
				str.append("SENT -> ");
				str.append(emumsg);
				SPINEManager.getLogger().log(Logger.SEVERE, str.toString());
			}

		} catch (NumberFormatException e) {
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
				SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
				SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
		}
	}

	public void setListener(WSNConnection.Listener l) {
		this.listener = l;
	}
}
