/*****************************************************************
SPINE - Signal Processing In-Note Environment is a framework that 
allows dynamic configuration of feature extraction capabilities 
of WSN nodes via an OtA protocol

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

package com.tilab.spine.packets;

/**
 *
 * This class represent a well formatted AMP Service Discovery Packet.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class ServiceDiscovery {
	
	private Header header;
	
	private boolean reset;
	
	private boolean start;
	
	private short numMotes;
	
	/**
	 * The constructor of a pure ServiceDiscovery packet needs anything but the 
	 * header, since all the necessary info are contained there.
	 * 
	 * @param header the packet header
	 */
	public ServiceDiscovery(Header header) {
		this.header = header;
		this.reset = false;
		this.start = false;
		this.numMotes = 0;
	}
	
	/**
	 * The constructor of a ServiceDiscovery START packet. It also lets the motes know
	 * how many they are in the same subnet. 
	 * 
	 * @param header the packet header
	 * @param start true if this packet will be used to start the network
	 * @param numMotes the number of motes discovered
	 */
	public ServiceDiscovery(Header header, boolean start, short numMotes) {
		this.header = header;
		this.reset = false;
		this.start = start;
		this.numMotes = numMotes;
	}
	
	/**
	 * The constructor of a ServiceDiscovery RESET packet.
	 * 
	 * @param header the packet header
	 * @param reset true if this packet will be used to reset a mote (or the whole network)
	 */
	public ServiceDiscovery(Header header, boolean reset) {
		this.header = header;
		this.reset = reset;
		this.start = false;
		this.numMotes = 0;
	}
	
	/**
	 * Getter method
	 * 
	 * @return the AMP header
	 */
	public Header getHeader() {
		return header;
	}
	
	/**
	 * Setter method
	 * 
	 * @param header the AMP header
	 */
	public void setHeader(Header header) {
		this.header = header;
	}

	public boolean isReset() {
		return reset;
	}

	public boolean isStart() {
		return start;
	}

	public short getNumMotes() {
		return numMotes;
	}
}
