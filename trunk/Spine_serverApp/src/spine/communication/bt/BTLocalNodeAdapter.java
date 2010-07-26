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
 * Implementation of the GAL LocalNodeAdapter.
 * This class is responsible to implement the specific logic of accessing the Bluetooth stack 
 * in a way complying to the GAL APIs. 
 * 
 * Note that this class is only used internally at the framework. 
 *
 * @author Raffaele Gravina
 * @author Michele Capobianco
 *
 * @version 1.3
 * 
 * @see LocalNodeAdapter
 */

package spine.communication.bt;

import jade.util.Logger;

import java.util.Iterator;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

import spine.Properties;
import spine.SPINEFactory;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.SPINEPacketsConstants;
import spine.SPINESupportedPlatforms;
import spine.datamodel.Address;

import com.tilab.gal.ConfigurationDescriptor;
import com.tilab.gal.LocalNodeAdapter;
import com.tilab.gal.Message;
import com.tilab.gal.WSNConnection;


public class BTLocalNodeAdapter extends LocalNodeAdapter implements DiscoveryListener, Runnable {

	private WSNConnection wsnConnection = null;
	
	private Vector nodeConnections = new Vector(); // <values: BTNodeConnection>
	
	private Vector remoteDevices = new Vector(); // <values: RemoteDevice>
	
	private byte sequenceNumber = 0;
	
	private boolean discoveryCompleted = false;
	private boolean discoverySPINENodes = false;
	
	private int advReceived = 0;
	
	private Vector partials = new Vector(); // <values: Partial>
	
	private UUID[] uuidSet = {new UUID(0x1101)};
	private int[] attrSet = {0x0100, 0x0003, 0x0004};
	
	private DiscoveryAgent discoveryAgent = null;
	
	
	public WSNConnection createAPSConnection() {
		wsnConnection = new BTWSNConnection(this);
		return wsnConnection;
	}

	public ConfigurationDescriptor getConfigurationDescriptor() {
		return null;
	}

	public void init(Vector parms) {}

	public void run() {
		new DiscoveryThread().start();
	}
	
	public void start() {
		run();
		try {
			LocalDevice localDevice = LocalDevice.getLocalDevice();
			discoveryAgent = localDevice.getDiscoveryAgent();
			
			if (SPINEManager.getLogger().isLoggable(Logger.INFO)) 
				SPINEManager.getLogger().log(Logger.INFO, "[BT] Searching for Bluetooth devices...");
			
			discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
		} catch(BluetoothStateException e) {
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
				SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
		}
	}

	public void stop() {}
	
