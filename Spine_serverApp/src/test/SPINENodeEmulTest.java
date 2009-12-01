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
 * @version 1.3
 */

package test;

import java.util.Vector;

import spine.SPINEFactory;
import spine.SPINEFunctionConstants;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.SPINESensorConstants;

import spine.datamodel.*;

//functions sub-package to import for running apps on SPINE1.3 
import spine.datamodel.functions.*;

public class SPINENodeEmulTest implements SPINEListener {

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
		
		// !! NOT NEEDED ANYMORE !!
		//System.setProperty(Properties.LOCALNODEADAPTER_CLASSNAME_KEY, SPINEManager.getProperties().getProperty(Properties.LOCALNODEADAPTER_CLASSNAME_KEY));
		
		new SPINENodeEmulTest();
	}
	
	public SPINENodeEmulTest() {

		try {	
			// Initialize SPINE by passing the fileName with the configuration properties
			System.out.println("*** TestProgram   SPINEFactory.createSPINEManager(\"resources/SPINETestApp.properties\"); ***");
			// Initialize SPINE by passing the fileName with the configuration properties
			manager = SPINEFactory.createSPINEManager("resources/SPINETestApp.properties");
					
			// ... then we need to register a SPINEListener implementation to the SPINE manager instance
			// (I register myself since I'm a SPINEListener implementation!)
			System.out.println("*** TestProgram   manager.registerListener(this) ***");
			manager.addListener(this);	
			
			// We could even decide to change the default discoveryProcedureTimeout; after that: ok ...
			/* manager.setDiscoveryProcedureTimeout(1000); */
			
			// ... let's start playing! 
			System.out.println("*** TestProgram   manager.discoveryWsn() ***");
			manager.discoveryWsn(10000);
			
		} catch (InstantiationException e) {
			// if we are here, then the SPINEManager initialization did not work properly
			e.printStackTrace();
		}
		

	}

	
	
	public void newNodeDiscovered(Node newNode) {
		// after my 'discoveryWsn' request we should receive this event one or more times.  
		// However, we prefer to wait for the 'discoveryCompleted' event	
		System.out.println("*** TestProgram   newNodeDiscovered event ***");
	}
	
	public void discoveryCompleted(Vector activeNodes) {
		
		System.out.println("*** TestProgram   discoveryCompleted event ***");
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
					manager.setup(curr, sss);

					// ... we can setup a specific function (in this case a Feature) on that sensor; then ...
					FeatureSpineSetupFunction ssf = new FeatureSpineSetupFunction();
					ssf.setSensor(sensor);
					ssf.setWindowSize(WINDOW_SIZE);
					ssf.setShiftSize(SHIFT_SIZE);
					manager.setup(curr, ssf);

					// ... we can activate that function with function specific parameters 
					// (for Feature they are the desired feature extractors); we can also ... 
					FeatureSpineFunctionReq sfr = new FeatureSpineFunctionReq();
					sfr.setSensor(sensor);
					sfr.add(new Feature(SPINEFunctionConstants.MODE, 
															  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask()));
					sfr.add(new Feature(SPINEFunctionConstants.MEDIAN, 
						  									  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask()));
					sfr.add(new Feature(SPINEFunctionConstants.MAX, 
							  								  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask()));
					sfr.add(new Feature(SPINEFunctionConstants.MIN, 
							  								  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask()));
					manager.activate(curr, sfr);

					// ... split a more complex activation in multiple activations 
					// (if the specific function implementation in the node side allows that); of course we always can ...
					sfr = new FeatureSpineFunctionReq();
					sfr.setSensor(sensor);
					sfr.add(new Feature(SPINEFunctionConstants.MEAN, 
															  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask()));
					sfr.add(new Feature(SPINEFunctionConstants.AMPLITUDE, 
						  									  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask()));
					manager.activate(curr, sfr);	
					
					
					
					// SetUp Alarm Engine
					// Window and Shift may be set to value different from the feature engine ones.
					//here we use the same values for debigging proposes		
					AlarmSpineSetupFunction ssf2 = new AlarmSpineSetupFunction();
					ssf2.setSensor(sensor);
					ssf2.setWindowSize(WINDOW_SIZE);
					ssf2.setShiftSize(SHIFT_SIZE);
					manager.setup(curr, ssf2);					

					//Activate alarm on MAX value (one of the features computed above) on CH1
					//alarm sent when MAX > upperThresold
					AlarmSpineFunctionReq sfr2 = new AlarmSpineFunctionReq();

					
					int lowerThreshold = 20;
					int upperThreshold = 40;
					
					sfr2.setDataType(SPINEFunctionConstants.MAX);
					sfr2.setSensor(SPINESensorConstants.ACC_SENSOR);
					sfr2.setValueType((SPINESensorConstants.CH1_ONLY));
					sfr2.setLowerThreshold(lowerThreshold);
					sfr2.setUpperThreshold(upperThreshold);
					sfr2.setAlarmType(SPINEFunctionConstants.ABOVE_THRESHOLD);

					manager.activate(curr, sfr2);
					
					
					//Activate alarm on AMPLITUDE value (one of the features computed above) on CH2
					//alarm sent when AMPLITUDE < lowerThreshold
					
					lowerThreshold = 2000;
					upperThreshold = 1000;

					sfr2.setDataType(SPINEFunctionConstants.AMPLITUDE);
					sfr2.setSensor(SPINESensorConstants.ACC_SENSOR);
					sfr2.setValueType((SPINESensorConstants.CH2_ONLY));
					sfr2.setLowerThreshold(lowerThreshold);
					sfr2.setUpperThreshold(upperThreshold);
					sfr2.setAlarmType(SPINEFunctionConstants.BELOW_THRESHOLD);

					manager.activate(curr, sfr2);
					
				}
				
				
				// repeat this process for other desired sensors; after that we can finally ... 
				else if (sensor == SPINESensorConstants.INTERNAL_TEMPERATURE_SENSOR) {
					SpineSetupSensor sss = new SpineSetupSensor();
					sss.setSensor(sensor);
					sss.setTimeScale(SPINESensorConstants.MILLISEC);
					sss.setSamplingTime(OTHER_SAMPLING_TIME);
					manager.setup(curr, sss);

					FeatureSpineSetupFunction ssf = new FeatureSpineSetupFunction();
					ssf.setSensor(sensor);
					ssf.setWindowSize(OTHER_WINDOW_SIZE);
					ssf.setShiftSize(OTHER_SHIFT_SIZE);
					manager.setup(curr, ssf);

					FeatureSpineFunctionReq sfr = new FeatureSpineFunctionReq();
					sfr.setSensor(sensor);
					sfr.add(new Feature(SPINEFunctionConstants.MODE, 
															  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask()));
					sfr.add(new Feature(SPINEFunctionConstants.MEDIAN, 
						  									  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask()));
					sfr.add(new Feature(SPINEFunctionConstants.MAX, 
							  								  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask()));
					sfr.add(new Feature(SPINEFunctionConstants.MIN, 
							  								  ((Sensor) curr.getSensorsList().elementAt(i)).getChannelBitmask()));
					manager.activate(curr, sfr);	
					
					// SetUp Alarm Engine
					// Same Window and Shift as before
					AlarmSpineSetupFunction ssf3 = new AlarmSpineSetupFunction();
					ssf3.setSensor(sensor);
					ssf3.setWindowSize(WINDOW_SIZE);
					ssf3.setShiftSize(SHIFT_SIZE);
					manager.setup(curr, ssf3);	
					
					//Activate alarm on MIN value (one of the features computed above)on CH1
					//alarm sent when lowerThreshold < MIN < upperThreshold
					AlarmSpineFunctionReq sfr3 = new AlarmSpineFunctionReq();

					
					int lowerThreshold = 1000;
					int upperThreshold = 3000;
					
					sfr3.setDataType(SPINEFunctionConstants.MIN);
					sfr3.setSensor(sensor);
					sfr3.setValueType((SPINESensorConstants.CH1_ONLY));
					sfr3.setLowerThreshold(lowerThreshold);
					sfr3.setUpperThreshold(upperThreshold);
					sfr3.setAlarmType(SPINEFunctionConstants.IN_BETWEEN_THRESHOLDS);

					manager.activate(curr, sfr3);

				}	
					
			}			
		}

		
		
		// ... start the sensor network sensing and computing our aforeactivated services.
		if (activeNodes.size() > 0)
			manager.startWsn(true, true); // we can tune a few node parameters at run-time for reducing the power consumption and the packets drop. 
	}

	private Feature[] features;
	public void received(Data data) {
		// the specific application logic behaves w.r.t. the type of data received 
		
		System.out.println(data);

		switch (data.getFunctionCode()) {
			case SPINEFunctionConstants.FEATURE: {
				
				features = ((FeatureData)data).getFeatures();
				
				// 02 Novembre
				for (int h=0; h<features.length; h++){
					System.out.println("*** " +   features[h].toString() + features[h].getFeatureLabel());
				}
				
				counter++;
				
				// even this simple application shows us up some nice SPINE properties; in fact ... 
				if(counter == 5) {
					// it's possible to deactivate functions computation at runtime (even when the radio on the node works in low-power mode)
					FeatureSpineFunctionReq sfr = new FeatureSpineFunctionReq();
					sfr.setSensor(features[0].getSensorCode());
					sfr.remove(new Feature(features[0].getFeatureCode(), SPINESensorConstants.ALL));
					manager.deactivate(data.getNode(), sfr);
				}	
				
				if(counter == 10) {
					// and, of course, we can activate new functions at runtime
					FeatureSpineFunctionReq sfr = new FeatureSpineFunctionReq();
					sfr.setSensor(features[0].getSensorCode());
					sfr.add(new Feature(SPINEFunctionConstants.RANGE, SPINESensorConstants.CH1_ONLY));
					manager.activate(data.getNode(), sfr);
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
				// OneShotData oneShot = (OneShotData)data; // if needed 'data' can be casted to spine.datamodel.OneShotData 
				break;
				
			case SPINEFunctionConstants.ALARM:
				// AlarmData alarm = (AlarmData)data; // if needed 'data' can be casted to spine.datamodel.AlarmData  
				counter_alarm ++;
				if(counter_alarm == 20) {
					AlarmSpineFunctionReq sfr2 = new AlarmSpineFunctionReq();
					sfr2.setSensor(SPINESensorConstants.ACC_SENSOR);
					sfr2.setAlarmType(SPINEFunctionConstants.ABOVE_THRESHOLD);
					sfr2.setDataType(SPINEFunctionConstants.MAX);
					sfr2.setValueType((SPINESensorConstants.CH1_ONLY));
					
					manager.deactivate(data.getNode(), sfr2);
				}
				break;
		}
		
	}	
	
	public void received(ServiceMessage msg) {
		// for this simple application, I just like to print the service message received
		System.out.println(msg);
	}

	public void dataReceived(int nodeID, Data data) {}

	public void serviceMessageReceived(int nodeID, ServiceMessage msg) {}
	
}
