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

package spine.datamodel;

import java.util.Vector;

import spine.Properties;

public class Node {
	
	private static final String NEW_LINE = Properties.getProperties().getProperty(Properties.LINE_SEPARATOR_KEY);
	
	private int nodeID;
	
	private Vector sensorsList = new Vector(); // <values:Sensor>
	
	private Vector librariesList = new Vector(); // <values:Functionality>
	
	public Node(int nodeID, byte[] nodeSpec) {
		this.nodeID = nodeID;
		
		int sensorsNr = nodeSpec[0];		
		for (int i = 0; i<sensorsNr; i++) 				
			sensorsList.addElement(new Sensor(nodeSpec[1+i*2], nodeSpec[1+i*2+1]));		
		
		int librariesNr = nodeSpec[1+sensorsNr*2];	
		for (int i = 0; i<librariesNr; i++) 
			librariesList.addElement(new Functionality(nodeSpec[(1+sensorsNr*2+1) + i*2], nodeSpec[(1+sensorsNr*2+1) + (i*2) + 1]));
	}

	public int getNodeID() {
		return nodeID;
	}

	public Vector getSensorsList() {
		return sensorsList;
	}

	public Vector getLibrariesList() {
		return librariesList;
	}
	
	public String toString() {
		String s = "Node ID: " + this.nodeID + NEW_LINE;
		
		s += "OnBoard Sensors:" + NEW_LINE;
		for (int i = 0; i<this.sensorsList.size(); i++) 
			s += "  " + (Sensor)this.sensorsList.elementAt(i) + NEW_LINE;
		
		s += "Supported Functionalities:" + NEW_LINE;
		for (int i = 0; i<this.librariesList.size(); i++) 
			s += "  " + (Functionality)this.librariesList.elementAt(i) + NEW_LINE;
		
		return s;
		
	}
	
}
