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

package demo;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.tilab.spine.SPINEManager;
import com.tilab.spine.constants.FeatureCodes;
import com.tilab.spine.constants.SensorCodes;
import com.tilab.spine.interfaces.AMPMessageListener;
import com.tilab.spine.logic.BsnNode;
import com.tilab.spine.logic.Feature;
import com.tilab.spine.packets.BatteryInfoReq;



/**
*
* This is the unit test class for SPINE project 
*
* @author Raffaele Gravina
* @author Antonio Guerrieri
*
* @version 1.0
*/
public class SPINETest implements AMPMessageListener {

	private SPINEManager manager;
	private final short MY_GROUP_ID = 1;
	public final static String PORT = "4";
	public final static String BS_SPEED = "telosb";
	
	private short one_node;
	private short another_node;
	
	private int counter =  0;

	private int window = 40;
	private int shift = 20;
	private int samplingTime = 50;		
	
		
	/*
	 * An object that uses SPINE's services
	 */
	public SPINETest(){
		
		// you have to specify what is your Base Station PORT and its speed. GroupId can vary from 0 to 15.
		manager = SPINEManager.getInstance(PORT, BS_SPEED, MY_GROUP_ID);
		
		// if you register to the manager, it can tell you when something happens. 
		// Usually the 'AmpBsnManager.getInstance' and the 'registerListener' go together.   
		manager.registerListener(this);	
		
		// to boot up the network (discover the motes). To start doing anything you first need to call this method.
		// Usually this is called just once during the application lifetime. 
		manager.bootUpBSN();
	}
	
	

	public static void main(String[] args) {
		new SPINETest();
	}


	public void advertisementReceived(short sourceNode, Hashtable sensor_Axis,
			Vector avFeatures) {
		// if you are here you got a notification from the manager that an advertisement was sent by the node with ID=sourceNode
		// this event can happen only after you bootUp the BSN. 
		// beside from specific reason requiring the actual use of this event, it's recommended to make use of 'discoveryCompleted' event 
		System.out.println("Message Received -> Service Advertisement from NODE_" + sourceNode);	
	}

	public void discoveryCompleted(Vector nodes) {
		// if you are here the discovery procedure has been completed and the given Vector 'nodes' will contain BsnNode objects 
		// corresponding to the nodes within the BSN.
		// The manager takes care of handling the timeout for the discovery process, which is by default set to 1000ms. 
		// It is possible to change the timeout value calling the manager method 'setDiscoveryProcedureTimeout' before the bootUp.
		System.out.println("... Discovery Completed!");
		
		if(nodes.size()>0) {
			// Here you have the details of the nodes you have discovered
			Iterator allNodes = nodes.iterator();
			
			System.out.println("  Printing discovered nodes specifications...");
			while(allNodes.hasNext()){
				BsnNode bN = (BsnNode)allNodes.next();
				System.out.println("    NODE_"+ bN.getNodeID()+ ": ");
				System.out.println("       Sensors: ");
				Iterator sensorAndAxisFromCurrentNode = bN.getSensor_Axis().keySet().iterator();
				while(sensorAndAxisFromCurrentNode.hasNext()){
					Short currSensorCode = (Short)sensorAndAxisFromCurrentNode.next();
					Short currAxisNum = (Short)bN.getSensor_Axis().get(currSensorCode);
					
					System.out.println("          " + 
							com.tilab.spine.constants.SensorCodes.getSensorName(currSensorCode.shortValue()) + 
							" with " + currAxisNum + " axes.");
					
				}
				System.out.println("       Available Features: ");
				Iterator avFeaturesFromCurrentNode = bN.getAvailableFeatures().iterator();
				while(avFeaturesFromCurrentNode.hasNext()){
					System.out.println("          " +
							FeatureCodes.getFeatureName(((Short)avFeaturesFromCurrentNode.next()).shortValue()) );
				}
			}
			
			one_node = 0;
			another_node = 0;			
						
			// Typically, once you know by how many and what kind of nodes your network is composed of, 
			// you are able to implement your application. 
			
			// In this simple example, the first step is asking features to one or two nodes (depending on how many they are).
			// In the example we don't consider the case of a network with more than two nodes. Note that,
			// you should look at the nodes details before asking features they could not compute.
			// The second step is to enable the fall detector.
			// The third step is to send the start command to the network. Only this way the nodes will begin to compute 
			// the requested features and possibly notify of fall events.
			// The fourth step is to request for a periodic battery info transmission at the first node ('one_node'). Note
			// there are other voltage level notification option, i.e. one-shot information ('requestBatteryStatus' method).
			// Finally note that the notification for batteries status is independent from the 'start' call; 
			// if you just need to be notified by the node for voltage levels, you won't call the 'start' method. 
			
			// STEP 1: Here you request some feature to the BSN
			Feature[] features;
			
			Iterator it = nodes.iterator();
			while (it.hasNext()) {
				BsnNode currNode = (BsnNode)it.next(); 
				
				// you'll use only the first two nodes detected 
				if(one_node == 0 ) 
					one_node = currNode.getNodeID();
				else if(another_node == 0) 
					another_node = currNode.getNodeID();
				
				// you can request to different nodes different features
				if (currNode.getNodeID() == one_node) {
					features = new Feature[3];
					features[0] = new Feature(FeatureCodes.MEAN, SensorCodes.ACCELEROMETER, SensorCodes.X_Y_Z_AXIS);
					features[1] = new Feature(FeatureCodes.MIN, SensorCodes.ACCELEROMETER, SensorCodes.X_AXIS);
					features[2] = new Feature(FeatureCodes.MAX, SensorCodes.ACCELEROMETER, SensorCodes.X_AXIS);
					manager.activateFeatures(currNode.getNodeID(), features, window, shift, samplingTime);
				}
				else if (currNode.getNodeID() == another_node) {
					features = new Feature[1];
					features[0] = new Feature(FeatureCodes.MIN, SensorCodes.ACCELEROMETER, SensorCodes.X_AXIS);
					manager.activateFeatures(currNode.getNodeID(), features, window, shift, samplingTime);
				}
			}
			
			// STEP 2: Here you request the NODE with NODE_ID='one_node' to enable the 'on-mote fall detection module'
			manager.enableFallDetection(new short[]{one_node});
			
			// STEP 3: you have to start the network to start receiving features values and being notified of fall events
			manager.start();
			
			// STEP 4:  you request a periodic Battery status to the node "one_node"
			manager.requestPeriodicBatteryStatus(new short[]{one_node}, true, 10, BatteryInfoReq.SEC);					
		} 
	}

