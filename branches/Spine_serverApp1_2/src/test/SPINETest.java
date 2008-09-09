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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.Â  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MAÂ  02111-1307, USA.
*****************************************************************/

/**
 *
 *  This is the unit test class for SPINE framework 
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

package test;

import java.util.Vector;

import spine.Properties;
import spine.SPINEFunctionConstants;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.SPINESensorConstants;
import spine.communication.tinyos.AlarmSpineFunctionReq;
import spine.communication.tinyos.AlarmSpineSetupFunction;
import spine.communication.tinyos.FeatureSpineFunctionReq;
import spine.communication.tinyos.FeatureSpineSetupFunction;
import spine.communication.tinyos.SpineFunctionReq;
import spine.communication.tinyos.SpineSetupFunction;
import spine.communication.tinyos.SpineSetupSensor;
import spine.datamodel.Alarm;
import spine.datamodel.Data;
import spine.datamodel.Feature;
import spine.datamodel.Node;
import spine.datamodel.Sensor;
import spine.datamodel.ServiceMessage;

public class SPINETest implements SPINEListener {

	private static final int SAMPLING_TIME = 50;
	private static final int OTHER_SAMPLING_TIME = 100;
	
	private static final short WINDOW_SIZE = 40;
	private static final short OTHER_WINDOW_SIZE = 80;
	
	private static final short SHIFT_SIZE = 20;
	private static final short OTHER_SHIFT_SIZE = 40;
	
	private static SPINEManager manager;
	
	private static int counter = 0;
	private static int counter_alarm = 0;
	
	public static void main(String[] args) {
		
		System.setProperty(Properties.LOCALNODEADAPTER_CLASSNAME_KEY, SPINEManager.getProperties().getProperty(Properties.LOCALNODEADAPTER_CLASSNAME_KEY));
		
		new SPINETest();
	}
	
	public SPINETest() {
		// the first step is to get the SPINEManager instance; then ... 
		String[] args = { SPINEManager.getProperties().getProperty(Properties.MOTECOM_KEY) };
		manager = SPINEManager.getInstance(args);		
		
		// ... we need to register a SPINEListener implementation to the SPINE manager instance
		// (I register myself since I'm a SPINEListener implementation!)
		manager.registerListener(this);	
		
		// We could even decide to change the default discoveryProcedureTimeout; after that: ok ...
		/* manager.setDiscoveryProcedureTimeout(1000); */
		
		// ... let's start playing! 
		manager.discoveryWsn();
	}

	
	
	public void newNodeDiscovered(Node newNode) {
		// after my 'discoveryWsn' request we should receive this event one or more times.  
		// However, we prefer to wait for the 'discoveryCompleted' event		
	}
	
	public void discoveryCompleted(Vector activeNodes) {
		
		// we loop over the discovered nodes (hopefully, at least a node's showed up!)
		Node curr = null;
		for (int j = 0; j<activeNodes.size(); j++) {
			
			curr = (Node)activeNodes.elementAt(j);
			
			// we print for each node its details (nodeID, sensors and functions provided)
			System.out.println(curr);			
			
			// for each node, we look for specific services
			for (int i = 0; i < curr.getSensorsList().size(); i++) {
				
				byte sensor = ((Sensor)curr.getSensorsList().elementAt(i)).getCode();
				
				// if the current node has an accelerometer, then...
				if (sensor == SPINESensorConstants.ACC_SENSOR) {
					
					// ... we first setup that sensor, specifying its sampling time and time scale; then ...
					SpineSetupSensor sss = new SpineSetupSensor();
					sss.setSensor(sensor);
					sss.setTimeScale(SPINESensorConstants.MILLISEC);
					sss.setSamplingTime(SAMPLING_TIME);
					manager.setupSensor(curr.getNodeID(), sss);

					// ... we can setup a specific function (in this case a Feature) on that sensor; then ...
					SpineSetupFunction ssf = new FeatureSpineSetupFunction();
					((FeatureSpineSetupFunction)ssf).setSensor(sensor);
					((FeatureSpineSetupFunction)ssf).setWindowSize(WINDOW_SIZE);
					((FeatureSpineSetupFunction)ssf).setShiftSize(SHIFT_SIZE);
					manager.setupFunction(curr.getNodeID(), ssf);

					// ... we can activate that function with function specific parameters 
					// (for Feature they are the desired feature extractors); we can also ... 
					SpineFunctionReq sfr = new FeatureSpineFunctionReq();
					((FeatureSpineFunctionReq)sfr).setSensor(sensor);
					((FeatureSpineFunctionReq)sfr).addFeature(SPINEFunctionConstants.MODE, 
															  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask());
					((FeatureSpineFunctionReq)sfr).addFeature(SPINEFunctionConstants.MEDIAN, 
						  									  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask());
					((FeatureSpineFunctionReq)sfr).addFeature(SPINEFunctionConstants.MAX, 
							  								  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask());
					((FeatureSpineFunctionReq)sfr).addFeature(SPINEFunctionConstants.MIN, 
							  								  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask());
					manager.activateFunction(curr.getNodeID(), sfr);

					// ... split a more complex activation in multiple activations 
					// (if the specific function implementation in the node side allows that); of course we always can ...
					sfr = new FeatureSpineFunctionReq();
					((FeatureSpineFunctionReq)sfr).setSensor(sensor);
					((FeatureSpineFunctionReq)sfr).addFeature(SPINEFunctionConstants.MEAN, 
															  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask());
					((FeatureSpineFunctionReq)sfr).addFeature(SPINEFunctionConstants.AMPLITUDE, 
						  									  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask());
					manager.activateFunction(curr.getNodeID(), sfr);	
					
					// SetUp Alarm Engine
					// Window and Shift may be set to value different from the feature engine ones.
					//here we use the same values for debigging proposes
					
					SpineSetupFunction ssf2 = new AlarmSpineSetupFunction();
					((AlarmSpineSetupFunction)ssf2).setSensor(sensor);
					((AlarmSpineSetupFunction)ssf2).setWindowSize(WINDOW_SIZE);
					((AlarmSpineSetupFunction)ssf2).setShiftSize(SHIFT_SIZE);
					manager.setupFunction(curr.getNodeID(), ssf2);					

					//Activate alarm on MAX value (one of the features computed above) on CH1
					//alarm sent when MAX > upperThresold
					SpineFunctionReq sfr2 = new AlarmSpineFunctionReq();

					int lowerThreshold = 20;
					int upperThreshold = 40;
					
					((AlarmSpineFunctionReq)sfr2).setDataType(SPINEFunctionConstants.MAX);
					((AlarmSpineFunctionReq)sfr2).setSensor(SPINESensorConstants.ACC_SENSOR);
					((AlarmSpineFunctionReq)sfr2).setValueType((SPINESensorConstants.CH1_ONLY));
					((AlarmSpineFunctionReq)sfr2).setLowerThreshold(lowerThreshold);
					((AlarmSpineFunctionReq)sfr2).setUpperThreshold(upperThreshold);
					((AlarmSpineFunctionReq)sfr2).setAlarmType(SPINEFunctionConstants.ABOVE_THRESHOLD);

					manager.activateFunction(curr.getNodeID(), sfr2);

					//Activate alarm on AMPLITUDE value (one of the features computed above) on CH2
					//alarm sent when AMPLITUDE < lowerThreshold
					
					lowerThreshold = 2000;
					upperThreshold = 1000;

					((AlarmSpineFunctionReq)sfr2).setDataType(SPINEFunctionConstants.AMPLITUDE);
					((AlarmSpineFunctionReq)sfr2).setSensor(SPINESensorConstants.ACC_SENSOR);
					((AlarmSpineFunctionReq)sfr2).setValueType((SPINESensorConstants.CH2_ONLY));
					((AlarmSpineFunctionReq)sfr2).setLowerThreshold(lowerThreshold);
					((AlarmSpineFunctionReq)sfr2).setUpperThreshold(upperThreshold);
					((AlarmSpineFunctionReq)sfr2).setAlarmType(SPINEFunctionConstants.BELOW_THRESHOLD);

					manager.activateFunction(curr.getNodeID(), sfr2);
					
				}
				// repeat this process for other desired sensors; after that we can finally ... 
				else if (sensor == SPINESensorConstants.INTERNAL_TEMPERATURE_SENSOR) {
					SpineSetupSensor sss = new SpineSetupSensor();
					sss.setSensor(sensor);
					sss.setTimeScale(SPINESensorConstants.MILLISEC);
					sss.setSamplingTime(OTHER_SAMPLING_TIME);
					manager.setupSensor(curr.getNodeID(), sss);

					SpineSetupFunction ssf = new FeatureSpineSetupFunction();
					((FeatureSpineSetupFunction)ssf).setSensor(sensor);
					((FeatureSpineSetupFunction)ssf).setWindowSize(OTHER_WINDOW_SIZE);
					((FeatureSpineSetupFunction)ssf).setShiftSize(OTHER_SHIFT_SIZE);
					manager.setupFunction(curr.getNodeID(), ssf);

					SpineFunctionReq sfr = new FeatureSpineFunctionReq();
					((FeatureSpineFunctionReq)sfr).setSensor(sensor);
					((FeatureSpineFunctionReq)sfr).addFeature(SPINEFunctionConstants.MODE, 
															  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask());
					((FeatureSpineFunctionReq)sfr).addFeature(SPINEFunctionConstants.MEDIAN, 
						  									  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask());
					((FeatureSpineFunctionReq)sfr).addFeature(SPINEFunctionConstants.MAX, 
							  								  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask());
					((FeatureSpineFunctionReq)sfr).addFeature(SPINEFunctionConstants.MIN, 
							  								  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask());
					manager.activateFunction(curr.getNodeID(), sfr);	
					
					// SetUp Alarm Engine
					// Same Window and Shift as before
					SpineSetupFunction ssf3 = new AlarmSpineSetupFunction();
					((AlarmSpineSetupFunction)ssf3).setSensor(sensor);
					((AlarmSpineSetupFunction)ssf3).setWindowSize(WINDOW_SIZE);
					((AlarmSpineSetupFunction)ssf3).setShiftSize(SHIFT_SIZE);
					manager.setupFunction(curr.getNodeID(), ssf3);	
					
					//Activate alarm on MIN value (one of the features computed above)on CH1
					//alarm sent when lowerThreshold < MIN < upperThreshold
					SpineFunctionReq sfr3 = new AlarmSpineFunctionReq();

					
					int lowerThreshold = 1000;
					int upperThreshold = 3000;
					
					((AlarmSpineFunctionReq)sfr3).setDataType(SPINEFunctionConstants.MIN);
					((AlarmSpineFunctionReq)sfr3).setSensor(sensor);
					((AlarmSpineFunctionReq)sfr3).setValueType((SPINESensorConstants.CH1_ONLY));
					((AlarmSpineFunctionReq)sfr3).setLowerThreshold(lowerThreshold);
					((AlarmSpineFunctionReq)sfr3).setUpperThreshold(upperThreshold);
					((AlarmSpineFunctionReq)sfr3).setAlarmType(SPINEFunctionConstants.IN_BETWEEN_THRESHOLDS);

					manager.activateFunction(curr.getNodeID(), sfr3);

				}				
			}			
		}
		
		// ... start the sensor network sensing and computing our aforeactivated services.
		if (activeNodes.size() > 0)
			manager.start(true, true); // we can tune a few node parameters at run-time for reducing the power consumption and the packets drop. 
	}

	private Vector features;
	public void dataReceived(int nodeID, Data data) {
		// the specific application logic behaves w.r.t. the type of data received 
		switch (data.getFunctionCode()) {
			case SPINEFunctionConstants.FEATURE: {
				features = (Vector)(data.getData());
				for (int i = 0; i<features.size(); i++)
					System.out.println((Feature)features.elementAt(i));
				
				counter++;
				
				// even this simple application shows us up some nice SPINE properties; in fact ... 
				if(counter == 5) {
					// it's possible to deactivate functions computation at runtime (even when the radio on the node works in low-power mode)
					SpineFunctionReq sfr = new FeatureSpineFunctionReq();
					((FeatureSpineFunctionReq)sfr).setSensor(((Feature)features.elementAt(0)).getSensorCode());
					((FeatureSpineFunctionReq)sfr).removeFeature(((Feature)features.elementAt(0)).getFeatureCode(), SPINESensorConstants.ALL);
					manager.deactivateFunction(nodeID, sfr);
				}	
				
				if(counter == 10) {
					// and, of course, we can activate new functions at runtime
					SpineFunctionReq sfr = new FeatureSpineFunctionReq();
					((FeatureSpineFunctionReq)sfr).setSensor(((Feature)features.elementAt(0)).getSensorCode());
					((FeatureSpineFunctionReq)sfr).addFeature(SPINEFunctionConstants.RANGE, SPINESensorConstants.CH1_ONLY);
					manager.activateFunction(nodeID, sfr);
				}
				
				if(counter == 20) {
					// when we are set, we can decide to ...
					
					// stop the WSN, forcing a 'software' reset of the nodes
					/* manager.resetWsn(); */
					
					// or just deregister ourself to further SPINE events. 
					//manager.deregisterListener(this);
				}				
								
				break;
			}
			case SPINEFunctionConstants.ONE_SHOT:
				// if the current data received is a ONE_SHOT function, we just print this one-shot sensor reading
				System.out.println((Feature)data.getData()); 
				break;
				
			case SPINEFunctionConstants.ALARM:
				System.out.println((Alarm)data.getData());
				counter_alarm ++;
				if(counter_alarm == 20) {
					SpineFunctionReq sfr2 = new AlarmSpineFunctionReq();
					((AlarmSpineFunctionReq)sfr2).setSensor(SPINESensorConstants.ACC_SENSOR);
					((AlarmSpineFunctionReq)sfr2).setAlarmType(SPINEFunctionConstants.ABOVE_THRESHOLD);
					((AlarmSpineFunctionReq)sfr2).setDataType(SPINEFunctionConstants.MAX);
					((AlarmSpineFunctionReq)sfr2).setValueType((SPINESensorConstants.CH1_ONLY));
					
					manager.deactivateFunction(nodeID, sfr2);
				}

				break;
		}
		
	}	
	
	public void serviceMessageReceived(int nodeID, ServiceMessage msg) {
		// for this simple application, I just like to print the service message received
		System.out.println(msg);
	}
	
}
