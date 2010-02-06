/*****************************************************************
 SPINE - Signal Processing In-Node Environment is a framework that 
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

/**
 * @author Alessia Salmeri : alessia.salmeri@telcomitalia.it
 */

package logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.TreeSet;
import java.util.Vector;
import spine.SPINEFunctionConstants;
import spine.SPINEPacketsConstants;
import spine.SPINESensorConstants;
import spine.datamodel.Address;
import spine.datamodel.Node;
import spine.datamodel.Feature;
import spine.datamodel.Sensor;
import logic.Command;
import dataSetIO.ArffFile;
import dataSetIO.ArffFileParseException;
import dataSetIO.TxtCsvFile;
import dataSetIO.TxtCsvFileParseException;
import spine.communication.emu.EMUMessage;
import logic.PropertiesController;
import dataSetIO.CommentManager;
import exceptions.FunctionNotSupportedException;
import exceptions.SensorNotPresentException;
import exceptions.SensorSetupException;
import spine.datamodel.functions.SpineSetupSensor;
import spine.datamodel.functions.FeatureSpineSetupFunction;
import spine.datamodel.functions.FeatureSpineFunctionReq;

/**
 * VirtualNode: is a virtual sensor node.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */

public class VirtualNode extends Observable implements Command, SocketCommandListener {

	
	/**
	 * connectToWSN: true or false (default false)
	 * 
	 */
	public boolean connectToWSN = false;

	/**
	 *  computeFeature: ON or OFF (default compute_feature in configuration.properties)
	 * 
	 */
	public String computeFeature = null;
	 
	 
	/**
	 *  labelAlgorithm: ALLWITHFREQ or MOREFREQ or LAST (default label_algorithm in configuration.properties)
	 * 
	*/
	public String labelAlgorithm = null;

	private boolean isStarted = false;

	private Thread actionNode;

	private static VirtualNode instance = null;

	private String URL_PREFIX;

	private int nodeCoordinatorPort=-1;

	private PropertiesController propertControll;

	private int nodeID;

	private Vector sensorsList = new Vector(); // (Sensor((byte)sensorCode,(byte)sensorChannelBitmask)

	private Vector sensorCodeSetupEx = new Vector();

	private Hashtable<Integer, Vector> sensorSetUpInfo = new Hashtable<Integer, Vector>();

	private Hashtable<Integer, Vector> featureSetUpInfo = new Hashtable<Integer, Vector>();

	private Hashtable<Integer, Integer> dsSensorTimeSampling = new Hashtable<Integer, Integer>();

	private Hashtable functionsList = new Hashtable(); // <values:Function>

	private Hashtable<Integer, SpineSetupSensor> setupSensorInformation = new Hashtable<Integer, SpineSetupSensor>(); // sencorCode

	private Hashtable<Integer, FeatureSpineSetupFunction> setupFeatureInformation = new Hashtable<Integer, FeatureSpineSetupFunction>(); // sencorCode

	private PayloadManager payloadManager = new PayloadManager();

	private byte[] payloadSerAdv;

	private byte[] payloadData;

	// Label
	private Hashtable<Integer, Vector> sensorLabelDataStore = new Hashtable<Integer, Vector>();

	private Vector sensorLabelValue = new Vector();

	private Hashtable<Integer, Vector> sensorLabelCalculateDataStore = new Hashtable<Integer, Vector>();

	private Vector sensorLabelCalculateValue = new Vector();

	// SensorCode (Key), int[4 - Vector] -- 1 vector for each channel
	// RAW
	private Hashtable<Integer, Vector[]> sensorRawDataStore = new Hashtable<Integer, Vector[]>();

	private Vector[] sensorRawValue = new Vector[4];

	// RAW (Calculate)
	private Hashtable<Integer, Vector[]> sensorRawCalculateDataStore = new Hashtable<Integer, Vector[]>();

	private Vector[] sensorRawCalculateValue = new Vector[4];

	// MAX
	private Hashtable<Integer, Vector[]> sensorMaxDataStore = new Hashtable<Integer, Vector[]>();

	private Vector[] sensorMaxValue = new Vector[4];

	// MIN
	private Hashtable<Integer, Vector[]> sensorMinDataStore = new Hashtable<Integer, Vector[]>();

	private Vector[] sensorMinValue = new Vector[4];

	// RANGE
	private Hashtable<Integer, Vector[]> sensorRangeDataStore = new Hashtable<Integer, Vector[]>();

	private Vector[] sensorRangeValue = new Vector[4];

	// MEAN
	private Hashtable<Integer, Vector[]> sensorMeanDataStore = new Hashtable<Integer, Vector[]>();

	private Vector[] sensorMeanValue = new Vector[4];

	// AMPLITUDE
	private Hashtable<Integer, Vector[]> sensorAmplitudeDataStore = new Hashtable<Integer, Vector[]>();

	private Vector[] sensorAmplitudeValue = new Vector[4];

	// RMS
	private Hashtable<Integer, Vector[]> sensorRmsDataStore = new Hashtable<Integer, Vector[]>();

	private Vector[] sensorRmsValue = new Vector[4];

	// VARIANGE
	private Hashtable<Integer, Vector[]> sensorVarianceDataStore = new Hashtable<Integer, Vector[]>();

	private Vector[] sensorVarianceValue = new Vector[4];

	// STDEV
	private Hashtable<Integer, Vector[]> sensorStDevDataStore = new Hashtable<Integer, Vector[]>();

	private Vector[] sensorStDevValue = new Vector[4];

	// MODE
	private Hashtable<Integer, Vector[]> sensorModeDataStore = new Hashtable<Integer, Vector[]>();

	private Vector[] sensorModeValue = new Vector[4];

	// MEDIAN
	private Hashtable<Integer, Vector[]> sensorMedianDataStore = new Hashtable<Integer, Vector[]>();

	private Vector[] sensorMedianValue = new Vector[4];

	// TOTENERGY
	private Hashtable<Integer, Vector[]> sensorTotEnergyDataStore = new Hashtable<Integer, Vector[]>();

	private Vector[] sensorTotEnergyValue = new Vector[4];

	// SensorCode (Key), Vector of feature read from DataSet
	private Hashtable<Integer, Vector> sensorAllFeatRawDataStore = new Hashtable<Integer, Vector>();

	private Hashtable<Integer, Vector> sensorFeatRawDataStore = new Hashtable<Integer, Vector>();

	private Hashtable<Integer, Hashtable<Integer, boolean[]>> functionReqStore = new Hashtable<Integer, Hashtable<Integer, boolean[]>>();

	private Hashtable<Integer, Hashtable> featureNodeData = new Hashtable<Integer, Hashtable>();

	private String comment;

	private List<String> attribute;

	private List<Object[]> data;

	private Object[] datum;

	private Vector<ArrayList<Feature>> featureDataVector = new Vector<ArrayList<Feature>>();

	private ArrayList<Feature> featureData = new ArrayList<Feature>();

	private Feature feature = new Feature();

	private short sSPort = 0;

	private ObjectOutputStream oos = null;

	private ObjectInputStream ois = null;

	public static VirtualNode getInstance() {
		if (instance == null)
			instance = new VirtualNode();
		return instance;
	}

