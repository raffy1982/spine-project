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

package test;

import java.util.Vector;

import com.tilab.zigbee.LocalNodeAdapter;

import spine.Properties;
import spine.SPINEFunctionConstants;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.SPINESensorConstants;
import spine.datamodel.Data;
import spine.datamodel.Feature;
import spine.datamodel.Node;
import spine.datamodel.Sensor;

public class SPINETest implements SPINEListener {

	private static final int SAMPLING_TIME = 50;
	private static final int OTHER_SAMPLING_TIME = 100;
	
	private static final short WINDOW_SIZE = 40;
	private static final short OTHER_WINDOW_SIZE = 80;
	
	private static final short SHIFT_SIZE = 20;
	private static final short OTHER_SHIFT_SIZE = 40;
	
	private static SPINEManager manager;
	
	private static int counter = 0;
	
	
	public void newNodeDiscovered(Node newNode) {}
	
	public void discoveryCompleted(Vector activeNodes) {
		Node curr = null;
		for (int j = 0; j<activeNodes.size(); j++) {
			curr = (Node)activeNodes.elementAt(j);
			System.out.println(curr);			
			
			byte sensor;
			byte[] params;
			for (int i = 0; i < curr.getSensorsList().size(); i++) {
				
				sensor = ((Sensor)curr.getSensorsList().elementAt(i)).getCode();
				
				if (sensor == SPINESensorConstants.ACC_SENSOR) {
					manager.setupSensor(curr.getNodeID(), sensor, SPINESensorConstants.MILLISEC, SAMPLING_TIME);

					params = new byte[3];
					params[0] = sensor;
					params[1] = (byte) WINDOW_SIZE;
					params[2] = (byte) SHIFT_SIZE;
					manager.setupFunction(curr.getNodeID(), SPINEFunctionConstants.FEATURE, params);

					params = new byte[10];
					params[0] = sensor;
					params[1] = 4; // how many libraries activation request
					params[2] = SPINEFunctionConstants.MODE;
					params[3] = ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask();
					params[4] = SPINEFunctionConstants.MEDIAN;
					params[5] = ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask();
					params[6] = SPINEFunctionConstants.MAX;
					params[7] = ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask();
					params[8] = SPINEFunctionConstants.MIN;
					params[9] = ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask();
					//manager.activateFunction(node.getNodeID(), SPINEFunctionConstants.FEATURE, params);

					params = new byte[6];
					params[0] = sensor;
					params[1] = 2; // how many libraries activation request
					params[2] = SPINEFunctionConstants.MEAN;
					params[3] = ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask();
					params[4] = SPINEFunctionConstants.AMPLITUDE;
					params[5] = ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask();
					manager.activateFunction(curr.getNodeID(), SPINEFunctionConstants.FEATURE, params);					
				}
				else if (sensor == SPINESensorConstants.INTERNAL_TEMPERATURE_SENSOR) {
					manager.setupSensor(curr.getNodeID(), sensor, SPINESensorConstants.MILLISEC, OTHER_SAMPLING_TIME);

					params = new byte[3];
					params[0] = sensor;
					params[1] = (byte)OTHER_WINDOW_SIZE;
					params[2] = (byte)OTHER_SHIFT_SIZE;
					manager.setupFunction(curr.getNodeID(), SPINEFunctionConstants.FEATURE, params);

					params = new byte[10];
					params[0] = sensor;
					params[1] = 4; // how many libraries activation request
					params[2] = SPINEFunctionConstants.MODE;
					params[3] = ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask();
					params[4] = SPINEFunctionConstants.MEDIAN;
					params[5] = ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask();
					params[6] = SPINEFunctionConstants.MAX;
					params[7] = ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask();
					params[8] = SPINEFunctionConstants.MIN;
					params[9] = ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask();
					manager.activateFunction(curr.getNodeID(), SPINEFunctionConstants.FEATURE, params);		
				}				
			}			
		}
		
		//manager.start(true, true);
		//manager.start(true, false);
		manager.start(false, true);
		//manager.start(false, false);
	}

	private Vector features;
	public void dataReceived(int nodeID, Data data) {
		switch (data.getFunctionCode()) {
			case SPINEFunctionConstants.FEATURE: {
				features = (Vector)(data.getData());
				for (int i = 0; i<features.size(); i++)
					System.out.println((Feature)features.elementAt(i));
				
				counter++;
				
				if(counter == 20) {
					//manager.resetWsn();
					manager.deregisterListener(this);
				}
				
				if(counter == 5) {
					byte[] params = new byte[4];
					params[0] = ((Feature)features.elementAt(0)).getSensorCode();
					params[1] = 1; // how many libraries deactivation request
					params[2] = ((Feature)features.elementAt(0)).getFeatureCode();
					params[3] = 0; // want to disable that feature on every channels
					//manager.deactivateFunction(nodeID, SPINEFunctionConstants.FEATURE, params);
				}	
				
				if(counter == 10) {
					byte[] params = new byte[4];
					params[0] = ((Feature)features.elementAt(0)).getSensorCode();
					params[1] = 1; // how many libraries activation request
					params[2] = SPINEFunctionConstants.RANGE;
					params[3] = SPINESensorConstants.CH1_ONLY; 
					//manager.activateFunction(nodeID, SPINEFunctionConstants.FEATURE, params);
				}
				
				break;
			}
			case SPINEFunctionConstants.ONE_SHOT: 
				System.out.println((Feature)data.getData()); 
				break;
		}
		
		//new MyTimer(nodeID, SAMPLING_TIME).start();
	}	
	
	public void serviceMessageReceived() {
		System.out.println("new service message");
	}
	
	public SPINETest() {
		manager = SPINEManager.getInstance(SPINEManager.getProperties().getProperty(Properties.BASE_STATION_PORT_KEY), 
										   SPINEManager.getProperties().getProperty(Properties.BASE_STATION_SPEED_KEY));		
		
		manager.registerListener(this);	
		
		//manager.setDiscoveryProcedureTimeout(1000);
		manager.discoveryWsn();
	}
	
	

	public static void main(String[] args) {
		
		System.setProperty(LocalNodeAdapter.LOCALNODEADAPTER_CLASSNAME_KEY, "spine.communication.tinyos.TOSLocalNodeAdapter");
		
		new SPINETest();
	}
	
	private class MyTimer extends Thread {
		
		private int nodeID = 0;
		private long delay = 0;
		
		MyTimer(int nodeID, long delay) {
			this.nodeID = nodeID;
			this.delay = delay;
		}
		
		public void run () {
			try { sleep(this.delay); } catch (InterruptedException e) {}
			
			//manager.readNow(this.nodeID, SPINESensorConstants.ACC_SENSOR);
			manager.readNow(this.nodeID, SPINESensorConstants.INTERNAL_TEMPERATURE_SENSOR);
		}
	}
}
