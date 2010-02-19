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
 * This class is responsible to implement the specific logic of accessing a TinyOS base station 
 * in a way complying to the GAL APIs.
 * Hence, it's responsible of receiving packets from the serial port thru the TinyOS.jar APIs and to 
 * provide a standard way of transmitting packets to the attached base-station that will eventually be forwarded Ota. 
 * 
 * Note that this class is only used internally at the framework. 
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 * 
 * @see LocalNodeAdapter
 */

package spine.communication.tinyos;

import jade.util.Logger;

import java.io.IOException;
import java.util.Vector;

import net.tinyos.message.MessageListener;
import net.tinyos.message.MoteIF;
import net.tinyos.packet.BuildSource;
import net.tinyos.util.PrintStreamMessenger;
import spine.SPINEManager;
import spine.SPINEPacketsConstants;
import spine.SPINEServiceMessageConstants;
import spine.datamodel.Address;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;
import spine.exceptions.MethodNotSupportedException;
import spine.payload.codec.tinyos.ServiceAckMessage;

import com.tilab.gal.ConfigurationDescriptor;
import com.tilab.gal.LocalNodeAdapter;
import com.tilab.gal.WSNConnection;


public class TOSLocalNodeAdapter extends LocalNodeAdapter implements MessageListener {

	private Vector connections = new Vector(); // <values: WSNConnection>
	
	protected String motecom = null;
	protected String port = null;
	protected String speed = null;
	private MoteIF moteIF = null;
	
	private Vector partials = new Vector(); // <values: Partial>
	
	private Vector messagesQueue = new Vector(); // <values: Msg>
	
	private boolean sendImmediately = true;
	
