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

package spine;

import java.util.Vector;

import spine.datamodel.Data;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;
import spine.datamodel.serviceMessages.ServiceWarningMessage;

/**
 * This class is responsible for dispatching events on behalf of the SPINEManager
 * @author Fabio Bellifemine, Telecom Italia
 * @since 1.3
 */
class EventDispatcher {

	/** package-scoped constructor  **/
	EventDispatcher() {

	}
	

	
	private Vector listeners = new Vector(1); // initialized to 1 element as we expect usually to have just 1 listener
	
	/**
	 * Registers a SPINEListener with the manager instance
	 * 
	 * @param listener the listener to register
	 */
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
	 void notifyListeners(short eventType, Object o, SPINEManager spineManager) {
		for (int i = 0; i<this.listeners.size(); i++) 
			switch(eventType) {
				case SPINEPacketsConstants.SERVICE_ADV:
					if (!spineManager.isDiscoveryCompleted())
						((SPINEListener)this.listeners.elementAt(i)).newNodeDiscovered((Node)spineManager.getActiveNodes().lastElement()); 
					break;
				case SPINEPacketsConstants.DATA: 
					((SPINEListener)this.listeners.elementAt(i)).received((Data)o); 
					((SPINEListener)this.listeners.elementAt(i)).dataReceived(((Data)o).getNode().getPhysicalID().getAsInt(), (Data)o);
					break;	
				case SPINEPacketsConstants.SVC_MSG: 
					if(((ServiceMessage)o).getNode() != null) {
						((SPINEListener)this.listeners.elementAt(i)).received((ServiceMessage)o);
						((SPINEListener)this.listeners.elementAt(i)).serviceMessageReceived(((ServiceMessage)o).getNode().getPhysicalID().getAsInt(), (ServiceMessage)o);
					}
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
	
}
