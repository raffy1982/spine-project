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

package logic;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Vector;
import logic.PropertiesController;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.SPINEFactory;
import spine.datamodel.Data;
import spine.datamodel.FeatureData;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;
import spine.datamodel.functions.FeatureSpineFunctionReq;
import spine.datamodel.functions.SpineFunctionReq;
import spine.datamodel.functions.SpineSetupFunction;
import spine.datamodel.functions.FeatureSpineSetupFunction;
import spine.datamodel.functions.SpineSetupSensor;

/**
 * Manager: implements Command and SPINEListener.
 * 
 * @author Luigi Buondonno : luigi.buondonno@gmail.com
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */

public class Manager extends Observable implements Command, SPINEListener {

	private static Manager instance = null;

	private DataManager ds;

	private SPINEManager spineMan;

	private String dataSetFile = "";

	private String featureLabel;

	private boolean pause;

	private boolean reset = false;

	PropertiesController propertControll;

	private Manager() {

		ds = DataManager.getInstance();
		try {
			// Read COM_PORT, BS_SPEED from and DATASET_PATH app.properties
			propertControll = PropertiesController.getInstance();
			try {
				propertControll.load();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			spineMan = SPINEFactory
					.createSPINEManager(PropertiesController.PROPERTIES_FILE_PATH);

			spineMan.addListener(this);
		} catch (Exception ec) {
			ec.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Get DataManager instance.
	 */
	public static Manager getInstance() {
		if (instance == null)
			instance = new Manager();
		return instance;
	}

	/**
	 * Handler of received event.
	 */
	public void received(Data data) {
		if (!pause && !reset) {
			this.setChanged();
			this.notifyObservers(data);
			if (data instanceof FeatureData) {
				System.out.println("CLASS_LABEL: " + featureLabel);
				ds.addData(data.getNode().getPhysicalID().getAsInt(),
						((FeatureData) data).getFeatures(), featureLabel);
			}
		}

	}

	/**
	 * Handler of discovery completed event.
	 */
	public void discoveryCompleted(Vector activeNodes) {
		LinkedList<Node> nod = new LinkedList<Node>();
		for (int i = 0; i < activeNodes.size(); i++) {
			nod.addLast((Node) activeNodes.get(i));
		}
		ds.setNode(nod);
		this.setChanged();
		this.notifyObservers(nod);
	}

	/**
	 * Handler of new node discovered event.
	 */
	public void newNodeDiscovered(Node newNode) {

	}

	/**
	 * Handler of received event.
	 */
	public void received(ServiceMessage msg) {
		System.out.println(msg);
	}

	/**
	 * Handler of serviceMessageReceived event.
	 */

	public void serviceMessageReceived(int nodeID, ServiceMessage msg) {
	}

	/**
	 * Handler of dataReceived event.
	 */
	public void dataReceived(int nodeID, Data data) {
	}

	/**
	 * Get Node from Id.
	 */
	public Node getNodeFromId(int idn) {
		return ds.getNodeFromId(idn);

	}

	/**
	 * Setup Sensor: SPINE MANAGER setup and store sensor setting.
	 */
	public void setupSensor(Node node, SpineSetupSensor setupSensor) {
		spineMan.setup(node, setupSensor);
		ds.addSensorSetting(node.getPhysicalID().getAsInt(), setupSensor);
	}

	/**
	 * Setup Function: SPINE MANAGER setup and store function setting.
	 */
	public void setupFunction(Node node, SpineSetupFunction setupFunction) {
		spineMan.setup(node, setupFunction);
		ds.addFunctionSetting(node.getPhysicalID().getAsInt(),
				(FeatureSpineSetupFunction) setupFunction);
	}

	/**
	 * Activate Function: SPINE MANAGER activate and store feature setting.
	 */
	public void activateFunction(Node node, SpineFunctionReq functionReq) {
		functionReq.setActivationFlag(true);
		spineMan.activate(node, functionReq);
		ds.addFeatureSetting(node.getPhysicalID().getAsInt(),
				(FeatureSpineFunctionReq) functionReq);
	}

	/*
	 * public void deactivateFunction(Node node, SpineFunctionReq functionReq) {
	 * functionReq.setActivationFlag(false); spineMan.deactivate(node,
	 * functionReq); }
	 */

	/**
	 * Discovery WSN: SPINE MANAGER discoveryWsn.
	 */
	public void discoveryWsn() {
		spineMan.discoveryWsn();
	}

	/**
	 * Start WSN: SPINE MANAGER startWsn.
	 */
	public void startWsn() {
		spineMan.startWsn(true);
	}

	/**
	 * Reset WSN: SPINE MANAGER resetWsn.
	 */
	public void resetWsn() {
		spineMan.resetWsn();
		this.reset = true;
		dataSetFile = propertControll.getProperty("DATASET_PATH");
		this.setChanged();
		this.notifyObservers("StartSave");
		ds.saveDataSet(dataSetFile);
		this.setChanged();
		this.notifyObservers("EndSave");
	}

	/**
	 * Set Feature Label.
	 */
	public void setFeatureLabel(String classLabel) {
		this.featureLabel = classLabel;
	}

	/**
	 * Pause: discard data.
	 */
	public void pause(boolean isPause) {
		this.pause = isPause;
	}
}