	/**
	 * This method is called by the TinyOS APIs when a new message is received by the base-station.
	 * It's also responsible of reassembling the fragments of complete message and of forwarding the 
	 * messages as soon as they are fully reassembled.  
	 */
	public void messageReceived(int srcID, net.tinyos.message.Message tosmsg) {
		if (tosmsg instanceof SpineTOSMessage) {			
			try {
				SPINEHeader h = ((SpineTOSMessage)tosmsg).getHeader();
				int sourceNodeID = h.getSourceID();
				
				// some controls for reducing the risk of start elaborating erroneous received messages 
				if(sourceNodeID == SPINEPacketsConstants.SPINE_BASE_STATION || 
					sourceNodeID == SPINEPacketsConstants.SPINE_BROADCAST || 
					h.getVersion() != SPINEPacketsConstants.CURRENT_SPINE_VERSION || 
					h.getDestID() != SPINEPacketsConstants.SPINE_BASE_STATION ||					
					h.getGroupID() != SPINEManager.getMyGroupID()) {
					
						if (SPINEManager.getLogger().isLoggable(Logger.WARNING)) {
							StringBuffer str = new StringBuffer();
							str.append("[ERRONEOUS, ");
							str.append(h);
							str.append(" ]... discarded!");
							SPINEManager.getLogger().log(Logger.WARNING, str.toString());
						}	
					
						return;
				}
				
				if (SPINEManager.getLogger().isLoggable(Logger.INFO)) {
					StringBuffer str = new StringBuffer();
					str.append("REC. -> ");
					str.append(tosmsg);				
					SPINEManager.getLogger().log(Logger.INFO, str.toString());
				}				
				
				if (h.getPktType() == SPINEPacketsConstants.SVC_MSG) {
                    com.tilab.gal.Message msg = ((SpineTOSMessage)tosmsg).parse();
                    
                    short[] payloadShort = msg.getPayload();
        			byte[] payload = new byte[payloadShort.length];
        			for (int i = 0; i<payloadShort.length; i++)
        				payload[i] = (byte)payloadShort[i];
                    
                    int type=new spine.payload.codec.tinyos.CodecInformation().getServiceMessageType(payload);
                    if (type== SPINEServiceMessageConstants.ACK) {
                          // if an ACK is received for a certain msg, I can remove that message from the messages-to-send queue
                          try{
                        	  ServiceMessage svcMsg = (ServiceMessage) new ServiceAckMessage().decode(new  Node(new Address(""+sourceNodeID)), payload);
                              byte msgSeqNrAcknowledged = svcMsg.getMessageDetail();
                              removeAcknowledgedMsg(sourceNodeID, msgSeqNrAcknowledged);}
                          catch(MethodNotSupportedException e) {
                        	  e.printStackTrace();
                        	  if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
          						SPINEManager.getLogger().log(Logger.SEVERE, e.toString());          						
                          }                    
                    }
				}
	
				else 
					sendMessages(sourceNodeID);
				
				// re-assembly of fragments into complete messages 
				if (h.getTotalFragments() != 1) {
					int index = inPartials(sourceNodeID, h.getSequenceNumber());
					if (index == -1) {
						if (h.getFragmentNumber() != 1)
							return;
						else {
							partials.addElement(new Partial(sourceNodeID, h.getSequenceNumber(), 
															h.getTotalFragments(), 
															((SpineTOSMessage)tosmsg).getRawPayload()));
							return;
						}
					}
					else {
						if (h.getFragmentNumber() != ( ((Partial)partials.elementAt(index)).lastFragmentNr + 1 ) ) {
							partials.removeElementAt(index); // no need to keep a partial if a fragment is lost
							return;
						}
						else {
							if (h.getFragmentNumber() < ((Partial)partials.elementAt(index)).totFragments) {
								((Partial)partials.elementAt(index)).addToPayload( ((SpineTOSMessage)tosmsg).getRawPayload() );
								return;
							}
							else {
								Partial complete = ((Partial)partials.elementAt(index));
								complete.addToPayload( ((SpineTOSMessage)tosmsg).getRawPayload() );
								((SpineTOSMessage)tosmsg).setRawPayload(complete.partialPayload);
								partials.removeElementAt(index);								
							}
						}
					}
				}

				// notification to upper layer of a message reception
				com.tilab.gal.Message msg = ((SpineTOSMessage)tosmsg).parse();					
				for (int i = 0; i<connections.size(); i++)
					((TOSWSNConnection)connections.elementAt(i)).messageReceived(msg);				
				
			} catch (IllegalSpineHeaderSizeException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.WARNING)) 
					SPINEManager.getLogger().log(Logger.WARNING, "[SPINE1.3-MALFORMED-HEADER]... discarded!");
			}			
		}
		else
			if (SPINEManager.getLogger().isLoggable(Logger.WARNING))
				SPINEManager.getLogger().log(Logger.WARNING, "[NON-SPINE]... discarded!");
	}
	
	private int inPartials(int sourceID, byte sequenceNumber) {
		for (int i = 0; i<this.partials.size(); i++)
			if (((Partial)partials.elementAt(i)).equal(sourceID, sequenceNumber)) return i;
		return -1;
	}
	
	private void removeAcknowledgedMsg(int nodeID, byte seqNr) {
		for (int i = 0; i < this.messagesQueue.size(); i++) {
			try {
				Msg tmp = (Msg)this.messagesQueue.elementAt(i);
				if(tmp.destNodeID == nodeID && tmp.tosmsg.getHeader().getSequenceNumber() == seqNr) {
					this.messagesQueue.removeElementAt(i);
					return;
				}
			} catch (IllegalSpineHeaderSizeException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE))
					SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
			}
		}
	}
	

	public WSNConnection createAPSConnection() {
		WSNConnection newConnection = new TOSWSNConnection(this);
		connections.add(newConnection);
		return newConnection;
	}

	public ConfigurationDescriptor getConfigurationDescriptor() {
		return null;
	}

	public void init(Vector parms) {
		if (parms.size() == 1) {
			this.motecom = (String)parms.elementAt(0);
		} else {
			this.port = (String)parms.elementAt(0);
			this.speed = (String)parms.elementAt(1);
		}
	}

	public void reset() {
		// TODO		
	}

	public void start() {
		if (motecom == null) {
			String prefix = "serial@";
			String osName = System.getProperty("os.name").toLowerCase();
			if (osName.matches("[ a-z]*windows[ a-z]*"))
				prefix += "COM";
			else if (osName.matches("[ a-z]*linux[ a-z]*"))
				prefix += "/dev/ttyUSB";
			else prefix += ""; 
			
			String baseStation = prefix + this.port + ":" + this.speed;

			moteIF = new MoteIF(BuildSource.makePhoenix(BuildSource.makePacketSource(baseStation), 
														PrintStreamMessenger.err)); 
		}
		else {
			moteIF = new MoteIF(BuildSource.makePhoenix(BuildSource.makePacketSource(motecom), 
														PrintStreamMessenger.err)); 
		}
		
        
	    moteIF.registerListener(new SpineTOSMessage(), this);		
	}

	public void stop() {
		// TODO		
	}
	
	protected void sendMessages(int nodeID) {		
		Msg curr = null;	
		for (int i = 0; i<this.messagesQueue.size(); i++) {
			curr = (Msg)this.messagesQueue.elementAt(i);
			if (curr.destNodeID == nodeID || curr.destNodeID == SPINEPacketsConstants.SPINE_BROADCAST) {
				try {
					if (!curr.transmitted || --curr.retransmissionCounter == 0) {
						this.moteIF.send(curr.destNodeID, curr.tosmsg);
						curr.transmitted = true;
					}
					else if(curr.retransmissionCounter <= 0)
						this.messagesQueue.removeElementAt(i);
						
					//this.messagesQueue.removeElementAt(i);
					Thread.sleep(2);
				} catch (IOException e) {
					e.printStackTrace();
					if (SPINEManager.getLogger().isLoggable(Logger.WARNING)) 
						SPINEManager.getLogger().log(Logger.WARNING, e.getMessage());
				}
				catch (InterruptedException e) {}
			}
		}		
	}

	protected synchronized void send(int destNodeID, SpineTOSMessage tosmsg) {
		if(this.sendImmediately) {
			try {
				this.moteIF.send(destNodeID, tosmsg);
				if(tosmsg.getHeader().getPktType() == SPINEPacketsConstants.START && tosmsg.getRawPayload()[2] == 0)
					this.sendImmediately = false;
				
			} catch (IOException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.WARNING)) 
					SPINEManager.getLogger().log(Logger.WARNING, e.getMessage());
			} catch (IllegalSpineHeaderSizeException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.WARNING)) 
					SPINEManager.getLogger().log(Logger.WARNING, "[SPINE1.3-MALFORMED-HEADER]... discarded!");
			}			
		}
		else 
			this.messagesQueue.addElement(new Msg(destNodeID, tosmsg));
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
	
	static int DEFAULT_RECEIVED_PKTS_BEFORE_RETRANSMISSION = 5;
	
	private class Msg {
		int destNodeID;
		SpineTOSMessage tosmsg;
		boolean transmitted;
		int retransmissionCounter;
		
		private Msg(int destNodeID, SpineTOSMessage tosmsg) {
			this.destNodeID = destNodeID;
			this.tosmsg = tosmsg;
			this.transmitted = false;
			this.retransmissionCounter = DEFAULT_RECEIVED_PKTS_BEFORE_RETRANSMISSION;
		}
	}

}
