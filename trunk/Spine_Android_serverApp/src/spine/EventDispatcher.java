/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
�
GNU Lesser General Public License
�
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
�
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
�
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package spine;


import jade.util.Logger;

import java.util.Vector;

import spine.datamodel.Address;
import spine.datamodel.Data;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;
import spine.datamodel.functions.CodecInfo;
import spine.datamodel.functions.SpineCodec;
import spine.datamodel.functions.SpineObject;
import spine.datamodel.serviceMessages.ServiceWarningMessage;
import spine.exceptions.MethodNotSupportedException;
import spine.exceptions.PacketDecodingException;
import spine.exceptions.UnexpectedMessageException;


import com.tilab.gal.WSNConnection;

/**
 * This class is responsible for dispatching events on behalf of the SPINEManager
 * @author Fabio Bellifemine, Telecom Italia
 * @since 1.3
 */
public class EventDispatcher extends Thread {

	SPINEManager spineManager;
	
	/** package-scoped constructor  **/
	EventDispatcher(SPINEManager spineManager) {
		this.spineManager = spineManager;
		this.spineManager.connection.setListener(new WSNConnectionListenerImpl());
	}
	

	
	@SuppressWarnings("rawtypes")
	private Vector listeners = new Vector(1); // initialized to 1 element as we expect usually to have just 1 listener
	
	/**
	 * Returns the list of listeners registered to the SPINE events
	 * 
	 * @return a Vector of SPINEListener objects
	 */
	@SuppressWarnings("rawtypes")
	public Vector getListeners() {
		return listeners;
	}

	/**
	 * Registers a SPINEListener with the manager instance
	 * 
	 * @param listener the listener to register
	 */
	@SuppressWarnings("unchecked")
	void addListener(SPINEListener listener) {
		this.listeners.addElement(listener);
	}
	
	
	
	
	/**
	 * Deregisters a SPINEListener with the manager instance
	 * 
	 * @param listener the listener to deregister
	 */
	 void removeListener(SPINEListener listener) {
		this.listeners.removeElement(listener);
	}
	
	
	/*
	 * Regarding to the 'eventType', this method notify the SPINEListeners properly, by
	 * casting in the right way the Object 'o'.
	 * Notice that if the eventType is SERVICE_ADV the listener is notified only if !discoveryCompleted
	 * @param eventType
	 * @param o
	 * @param spineManager a reference to the SPINEManager which is used to retrieve activeNodes, baseStation, and discoveryCompleted 
	 */
	 @SuppressWarnings("rawtypes")
	void notifyListeners(short eventType, Object o) {
		for (int i = 0; i<this.listeners.size(); i++) 
			switch(eventType) {
				case SPINEPacketsConstants.SERVICE_ADV:
					if (!spineManager.isDiscoveryCompleted())
						((SPINEListener)this.listeners.elementAt(i)).newNodeDiscovered((Node)spineManager.getActiveNodes().lastElement()); 
					break;
				case SPINEPacketsConstants.DATA: 
					((SPINEListener)this.listeners.elementAt(i)).received((Data)o); 
					break;	
				case SPINEPacketsConstants.SVC_MSG: 
					if(((ServiceMessage)o).getNode() != null) 
						((SPINEListener)this.listeners.elementAt(i)).received((ServiceMessage)o);
					break;
				case SPINEManager.DISC_COMPL_EVT_COD:
					((SPINEListener)this.listeners.elementAt(i)).discoveryCompleted((Vector)o);
					break;
				default: {
					ServiceMessage sm = new ServiceWarningMessage();
					sm.setMessageDetail(SPINEServiceMessageConstants.UNKNOWN_PKT_RECEIVED);
					sm.setNode(spineManager.getBaseStation());
					((SPINEListener)this.listeners.elementAt(i)).received(sm);				
					break;
				}
			}
		
	}
	 
	
	 private class WSNConnectionListenerImpl implements WSNConnection.Listener {
			
