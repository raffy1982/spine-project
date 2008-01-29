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

package com.tilab.spine;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;

import com.tilab.spine.constants.Constants;
import com.tilab.spine.constants.FeatureCodes;
import com.tilab.spine.constants.PacketConstants;
import com.tilab.spine.constants.SensorCodes;
import com.tilab.spine.constants.ServiceMessageCodes;
import com.tilab.spine.interfaces.AMPMessageListener;
import com.tilab.spine.logic.BsnNode;
import com.tilab.spine.logic.Feature;
import com.tilab.spine.packets.BatteryInfo;
import com.tilab.spine.packets.BatteryInfoPkt;
import com.tilab.spine.packets.BatteryInfoReq;
import com.tilab.spine.packets.BatteryInfoReqPkt;
import com.tilab.spine.packets.Data;
import com.tilab.spine.packets.DataPkt;
import com.tilab.spine.packets.FeatureActivation;
import com.tilab.spine.packets.FeatureActivationPkt;
import com.tilab.spine.packets.Header;
import com.tilab.spine.packets.PacketManager;
import com.tilab.spine.packets.RemoveFeature;
import com.tilab.spine.packets.RemoveFeaturePkt;
import com.tilab.spine.packets.ServiceAdvertisement;
import com.tilab.spine.packets.ServiceAdvertisementPkt;
import com.tilab.spine.packets.ServiceDiscovery;
import com.tilab.spine.packets.ServiceDiscoveryPkt;
import com.tilab.spine.packets.ServiceMessage;
import com.tilab.spine.packets.ServiceMessagePkt;


import net.tinyos.message.Message;
import net.tinyos.message.MessageListener;
import net.tinyos.message.MoteIF;
import net.tinyos.packet.BuildSource;
import net.tinyos.packet.PacketSource;
import net.tinyos.packet.PhoenixSource;
import net.tinyos.util.PrintStreamMessenger;

/**
 *
 * This is the class that abstract the AMP procedure 
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.1
 */
public class SPINEManager implements MessageListener {
	
	private final static long DISCOVERY_TIME = 1000;
	
	private final static int DEFAULT_TRANSITORY = 3;
	
	private Hashtable totalRequestedFeatures = new Hashtable();
	
	private Hashtable totalAwaitedFeatures = new Hashtable();
	
	
	private Hashtable currentFrames = new Hashtable();
	
	private Hashtable currentTimestamps = new Hashtable();
	
	private PacketSource pks;
	private PhoenixSource phoenix;
	private MoteIF moteIF;
	
	private AMPMessageListener listener;
	
	private boolean discoveryCompleted = false;
	
	private long discoveryProcedureTimeout = DISCOVERY_TIME;
	
	private Vector nodes = new Vector();
	
	private Hashtable superFrame = new Hashtable();
	
	private int superFrameTimestamp = 0;
	
	private int transitory = DEFAULT_TRANSITORY;
	
	private int transitoryCounter = 0;
		
	private String bsPrefix = "serial@";
	private String baseStation = null;
	String bsPort = null;
	String bsSpeed = null;
	private short groupID;
	

	private Vector featPositions = new Vector(); 
	private Vector featCaptions = FeatureCodes.getFeatureCaptions();
	
	
	public static SPINEManager instance;
	
	
	private SPINEManager(String bsPort, String bsSpeed, short groupID) {	
		
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.matches("[ a-z]*windows[ a-z]*"))
			this.bsPrefix += "COM";
		else if (osName.matches("[ a-z]*linux[ a-z]*"))
			this.bsPrefix += "/dev/ttyUSB";
		else this.bsPrefix += "/dev/ttyUSB"; // temp condition
		
