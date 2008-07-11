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

import spine.SPINEFunctionConstants;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.SPINESensorConstants;
import spine.datamodel.Data;
import spine.datamodel.Feature;
import spine.datamodel.Node;
import spine.datamodel.Sensor;

public class SPINETest implements SPINEListener {

	private static final String PORT = "42";
	private static final String SPEED = "telosb";
	
	private static final int SAMPLING_TIME = 25;
	
	private static final byte WINDOW_SIZE = 40;
	private static final byte SHIFT_SIZE = 20;
	
	private static SPINEManager manager;
	
	private static int nodes = 0;
	
	private static int counter = 0;
	
	
	public void newNodeDiscovered(Node newNode) {
		/*System.out.println(newNode);		
		
		for (int i=0; i<newNode.getSensorsList().size(); i++) {
			byte sensor = ((Sensor)newNode.getSensorsList().elementAt(i)).getCode();
			if (sensor == SPINESensorConstants.VOLTAGE_SENSOR) {
				manager.setupSensor(newNode.getNodeID(), 
									sensor, SPINESensorConstants.MILLISEC, SAMPLING_TIME);
				
				byte[] params = new byte[3];
				params[0] = sensor;
				params[1] = WINDOW_SIZE;
				params[2] = SHIFT_SIZE;
				manager.setupFunction(newNode.getNodeID(), SPINEFunctionConstants.FEATURE, params);
				
				params = new byte[10];
				params[0] = sensor;
				params[1] = 4; // how many libraries activation request
				params[2] = SPINEFunctionConstants.VARIANCE;
				params[3] = ((Sensor)newNode.getSensorsList().elementAt(i)).getChannelBitmask();
				params[4] = SPINEFunctionConstants.ST_DEV;
				params[5] = ((Sensor)newNode.getSensorsList().elementAt(i)).getChannelBitmask();
				params[6] = SPINEFunctionConstants.MAX;
				params[7] = ((Sensor)newNode.getSensorsList().elementAt(i)).getChannelBitmask();
				params[8] = SPINEFunctionConstants.MIN;
				params[9] = ((Sensor)newNode.getSensorsList().elementAt(i)).getChannelBitmask();
				manager.activateFunction(newNode.getNodeID(), SPINEFunctionConstants.FEATURE, params);
				break;
			}
		}
		
		if (++nodes > 0)
			manager.start(true);*/
		
		//manager.setupSensor(newNode.getNodeID(), SPINESensorConstants.ACC_SENSOR, SPINESensorConstants.NOW, 0);
		new MyTimer(newNode.getNodeID(), SAMPLING_TIME).start();
	}
	


	public void dataReceived(int nodeID, Data data) {
		/*switch (data.getFunctionCode()) {
			case SPINEFunctionConstants.FEATURE: {
				Vector features = (Vector)(data.getData());
				for (int i = 0; i<features.size(); i++)
					System.out.println((Feature)features.elementAt(i));
				
				counter++;
				
				if(counter == 5) {
					byte[] params = new byte[4];
					params[0] = ((Feature)features.elementAt(0)).getSensorCode();
					params[1] = 1; // how many libraries deactivation request
					params[2] = ((Feature)features.elementAt(0)).getFeatureCode();
					params[3] = 0; // want to disable that feature on every channels
					manager.deactivateFunction(nodeID, SPINEFunctionConstants.FEATURE, params);
				}	
				
				if(counter == 10) {
					byte[] params = new byte[4];
					params[0] = ((Feature)features.elementAt(0)).getSensorCode();
					params[1] = 1; // how many libraries activation request
					params[2] = SPINEFunctionConstants.RANGE;
					params[3] = SPINESensorConstants.CH1_ONLY; // want to disable that feature on every channels
					manager.activateFunction(nodeID, SPINEFunctionConstants.FEATURE, params);
				}
				
				System.out.println("Memory available: " + Runtime.getRuntime().freeMemory() + " KB");
				System.gc();
				break;
			}
			case SPINEFunctionConstants.ONE_SHOT: 
				System.out.println((Feature)data.getData()); 
				break;
		}*/
		
		new MyTimer(nodeID, SAMPLING_TIME).start();
	}	
	
	public void serviceMessageReceived() {
		System.out.println("new service message");
	}
	
	public SPINETest() {
		manager = SPINEManager.getInstance(PORT, SPEED);		
		
		manager.registerListener(this);	
		
		manager.discoveryWsn();
	}
	
	

	public static void main(String[] args) {
		
		System.setProperty(LocalNodeAdapter.LOCALNODEADAPTER_CLASSNAME_KEY, "spine.communication.tinyos.TOSLocalNodeAdapter");
		System.setProperty(SPINEManager.MESSAGE_CLASSNAME_KEY, "spine.communication.tinyos.TOSMessage");
		System.setProperty(SPINEManager.URL_PREFIX_KEY, "http://tinyos:");
		
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
			
			manager.setupSensor(this.nodeID, SPINESensorConstants.ACC_SENSOR, SPINESensorConstants.NOW, 0);
		}
	}
}