	public void reset() {}
	
	
	protected void send(Message msg) {		
		
		if ((byte)msg.getClusterId() == SPINEPacketsConstants.SERVICE_DISCOVERY) {
			discoverySPINENodes = true;
		}
		else {		
			int destNodeID = Integer.parseInt(msg.getDestinationURL().substring(Properties.getDefaultProperties().getProperty(SPINESupportedPlatforms.BLUETOOTH + "_" + Properties.URL_PREFIX_KEY).length()));
			
			byte[] compressedPayload = new byte[0];
			try {
				short[] compressedPayloadShort = msg.getPayload();
				compressedPayload = new byte[compressedPayloadShort.length];
				for (int i = 0; i<compressedPayloadShort.length; i++)
					compressedPayload[i] = (byte)compressedPayloadShort[i];
			} catch (Exception e) {} 
			
			SpineBTMessage btmsg = new SpineBTMessage((byte)msg.getClusterId(), (byte)msg.getProfileId(), 
					 SPINEPacketsConstants.SPINE_BASE_STATION, destNodeID, 
					 ++this.sequenceNumber, (byte)1, (byte)1, compressedPayload);
			
			Iterator it = nodeConnections.iterator();
			while (it.hasNext()) {
				BTNodeConnection currConn = (BTNodeConnection)it.next();
				if (currConn.getNodeID().getAsInt() == destNodeID) {
					currConn.send(btmsg.getRawMessage());
					if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
						StringBuffer str = new StringBuffer();
						str.append("SENT -> ");
						str.append(btmsg);				
						SPINEManager.getLogger().log(Logger.INFO, str.toString());
					}
					break;
				}
			}
			
			if(destNodeID == SPINEPacketsConstants.SPINE_BROADCAST) {
				it = nodeConnections.iterator();
				while (it.hasNext()) {
					BTNodeConnection currConn = (BTNodeConnection)it.next();
					currConn.send(btmsg.getRawMessage());
					if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
						StringBuffer str = new StringBuffer();
						str.append("SENT -> ");
						str.append(btmsg);				
						SPINEManager.getLogger().log(Logger.INFO, str.toString());
					}					
				}
			}
		}
	}

	public void received(BTNodeConnection nodeConnection, byte[] msg) {
		if (msg != null) {
			try {
				byte[] headerBuf = new byte[SPINEPacketsConstants.SPINE_HEADER_SIZE];
				System.arraycopy(msg, 0, headerBuf, 0,
						SPINEPacketsConstants.SPINE_HEADER_SIZE);
				SPINEHeader header = new SPINEHeader(headerBuf);

				byte[] payloadBuf = new byte[msg.length
						- SPINEPacketsConstants.SPINE_HEADER_SIZE];
				System.arraycopy(msg, SPINEPacketsConstants.SPINE_HEADER_SIZE,
						payloadBuf, 0, payloadBuf.length);

				BTMessage message = new BTMessage();
				
				message.setClusterId(header.getPktType());
				message.setProfileId(header.getGroupID());
				
				int sourceNodeID = header.getSourceID();
				message.setSourceURL(SpineBTMessage.BT_URL_PREFIX + sourceNodeID);
				
				message.setDestinationURL(SpineBTMessage.BT_URL_PREFIX + header.getDestID());
				message.setSeqNo(header.getSequenceNumber());

				short[] payloadBufShort = new short[payloadBuf.length];
				for (int i = 0; i < payloadBuf.length; i++)
					payloadBufShort[i] = payloadBuf[i];

				message.setPayload(payloadBufShort);
				
				
				// START "re-assembly of fragments into complete messages"
				if (header.getTotalFragments() != 1) {
					int index = inPartials(sourceNodeID, header.getSequenceNumber());
					if (index == -1) {
						if (header.getFragmentNumber() != 1)
							return;
						else {
							partials.addElement(new Partial(sourceNodeID, header.getSequenceNumber(), 
															header.getTotalFragments(), 
															payloadBuf));
							return;
						}
					}
					else {
						if (header.getFragmentNumber() != ( ((Partial)partials.elementAt(index)).lastFragmentNr + 1 ) ) {
							partials.removeElementAt(index); // no need to keep a partial if a fragment is lost
							return;
						}
						else {
							if (header.getFragmentNumber() < ((Partial)partials.elementAt(index)).totFragments) {
								((Partial)partials.elementAt(index)).addToPayload( payloadBuf );
								return;
							}
							else {
								Partial complete = ((Partial)partials.elementAt(index));
								complete.addToPayload( payloadBuf );
								message.setPayload(complete.getPartialPayloadShort());
								partials.removeElementAt(index);								
							}
						}
					}
				}
				// END "re-assembly of fragments into complete messages"
				
				payloadBuf = new byte[message.getPayload().length];
				for (int i = 0; i < payloadBuf.length; i++)
					payloadBuf[i] = (byte)message.getPayload()[i];
				
				SpineBTMessage btmsg = new SpineBTMessage(header.getPktType(), 
						header.getGroupID(), header.getSourceID(), header.getDestID(), 
						header.getSequenceNumber(), header.getFragmentNumber(), 
						header.getTotalFragments(), payloadBuf);
				
				if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
					StringBuffer str = new StringBuffer();
					str.append("REC. -> ");
					str.append(btmsg);				
					SPINEManager.getLogger().log(Logger.INFO, str.toString());
				}

				((BTWSNConnection) wsnConnection).received(message);
				

				if (header.getPktType() == SPINEPacketsConstants.SERVICE_ADV) {
					nodeConnection.setNodeID(new Address(""+ header.getSourceID()));
					advReceived++;
					
					if (!discoveryCompleted) {
						if (allMapped()) { // TODO could an active NOT SPINE shimmer mote in the nearby create problems?!
							Iterator it = SPINEFactory.getSPINEManagerInstance().getEventDispatcher().getListeners().iterator();
							while (it.hasNext()) {
								SPINEListener currListener = (SPINEListener) it.next();
								currListener.discoveryCompleted(SPINEFactory.getSPINEManagerInstance().getActiveNodes());
							}
							discoveryCompleted = true;
						} /*else if (nodeConnections.size() < 2) {
							ServiceErrorMessage serviceErrorMessage = new ServiceErrorMessage();
							Node baseStation = new Node(new Address(""
									+ SPINEPacketsConstants.SPINE_BASE_STATION));
							baseStation
									.setLogicalID(new Address(
											SPINEPacketsConstants.SPINE_BASE_STATION_LABEL));
							serviceErrorMessage.setNode(baseStation);
							serviceErrorMessage
									.setMessageDetail(SPINEServiceMessageConstants.CONNECTION_FAIL);

							Iterator it;
							try {
								it = SPINEFactory.getSPINEManagerInstance()
										.getEventDispatcher().getListeners()
										.iterator();
								while (it.hasNext()) {
									SPINEListener currListener = (SPINEListener) it
											.next();
									currListener.received(serviceErrorMessage);
									currListener
											.discoveryCompleted(SPINEFactory
													.getSPINEManagerInstance()
													.getActiveNodes());
								}
							} catch (InstantiationException e) {
								e.printStackTrace();
							}

							discoveryCompleted = true;
						}*/
					}
				}
				
			} catch (IllegalSpineHeaderSizeException e) {
				if (SPINEManager.getLogger().isLoggable(Logger.WARNING)) 
					SPINEManager.getLogger().log(Logger.WARNING, "[SPINE1.3-MALFORMED-HEADER]... discarded!");
			} catch (InstantiationException e) {
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
					SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
			}
		}
	}
	
	private boolean allMapped() {
		int mapped = 0;
		Iterator it = nodeConnections.iterator();
		while (it.hasNext()) {
			if (((BTNodeConnection)it.next()).getNodeID() != null)
				mapped++;
		}
		return (mapped == nodeConnections.size() && advReceived == SPINEManager.getBTNetworkSize());
	}
	
	public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass cod) {
		try {
			if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
				StringBuffer str = new StringBuffer();
				str.append("[BT] Device: ");
				str.append(remoteDevice.getFriendlyName(true));				
				SPINEManager.getLogger().log(Logger.INFO, str.toString());
			}
			if(remoteDevice.getFriendlyName(true).length() > 7 && remoteDevice.getFriendlyName(true).substring(0,7).equals("FireFly")) {
            	remoteDevices.addElement(remoteDevice);
            	if (SPINEManager.getLogger().isLoggable(Logger.INFO))			
    				SPINEManager.getLogger().log(Logger.INFO, "... The device is a Shimmer Mote: will check if active!");
			}
			else 
				if (SPINEManager.getLogger().isLoggable(Logger.INFO))			
    				SPINEManager.getLogger().log(Logger.INFO, "... The device is NOT a Shimmer Mote: discarded!");
		} catch(Exception e){
			//System.err.println("[BT] A new device has been discovered, but could not get its name! ");
			//e.printStackTrace();
		}
	}

	public void inquiryCompleted(int discType) {
		for(int i=0; i<remoteDevices.size(); i++) {
			try {
				RemoteDevice remoteDevice = (RemoteDevice)remoteDevices.get(i);
	
				discoveryAgent.searchServices(attrSet, uuidSet, remoteDevice, this);
				
				//Thread.sleep(1000);
			} catch (BluetoothStateException e){
				if (SPINEManager.getLogger().isLoggable(Logger.WARNING)) 
					SPINEManager.getLogger().log(Logger.WARNING, e.getMessage());
			}
		}		
	}
	
	int discoveredNodes = 0;
	
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {

		for(int i = 0; i < servRecord.length; i++) {

	        DataElement serviceNameElement = servRecord[i].getAttributeValue(0x0100);
			String serviceName = (String)serviceNameElement.getValue();

			if(serviceName.equals("SPP")) {
				nodeConnections.addElement(new BTNodeConnection(servRecord[i].getConnectionURL(1, false), this));
				discoveredNodes++;
				if (discoveredNodes == SPINEManager.getBTNetworkSize())
					serviceSearchCompleted();				
			}
		}		
	}
	
	private void serviceSearchCompleted() {
		if(discoverySPINENodes) {
			SpineBTMessage btmsg = new SpineBTMessage(SPINEPacketsConstants.SERVICE_DISCOVERY, SPINEManager.getMyGroupID(), 
					 SPINEPacketsConstants.SPINE_BASE_STATION, SPINEPacketsConstants.SPINE_BROADCAST, 
					 this.sequenceNumber, (byte)1, (byte)1, new byte[]{});
			
			Iterator it = nodeConnections.iterator();
			while(it.hasNext()) {
				BTNodeConnection curr = (BTNodeConnection)it.next();
				if (!curr.isAlive()) {
					curr.start();
					curr.send(btmsg.getRawMessage());
					if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
						StringBuffer str = new StringBuffer();
						str.append("SENT -> ");
						str.append(btmsg);				
						SPINEManager.getLogger().log(Logger.INFO, str.toString());
					}								
				}
			}	
		}		
	}
	
	public void serviceSearchCompleted(int transID, int respCode) {
		
	}
	
	
	private class DiscoveryThread extends Thread {		
		public void run () {
			try {
				while(!discoveryCompleted)
					sleep(100);
			} catch (InterruptedException e) {}
		}
	}
	
	private class Partial {
		int nodeID;
		byte seqNr;
		byte lastFragmentNr;
		byte totFragments;
		byte[] partialPayload;
		
		private Partial(int nodeID, byte seqNr, byte totFragments, byte[] partialPayload) {
			this.nodeID = nodeID;
			this.seqNr = seqNr;
			this.lastFragmentNr = 1;
			this.totFragments = totFragments;
			this.partialPayload = partialPayload;
		}
		
		public short[] getPartialPayloadShort() {
			short[] payloadBufShort = new short[partialPayload.length];
			for (int i = 0; i < partialPayload.length; i++)
				payloadBufShort[i] = partialPayload[i];
			return payloadBufShort;
		}

		private void addToPayload(byte[] newPartial) {
			byte[] newPartialPayload = new byte[partialPayload.length + newPartial.length];
			System.arraycopy(partialPayload, 0, newPartialPayload, 0, partialPayload.length);
			System.arraycopy(newPartial, 0, newPartialPayload, partialPayload.length, newPartial.length);
			
			partialPayload = newPartialPayload;
			
			lastFragmentNr++;
		}
		
		private boolean equal(int nodeID, byte seqNr) {
			return (this.nodeID == nodeID && this.seqNr == seqNr);
		}
		
	}
	
	private int inPartials(int sourceID, byte sequenceNumber) {
		for (int i = 0; i<this.partials.size(); i++)
			if (((Partial)partials.elementAt(i)).equal(sourceID, sequenceNumber)) return i;
		return -1;
	}
}