		this.baseStation = this.bsPrefix + bsPort + ":" + bsSpeed; 
		this.bsPort = bsPort;
		this.bsSpeed = bsSpeed;
		this.groupID = groupID;
		prepareConnection(bsPort, bsSpeed);
	}
	
	
	/**
	 * As the AmpBsnManager is a singleton, it can't be instantiated with a standard constructor;
	 * instead, use this method returns the manager singleton. 
	 * 
	 * @param bsPort the COM port number to which the base station node is attached (i.e. "8")
	 * @param bsSpeed the base station communication speed (i.e. "telosb" or "115200")
	 * @param groupID the groupID the application choose for the detected nodes 
	 * 
	 * @return the manager singleton instance
	 */
	public static SPINEManager getInstance(String bsPort, String bsSpeed, short groupID) {
		if (instance == null) 
			instance = new SPINEManager(bsPort, bsSpeed, groupID);
		return instance;
	}
	
	private void prepareConnection(String bsPort, String bsSpeed) {
		this.baseStation = this.bsPrefix + bsPort + ":" + bsSpeed;
		this.bsPort = bsPort;
		this.bsSpeed = bsSpeed;
		
		//PhoenixSource phoenix = BuildSource.makePhoenix(PrintStreamMessenger.err);    
		//this.moteIF = new MoteIF(phoenix);	   
	    
		// the following two lines let useless the use of the SerialForwarder
		pks = BuildSource.makePacketSource(baseStation);
		
		phoenix = BuildSource.makePhoenix(pks, PrintStreamMessenger.err);
		
        moteIF = new MoteIF(phoenix); 
        
	    moteIF.registerListener(new ServiceDiscoveryPkt(), this);
	    moteIF.registerListener(new ServiceAdvertisementPkt(), this);
	    moteIF.registerListener(new DataPkt(), this);
	    moteIF.registerListener(new ServiceMessagePkt(), this);
	    moteIF.registerListener(new RemoveFeaturePkt(), this);
	    moteIF.registerListener(new FeatureActivationPkt(), this);
	    moteIF.registerListener(new BatteryInfoReqPkt(), this);
	    moteIF.registerListener(new BatteryInfoPkt(), this);
	}
	
	/**
	 * This method registers the application listener for the AMP protocol
	 * 
	 * @param listener the AMPMessageListener implementation of the application 	  
	 */
	public void registerListener(AMPMessageListener listener) {
		this.listener = listener;
	}
	
	/**
	 * This method is used to boot up the BSN. That means the manager broadcast a ServiceDiscovery
	 * thru the base-station and starts the discoveryProcedureTimer to wait for the nodes advertisements. 
	 * 	  
	 */
	public void bootUpBSN() {
	    sendServiceDiscovery();
		
	    new MyTimer(discoveryProcedureTimeout).start();
	}
	
	/**
	 * This method makes the BSN running. That means the BSN nodes start to compute and transmit 
	 * their features over the raw-data.  
	 * Use it when the BSN nodes are successfully configured and the feature needed are requested.
	 * 
	 */
	public void start() {
		start("Start!");				
	}
	
	private void start(String caption) {
		Header sdH = new Header(PacketConstants.SERVICE_DISCOVERY_PKT, groupID, Constants.BROADCAST_ADDRESS);
    	ServiceDiscoveryPkt sdp = PacketManager.buildServiceDiscoveryPkt(
    										  new ServiceDiscovery(sdH, true, (short)nodes.size()));    	
        try {
			moteIF.send(MoteIF.TOS_BCAST_ADDR, sdp);
			System.out.println("Message Sent -> " + caption);
		} catch (IOException e) {
			e.printStackTrace();
		} 				
	}
	
	/**
	 * This method restart/resynchronize the BSN.
	 * Use it when some nodes have suspended for a period of time and becomes again active:
	 * this will ensure a correct time synchronization among all the BSN nodes.    
	 * 
	 */
	public void restart() {
		start("Restart!");			
	}

	/**
	 * This method stops and reset the whole BSN. That means every node whitin the net will be
	 * stopped and reset in its idle state.
	 * It is suggested to use this method before closing the application. 
	 * 
	 */
	public void stop() { 
		Header sdH = new Header(PacketConstants.SERVICE_DISCOVERY_PKT, groupID, Constants.BROADCAST_ADDRESS);
    	ServiceDiscoveryPkt sdp = PacketManager.buildServiceDiscoveryPkt(
    										  new ServiceDiscovery(sdH, true));    	
        try {
			moteIF.send(MoteIF.TOS_BCAST_ADDR, sdp);
			System.out.println("Message Sent -> " + "Stop!");
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		transitory = 0;
		
		/*moteIF.deregisterListener(new ServiceDiscoveryPkt(), this);
	    moteIF.deregisterListener(new ServiceAdvertisementPkt(), this);
	    moteIF.deregisterListener(new DataPkt(), this);
	    moteIF.deregisterListener(new ServiceMessagePkt(), this);
	    moteIF.deregisterListener(new RemoveFeaturePkt(), this);
	    moteIF.deregisterListener(new FeatureActivationPkt(), this);
	    moteIF.deregisterListener(new BatteryInfoReqPkt(), this);
	    moteIF.deregisterListener(new BatteryInfoPkt(), this);*/
	    
	    //phoenix.shutdown();
        
	}
	
	private void sendServiceDiscovery() {
		Header sdH = new Header(PacketConstants.SERVICE_DISCOVERY_PKT, groupID, Constants.BROADCAST_ADDRESS);
    	ServiceDiscoveryPkt sdp = PacketManager.buildServiceDiscoveryPkt(new ServiceDiscovery(sdH));    	
        try {
			moteIF.send(MoteIF.TOS_BCAST_ADDR, sdp);
			System.out.println("Message Sent -> " + PacketConstants.SERVICE_DISCOVERY_PKT_CAPTION);
		} catch (IOException e) {
			e.printStackTrace();
		} 		
	}
	
	/**
	 * This method is used to request the activation of the specified Features set to a 
	 * particular node.   
	 * 
	 * @param to the node to which this message is addressed 
	 * @param features the Feature array containing the requested list of features  
	 * @param window the window, expressed in number of sensor readings (samples), over which the features
	 * 		  have to be computed 
	 * @param shift the overlap amount for the sliding window, expressed in number of sensor readings (samples)
	 * @param samplingTime the period, expressed in ms, the node has to sample at. 	  
	 */
	public void activateFeatures(short to, Feature[] features, int window, int shift, int samplingTime) {
		if (!isValidNodeID(to))
			return;
		
		for (int i = 0; i < features.length; i++) {
    		Header hd = new Header(PacketConstants.FEATURE_ACTIVATION_PKT, groupID, to);
			boolean[] enable = SensorCodes.getAxisBitMask(features[i].getAxisCombination());
			FeatureActivation fa = new FeatureActivation(hd, features[i].getFeatureCode(), (short)window, 
														(short)shift, samplingTime, features[i].getSensorCode(), 
														enable[0], enable[1], enable[2]);
			
			int featPosShift = featCaptions.size()*getNodeIndex(to); // assuming nodes is ordered by nodeID (is't actually ordered when the discoverTimeout fires)
			if (enable[0])
				featPositions.add(new Integer(1 + featCaptions.indexOf(getFeatureShortName(features[i].getSensorCode(), SensorCodes.AXIS_X, features[i].getFeatureCode())) + featPosShift) );
			if (enable[1])
				featPositions.add(new Integer(1 + featCaptions.indexOf(getFeatureShortName(features[i].getSensorCode(), SensorCodes.AXIS_Y, features[i].getFeatureCode())) + featPosShift) );
			if (enable[2])
				featPositions.add(new Integer(1 + featCaptions.indexOf(getFeatureShortName(features[i].getSensorCode(), SensorCodes.AXIS_Z, features[i].getFeatureCode())) + featPosShift) );
			
			FeatureActivationPkt fap = PacketManager.buildFeatureActivationPkt(fa);
			try {
				moteIF.send(MoteIF.TOS_BCAST_ADDR, fap);
				
				Vector featureCurrNode = (Vector)totalRequestedFeatures.get(new Short(to));
				boolean featureFound = false;
				for(int s = 0; s < featureCurrNode.size(); s++){
					Feature temp = (Feature)featureCurrNode.elementAt(s);
					if(temp.getSensorCode() == features[i].getSensorCode() &&
							temp.getFeatureCode() == features[i].getFeatureCode() ) {
						temp.mergeAxisCombination(features[i].getAxisCombination());
						featureFound = true;
						break;
					}
				}
				if(!featureFound)
					featureCurrNode.addElement(features[i]);
				
				System.out.println("Message Sent -> " + PacketConstants.FEATURE_ACTIVATION_PKT_CAPTION);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {e.printStackTrace(); }
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		
	}
	
	/**
	 * This method is used to disable the computation and transmission of the specified 
	 * Features set to a particular node. 
	 * 
	 * @param to the node to which this message is addressed
	 * @param features the Feature array containing the list of features the node have to disable
	 *        (remove from the tasks list)	  
	 */
	public void removeFeatures(short to, Feature[] features) {
		if (!isValidNodeID(to))
			return;
		
		for (int i = 0; i < features.length; i++) {
    		Header hd = new Header(PacketConstants.REMOVE_FEATURE_PKT, groupID, to);
			boolean[] disable = SensorCodes.getAxisBitMask(features[i].getAxisCombination());
			RemoveFeature rf = new RemoveFeature(hd, features[i].getFeatureCode(), features[i].getSensorCode(), 
												 disable[0], disable[1], disable[2]);
			
			int featPosShift = featCaptions.size()*getNodeIndex(to); // assuming nodes is ordered by nodeID (is't actually ordered when the discoverTimeout fires)
			if (disable[0])
				featPositions.remove(new Integer(1 + featCaptions.indexOf(getFeatureShortName(features[i].getSensorCode(), SensorCodes.AXIS_X, features[i].getFeatureCode())) + featPosShift) );
			if (disable[1])
				featPositions.remove(new Integer(1 + featCaptions.indexOf(getFeatureShortName(features[i].getSensorCode(), SensorCodes.AXIS_Y, features[i].getFeatureCode())) + featPosShift) );
			if (disable[2])
				featPositions.remove(new Integer(1 + featCaptions.indexOf(getFeatureShortName(features[i].getSensorCode(), SensorCodes.AXIS_Z, features[i].getFeatureCode())) + featPosShift) );
			
			try {
				moteIF.send(MoteIF.TOS_BCAST_ADDR, PacketManager.buildRemoveFeaturePkt(rf));
				
				Short k = new Short(to);
				Vector featureCurrNode = (Vector)totalRequestedFeatures.get(k);
				int tot = featureCurrNode.size();
				for(int s = 0; s < tot; s++){
					Feature temp = (Feature)featureCurrNode.elementAt(s);
					if(temp.getSensorCode() == features[i].getSensorCode() &&
							temp.getFeatureCode() == features[i].getFeatureCode() ){
						if(temp.deleteAxisFromCurrAxisCombination(features[i].getAxisCombination())) {
							featureCurrNode.removeElementAt(s);
							totalRequestedFeatures.put(k, featureCurrNode);
						}
						
						break;
					}
				}
				
				System.out.println("Message Sent -> " + PacketConstants.REMOVE_FEATURE_PKT_CAPTION);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {e.printStackTrace(); }
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}

	public void messageReceived(int to, Message message) {
		
		if (message instanceof ServiceAdvertisementPkt) {
	    	ServiceAdvertisement msg = PacketManager.parseServiceAdvertisementPkt((ServiceAdvertisementPkt)message);
	    	short sourceNode = msg.getHeader().getSourceID(); 
	    	Short k = new Short(sourceNode);
	    	totalRequestedFeatures.put(k, new Vector());
	    	totalAwaitedFeatures.put(k, new Vector());
	    	currentTimestamps.put(k, new Integer(-1));
	    	
	    	if (!discoveryCompleted) {
	    		listener.advertisementReceived(sourceNode, msg.getSensor_Axis(), msg.getAvFeatures());
	    		nodes.add(new BsnNode(sourceNode, msg.getSensor_Axis(), msg.getAvFeatures()));
	    	}	    	
		}
		else if (message instanceof DataPkt) {
			try {
				Data msg = PacketManager.parseDataPkt((DataPkt)message);
				Short k = new Short(msg.getHeader().getSourceID());
				Feature[] frame;
				
				Vector featureCurrNode = (Vector)totalRequestedFeatures.get(k);
				int tot = featureCurrNode.size();
				Vector featureAwaitedCurrNode = (Vector)totalAwaitedFeatures.get(k);
				
				Feature portion = new Feature(msg.getFeatureCode(), 
				        msg.getSensorCode(), 
				        SensorCodes.getAxisCombination(msg.isActiveAxis0(), msg.isActiveAxis1(), msg.isActiveAxis2()), 
				        msg.getFeatureAxis0(), msg.getFeatureAxis1(), msg.getFeatureAxis2());
				int currTimestamp = msg.getHeader().getTimeStamp();
				if (currTimestamp != ((Integer)currentTimestamps.get(k)).shortValue()) {
					// if current timestamp != timestamp old I clean the frame of node 'to', then, if I haven't forwarded 
					// it to the listener  - see following code - I'll discard it, as some DataPkt didn't arrived 
					
					frame = new Feature[tot];
					frame[0] = portion;
					currentFrames.put(k, frame);
					currentTimestamps.put(k, new Integer(currTimestamp));
					
					//now I clone totalRequestedFeatures in totalAwaitedFeatures to be sure I receive what I requested
					Vector temp = (Vector)(((Vector)totalRequestedFeatures.get(k)).clone());
					totalAwaitedFeatures.put(k, temp);
					featureAwaitedCurrNode = (Vector)totalAwaitedFeatures.get(k);
				}
				else {
					frame = (Feature[])currentFrames.get(k);
					frame[tot - featureAwaitedCurrNode.size()] = portion;
				}
				
				for(int s = 0; s < featureAwaitedCurrNode.size(); s++){
					Feature temp = (Feature)featureAwaitedCurrNode.elementAt(s);
					if(temp.getSensorCode() == portion.getSensorCode() &&
							temp.getFeatureCode() == portion.getFeatureCode() ) {
						featureAwaitedCurrNode.removeElementAt(s);
						break;
					}
				}
			
				if ((featureAwaitedCurrNode.size()) == 0) {
					listener.frameReceived(k.shortValue(), frame, currTimestamp);	
					
					if(superFrameTimestamp != currTimestamp) {
						superFrameTimestamp = currTimestamp;
						superFrame.clear();
					}
					superFrame.put(k, frame);
					
					if(superFrame.size() == getActiveNodes()) {
						if (transitoryCounter < transitory)
							transitoryCounter++;
						else
							listener.superFrameReceived((Hashtable)superFrame.clone(), superFrameTimestamp);
					}
				}
					
			} catch (RuntimeException e) {
				// discard the data packet since it's corrupted or not expected
			}			
		}
		else if (message instanceof ServiceMessagePkt) {
			ServiceMessage msg = PacketManager.parseErrorPkt((ServiceMessagePkt)message);
			listener.messageReceived(msg.getHeader().getSourceID(), msg.getMessageType(), msg.getMessageDetail());
			if (msg.getMessageType() == ServiceMessageCodes.ACTIVATE_FEATURE_ERROR) {
				//Short k = new Short(msg.getHeader().getSourceID());
				//totalActiveFeatures.put(k, new Integer(((Integer)totalActiveFeatures.get(k)).intValue()-1));
				//TODO: Delete feature from totalRequestedFeatures needs a AMP ServiceMessagePkt review, according to
				// notify which particular feature request couldn't be accepted 
			}
		}
		else if (message instanceof BatteryInfoPkt) {
			BatteryInfo msg = PacketManager.parseBatteryInfoPkt((BatteryInfoPkt)message);
			listener.batteryInfoReceived(msg.getHeader().getSourceID(), msg.getActualVoltage());
		}
		
	}
	
	/**
	 * This method is used to enable the "fall detection" functionality to the specified list of nodes. 
	 * 
	 * @param to the list node to which this message is addressed	  
	 */
	public void enableFallDetection(short[] to) {
		for(int i = 0; i < to.length; i++) {
			if (!isValidNodeID(to[i]))
				continue;
			
			FeatureActivation fa = new FeatureActivation(new Header(
					PacketConstants.FEATURE_ACTIVATION_PKT, groupID, to[i]),
					FeatureCodes.TOTAL_ENERGY, (short) 0, (short) 0,
					0, SensorCodes.ACCELEROMETER, true, true, true);
			FeatureActivationPkt fap = PacketManager.buildFeatureActivationPkt(fa);
			try {
				moteIF.send(MoteIF.TOS_BCAST_ADDR, fap);
				System.out.println("Message Sent -> Fall Detection Request");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {e.printStackTrace(); }
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
	
	/**
	 * This method is used to disable the "fall detection" functionality to the specified list of nodes.
	 * 
	 * @param to the list node to which this message is addressed	  
	 */
	public void disableFallDetection(short[] to){
		for (int i = 0; i < to.length; i++) {
			if (!isValidNodeID(to[i]))
				continue;
			
    		Header hd = new Header(PacketConstants.REMOVE_FEATURE_PKT, groupID, to[i]);
			RemoveFeature rf = new RemoveFeature(hd, FeatureCodes.TOTAL_ENERGY, SensorCodes.ACCELEROMETER, 
												 true, true, true);
			try {
				moteIF.send(MoteIF.TOS_BCAST_ADDR, PacketManager.buildRemoveFeaturePkt(rf));
				System.out.println("Message Sent -> Fall Detection Disable");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {e.printStackTrace(); }
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
	
	/**
	 * This method is used to make an asynchronous request for the voltage level 
	 * to the specified list of nodes.
	 * 
	 * @param to the list node to which this message is addressed	  
	 */
	public void requestBatteryStatus(short[] to) {
		
		for (int i = 0; i < to.length; i++) {
			if (!isValidNodeID(to[i]))
				continue;
			
			BatteryInfoReq bir = new BatteryInfoReq(new Header(
					PacketConstants.BATTERY_INFO_REQUEST_PKT, groupID, to[i]));
			BatteryInfoReqPkt birp = PacketManager.buildBatteryInfoReqPkt(bir);
			try {
				moteIF.send(MoteIF.TOS_BCAST_ADDR, birp);
				System.out.println("Message Sent -> "
						+ PacketConstants.BATTERY_INFO_REQUEST_PKT_CAPTION);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {e.printStackTrace(); }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method is used to make a synchronous request for the voltage level 
	 * to the specified list of nodes.
	 * 
	 * @param to the list node to which this message is addressed 
	 * @param isTimePeriodic 'true' if the request is periodic by time; 
	 *        'false' if the request is periodic by packets
	 * @param period this number is interpreted as time-period if the flag 'isTimePeriodic' is true;
	 * 		  otherwise represents the number of AMP packets the specified nodes have to wait before sending a 
	 *        BatteryInfo packet to the base-station.  
	 * @param scale 	  
	 */
	public void requestPeriodicBatteryStatus(short[] to, boolean isTimePeriodic, int period, short scale) {
		for (int i = 0; i < to.length; i++) {
			if (!isValidNodeID(to[i]))
				continue;
			
			BatteryInfoReq bir = new BatteryInfoReq(new Header(
					PacketConstants.BATTERY_INFO_REQUEST_PKT, groupID, to[i]),
					isTimePeriodic, scale, (short) period);
			BatteryInfoReqPkt birp = PacketManager.buildBatteryInfoReqPkt(bir);
			try {
				moteIF.send(MoteIF.TOS_BCAST_ADDR, birp);
				System.out.println("Message Sent -> Periodic "
						+ PacketConstants.BATTERY_INFO_REQUEST_PKT_CAPTION);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method is used to cancel a previous periodic request for voltage level transmission 
	 * to the specified list of nodes.
	 * 
	 * @param to the list node to which this message is addressed	  
	 */
	public void cancelBatteryStatusRequest(short[] to) {
		for (int i = 0; i < to.length; i++) {
			if (!isValidNodeID(to[i]))
				continue;
			
			BatteryInfoReq bir = new BatteryInfoReq(new Header(PacketConstants.BATTERY_INFO_REQUEST_PKT, groupID, to[i]),
													false, (short)0, BatteryInfoReq.CANCEL_PERIODIC_REQUEST);
			BatteryInfoReqPkt birp = PacketManager.buildBatteryInfoReqPkt(bir);
			try {
				moteIF.send(MoteIF.TOS_BCAST_ADDR, birp);
				System.out.println("Message Sent -> Cancel " + PacketConstants.BATTERY_INFO_REQUEST_PKT_CAPTION);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method returns the number of nodes that are supposed to be currently active 
	 * (nodes currently computing and sending features values).
	 * 
	 * @return the number of nodes supposed to be currently active 	  
	 */
	public int getActiveNodes() {
		int actives = 0;
		Iterator it = totalRequestedFeatures.keySet().iterator();
		while(it.hasNext()) {
			if(((Vector)totalRequestedFeatures.get(it.next())).size() > 0)
				actives++;
		}

		return actives;
	}
	
	/**
	 * This method returns the number of all the features requested to all the nodes. 
	 * 
	 * @return the number of all the features requested to all the nodes	  
	 */
	public int getTotalActiveFeatures() {
		int totalActiveFeaturesCount = 0;
		
		Iterator it = totalRequestedFeatures.keySet().iterator();
		while(it.hasNext())
			totalActiveFeaturesCount += ((Vector)totalRequestedFeatures.get(it.next())).size();
		
		return totalActiveFeaturesCount;
	}
	
	/**
	 * This method sets the transitory typical of the particular application, expressed in number of superFrames.
	 * Setting the transitory allows the manager to don't forward the first superFrames that would be 
	 * affected by some noise and would negative influence the first classifications.
	 * Use this method if the BSN is charactized by some transitory after its first configuration.
	 *  
	 * This method has effect only if used before the 'start()'; moreover, if not used, a default transitory 
	 * (3 superFrames) will be considered.
	 * 
	 * @param transitory	  
	 */
	public void setTransitory(int transitory) {
		this.transitory = transitory;
	}
	
	/**
	 * This method sets the timeout for the discovery procedure.
	 * 
	 * This method has effect only if used before the 'bootUpBsn()'; if not used, a default timeout of 1s, is used.
	 * 
	 * @param discoveryProcedureTimeout the timeout for the discovery procedure
	 */
	public void setDiscoveryProcedureTimeout(long discoveryProcedureTimeout) {
		this.discoveryProcedureTimeout = discoveryProcedureTimeout;
	}
	
	/**
	 * This method returns the BaseStation COM port number
	 * 
	 * @return the BaseStation COM port number	  
	 */
	public String getBsPort() {
		return bsPort;
	}
	
	/**
	 * This method returns the list of indices of all the activated features. 
	 * following the same order as in 'FeatureCodes.getFeatureCaptions()' Vector.
	 * 
	 * This method is particular useful as let you know immediately the column positions
	 * in the trainingSet of all the feature activated on all the BSN nodes. 
	 * 
	 * @return the list of indices corresponding to the whole BSN feature set  	  
	 */
	public Vector getFeatureColumnPositions() {
		return featPositions;
	}
	
	private int getNodeIndex(short node) {
		for (int i=0; i<nodes.size(); i++) 
			if ((short)(((BsnNode)nodes.elementAt(i)).getNodeID()) == node)
				return i;
		return 0;
	}
	
	private static String getFeatureShortName(short sensor, byte axis, short feature) {
		//return ""+SensorCodes.getSensorShortName(sensor)+"_"+axis+"_"+FeatureCodes.getFeatureShortName(feature);
		return ""+sensor+"_"+axis+"_"+feature;
	}
	
	private boolean isValidNodeID(short nodeID) {
		for (int i=0; i<nodes.size(); i++) {
			if (((BsnNode)nodes.elementAt(i)).getNodeID() == nodeID) 
				return true;
		}
		return false;
	}
	
	class MyTimer extends Thread {
		
		private long delay = 0;
		
		public MyTimer(long delay) {
			this.delay = delay;
		}
		
		public void run () {
			try {
				sleep(delay);
			} catch (InterruptedException e) {e.printStackTrace();}
			
			if (nodes.size()==0) 
				listener.messageReceived(Constants.BASE_STATION_ADDRESS, ServiceMessageCodes.FATAL_ERROR, ServiceMessageCodes.CONNECTION_FAIL);
			else {
				try {
					nodes = SPINEUtils.sortNodesByID(nodes);
				} catch (RuntimeException e) {}
			}
			
			discoveryCompleted = true;			
			listener.discoveryCompleted(nodes);
		}
	}
	
}
