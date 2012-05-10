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
 * @author Giuseppe Cristofaro
 *
 * @version 1.3
 * 
 * @see LocalNodeAdapter
 */

package spine.communication.bt;


import jade.util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import spine.Properties;
import spine.SPINEFactory;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.SPINEPacketsConstants;
import spine.SPINESupportedPlatforms;
import spine.datamodel.Address;
import spine.utils.ResourceManagerApplication;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.tilab.gal.ConfigurationDescriptor;
import com.tilab.gal.LocalNodeAdapter;
import com.tilab.gal.Message;
import com.tilab.gal.WSNConnection;

@SuppressWarnings("rawtypes")
public class BTLocalNodeAdapter extends LocalNodeAdapter {

	//API_VERSION used on Smartphone
	private final static int API_VERSION = Integer.valueOf(android.os.Build.VERSION.SDK);
	private static final String SENSOR_TYPE="FireFly-";
	private static final String SENSOR_TYPE2="RN42-";
	private final UUID SHIMMER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private WSNConnection wsnConnection = null;
	
	private BluetoothAdapter bta;
	
	private byte sequenceNumber = 0;
	
	private boolean discoveryCompleted = false;
	
	private int advReceived = 0;
	
	private Vector nodeConnections = new Vector(); // <values: BTNodeConnection>
	private Vector devices = new Vector(); // <values: BluetoothDevice>
	private Vector partials = new Vector(); // <values: Partial>
	
	private BTNodeConnection curr;
	private BluetoothDevice device;
	private BluetoothSocket socket;
	private BroadcastReceiver mReceiver;
	
	private boolean firstTime = true;
	@SuppressWarnings("unused")
	private boolean finish = false;
	
	public WSNConnection createAPSConnection() {
		wsnConnection = new BTWSNConnection(this);
		return wsnConnection;
	}

	public ConfigurationDescriptor getConfigurationDescriptor() {
		return null;
	}

	public void init(Vector parms) {}

	public void start() {
			
		bta = BluetoothAdapter.getDefaultAdapter();
		
		if(!bta.isEnabled()) {
			enableBT();
			
			while(!bta.isEnabled())
				try {
					Thread.sleep(1000);
				} catch(InterruptedException e) {}
		}
		
		registerReceiver();
	    
//	    setPaired();
//		
//		bta.startDiscovery();
//		ResourceManagerApplication.getContext().unregisterReceiver(mReceiver);

	}
	
	private void registerReceiver() {
		mReceiver = new MReceiver();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	    ResourceManagerApplication.getContext().registerReceiver(mReceiver, filter);
	    filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	    ResourceManagerApplication.getContext().registerReceiver(mReceiver, filter);
	}
	
	private void enableBT() {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ResourceManagerApplication.getContext().startActivity(enableBtIntent);
	}
		
	@SuppressWarnings("unchecked")
	private void setPaired() {
		for(BluetoothDevice bd : bta.getBondedDevices()) {	        			
			if( (bd.getName().contains(SENSOR_TYPE) || bd.getName().contains(SENSOR_TYPE2)) && !devices.contains(bd)) {
				Log.d("SET_PAIRED:",bd.getName());
				devices.add(bd);
			}	
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setDevices() {
		for (Object bd : devices) {
			try { 
				device = (BluetoothDevice)bd;
				if(device.getBondState() == BluetoothDevice.BOND_BONDED){
					//unsecure Socket since Android API Level 10 (Android 2.3.3)
					if(API_VERSION >= Build.VERSION_CODES.GINGERBREAD_MR1){
						socket = device.createInsecureRfcommSocketToServiceRecord(SHIMMER_UUID);
					}
					//secure Socket till Android API Level 9 (Android 2.3)
					else if(API_VERSION < Build.VERSION_CODES.GINGERBREAD_MR1){
						socket = device.createRfcommSocketToServiceRecord(SHIMMER_UUID);
					}
				}
				else { 
					//secure Socket if device is not bonded
					socket = device.createRfcommSocketToServiceRecord(SHIMMER_UUID);
				}
				socket.connect();
				Log.d("CONNECTION_OK: ",device.getName());
				curr = new BTNodeConnection(socket);
				curr.setNodeID(new Address(device.getAddress()));
				nodeConnections.add(curr);
			} catch(IOException e) {
				Log.d("CONNECTION_FAILED: ",device.getName());
			} 
		}
		
		if (nodeConnections.isEmpty()) {
			try {
				Iterator it = SPINEFactory.getSPINEManagerInstance()
						.getEventDispatcher().getListeners().iterator();
				while (it.hasNext()) {
					SPINEListener currListener = (SPINEListener) it.next();
					currListener.discoveryCompleted(SPINEFactory
							.getSPINEManagerInstance().getActiveNodes());
				}
				discoveryCompleted = true;
			} catch (InstantiationException e) {
			}
		}
	}
	
	private void startDevices() {
		for(Object o:nodeConnections) {
    		((BTNodeConnection)o).start();
    	}
	}

	public void stop() {}
	
	public void reset() {}
	
	private Message msg;
	
	protected void send(Message msg) {	
		
		if(firstTime) {
			setPaired();
			bta.startDiscovery();
			firstTime = false;
			this.msg = msg;
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
					
				try {
					if(btmsg.getHeader().getPktType() == SPINEPacketsConstants.RESET) {
						InputStream is = currConn.getSocket().getInputStream();
						if (is != null) {
							is.close();
							is = null;
						}
						OutputStream os = currConn.getSocket().getOutputStream();
						if (os != null) {
							os.close();
							os = null;
						}
						BluetoothSocket s = currConn.getSocket(); 
						if (s != null) {
							s.close();
							s = null;
						}
					}
				} 
				catch (IllegalSpineHeaderSizeException e) {}
				catch (IOException e) {}
					
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

	@SuppressWarnings("unchecked")
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
						} 
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
		return (advReceived == nodeConnections.size());
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
	
	private class MReceiver extends BroadcastReceiver { 
	
		public MReceiver() {
			super();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context context, Intent intent) {
			try{
			String action = intent.getAction();
			
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.d("DEVICE_FOUND: ", dev.getName());
				if (dev.getBondState() != BluetoothDevice.BOND_BONDED ){
					Log.d("SHIMMER_NOT_PAIRED: ", dev.getName());
				}
				if (dev.getBondState() != BluetoothDevice.BOND_BONDED && ( dev.getName().contains(SENSOR_TYPE) || dev.getName().contains(SENSOR_TYPE2)) && !devices.contains(dev)) {
					Log.d("SHIMMER_FOUND: ", dev.getName());
					devices.add(dev);
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				Log.d("DISCOVERY_FINISHED: ", action);
				if(bta.isDiscovering()){
					Log.d("DISCOVERING_IN_PROGRESS: ", "true");
					bta.cancelDiscovery();
				}
				finish = true;
				setDevices();
				startDevices();
				send(msg);
				}
			}catch(RuntimeException e){
				Log.e("ERROR_BROADCAST_INTENT: ", e.getMessage());
			}
		}
	}
}