			/*
			 * This method is called to notify the SPINEManager of a new SPINE message reception. 
			 */
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void messageReceived(com.tilab.gal.Message msg) {				
				Address nodeID = new Address(msg.getSourceURL().substring(SPINEManager.URL_PREFIX.length()));
				
				SpineObject o = null;
				
				short pktType = msg.getClusterId();
				short[] payloadShort = msg.getPayload();
				byte[] payload = new byte[payloadShort.length];
				for (int i = 0; i<payloadShort.length; i++)
					payload[i] = (byte)payloadShort[i];
		
				switch(pktType) {
					case SPINEPacketsConstants.SERVICE_ADV: {
						try {
							// dynamic class loading of the proper SpineCodec implementation						
							EventDispatcher.this.spineManager.spineCodec = (SpineCodec)EventDispatcher.this.spineManager.htInstance.get("ServiceAdvertisement");
							
							 if (EventDispatcher.this.spineManager.spineCodec == null) {
								 Class d = Class.forName(SPINEManager.SPINEDATACODEC_PACKAGE + "ServiceAdvertisement");
								 EventDispatcher.this.spineManager.spineCodec = (SpineCodec)d.newInstance();
								 EventDispatcher.this.spineManager.htInstance.put("ServiceAdvertisement", EventDispatcher.this.spineManager.spineCodec);
							 }
							 
							 // Invoking decode and setting SpineObject data
							 o = EventDispatcher.this.spineManager.spineCodec.decode(new Node(nodeID), payload);
							 
						} catch (Exception e) { 
							e.printStackTrace();
							if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
								SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
							return;
						} 
											
						if (!EventDispatcher.this.spineManager.discoveryCompleted) {
							boolean alreadyDiscovered = false;
							for(int i = 0; i<EventDispatcher.this.spineManager.activeNodes.size(); i++) {
								if(((Node)EventDispatcher.this.spineManager.activeNodes.elementAt(i)).getPhysicalID().equals(nodeID)) {
									alreadyDiscovered = true;
									break;
								}
							}
							if (!alreadyDiscovered)
								EventDispatcher.this.spineManager.activeNodes.addElement((Node)o);
						}					
						break;
					}
					case SPINEPacketsConstants.DATA: {
						if(EventDispatcher.this.spineManager.getNodeByPhysicalID(nodeID) == null)
							 throw new UnexpectedMessageException("Unexpected DATA message received " +
							 		"[from node:" + nodeID + "]");					 
						 
						byte functionCode;					
						//  Setting functionCode
						try {
							// dynamic class loading of the proper CodecInformation
							CodecInfo codecInformation = (CodecInfo)EventDispatcher.this.spineManager.htInstance.get("CodecInformation");
							if (codecInformation == null) {
								Class g = Class.forName(SPINEManager.SPINEDATACODEC_PACKAGE + "CodecInformation");
								codecInformation = (CodecInfo)g.newInstance();	
								EventDispatcher.this.spineManager.htInstance.put("CodecInformation", codecInformation);
							} 
							functionCode = codecInformation.getFunctionCode(payload);
						} catch (Exception e) { 
							e.printStackTrace();
							if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
								SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
							return;
						} 										
						
						try {
							// dynamic class loading of the proper SpineCodec implementation
							String className = SPINEFunctionConstants.functionCodeToString(functionCode) + SPINEManager.SPINEDATA_FUNCT_CLASSNAME_SUFFIX;
							EventDispatcher.this.spineManager.spineCodec = (SpineCodec)EventDispatcher.this.spineManager.htInstance.get (className);
							 if (EventDispatcher.this.spineManager.spineCodec == null){
								 Class d = Class.forName(SPINEManager.SPINEDATACODEC_PACKAGE + className);
								 EventDispatcher.this.spineManager.spineCodec = (SpineCodec)d.newInstance();
								 EventDispatcher.this.spineManager.htInstance.put(className, EventDispatcher.this.spineManager.spineCodec);
							 }
							 
							 // Invoking decode and setting SpineObject data
							 o = EventDispatcher.this.spineManager.spineCodec.decode(EventDispatcher.this.spineManager.getNodeByPhysicalID(nodeID), payload);
							
						} catch (PacketDecodingException e) {
							e.printStackTrace();
							if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
								SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
							return;
						} catch (MethodNotSupportedException e) {
							e.printStackTrace();
							if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
								SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
							return;
						} catch (InstantiationException e) {
							e.printStackTrace();
							if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
								SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
							return;
						} catch (IllegalAccessException e) {
							e.printStackTrace();
							if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
								SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
							return;
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
								SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
							return;
						}
						break;
					}
					case SPINEPacketsConstants.SVC_MSG: {
						
						byte serviceMessageType;
						
						//  Setting functionCode
						try {
							// dynamic class loading of the proper CodecInformation
							CodecInfo codecInformation = (CodecInfo)EventDispatcher.this.spineManager.htInstance.get("CodecInformation");
							if (codecInformation == null) {
								Class g = Class.forName(SPINEManager.SPINEDATACODEC_PACKAGE + "CodecInformation");
								codecInformation = (CodecInfo)g.newInstance();	
								EventDispatcher.this.spineManager.htInstance.put("CodecInformation", codecInformation);
							} 
							serviceMessageType = codecInformation.getServiceMessageType(payload);
						} catch (Exception e) { 
							e.printStackTrace();
							if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
								SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
							return;
						} 
						
						try {
							// dynamic class loading of the proper SpineCodec implementation
							String className = SPINEServiceMessageConstants.serviceMessageTypeToString(serviceMessageType) + 
							SPINEManager.SPINE_SERVICE_MESSAGE_CLASSNAME_SUFFIX;
							EventDispatcher.this.spineManager.spineCodec = (SpineCodec)EventDispatcher.this.spineManager.htInstance.get(className);
							
							if (EventDispatcher.this.spineManager.spineCodec == null){
								Class d = Class.forName(SPINEManager.SPINE_SERVICE_MESSAGE_CODEC_PACKAGE + className);
								EventDispatcher.this.spineManager.spineCodec = (SpineCodec)d.newInstance();
								EventDispatcher.this.spineManager.htInstance.put(className, EventDispatcher.this.spineManager.spineCodec);
							 }
							
							 // Invoking decode and setting SpineObject data
							 o = EventDispatcher.this.spineManager.spineCodec.decode(EventDispatcher.this.spineManager.getNodeByPhysicalID(nodeID), payload);
							
						} catch (Exception e) { 
							e.printStackTrace();
							if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
								SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
							return;
						}
						break;
					}	
					default: break;
				}
				
				// SPINEListeners are notified of the reception from the node 'nodeID' of some data  
				notifyListeners(pktType, o);
				
				//System.out.println("Memory available: " + Runtime.getRuntime().freeMemory() + " KB");
				// call to the garbage collector to favour the recycling of unused memory
				System.gc();		
			}
		} 
	 
	 public void run() {
		 // just a "keel alive", to ensure the underlying communication system continues to operate  
		 while(true) {
			 try { Thread.sleep(60000); } catch (InterruptedException e) {}
		 }
	 }
	
}