	public void batteryInfoReceived(short sourceNode, double voltageLevel) {
		// if you are here you received from node with ID=sourceNode its voltage level
		System.out.println("Message Received -> Battery Info from NODE_" + sourceNode + 
						   ". Voltage Detected: " + voltageLevel + "V");
	}

	public void frameReceived(short sourceNode, Feature[] frame, int timestamp) {
		// A "frame" is here a set of features (represented by Feature objects) belonging to a specific node. 
		// In particular, the 'frameReceived' event is generated if and only if, for a certain temporal window, all the 
		// requested features to a node are received correctly by the manager. 
		// Actually, at a low level, a set of features are usually sent with different data packets; if one or more
		// of these low level packets is lost, the overall information for that particular period will be incomplete and
		// consequently the frame not forwarded at higher level.
		
		// Rather than use this event to get the data from a single node, it's probably simpler to refer to the
		// 'superFrameReceived' event. 
		
		//System.out.println("Frame received from NODE_"+sourceNode);		
	}

	public void superFrameReceived(Hashtable superFrame, int timestamp) {
		// a "superframe" is a set of frames all belonging to a specific time interval. In particular, this event
		// is generated by the manager as he received correctly - for that time window - all the data packets 
		// he expects to receive. The set of frames is an Hashtable with node IDs as keys and Feature[] as values.
		
		counter++;
		
		System.out.print("\nSuperframe received (timestamp " + timestamp + ") from: ");
		Iterator it = superFrame.keySet().iterator();
		while(it.hasNext()) 
			System.out.print("NODE_" + ((Short)it.next()).shortValue() + " ");
		System.out.println();
		
		//Here we print feature values from BSN (in particular from nodes "one_node" and "another_node" )
		for(int j = 0; j < 2; j++ ) {
    		
    		short i = 0;
    		if(j == 0)
    			i = one_node;
    		else if(j == 1) 
    			i = another_node;
    		
    		if(superFrame.containsKey(new Short(i))) {
    			System.out.println("NODE_" + i + ": ");
    			Feature[] feats = (Feature[])superFrame.get(new Short(i));
    			
    			for(int k = 0; k < feats.length; k++)
    				System.out.println(feats[k]);
    		}
    	}
		
		// as trivial sample, we show how to remove feature(s) computation thru the manager. 
		// After 10 superframes received correctly we stop the computation of the MEAN feature on the X axis 
		// of the ACCELEROMETER sensor on the node with ID=one_node 
		
		if (counter == 10) 
			manager.removeFeatures(one_node, new Feature[]{
					new Feature(FeatureCodes.MEAN, SensorCodes.ACCELEROMETER, SensorCodes.X_AXIS)});			
		
		// after the removal, if you look carefully at the prints in the console, you should find, for the first node, 
		// the MEAN on the ACCELEROMETER sensor now computed only on the Y and Z axis.   
	}
	
	public void messageReceived(short sourceNode, short messageType, short messageDetail) {
		// if you are here, some events or errors have occurred and signaled by the node with ID='sourceNode'.
		// For instance, an event is a detection of a fall and an error message can regard the inability of handle a 
		// feature activation request.
		// Codes are signaled either for the type of the message than for its detail; you can then convert those codes into
		// user-frendly string as showed below.
		
		System.out.println("Message Received -> Service Message from NODE_" + sourceNode + " of type: '" +
				com.tilab.spine.constants.ServiceMessageCodes.getMessageTypeCaption(messageType) +
				"' with details: '" + com.tilab.spine.constants.ServiceMessageCodes.getMessageDetailCaption(messageDetail) + "'" );
		
	}

}