	/**
	 * Command received handler.
	 * 
	 * @param srcID is the source ID.
	 * @param emumsg is the message.
	 * 
	 */
	public void commandReceived(int srcID, EMUMessage emumsg) {

		FeatureSpineFunctionReq featureFunctReq;
		SpineSetupSensor setupSensor;
		FeatureSpineSetupFunction featureSetupFunct;

		Hashtable<Integer, boolean[]> featReq = new Hashtable<Integer, boolean[]>(); // featureCode

		if ((byte) emumsg.getClusterId() == SPINEPacketsConstants.START) {
			System.out.println("SPINE Command --> START   emumsg: " + emumsg.toString());
			if (!isStarted) {
				if (computeFeature.equalsIgnoreCase("ON")) {
					Enumeration eFunctReq = functionReqStore.keys();
					int sensorCodeKey;
					while (eFunctReq.hasMoreElements()) {
						sensorCodeKey = (Integer) eFunctReq.nextElement();
						featReq = functionReqStore.get(sensorCodeKey);
						featureNodeDataFromSensorXXDataStore(sensorCodeKey, featReq);
					}

				} else {

					int sensorCodeKey;
					for (int k = 0; k < this.sensorsList.size(); k++) {
						sensorCodeKey = ((Sensor) sensorsList.get(k)).getCode();
						featureNodeDataFromFeatureDataVector(sensorCodeKey);
					}
				}
				isStarted = true;
				start();
			}
		} else if ((byte) emumsg.getClusterId() == SPINEPacketsConstants.RESET) {
			System.out.println("SPINE Command --> RESET   emumsg: " + emumsg.toString());
			reset();
			// SYNC -- Not used in SPINE
		} else if ((byte) emumsg.getClusterId() == SPINEPacketsConstants.SYNCR) {
			System.out.println("SPINE Command --> SYNCR   emumsg: " + emumsg.toString());
			// SERVICE_DISCOVERY
		} else if ((byte) emumsg.getClusterId() == SPINEPacketsConstants.SERVICE_DISCOVERY) {
			System.out.println("SPINE Command --> SERVICE_DISCOVERY   emumsg: " + emumsg.toString());
			EMUMessage msg = new EMUMessage();
			// In msg type Node Information the ProfileId contains Server Socket
			// Node port number (otherwise 0)
			msg.setProfileId((short) 0);
			msg.setSourceURL(URL_PREFIX + this.getNodeID());
			msg.setClusterId(SPINEPacketsConstants.SERVICE_ADV); // SERVICE_ADV
			payloadSerAdv = payloadManager.createSerAdvPayload(this.sensorsList, this.functionsList);
			short[] payloadShort = new short[payloadSerAdv.length];
			for (int i = 0; i < payloadShort.length; i++)
				payloadShort[i] = payloadSerAdv[i];
			msg.setPayload(payloadShort);

			try {
				oos.writeObject(msg);
				oos.flush();
				System.out.println("Node information msg= " + msg);
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.setChanged();
			this.notifyObservers("Service Discovery");
		} else if ((byte) emumsg.getClusterId() == SPINEPacketsConstants.SETUP_SENSOR) {
			System.out.println("SPINE Command --> SETUP_SENSOR   emumsg: " + emumsg.toString());
			try {
				setupSensor = setupSensor(emumsg.getPayload(), sensorsList, sensorCodeSetupEx, dsSensorTimeSampling);
				setupSensorInformation.put((int) setupSensor.getSensor(), setupSensor);

				// CASE COMPUTE_FEATURE = ON --> Compute feature

				if (computeFeature.equalsIgnoreCase("ON")) {

					int sampleTime = setupSensor.getSamplingTime();
					if (setupSensor.getTimeScale() == SPINESensorConstants.SEC) {
						sampleTime = sampleTime * 1000;
					} else if (setupSensor.getTimeScale() == SPINESensorConstants.MIN) {
						sampleTime = sampleTime * 60000;
					}

					int dsSampleTime = (Integer) dsSensorTimeSampling.get(setupSensor.getSensor());

					int sampleRate = sampleTime / dsSampleTime;

					setSensorFeatRawDataStoreFromSensorAllFeatRawDataStore((int) setupSensor.getSensor(), sensorAllFeatRawDataStore, sampleRate);

					Enumeration e = sensorFeatRawDataStore.keys();
					while (e.hasMoreElements()) {
						int sensorCodeKey = (Integer) e.nextElement();
						setSensorRawLabelDataStoreFromFeatureSensorRawDataVector(sensorCodeKey, sensorFeatRawDataStore.get(sensorCodeKey));

					}

				} // END IF COMPUTATION FEATURE == ON

			} catch (SensorNotPresentException e) {
				System.err.println("Case sensor not present");
			} catch (SensorSetupException e) {
				System.err.println("Case sensor setup error");
			}
		} else if ((byte) emumsg.getClusterId() == SPINEPacketsConstants.SETUP_FUNCTION) {
			System.out.println("SPINE Command --> SETUP_FUNCTION   emumsg: " + emumsg.toString());
			try {
				featureSetupFunct = setupFunction(emumsg.getPayload(), sensorsList, sensorCodeSetupEx);
				setupFeatureInformation.put((int) featureSetupFunct.getSensor(), featureSetupFunct);
				if ((!isStarted) && (computeFeature.equalsIgnoreCase("ON"))) {

					int sensorCodeKey = featureSetupFunct.getSensor();
					short windowSize = featureSetupFunct.getWindowSize();
					short shiftSize = featureSetupFunct.getShiftSize();

					sensorRawValue = sensorRawDataStore.get(sensorCodeKey);

					SensorDataManager sensorDataManager = new SensorDataManager(sensorCodeKey, sensorRawValue, windowSize, shiftSize);

					// RAW
					sensorRawCalculateValue = sensorDataManager.calculateFeature(SPINEFunctionConstants.RAW_DATA);

					sensorRawCalculateDataStore.put(sensorCodeKey, sensorRawCalculateValue);

					// MAX
					sensorMaxValue = sensorDataManager.calculateFeature(SPINEFunctionConstants.MAX);

					sensorMaxDataStore.put(sensorCodeKey, sensorMaxValue);

					// MIN
					sensorMinValue = sensorDataManager.calculateFeature(SPINEFunctionConstants.MIN);
					sensorMinDataStore.put(sensorCodeKey, sensorMinValue);

					// RANGE
					sensorRangeValue = sensorDataManager.calculateFeature(SPINEFunctionConstants.RANGE);
					sensorRangeDataStore.put(sensorCodeKey, sensorRangeValue);

					// MEAN
					sensorMeanValue = sensorDataManager.calculateFeature(SPINEFunctionConstants.MEAN);
					sensorMeanDataStore.put(sensorCodeKey, sensorMeanValue);

					// AMPLITUDE
					sensorAmplitudeValue = sensorDataManager.calculateFeature(SPINEFunctionConstants.AMPLITUDE);
					sensorAmplitudeDataStore.put(sensorCodeKey, sensorAmplitudeValue);

					// RMS
					sensorRmsValue = sensorDataManager.calculateFeature(SPINEFunctionConstants.RMS);
					sensorRmsDataStore.put(sensorCodeKey, sensorRmsValue);

					// VARIANCE
					sensorVarianceValue = sensorDataManager.calculateFeature(SPINEFunctionConstants.VARIANCE);
					sensorVarianceDataStore.put(sensorCodeKey, sensorVarianceValue);

					// ST_DEV
					sensorStDevValue = sensorDataManager.calculateFeature(SPINEFunctionConstants.ST_DEV);
					sensorStDevDataStore.put(sensorCodeKey, sensorStDevValue);

					// MODE
					sensorModeValue = sensorDataManager.calculateFeature(SPINEFunctionConstants.MODE);
					sensorModeDataStore.put(sensorCodeKey, sensorModeValue);

					// MEDIAN
					sensorMedianValue = sensorDataManager.calculateFeature(SPINEFunctionConstants.MEDIAN);
					sensorMedianDataStore.put(sensorCodeKey, sensorMedianValue);

					// TOTAL_ENERGY
					sensorTotEnergyValue = sensorDataManager.calculateFeature(SPINEFunctionConstants.TOTAL_ENERGY);
					sensorTotEnergyDataStore.put(sensorCodeKey, sensorTotEnergyValue);

					// Label
					sensorLabelValue = sensorLabelDataStore.get(sensorCodeKey);

					SensorLabelManager sensorLabelManager = new SensorLabelManager(sensorCodeKey, sensorLabelValue, windowSize, shiftSize);

					// RAW
					sensorLabelCalculateValue = sensorLabelManager.calculateLabel(labelAlgorithm);

					sensorLabelCalculateDataStore.put(sensorCodeKey, sensorLabelCalculateValue);

				}

			} catch (FunctionNotSupportedException e) {
				System.err.println("Case function non supported");
			}

		} else if ((byte) emumsg.getClusterId() == SPINEPacketsConstants.FUNCTION_REQ) {
			System.out.println("SPINE Command --> FUNTION_REQ   emumsg: " + emumsg.toString());
			try {
				featureFunctReq = functionReq(emumsg.getPayload(), sensorsList, sensorCodeSetupEx, functionsList);
				if ((!isStarted) && (computeFeature.equalsIgnoreCase("ON"))) {
					setfunctionReqStore(featureFunctReq);
				}
			} catch (FunctionNotSupportedException e1) {
				System.err.println("Case function not supported");
			}

		} else
			System.out.println("SPINE Command --> unknown (" + emumsg.toString() + ")");

	}

	/**
	 * Server Socket Node port received handler.
	 * 
	 * @param srcID is the source ID.
	 * @param valSSPort is the message Server Socket Node port number.
	 * 
	 */
	public void portReceived(int srcID, String valSSPort) {

		if (valSSPort.contains("sSPort")) {
			System.out.println("sSPort -->" + valSSPort.substring(7));
			sSPort = (short) Integer.parseInt(valSSPort.substring(7));

			// Send node information to EMULocalNodeAdapter (SocketThrdServer)
			System.out.println("Send node information to EMULocalNodeAdapter (SocketThrdServer)");

			EMUMessage msg = new EMUMessage();
			// In msg type Node Information the ProfileId contains Server Socket
			// Node port number (otherwise 0)
			msg.setProfileId(sSPort);
			msg.setSourceURL(URL_PREFIX + this.getNodeID());
			msg.setClusterId(SPINEPacketsConstants.SERVICE_ADV); // SERVICE_ADV

			payloadSerAdv = payloadManager.createSerAdvPayload(this.sensorsList, this.functionsList);
			short[] payloadShort = new short[payloadSerAdv.length];
			for (int i = 0; i < payloadShort.length; i++)
				payloadShort[i] = payloadSerAdv[i];
			msg.setPayload(payloadShort);

			try {
				oos.writeObject(msg);
				oos.flush();
				System.out.println("Node information msg= " + msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			System.out.println("SPINE Command --> unknown (" + valSSPort + ")");

	}

	/**
	 * Constructor of a Node object.
	 */
	public VirtualNode() {

		propertControll = PropertiesController.getInstance();

		try {
			propertControll.load();
			// Read url_prefix propertie
			URL_PREFIX = propertControll.getProperty("url_prefix");
			nodeCoordinatorPort = Integer.parseInt(propertControll.getProperty("MOTECOM"));
			if (computeFeature == null) {
				computeFeature = propertControll.getProperty("compute_feature");
			}
			if (labelAlgorithm == null) {
				labelAlgorithm = propertControll.getProperty("label_algorithm");
			}
		} catch (Exception e1) {
			System.err.println("Error in configuration.properties: file or properties (url_prefix, MOTECOM, compute_feature or label_algorithm not found");
			System.exit(1);
		}
		if (URL_PREFIX==null || nodeCoordinatorPort==-1 || computeFeature==null || labelAlgorithm==null){
			System.err.println("Error in configuration.properties: url_prefix, MOTECOM, compute_feature or label_algorithm not found");
			System.exit(1);
		}
	};

	/**
	 * Getter method of the node id
	 * 
	 * @return the node id
	 */
	public int getNodeID() {
		return nodeID;
	}

	/**
	 * Set the node physical ID.
	 */
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	/**
	 * Get the node sensors list.
	 */
	public Vector getSensorsList() {
		return sensorsList;
	}

	/**
	 * Set the node sensors list.
	 */
	public void setSensorsList(Vector sensorsList) {
		this.sensorsList = sensorsList;
	}

	/**
	 * Get the node functions list
	 */
	public Hashtable getFunctionsList() {
		return functionsList;
	}

	/**
	 * Set the node functions list.
	 */
	public void setFunctionsList(Hashtable functionsList) {
		this.functionsList = functionsList;
	}

	/**
	 * Get sensor setup info.
	 */
	public Hashtable getSensorSetUpInfo() {
		return sensorSetUpInfo;
	}

	/**
	 * Set sensor setup info.
	 */
	public void setSensorSetUpInfo(Hashtable<Integer, Vector> sensorSetUpInfo) {
		this.sensorSetUpInfo = sensorSetUpInfo;
	}

	/**
	 * Get feature setup info.
	 * 
	 * @return featureSetUpInfo.
	 */
	public Hashtable getFeatureSetUpInfo() {
		return featureSetUpInfo;
	}

	/**
	 * Set feature setup info.
	 */
	public void setfeatureSetUpInfo(Hashtable<Integer, Vector> featureSetUpInfo) {
		this.featureSetUpInfo = featureSetUpInfo;
	}

	/**
	 * Get feature data.
	 * 
	 * @return featureDataVector.
	 */
	public Vector getFeatureDataVector() {
		return featureDataVector;
	}

	/**
	 * ResetWSN command.
	 */
	public void reset() {
		try {
			actionNode.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		isStarted = false;
		functionReqStore.clear();
		featureNodeData.clear();
		this.setChanged();
		this.notifyObservers("Reset Node");
	}

	/**
	 * SetupSensor command.
	 * 
	 * @return SpineSetupSensor object (SPINE datamodel functions).
	 */
	public SpineSetupSensor setupSensor(short[] shortPayload, Vector sensorsList, Vector sensorCodeSetupEx, Hashtable dsSensorTimeSampling) throws SensorNotPresentException, SensorSetupException {
		String outputMsg;
		SpineSetupSensor setupSensor;
		try {
			setupSensor = payloadManager.decodeSetupSensor(shortPayload, sensorsList, sensorCodeSetupEx, dsSensorTimeSampling, computeFeature);
			outputMsg = setupSensor.toString();
		} catch (SensorNotPresentException e) {
			outputMsg = "[Warning] " + e.getMessage();
			this.setChanged();
			this.notifyObservers(outputMsg);
			throw new SensorNotPresentException(e.getMessage());
		} catch (SensorSetupException e1) {
			outputMsg = "[Warning] " + e1.getMessage();
			this.setChanged();
			this.notifyObservers(outputMsg);
			throw new SensorSetupException(e1.getMessage());
		}
		this.setChanged();
		this.notifyObservers(outputMsg);

		return setupSensor;
	}

	/**
	 * SetupFunction command.
	 * 
	 * @return FeatureSpineSetupFunction object (SPINE datamodel functions).
	 */
	public FeatureSpineSetupFunction setupFunction(short[] shortPayload, Vector sensorsList, Vector sensorCodeSetupEx) throws FunctionNotSupportedException {
		String outputMsg;
		FeatureSpineSetupFunction featureSetupFunct = new FeatureSpineSetupFunction();
		try {
			featureSetupFunct = payloadManager.decodeSetupFunction(shortPayload, sensorsList, sensorCodeSetupEx);
			outputMsg = featureSetupFunct.toString();
		} catch (FunctionNotSupportedException e) {
			outputMsg = "[Warning] " + e.getMessage();
			this.setChanged();
			this.notifyObservers(outputMsg);
			throw new FunctionNotSupportedException(e.getMessage());
		}
		this.setChanged();
		this.notifyObservers(outputMsg);

		return featureSetupFunct;
	}

	private FeatureSpineFunctionReq functionReq(short[] shortPayload, Vector sensorsList, Vector sensorCodeSetupEx, Hashtable functionsList) throws FunctionNotSupportedException {
		String outputMsg;
		FeatureSpineFunctionReq featureFunctReq = new FeatureSpineFunctionReq();
		try {
			featureFunctReq = payloadManager.decodeFunctionReq(shortPayload, sensorsList, sensorCodeSetupEx, functionsList);
			outputMsg = featureFunctReq.toString();

		} catch (FunctionNotSupportedException e) {
			outputMsg = "[Warning] " + e.getMessage();
			this.setChanged();
			this.notifyObservers(outputMsg);
			throw new FunctionNotSupportedException(e.getMessage());
		}
		this.setChanged();
		this.notifyObservers(outputMsg);

		return featureFunctReq;
	}

	// This class extends Thread
	class LoadData extends Thread {
		String dataSetFile;

		public LoadData(String dataSetFile) {
			this.dataSetFile = dataSetFile;
		}

		// This method is called when the thread runs
		public void run() {

			setChanged();
			notifyObservers("StartLoading");
			setChanged();
			notifyObservers("Data set file - " + dataSetFile + " - loading...");
			setFeatureDataVectorFromData(data);

			// CASE COMPUTE_FEATURE = ON
			if (computeFeature.equalsIgnoreCase("ON")) {

				setSensorAllFeatRawDataStoreFromFeatureDataVector(featureDataVector);

			} // END IF COMPUTATION FEATURE == ON
			setChanged();
			notifyObservers("Data set file - " + dataSetFile + " - loaded");
			setChanged();
			notifyObservers("EndLoading");
			if (connectToWSN) {
				createSocket();
			}

		}
	}

	/**
	 * Load data set file.
	 * 
	 * @param dataSetFile is the dataSet path and fileName.
	 */
	public void loadDataSensor(String dataSetFile) {
		CommentManager commentManager = new CommentManager();

		if (dataSetFile.endsWith(".arff") || dataSetFile.endsWith(".csv") || dataSetFile.endsWith(".txt")) {

			if (dataSetFile.endsWith(".arff")) {
				// Case ARFF

				try {
					ArffFile fileArf = new ArffFile();
					fileArf = fileArf.load(dataSetFile);

					comment = fileArf.getComment();
					System.out.println("*** ARFF Comment: " + comment);

					// Set nodeID, sensorsList and functionsList
					commentManager.parseComment(comment, computeFeature);
					this.nodeID = commentManager.getNodeID();
					this.sensorsList = commentManager.getSensorsList();
					this.functionsList = commentManager.getFunctionsList();
					this.sensorSetUpInfo = commentManager.getSensorSetUpInfo();
					this.featureSetUpInfo = commentManager.getFeatureSetUpInfo();
					this.dsSensorTimeSampling = commentManager.getDsSensorTimeSampling();
					//
					attribute = fileArf.getAttributesList();
					Iterator itr = attribute.iterator();
					while (itr.hasNext()) {
						System.out.println("*** ARFF Attribute: " + (String) itr.next());
					}
					data = fileArf.getData();

					Thread thread = new LoadData(dataSetFile);
					thread.start();

				} catch (FileNotFoundException ex3) {
					System.err.println("File not found!");
				} catch (IOException ex2) {
					System.err.println("IO exception!");
				} catch (ArffFileParseException ex1) {
					System.err.println("Couldn't parse file!");
				}
			} else if (dataSetFile.endsWith(".csv") || dataSetFile.endsWith(".txt")) {
				// Case csv or txt
				try {
					TxtCsvFile fileTxtCsv = new TxtCsvFile();
					fileTxtCsv = fileTxtCsv.load(dataSetFile);

					comment = fileTxtCsv.getComment();
					System.out.println("*** TXT CSV Comment: " + comment);

					// Set nodeID, sensorsList and functionsList
					commentManager.parseComment(comment, computeFeature);
					this.nodeID = commentManager.getNodeID();
					this.sensorsList = commentManager.getSensorsList();
					this.functionsList = commentManager.getFunctionsList();
					this.sensorSetUpInfo = commentManager.getSensorSetUpInfo();
					this.featureSetUpInfo = commentManager.getFeatureSetUpInfo();
					this.dsSensorTimeSampling = commentManager.getDsSensorTimeSampling();
					//
					attribute = fileTxtCsv.getAttributesList();
					Iterator itr = attribute.iterator();
					while (itr.hasNext()) {
						System.out.println("*** TXT CSV Attribute: " + (String) itr.next());
					}
					data = fileTxtCsv.getData();

					Thread thread = new LoadData(dataSetFile);
					thread.start();

				} catch (FileNotFoundException ex3) {
					System.err.println("File not found!");
				} catch (IOException ex2) {
					System.err.println("IO exception!");
				} catch (TxtCsvFileParseException ex1) {
					System.err.println("Couldn't parse file!");
				}
			}
		}
		// return dataLoaded;
	}

	private void featureNodeDataFromFeatureDataVector(int sensorCodeKey) {

		ArrayList<Feature> featureData = new ArrayList<Feature>();

		Feature feature;
		Hashtable<Integer, ArrayList<Feature>> featureSensorData = new Hashtable<Integer, ArrayList<Feature>>();
		Vector sensorSetUpParam;
		Vector featureSetUpParam;

		int timeKey;
		int sampleTime;
		byte sampleTimeScale;
		int window;
		int shift;

		sensorSetUpParam = this.sensorSetUpInfo.get((int) sensorCodeKey);
		sampleTimeScale = (Byte) sensorSetUpParam.get(0);
		sampleTime = (Integer) sensorSetUpParam.get(1);
		featureSetUpParam = this.featureSetUpInfo.get((int) sensorCodeKey);
		window = (Integer) featureSetUpParam.get(0);
		shift = (Integer) featureSetUpParam.get(1);

		// Convert sampleTime in ms
		// MILLISEC = 0x01;
		// SEC = 0x02;
		// MIN = 0x03;

		if (sampleTimeScale == SPINESensorConstants.SEC) {
			sampleTime = sampleTime * 1000;
		} else if (sampleTimeScale == SPINESensorConstants.MIN) {
			sampleTime = sampleTime * 60000;
		}
		sampleTime = sampleTime * shift;

		int j = 0;
		for (int i = 0; i < featureDataVector.size(); i++) {
			featureData = (ArrayList<Feature>) featureDataVector.get(i).clone();
			feature = (Feature) featureData.get(0);
			if (feature.getSensorCode() == sensorCodeKey) {
				j = j + 1;

				// Add featureCalculateData to featureCalculateSensorData and
				// featureNodeData -- Key: time + sensorCode
				timeKey = sampleTime * j;

				featureSensorData = featureNodeData.get(timeKey);
				if (featureSensorData == null) {
					featureSensorData = new Hashtable<Integer, ArrayList<Feature>>();
				}
				featureSensorData.put(sensorCodeKey, (ArrayList<Feature>) featureData.clone());
				featureNodeData.put(timeKey, (Hashtable<Integer, ArrayList<Feature>>) featureSensorData.clone());

				featureSensorData.clear();

				featureData.clear();

			}

		}
	}

	private void featureNodeDataFromSensorXXDataStore(int sensorCodeKey, Hashtable<Integer, boolean[]> featReq) {

		ArrayList<Feature> featureCalculateData = new ArrayList<Feature>();

		Hashtable<Integer, ArrayList<Feature>> featureCalculateSensorData = new Hashtable<Integer, ArrayList<Feature>>();
		SpineSetupSensor setupSensor;
		FeatureSpineSetupFunction featureSetupFunct;
		// (key)
		// ch1,
		// ch2,
		// ch3,
		// ch4
		boolean[] chReq = new boolean[4];

		int timeKey;
		int sampleTime;
		int sampleTimeScale;
		int shift;

		setupSensor = (SpineSetupSensor) setupSensorInformation.get((int) sensorCodeKey);
		sampleTime = setupSensor.getSamplingTime();
		sampleTimeScale = setupSensor.getTimeScale();
		featureSetupFunct = (FeatureSpineSetupFunction) setupFeatureInformation.get((int) sensorCodeKey);
		shift = featureSetupFunct.getShiftSize();

		// Convert sampleTime in ms
		// MILLISEC = 0x01;
		// SEC = 0x02;
		// MIN = 0x03;

		if (sampleTimeScale == SPINESensorConstants.SEC) {
			sampleTime = sampleTime * 1000;
		} else if (sampleTimeScale == SPINESensorConstants.MIN) {
			sampleTime = sampleTime * 60000;
		}
		;
		sampleTime = sampleTime * shift;

		Feature featureCalculate;

		int numFeatureCalculate = 0;
		Vector[] sensorRawCalculateValue = new Vector[4];
		Vector ch1RawCalculateValue = new Vector();
		Vector ch2RawCalculateValue = new Vector();
		Vector ch3RawCalculateValue = new Vector();
		Vector ch4RawCalculateValue = new Vector();
		Vector[] sensorMaxValue = new Vector[4];
		Vector ch1MaxValue = new Vector();
		Vector ch2MaxValue = new Vector();
		Vector ch3MaxValue = new Vector();
		Vector ch4MaxValue = new Vector();
		Vector[] sensorMinValue = new Vector[4];
		Vector ch1MinValue = new Vector();
		Vector ch2MinValue = new Vector();
		Vector ch3MinValue = new Vector();
		Vector ch4MinValue = new Vector();
		Vector[] sensorRangeValue = new Vector[4];
		Vector ch1RangeValue = new Vector();
		Vector ch2RangeValue = new Vector();
		Vector ch3RangeValue = new Vector();
		Vector ch4RangeValue = new Vector();
		Vector[] sensorMeanValue = new Vector[4];
		Vector ch1MeanValue = new Vector();
		Vector ch2MeanValue = new Vector();
		Vector ch3MeanValue = new Vector();
		Vector ch4MeanValue = new Vector();
		Vector[] sensorAmplitudeValue = new Vector[4];
		Vector ch1AmplitudeValue = new Vector();
		Vector ch2AmplitudeValue = new Vector();
		Vector ch3AmplitudeValue = new Vector();
		Vector ch4AmplitudeValue = new Vector();
		Vector[] sensorRmsValue = new Vector[4];
		Vector ch1RmsValue = new Vector();
		Vector ch2RmsValue = new Vector();
		Vector ch3RmsValue = new Vector();
		Vector ch4RmsValue = new Vector();
		Vector[] sensorStDevValue = new Vector[4];
		Vector ch1StDevValue = new Vector();
		Vector ch2StDevValue = new Vector();
		Vector ch3StDevValue = new Vector();
		Vector ch4StDevValue = new Vector();
		Vector[] sensorTotEnergyValue = new Vector[4];
		Vector ch1TotEnergyValue = new Vector();
		Vector ch2TotEnergyValue = new Vector();
		Vector ch3TotEnergyValue = new Vector();
		Vector ch4TotEnergyValue = new Vector();
		Vector[] sensorVarianceValue = new Vector[4];
		Vector ch1VarianceValue = new Vector();
		Vector ch2VarianceValue = new Vector();
		Vector ch3VarianceValue = new Vector();
		Vector ch4VarianceValue = new Vector();
		Vector[] sensorModeValue = new Vector[4];
		Vector ch1ModeValue = new Vector();
		Vector ch2ModeValue = new Vector();
		Vector ch3ModeValue = new Vector();
		Vector ch4ModeValue = new Vector();
		Vector[] sensorMedianValue = new Vector[4];
		Vector ch1MedianValue = new Vector();
		Vector ch2MedianValue = new Vector();
		Vector ch3MedianValue = new Vector();
		Vector ch4MedianValue = new Vector();
		Vector sensorLabelCalculateValue = new Vector();

		Node dummyNode = new Node(new Address(Integer.toString(this.nodeID)));

		byte functionCode = SPINEFunctionConstants.FEATURE;
		byte sensorCode = (byte) sensorCodeKey;

		Enumeration e2 = featReq.keys();

		while (e2.hasMoreElements()) {
			int featureCodeKey = (Integer) e2.nextElement();

			chReq = featReq.get(featureCodeKey);

			switch (featureCodeKey) {
			case SPINEFunctionConstants.RAW_DATA: {
				sensorRawCalculateValue = sensorRawCalculateDataStore.get(sensorCodeKey);
				ch1RawCalculateValue = sensorRawCalculateValue[0];
				ch2RawCalculateValue = sensorRawCalculateValue[1];
				ch3RawCalculateValue = sensorRawCalculateValue[2];
				ch4RawCalculateValue = sensorRawCalculateValue[3];
				numFeatureCalculate = ch1RawCalculateValue.size();
				break;
			}
			case SPINEFunctionConstants.MAX: {
				sensorMaxValue = sensorMaxDataStore.get(sensorCodeKey);
				ch1MaxValue = sensorMaxValue[0];
				ch2MaxValue = sensorMaxValue[1];
				ch3MaxValue = sensorMaxValue[2];
				ch4MaxValue = sensorMaxValue[3];
				numFeatureCalculate = ch1MaxValue.size();
				break;
			}
			case SPINEFunctionConstants.MIN: {
				sensorMinValue = sensorMinDataStore.get(sensorCodeKey);
				ch1MinValue = sensorMinValue[0];
				ch2MinValue = sensorMinValue[1];
				ch3MinValue = sensorMinValue[2];
				ch4MinValue = sensorMinValue[3];
				numFeatureCalculate = ch1MinValue.size();
				break;
			}
			case SPINEFunctionConstants.RANGE: {
				sensorRangeValue = sensorRangeDataStore.get(sensorCodeKey);
				ch1RangeValue = sensorRangeValue[0];
				ch2RangeValue = sensorRangeValue[1];
				ch3RangeValue = sensorRangeValue[2];
				ch4RangeValue = sensorRangeValue[3];
				numFeatureCalculate = ch1RangeValue.size();
				break;
			}
			case SPINEFunctionConstants.MEAN: {
				sensorMeanValue = sensorMeanDataStore.get(sensorCodeKey);
				ch1MeanValue = sensorMeanValue[0];
				ch2MeanValue = sensorMeanValue[1];
				ch3MeanValue = sensorMeanValue[2];
				ch4MeanValue = sensorMeanValue[3];
				numFeatureCalculate = ch1MeanValue.size();
				break;
			}
			case SPINEFunctionConstants.AMPLITUDE: {
				sensorAmplitudeValue = sensorAmplitudeDataStore.get(sensorCodeKey);
				ch1AmplitudeValue = sensorAmplitudeValue[0];
				ch2AmplitudeValue = sensorAmplitudeValue[1];
				ch3AmplitudeValue = sensorAmplitudeValue[2];
				ch4AmplitudeValue = sensorAmplitudeValue[3];
				numFeatureCalculate = ch1AmplitudeValue.size();
				break;
			}
			case SPINEFunctionConstants.RMS: {
				sensorRmsValue = sensorRmsDataStore.get(sensorCodeKey);
				ch1RmsValue = sensorRmsValue[0];
				ch2RmsValue = sensorRmsValue[1];
				ch3RmsValue = sensorRmsValue[2];
				ch4RmsValue = sensorRmsValue[3];
				numFeatureCalculate = ch1RmsValue.size();
				break;
			}
			case SPINEFunctionConstants.ST_DEV: {
				sensorStDevValue = sensorStDevDataStore.get(sensorCodeKey);
				ch1StDevValue = sensorStDevValue[0];
				ch2StDevValue = sensorStDevValue[1];
				ch3StDevValue = sensorStDevValue[2];
				ch4StDevValue = sensorStDevValue[3];
				numFeatureCalculate = ch1StDevValue.size();
				break;
			}
			case SPINEFunctionConstants.TOTAL_ENERGY: {
				sensorTotEnergyValue = sensorTotEnergyDataStore.get(sensorCodeKey);
				ch1TotEnergyValue = sensorTotEnergyValue[0];
				ch2TotEnergyValue = sensorTotEnergyValue[1];
				ch3TotEnergyValue = sensorTotEnergyValue[2];
				ch4TotEnergyValue = sensorTotEnergyValue[3];
				numFeatureCalculate = ch1TotEnergyValue.size();
				break;
			}
			case SPINEFunctionConstants.VARIANCE: {
				sensorVarianceValue = sensorVarianceDataStore.get(sensorCodeKey);
				ch1VarianceValue = sensorVarianceValue[0];
				ch2VarianceValue = sensorVarianceValue[1];
				ch3VarianceValue = sensorVarianceValue[2];
				ch4VarianceValue = sensorVarianceValue[3];
				numFeatureCalculate = ch1VarianceValue.size();
				break;
			}
			case SPINEFunctionConstants.MODE: {
				sensorModeValue = sensorModeDataStore.get(sensorCodeKey);
				ch1ModeValue = sensorModeValue[0];
				ch2ModeValue = sensorModeValue[1];
				ch3ModeValue = sensorModeValue[2];
				ch4ModeValue = sensorModeValue[3];
				numFeatureCalculate = ch1ModeValue.size();
				break;
			}
			case SPINEFunctionConstants.MEDIAN: {
				sensorMedianValue = sensorMedianDataStore.get(sensorCodeKey);
				ch1MedianValue = sensorMedianValue[0];
				ch2MedianValue = sensorMedianValue[1];
				ch3MedianValue = sensorMedianValue[2];
				ch4MedianValue = sensorMedianValue[3];
				numFeatureCalculate = ch1MedianValue.size();
				break;
			}

			}

		}

		sensorLabelCalculateValue = sensorLabelCalculateDataStore.get(sensorCodeKey);

		Enumeration e3;

		for (int i = 0; i < numFeatureCalculate; i++) {

			e3 = featReq.keys();
			while (e3.hasMoreElements()) {
				int featureCodeKey = (Integer) e3.nextElement();

				chReq = featReq.get(featureCodeKey);

				featureCalculate = new Feature();
				featureCalculate.setNode(dummyNode);
				featureCalculate.setFunctionCode(functionCode);
				featureCalculate.setFeatureCode((byte) featureCodeKey);
				featureCalculate.setSensorCode(sensorCode);
				featureCalculate.setChannelBitmask(SPINESensorConstants.getValueTypesCodeByBitmask(chReq[0], chReq[1], chReq[2], chReq[3]));

				switch (featureCodeKey) {
				case SPINEFunctionConstants.RAW_DATA: {
					if (chReq[0]) {
						featureCalculate.setCh1Value((Integer) ch1RawCalculateValue.get(i));
					}
					;
					if (chReq[1]) {
						featureCalculate.setCh2Value((Integer) ch2RawCalculateValue.get(i));
					}
					;
					if (chReq[2]) {
						featureCalculate.setCh3Value((Integer) ch3RawCalculateValue.get(i));
					}
					;
					if (chReq[3]) {
						featureCalculate.setCh4Value((Integer) ch4RawCalculateValue.get(i));
					}
					;

					break;
				}
				case SPINEFunctionConstants.MAX: {
					if (chReq[0]) {
						featureCalculate.setCh1Value((Integer) ch1MaxValue.get(i));
					}
					;
					if (chReq[1]) {
						featureCalculate.setCh2Value((Integer) ch2MaxValue.get(i));
					}
					;
					if (chReq[2]) {
						featureCalculate.setCh3Value((Integer) ch3MaxValue.get(i));
					}
					;
					if (chReq[3]) {
						featureCalculate.setCh4Value((Integer) ch4MaxValue.get(i));
					}
					;

					break;
				}
				case SPINEFunctionConstants.MIN: {
					if (chReq[0]) {
						featureCalculate.setCh1Value((Integer) ch1MinValue.get(i));
					}
					;
					if (chReq[1]) {
						featureCalculate.setCh2Value((Integer) ch2MinValue.get(i));
					}
					;
					if (chReq[2]) {
						featureCalculate.setCh3Value((Integer) ch3MinValue.get(i));
					}
					;
					if (chReq[3]) {
						featureCalculate.setCh4Value((Integer) ch4MinValue.get(i));
					}
					;
					break;
				}
				case SPINEFunctionConstants.RANGE: {
					if (chReq[0]) {
						featureCalculate.setCh1Value((Integer) ch1RangeValue.get(i));
					}
					;
					if (chReq[1]) {
						featureCalculate.setCh2Value((Integer) ch2RangeValue.get(i));
					}
					;
					if (chReq[2]) {
						featureCalculate.setCh3Value((Integer) ch3RangeValue.get(i));
					}
					;
					if (chReq[3]) {
						featureCalculate.setCh4Value((Integer) ch4RangeValue.get(i));
					}
					;
					break;
				}
				case SPINEFunctionConstants.MEAN: {
					if (chReq[0]) {
						featureCalculate.setCh1Value((Integer) ch1MeanValue.get(i));
					}
					;
					if (chReq[1]) {
						featureCalculate.setCh2Value((Integer) ch2MeanValue.get(i));
					}
					;
					if (chReq[2]) {
						featureCalculate.setCh3Value((Integer) ch3MeanValue.get(i));
					}
					;
					if (chReq[3]) {
						featureCalculate.setCh4Value((Integer) ch4MeanValue.get(i));
					}
					;

					break;
				}
				case SPINEFunctionConstants.AMPLITUDE: {
					if (chReq[0]) {
						featureCalculate.setCh1Value((Integer) ch1AmplitudeValue.get(i));
					}
					;
					if (chReq[1]) {
						featureCalculate.setCh2Value((Integer) ch2AmplitudeValue.get(i));
					}
					;
					if (chReq[2]) {
						featureCalculate.setCh3Value((Integer) ch3AmplitudeValue.get(i));
					}
					;
					if (chReq[3]) {
						featureCalculate.setCh4Value((Integer) ch4AmplitudeValue.get(i));
					}
					;

					break;
				}
				case SPINEFunctionConstants.RMS: {
					if (chReq[0]) {
						featureCalculate.setCh1Value((Integer) ch1RmsValue.get(i));
					}
					;
					if (chReq[1]) {
						featureCalculate.setCh2Value((Integer) ch2RmsValue.get(i));
					}
					;
					if (chReq[2]) {
						featureCalculate.setCh3Value((Integer) ch3RmsValue.get(i));
					}
					;
					if (chReq[3]) {
						featureCalculate.setCh4Value((Integer) ch4RmsValue.get(i));
					}
					;
					break;
				}
				case SPINEFunctionConstants.ST_DEV: {
					if (chReq[0]) {
						featureCalculate.setCh1Value((Integer) ch1StDevValue.get(i));
					}
					;
					if (chReq[1]) {
						featureCalculate.setCh2Value((Integer) ch2StDevValue.get(i));
					}
					;
					if (chReq[2]) {
						featureCalculate.setCh3Value((Integer) ch3StDevValue.get(i));
					}
					;
					if (chReq[3]) {
						featureCalculate.setCh4Value((Integer) ch4StDevValue.get(i));
					}
					;

					break;
				}
				case SPINEFunctionConstants.TOTAL_ENERGY: {
					if (chReq[0]) {
						featureCalculate.setCh1Value((Integer) ch1TotEnergyValue.get(i));
					}
					;
					if (chReq[1]) {
						featureCalculate.setCh2Value((Integer) ch2TotEnergyValue.get(i));
					}
					;
					if (chReq[2]) {
						featureCalculate.setCh3Value((Integer) ch3TotEnergyValue.get(i));
					}
					;
					if (chReq[3]) {
						featureCalculate.setCh4Value((Integer) ch4TotEnergyValue.get(i));
					}
					;
					break;
				}
				case SPINEFunctionConstants.VARIANCE: {
					if (chReq[0]) {
						featureCalculate.setCh1Value((Integer) ch1VarianceValue.get(i));
					}
					;
					if (chReq[1]) {
						featureCalculate.setCh2Value((Integer) ch2VarianceValue.get(i));
					}
					;
					if (chReq[2]) {
						featureCalculate.setCh3Value((Integer) ch3VarianceValue.get(i));
					}
					;
					if (chReq[3]) {
						featureCalculate.setCh4Value((Integer) ch4VarianceValue.get(i));
					}
					;
					break;
				}
				case SPINEFunctionConstants.MODE: {
					if (chReq[0]) {
						featureCalculate.setCh1Value((Integer) ch1ModeValue.get(i));
					}
					;
					if (chReq[1]) {
						featureCalculate.setCh2Value((Integer) ch2ModeValue.get(i));
					}
					;
					if (chReq[2]) {
						featureCalculate.setCh3Value((Integer) ch3ModeValue.get(i));
					}
					;
					if (chReq[3]) {
						featureCalculate.setCh4Value((Integer) ch4ModeValue.get(i));
					}
					;

					break;
				}
				case SPINEFunctionConstants.MEDIAN: {
					if (chReq[0]) {
						featureCalculate.setCh1Value((Integer) ch1MedianValue.get(i));
					}
					;
					if (chReq[1]) {
						featureCalculate.setCh2Value((Integer) ch2MedianValue.get(i));
					}
					;
					if (chReq[2]) {
						featureCalculate.setCh3Value((Integer) ch3MedianValue.get(i));
					}
					;
					if (chReq[3]) {
						featureCalculate.setCh4Value((Integer) ch4MedianValue.get(i));
					}
					;

					break;
				}

				}

				// Set featureLabel in featureCalculate
				featureCalculate.setFeatureLabel((String) sensorLabelCalculateValue.get(i));

				featureCalculateData.add((Feature) featureCalculate.clone());

			}

			// Add featureCalculateData to featureCalculateSensorData and
			// featureNodeData -- Key: time + sensorCode
			timeKey = sampleTime * (i + 1);

			featureCalculateSensorData = featureNodeData.get(timeKey);
			if (featureCalculateSensorData == null) {
				featureCalculateSensorData = new Hashtable<Integer, ArrayList<Feature>>();
			}
			featureCalculateSensorData.put(sensorCodeKey, (ArrayList<Feature>) featureCalculateData.clone());
			featureNodeData.put(timeKey, (Hashtable<Integer, ArrayList<Feature>>) featureCalculateSensorData.clone());

			featureCalculateSensorData.clear();

			featureCalculateData.clear();

		}

	}

	private void setFeatureDataVectorFromData(List<Object[]> data) {

		String featureLoaded = "";

		byte sensorCode;
		byte featureCode;
		byte FUNCTIONCODE = 0x01;
		byte featureCodeOld = -1;
		int chNum;
		int featureValue;
		int featureId = 0;
		int featureDataId = 0;
		int featureIdOld = -1;
		int featureDataIdOld = -1;

		boolean[] channelBitmask = new boolean[4];
		boolean hasCh1;
		boolean hasCh2;
		boolean hasCh3;
		boolean hasCh4;

		String featureLabel = "";

		// Initialization channelBitmask
		for (int k = 0; k < 4; k++) {
			channelBitmask[k] = false;
		}

		feature = new Feature();
		Node dummyNode = new Node(new Address("-1"));
		feature.setNode(dummyNode);
		Iterator itr = data.iterator();
		while (itr.hasNext()) {

			datum = (Object[]) itr.next();
			System.out.println(datum[0] + "," + Math.round((Double) datum[1]) + "," + Math.round((Double) datum[2]) + "," + datum[3] + "," + Math.round((Double) datum[4]));

			featureLabel = (String) datum[0];
			// featureDataId
			featureDataId = (int) (Math.round((Double) datum[1]));
			if ((featureDataId != featureDataIdOld) && (featureDataIdOld != -1)) {
				// Set feature.channelBitmask
				hasCh1 = channelBitmask[0];
				hasCh2 = channelBitmask[1];
				hasCh3 = channelBitmask[2];
				hasCh4 = channelBitmask[3];
				feature.setChannelBitmask(SPINESensorConstants.getValueTypesCodeByBitmask(hasCh1, hasCh2, hasCh3, hasCh4));
				// clear channelBitmask
				for (int k = 0; k < 4; k++) {
					channelBitmask[k] = false;
				}

				System.out.println("Set in featureData: " + feature.toString());

				featureData.add((Feature) feature.clone());

				featureLoaded = feature.toString();
				// Notify in log window
				this.setChanged();
				this.notifyObservers(featureLoaded);

				featureIdOld = featureId;
				feature = new Feature();
				feature.setNode(dummyNode);
				featureDataVector.add((ArrayList<Feature>) featureData.clone());
				featureData.clear();
			}
			// featureId
			featureId = (int) (Math.round((Double) datum[2]));
			if ((featureId != featureIdOld) && (featureIdOld != -1) && (feature.getNode().getPhysicalID().getAsInt() != -1)) {
				// Set feature.channelBitmask
				hasCh1 = channelBitmask[0];
				hasCh2 = channelBitmask[1];
				hasCh3 = channelBitmask[2];
				hasCh4 = channelBitmask[3];
				feature.setChannelBitmask(SPINESensorConstants.getValueTypesCodeByBitmask(hasCh1, hasCh2, hasCh3, hasCh4));
				// clear channelBitmask
				for (int k = 0; k < 4; k++) {
					channelBitmask[k] = false;
				}

				System.out.println("Set in featureData: " + feature.toString());

				featureData.add((Feature) feature.clone());

				featureLoaded = feature.toString();
				// Notify in log window
				this.setChanged();
				this.notifyObservers(featureLoaded);

				//
				feature = new Feature();
				feature.setNode(dummyNode);
			}
			// featureCode
			featureCode = Byte.parseByte(((String) datum[3]).substring(2, 3));
			if (feature.getNode().getPhysicalID().getAsInt() == -1) {
				// FunctionCode = 0x1 DEFAULT
				feature.setFunctionCode(FUNCTIONCODE);
				feature.setFeatureCode(featureCode);

				feature.setFeatureLabel(featureLabel);
				feature.setNode(new Node(new Address("" + this.nodeID)));
				sensorCode = Byte.parseByte(((String) datum[3]).substring(0, 1));
				feature.setSensorCode(sensorCode);
				// functionCode = FEATURE
				featureCodeOld = featureCode;
			}
			// featureValue
			featureValue = (int) Math.round((Double) datum[4]);
			chNum = (Integer.parseInt(((String) datum[3]).substring(4, 5)));
			// Set channelBitmask
			channelBitmask[chNum] = true;
			if (chNum == 0) {
				feature.setCh1Value(featureValue);
			} else {
				if (chNum == 1) {
					feature.setCh2Value(featureValue);
				} else {
					if (chNum == 2) {
						feature.setCh3Value(featureValue);
					} else if (chNum == 3) {
						feature.setCh4Value(featureValue);
					}
				}
			}
			featureIdOld = featureId;
			featureDataIdOld = featureDataId;
		}
		// Set feature.channelBitmask
		hasCh1 = channelBitmask[0];
		hasCh2 = channelBitmask[1];
		hasCh3 = channelBitmask[2];
		hasCh4 = channelBitmask[3];
		feature.setChannelBitmask(SPINESensorConstants.getValueTypesCodeByBitmask(hasCh1, hasCh2, hasCh3, hasCh4));
		// clear channelBitmask
		for (int k = 0; k < 4; k++) {
			channelBitmask[k] = false;
		}

		System.out.println("Set in featureData: " + feature.toString());

		featureData.add((Feature) feature.clone());

		featureLoaded = feature.toString();
		// Notify in log window
		this.setChanged();
		this.notifyObservers(featureLoaded);

		featureDataVector.add((ArrayList<Feature>) featureData.clone());

		// return dataLoaded;

	}

	private void setSensorFeatRawDataStoreFromSensorAllFeatRawDataStore(int sensorCode, Hashtable<Integer, Vector> sensorAllFeatRawDataStore, int sampleRate) {

		Vector<Feature> featureSensorRawDataVector;
		Vector<Feature> newFeatureSensorRawDataVector = new Vector<Feature>();

		featureSensorRawDataVector = sensorAllFeatRawDataStore.get(sensorCode);
		int index;

		for (int i = 0; (i + 1) * sampleRate < featureSensorRawDataVector.size(); i++) {
			index = i * sampleRate;

			newFeatureSensorRawDataVector.add((Feature) featureSensorRawDataVector.get(index).clone());

			sensorFeatRawDataStore.put(sensorCode, (Vector) newFeatureSensorRawDataVector.clone());
		}

	}

	private void setSensorAllFeatRawDataStoreFromFeatureDataVector(Vector<ArrayList<Feature>> featureDataVector) {

		int sensorCode;
		Vector<Feature> featureSensorRawDataVector;

		Feature featureRawData = new Feature();

		// sensorFeatRawDataStore

		int i;
		for (i = 0; i < featureDataVector.size(); i++) {
			featureData = featureDataVector.get(i);
			feature = (Feature) featureData.get(0);
			sensorCode = feature.getSensorCode();
			for (int t = 0; t < featureData.size(); t++) {
				feature = (Feature) featureData.get(t);
				if (feature.getFeatureCode() == SPINEFunctionConstants.RAW_DATA) {
					featureRawData = feature;
				}

			}

			featureSensorRawDataVector = sensorAllFeatRawDataStore.get(sensorCode);
			if (featureSensorRawDataVector == null) {
				featureSensorRawDataVector = new Vector<Feature>();
			}
			featureSensorRawDataVector.add(feature);
			sensorAllFeatRawDataStore.put(sensorCode, (Vector) featureSensorRawDataVector.clone());
		}

	}

	private void setfunctionReqStore(FeatureSpineFunctionReq featureFunctReq) {

		int sensorCode;
		Hashtable<Integer, boolean[]> featReq;
		Vector<Feature> features;
		Feature feature;
		boolean[] chReq = new boolean[4];
		byte channelBitmask;
		byte chReqBitmask;
		int numChFalse;

		sensorCode = featureFunctReq.getSensor();

		featReq = functionReqStore.get(sensorCode);

		if (featReq == null) {
			featReq = new Hashtable<Integer, boolean[]>();
		}

		features = featureFunctReq.getFeatures();

		// CASE Activation
		if (featureFunctReq.getActivationFlag() == true) {

			for (int j = 0; j < features.size(); j++) {
				feature = features.get(j);

				try {

					chReq = featReq.get((int) feature.getFeatureCode());

					if (chReq == null) {

						chReq = new boolean[4];

						for (int k = 0; k < 4; k++) {
							chReq[k] = false;
						}

					}

				} catch (Exception e) {
					e.printStackTrace();

				}

				channelBitmask = feature.getChannelBitmask();

				for (int h = 0; h < 4; h++) {
					if (SPINESensorConstants.chPresent(h, channelBitmask)) {
						chReq[h] = true;
					}
				}

				featReq.put((int) feature.getFeatureCode(), chReq.clone());

			}

		} else {
			// CASE Deactivation

			for (int j = 0; j < features.size(); j++) {
				numChFalse = 0;
				feature = features.get(j);

				try {

					chReq = featReq.get((int) feature.getFeatureCode());

					if (chReq == null) {
						// Case Deactivate without activate
					} else {
						channelBitmask = feature.getChannelBitmask();

						chReqBitmask = SPINESensorConstants.getValueTypesCodeByBitmask(chReq[0], chReq[1], chReq[2], chReq[3]);

						chReqBitmask = (byte) (channelBitmask & chReqBitmask);

						for (int h = 0; h < 4; h++) {
							chReq[h] = false;
							if (SPINESensorConstants.chPresent(h, chReqBitmask)) {
								chReq[h] = true;
							}
						}

						for (int i = 0; i < 4; i++) {
							if (chReq[i] == false) {
								numChFalse++;
							}
						}

						if (numChFalse != 4) {

							featReq.put((int) feature.getFeatureCode(), chReq.clone());
						} else {
							featReq.remove((int) feature.getFeatureCode());
						}

					}

				} catch (Exception e) {
					e.printStackTrace();

				}

			}

		}

		functionReqStore.put(sensorCode, (Hashtable<Integer, boolean[]>) featReq.clone());

	}

	private void setSensorRawLabelDataStoreFromFeatureSensorRawDataVector(int sensorCodekey, Vector<Feature> featureSensorRawDataVector) {

		Vector<String> sensorLabelValue = new Vector<String>();

		Vector[] sensorRawValue;

		Vector<Integer> ch1RawValue = new Vector<Integer>();

		Vector<Integer> ch2RawValue = new Vector<Integer>();

		Vector<Integer> ch3RawValue = new Vector<Integer>();

		Vector<Integer> ch4RawValue = new Vector<Integer>();

		int ch1Value;
		int ch2Value;
		int ch3Value;
		int ch4Value;

		sensorRawValue = sensorRawDataStore.get(sensorCodekey);
		if (sensorRawValue == null) {
			sensorRawValue = new Vector[4];
		}

		for (int i = 0; i < featureSensorRawDataVector.size(); i++) {

			feature = (Feature) featureSensorRawDataVector.get(i);

			ch1Value = feature.getCh1Value();
			ch2Value = feature.getCh2Value();
			ch3Value = feature.getCh3Value();
			ch4Value = feature.getCh4Value();

			ch1RawValue.add(ch1Value);

			ch2RawValue.add(ch2Value);

			ch3RawValue.add(ch3Value);

			ch4RawValue.add(ch4Value);

			sensorLabelValue.add(feature.getFeatureLabel());

		}

		sensorRawValue[0] = ch1RawValue;
		sensorRawValue[1] = ch2RawValue;
		sensorRawValue[2] = ch3RawValue;
		sensorRawValue[3] = ch4RawValue;
		sensorRawDataStore.put(sensorCodekey, sensorRawValue);
		sensorLabelDataStore.put(sensorCodekey, sensorLabelValue);

	}

	// ******************************
	// Client Socket
	// ******************************

	/**
	 * Create client socket connection (Cmd Connect to WSN).
	 * 
	 * @return connectResult: "Connection successful" or "Unknown host" or "No
	 *         I/O".
	 */
	public String createSocket() {
		String connectResult = "";

		// Create client socket connection
		try {
			Socket socket = new Socket("localhost", nodeCoordinatorPort);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());

			connectResult = "Connection successful";
			System.out.println("Connection successful");

		} catch (UnknownHostException e) {
			System.err.println("Unknown host");
			connectResult = "Unknown host";
		} catch (IOException e) {
			System.err.println("No I/O");
			connectResult = "No I/O";
		}

		if (!(connectResult.equals("Unknown host")) && !(connectResult.equals("No I/O"))) {
			// Create ServerSocket Node
			SocketServer nodeSocketServer = new SocketServer();
			nodeSocketServer.registerListener(this);
			Thread t = new Thread(nodeSocketServer);
			t.start();

		}

		setChanged();
		notifyObservers(connectResult);

		return connectResult;
	}

	// Send EMUMessage to SocketThrdServer
	private String sendData(EMUMessage msg) {
		String sendResult = "Send...";
		try {
			// object stream caches the first version of each object that it
			// writes and just reuses it unless you reset the stream
			oos.reset();
			oos.writeObject(msg);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		sendResult = sendResult + " " + msg;
		return sendResult;
	}

	private void actionResultNotification(String result) {
		this.setChanged();
		this.notifyObservers(result);
	}

	/**
	 * StartWSN command.
	 */
	public void start() {
		actionNode = new actionNodeThread();
		actionNode.start();
		this.setChanged();
		this.notifyObservers("Start Node");
	}

	class actionNodeThread extends Thread {
		public void run() {

			int sleepTime;
			int indexTime;
			ArrayList<Feature> featureData = new ArrayList<Feature>();
			Hashtable<Integer, ArrayList<Feature>> featureSensorData = new Hashtable<Integer, ArrayList<Feature>>();

			String sendResult = "";

			EMUMessage msg = new EMUMessage();
			msg.setProfileId((short) 0);
			msg.setSourceURL(URL_PREFIX + getNodeID());
			msg.setClusterId(SPINEPacketsConstants.DATA); // DATA

			if (featureNodeData.size() == 0) {
				sendResult = "[Warning] There are no features to send";
				actionResultNotification(sendResult);
			} else {

				TreeSet sortTimeKey = new TreeSet(featureNodeData.keySet());
				Iterator tsIter = sortTimeKey.iterator();
				sleepTime = 0;
				while (tsIter.hasNext()) {
					indexTime = (Integer) tsIter.next();
					featureSensorData = featureNodeData.get(indexTime);
					try {
						Thread.sleep(indexTime - sleepTime);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					sleepTime = indexTime;

					Enumeration e = featureSensorData.keys();

					while (e.hasMoreElements()) {
						int sensorCodeKey = (Integer) e.nextElement();
						featureData = featureSensorData.get(sensorCodeKey);
						payloadData = payloadManager.createDataPayload(featureData);
						short[] payloadShort = new short[payloadData.length];
						for (int h = 0; h < payloadShort.length; h++)
							payloadShort[h] = payloadData[h];
						msg.setPayload(payloadShort);

						// Send EMUMessage to SocketThrdServer
						sendResult = sendData(msg);

						actionResultNotification(sendResult);

					}
				}

			}

		}
	}

}
