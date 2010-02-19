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

import java.io.FileWriter;
import java.io.PrintWriter;
//import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Observable;
import java.util.StringTokenizer;
import java.util.Vector;

//import logic.PropertiesController;

//import apps.Util.NetworkParameters;
//import apps.Util.ParserXml;

import spine.SPINEFactory;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.datamodel.Data;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;
import spine.datamodel.functions.SpineFunctionReq;
import spine.datamodel.functions.SpineSetupFunction;
import spine.datamodel.functions.SpineSetupSensor;

/**
 * Manager: implements Command and SPINEListener.
 * 
 * @author Luigi Buondonno : luigi.buondonno@gmail.com
 * @author Antonio Giordano : antoniogior@hotmail.com
 * 
 * @version 1.0
 */
public class Manager extends Observable implements Command, SPINEListener {

	private static Manager instance = null;

	private DataStorage ds;

	private SPINEManager spineMan;

	private Manager() {

		ds = DataStorage.getInstance();
		try {

			spineMan = SPINEFactory
					.createSPINEManager(PropertiesController.PROPERTIES_FILE_PATH);

			spineMan.addListener(this);
		} catch (Exception ec) {

		}

	}

	/**
	 * Get Manager instance.
	 */
	public static Manager getInstance() {
		if (instance == null) {
			instance = new Manager();
		}
		return instance;
	}

	/**
	 * Handler of received event.
	 */
	public void received(Data data) {
		this.setChanged();
		this.notifyObservers(data);
		ds.dataIn(data.getNode().getPhysicalID().getAsInt(), data);
		return;
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

	public void storeDatainCsv(String path) {
		ds.commitInCsvFile(path);
	}

	/**
	 * Handler of received event.
	 */
	public void received(ServiceMessage msg) {
		this.setChanged();
		this.notifyObservers(msg);
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
	 * Setup Sensor: SPINE MANAGER setup sensor.
	 */
	public void setupSensor(Node node, SpineSetupSensor setupSensor) {
		spineMan.setup(node, setupSensor);
	}

	/**
	 * Setup Function: SPINE MANAGER setup function.
	 */
	public void setupFunction(Node node, SpineSetupFunction setupFunction) {
		spineMan.setup(node, setupFunction);
	}

	/**
	 * Activate Function: SPINE MANAGER activate function.
	 */
	public void actFunction(Node node, SpineFunctionReq functionReq) {
		functionReq.setActivationFlag(true);
		spineMan.activate(node, functionReq);
	}

	/**
	 * Decctivate Function: SPINE MANAGER deactivate function.
	 */
	public void deactivateFunction(Node node, SpineFunctionReq functionReq) {
		functionReq.setActivationFlag(false);
		spineMan.deactivate(node, functionReq);
	}


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
	}

	public void notifyResultAlg(String res) {
		this.setChanged();
		this.notifyObservers(res);
	}

	public void storeDatainSTF(String path, String info) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(path));
			StringTokenizer st = new StringTokenizer(info, "\n");
			while (st.hasMoreTokens())
				pw.println(st.nextToken());
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
