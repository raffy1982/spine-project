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
 *  This class represents the Node entity.
 *  It contains a constructor, a toString and getters methods.
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

package spine.datamodel;

import java.util.Vector;

import spine.Properties;
import spine.SPINEFunctionConstants;
import spine.datamodel.functions.Exception.BadFunctionSpecException;
import spine.datamodel.functions.Function;
import spine.datamodel.functions.SpineObject;

public class Node implements SpineObject{
	
	private final static String FUNCTION_CLASSNAME_PREFIX = "spine.datamodel.functions.";
	private final static String FUNCTION_CLASSNAME_SUFFIX = "Function";
	
	private static final String NEW_LINE = Properties.getProperties().getProperty(Properties.LINE_SEPARATOR_KEY);
	
	private int nodeID;
	
	private Vector sensorsList = new Vector(); // <values:Sensor>
	
	private Vector functionsList = new Vector(); // <values:Function>
	
	
	/**
	 * Constructor of a Node object.
	 */
	public Node(){
		
	};
	
	/**
	 * Getter method of the node id
	 * @return the node id
	 */
	public int getNodeID() {
		return nodeID;
	}
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}
	

	/**
	 * Getter method of the node sensors list
	 * @return the sensors list of the node
	 */
	public Vector getSensorsList() {
		return sensorsList;
	}
	public void setSensorsList(Vector sensorsList) {
		this.sensorsList = sensorsList;
	}
	
	

	/**
	 * Getter method of the node functionality (function libraries) list
	 * @return the functionality list of the node
	 */
	public Vector getFunctionsList() {
		return functionsList;
	}
	public void setFunctionsList(Vector functionsList) {
		this.functionsList = functionsList;
	}
	
	/**
	 * 
	 * Returns a string representation of the Node object.
	 * 
	 */
	public String toString() {
		String s = "Node ID: " + this.nodeID + NEW_LINE;
		
		s += "OnBoard Sensors:" + NEW_LINE;
		for (int i = 0; i<this.sensorsList.size(); i++) 
			s += "  " + (Sensor)this.sensorsList.elementAt(i) + NEW_LINE;
		
		s += "Supported Functions:" + NEW_LINE;
		for (int i = 0; i<this.functionsList.size(); i++) 
			s += "  " + (Function)this.functionsList.elementAt(i) + NEW_LINE;
		
		return s;
		
	}
	
}
