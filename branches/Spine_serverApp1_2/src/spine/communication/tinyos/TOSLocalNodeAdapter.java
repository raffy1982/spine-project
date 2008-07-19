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

import java.io.IOException;
import java.util.Vector;

import spine.SPINEManager;
import spine.SPINEPacketsConstants;
import spine.communication.tinyos.SpineTOSMessage.SPINEHeader;

import net.tinyos.message.MessageListener;
import net.tinyos.message.MoteIF;
import net.tinyos.packet.BuildSource;
import net.tinyos.util.PrintStreamMessenger;

import com.tilab.zigbee.ConfigurationDescriptor;
import com.tilab.zigbee.LocalNodeAdapter;
import com.tilab.zigbee.WSNConnection;


public class TOSLocalNodeAdapter extends LocalNodeAdapter implements MessageListener {

	
	private Vector connections = new Vector(); // <values: WSNConnection>
	
	private String port = null;
	private String speed = null;
	private MoteIF moteIF = null;
	
	private Vector partials = new Vector(); // <values: Partial>
	
	private Vector messagesQueue = new Vector(); // <values: Msg>
	
	private boolean sendImmediately = true;
	
	public void messageReceived(int srcID, net.tinyos.message.Message tosmsg) {
System.out.print("messageReceived -> ");		
		if (tosmsg instanceof SpineTOSMessage) {			
			try {
				SPINEHeader h = ((SpineTOSMessage)tosmsg).getHeader();
				int sourceNodeID = h.getSourceID();
				
				if(sourceNodeID == SPINEPacketsConstants.SPINE_BASE_STATION || 
					sourceNodeID == SPINEPacketsConstants.SPINE_BROADCAST || 
					h.getVersion() != SPINEPacketsConstants.CURRENT_SPINE_VERSION || 
					h.getDestID() != SPINEPacketsConstants.SPINE_BASE_STATION || 
					h.getGroupID() != SPINEManager.MY_GROUP_ID) 
					return;

printPayload(((SpineTOSMessage)tosmsg).getRawPayload());
				
				sendMessages(sourceNodeID);
				
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

				com.tilab.zigbee.Message msg = ((SpineTOSMessage)tosmsg).parse();					
				for (int i = 0; i<connections.size(); i++)
					((TOSWSNConnection)connections.elementAt(i)).messageReceived(msg);				
				
			} catch (IllegalSpineHeaderSizeException e) {}			
		}
	}
	
	private int inPartials(int sourceID, byte sequenceNumber) {
		for (int i = 0; i<this.partials.size(); i++)
			if (((Partial)partials.elementAt(i)).equal(sourceID, sequenceNumber)) return i;
		return -1;
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
		this.port = (String)parms.elementAt(0);
		this.speed = (String)parms.elementAt(1);
	}

	public void reset() {
		// TODO		
	}

	public void start() {
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
        
	    moteIF.registerListener(new SpineTOSMessage(), this);		
	}

	public void stop() {
		// TODO		
	}
	
	public void sendMessages(int nodeID) {		
		Msg curr = null;
		for (int i = 0; i<this.messagesQueue.size(); i++) {
			curr = (Msg)this.messagesQueue.elementAt(i);
			if (curr.destNodeID == nodeID || curr.destNodeID == SPINEPacketsConstants.SPINE_BROADCAST) {
				try {
					this.moteIF.send(curr.destNodeID, curr.tosmsg);
System.out.println("Ota deferred send.");					
					this.messagesQueue.removeElementAt(i);
					Thread.sleep(2);
				} catch (IOException e) {
					System.out.println(e);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}		
	}

	public synchronized void send(int destNodeID, SpineTOSMessage tosmsg) {
		if(this.sendImmediately) {
			try {
				this.moteIF.send(destNodeID, tosmsg);
System.out.println("Ota immediate send.");																										 // check if the flag radioAlwaysOn flag is false
				if(tosmsg.getHeader().getPktType() == SPINEPacketsConstants.START && tosmsg.getRawPayload()[2] == 0)
					this.sendImmediately = false;
				
			} catch (IOException e) {
				System.out.println(e);
			} catch (IllegalSpineHeaderSizeException e) {
				System.out.println(e);
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
		
		Partial(int nodeID, byte seqNr, byte totFragments, byte[] partialPayload) {
			this.nodeID = nodeID;
			this.seqNr = seqNr;
			this.lastFragmentNr = 1;
			this.totFragments = totFragments;
			this.partialPayload = partialPayload;
		}
		
		void addToPayload(byte[] newPartial) {
			byte[] newPartialPayload = new byte[partialPayload.length + newPartial.length];
			System.arraycopy(partialPayload, 0, newPartialPayload, 0, partialPayload.length);
			System.arraycopy(newPartial, 0, newPartialPayload, partialPayload.length, newPartial.length);
			
			partialPayload = newPartialPayload;
			
			lastFragmentNr++;
		}
		
		boolean equal(int nodeID, byte seqNr) {
			return (this.nodeID == nodeID && this.seqNr == seqNr);
		}
		
	}
	
	private class Msg {
		int destNodeID;
		SpineTOSMessage tosmsg;
		
		Msg(int destNodeID, SpineTOSMessage tosmsg) {
			this.destNodeID = destNodeID;
			this.tosmsg = tosmsg;
		}
	}
	
	private void printPayload(byte[] payload) {  // DEBUG CODE
		System.out.print("in.lowLevel: "); 
		if(payload == null || payload.length == 0)
			System.out.print("empty payload");
		else{
			for (int i = 0; i<payload.length; i++) {
				short b =  payload[i];
				if (b<0) b += 256;
				System.out.print(Integer.toHexString(b) + " ");
			}
		}
		System.out.println("");		
	}

}
