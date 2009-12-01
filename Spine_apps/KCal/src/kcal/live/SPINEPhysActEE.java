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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.Ã‚Â  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MAÃ‚Â  02111-1307, USA.
*****************************************************************/

/**
 *
 *  This class measures physical activity and estimates 
 *	energy expenditure using the SPINE framework 
 *
 * @author Edmund Seto  <seto@berkeley.edu>
 * @author Raffaele Gravina <rgravina@wsnlabberkeley.com>
 * @author Po Yan <pyan@eecs.berkeley.edu>
 *
 * @version 1.3
 */

package kcal.live;

import java.util.Vector;

import spine.SPINEFactory;
import spine.SPINEFunctionConstants;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.SPINESensorConstants;
import spine.datamodel.*;

import spine.datamodel.functions.*;

public class SPINEPhysActEE implements SPINEListener {

	private static String APP_PROPERTY_PATH = "resources/";
	
	private static final int SAMPLING_TIME = 33;	// 33 millsec = 30 Hz
	private static final short WINDOW_SIZE = 30;	// window as number of samples
	private static final short SHIFT_SIZE = 30;		// shift in number of samples from start of previous frame
	
	private static SPINEManager manager;
	
	private static int counter = 0;
	private static double accum_minute_V = 0;
	private static double accum_minute_H = 0;
	
	
	public static void main(String[] args) {
		new SPINEPhysActEE();
	}
	
	
	public SPINEPhysActEE() {
		try {
			manager = SPINEFactory.createSPINEManager(APP_PROPERTY_PATH + "app.properties");		
		
			// Next, we need to register a SPINEListener implementation to the SPINE manager instance...
			// (I register myself since I'm a SPINEListener implementation!)
			manager.addListener(this);	
			
			// Let's start playing! Start the network discovery...
			manager.discoveryWsn();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
	}

		
	public void newNodeDiscovered(Node newNode) {}
	
	
	public void discoveryCompleted(Vector activeNodes) {
		// we loop over the discovered nodes (hopefully, at least one node has been discovered.)
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
					
					// ... we first setup that sensor, specifying its sampling time and time scale...
					SpineSetupSensor sss = new SpineSetupSensor();
					sss.setSensor(sensor);
					sss.setTimeScale(SPINESensorConstants.MILLISEC);
					sss.setSamplingTime(SAMPLING_TIME);
					manager.setup(curr, sss);

					// we're too anxious and we're are going to breath a bit from a request to the following one...
					try { Thread.sleep(200); } catch (InterruptedException e) {}
					
					// ... we can setup a specific function (in this case a Feature) on that sensor...
					FeatureSpineSetupFunction ssf = new FeatureSpineSetupFunction();
					ssf.setSensor(sensor);
					ssf.setWindowSize(WINDOW_SIZE);
					ssf.setShiftSize(SHIFT_SIZE);
					manager.setup(curr, ssf);

					try { Thread.sleep(200); } catch (InterruptedException e) {}
					
					// ... we can activate that function with function specific parameters 
					// (for Feature they are the desired feature extractors)... 
					FeatureSpineFunctionReq sfr = new FeatureSpineFunctionReq();
					sfr.setSensor(sensor);

					// 3-axis accelerometer using my own feature that computes the gravity-subtracted V and two H components
					// for the Sun and Chen energy expenditure calculation.
				    sfr.add(new Feature(SPINEFunctionConstants.KCAL, SPINESensorConstants.CH1_CH2_CH3_ONLY));
					
				    manager.activate(curr, sfr);
				    
				    try { Thread.sleep(200); } catch (InterruptedException e) {}
				}
			}			
		}
		
		// We now start the sensor network sensing and computing our previously activated services...
		if (activeNodes.size() > 0)
			manager.startWsn(true, false); // we can tune a few node parameters at run-time for reducing the power consumption and the packets drop. 
	}


	public void received(Data data) {
		// This application processes the computed features on the server side to summarize Physical Activity 
		// and Estimate Energy Expenditure
		
		double EE_minute = 0;	

		if (data.getFunctionCode() == SPINEFunctionConstants.FEATURE) {
			// A feature has been computed (as opposed to an ALARM or ONE_SHOT), so we can process it...

			Feature[] features = ((FeatureData)data).getFeatures();
								
			if (features[0].getFeatureCode() == SPINEFunctionConstants.KCAL) {
				// Process the KCAL features
				// KCAL computes a vertical activity count (ch 1) and a horizontal activity count (ch 2)
				switch (features[0].getChannelBitmask()) {
					case SPINESensorConstants.CH1_CH2_ONLY:
						System.out.println("KCAL 1(V) 2(H): " + features[0].getCh1Value() + " " + features[0].getCh2Value() );
									 
						accum_minute_V += (((double)features[0].getCh1Value())/30.0)/1024.0;  // rescale the sensor data by 1024.  // note, divide by 30 because of 30 samples within a sec.
						accum_minute_H += (((double)features[0].getCh2Value())/30.0)/1024.0;
						counter++;
						
						if (counter >= 60) {
							
							// EEact(k) = a*H^p1 + b*V^p2
							//
							// assume:  mass(kg) = 80 kg
							// 			gender = 1 (male)
							//
							// a = (12.81 * mass(kg) + 843.22) / 1000 = 1.87
							// b = (38.90 * mass(kg) - 682.44 * gender(1=male,2=female) + 692.50)/1000 = 3.12
							// p1 = (2.66 * mass(kg) + 146.72)/1000 = 0.36
							// p2 = (-3.85 * mass(kg) + 968.28)/1000 = 0.66
							
							EE_minute = (1.87 * java.lang.Math.pow(accum_minute_H, 0.36) + 3.12 * java.lang.Math.pow(accum_minute_V, 0.66)) / 4.184;  // the 4.184 is to convert from KJ to kilocalories 
				
							System.out.println("*************************************");
							System.out.println("EE in units kcal/minute: " + EE_minute); 

							accum_minute_V = 0;
							accum_minute_H = 0;
							counter = 0;
						}

						
						break;
					default:
						System.out.println(data + " has no kcal data!");
					break;
				}
			}
		}	
	}
	
	
	public void received(ServiceMessage msg) {}
	
	
	public void dataReceived(int nodeID, Data data) {}	
	public void serviceMessageReceived(int nodeID, ServiceMessage msg) {}
	
}
