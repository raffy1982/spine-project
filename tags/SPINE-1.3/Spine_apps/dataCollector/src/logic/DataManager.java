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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.JOptionPane;
import dataSetIO.ArffFile;
import spine.SPINESensorConstants;
import spine.datamodel.Feature;
import spine.datamodel.Node;
import spine.datamodel.functions.SpineSetupSensor;
import spine.datamodel.functions.FeatureSpineSetupFunction;
import spine.datamodel.functions.FeatureSpineFunctionReq;
import logic.PropertiesController;

/**
 * Data Manager: store Feature, Function and Sensor setting.
 * 
 * @author Luigi Buondonno : luigi.buondonno@gmail.com
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */

public class DataManager {

	private PropertiesController propertControll;

	private LinkedList<Node> node;

	private Hashtable<Integer, Vector> data = new Hashtable<Integer, Vector>();

	private Hashtable<Integer, Vector<SpineSetupSensor>> sensorSetting = new Hashtable<Integer, Vector<SpineSetupSensor>>();

	private Hashtable<Integer, Vector<FeatureSpineSetupFunction>> functionSetting = new Hashtable<Integer, Vector<FeatureSpineSetupFunction>>();

	private Hashtable<Integer, Vector<FeatureSpineFunctionReq>> featureSetting = new Hashtable<Integer, Vector<FeatureSpineFunctionReq>>();

	private static DataManager instance = null;

	final static String STARTLINECHR = "!";

	final static String NEWLINE = "\n%";

	private DataManager() {
	}

	/**
	 * Get DataManager instance.
	 */
	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	/**
	 * Set node.
	 */
	public void setNode(LinkedList<Node> node) {
		this.node = node;
	}

	/**
	 * Get Node from PhysicalID.
	 */
	public Node getNodeFromId(int idn) {
		for (Node n : node) {
			if (n.getPhysicalID().getAsInt() == idn)
				return n;
		}
		return null;
	}

	/**
	 * Add features in data.
	 */
	public void addData(int nodeId, Feature[] feat, String featureLabel) {
		int i;
		Vector featuresVector = null;
		Feature currFeature = new Feature();
		featuresVector = data.get(nodeId);
		if (featuresVector == null) {
			featuresVector = new Vector();
		}
		// set featureLabel
		for (i = 0; i < feat.length; i++) {
			currFeature = feat[i];
			currFeature.setFeatureLabel(featureLabel);
			feat[i] = currFeature;
		}
		featuresVector.add(feat);
		data.put(nodeId, featuresVector);
	}

	/**
	 * Store sensor setting.
	 */
	public void addSensorSetting(int nodeId, SpineSetupSensor setupSensor) {
		Vector<SpineSetupSensor> sensorSetVector = null;
		sensorSetVector = sensorSetting.get(nodeId);
		if (sensorSetVector == null) {
			sensorSetVector = new Vector<SpineSetupSensor>();
		}
		sensorSetVector.add(setupSensor);
		sensorSetting.put(nodeId, sensorSetVector);
	}

	/**
	 * Store function setting.
	 */
	public void addFunctionSetting(int nodeId,
			FeatureSpineSetupFunction setupFeatureFunction) {
		Vector<FeatureSpineSetupFunction> feaureFunSetVector = null;
		feaureFunSetVector = functionSetting.get(nodeId);
		if (feaureFunSetVector == null) {
			feaureFunSetVector = new Vector<FeatureSpineSetupFunction>();
		}
		feaureFunSetVector.add(setupFeatureFunction);
		functionSetting.put(nodeId, feaureFunSetVector);
	}

	/**
	 * Store feature setting.
	 */
	public void addFeatureSetting(int nodeId,
			FeatureSpineFunctionReq featureFunctionReq) {
		Vector<FeatureSpineFunctionReq> feaureFunReqVector = null;
		feaureFunReqVector = featureSetting.get(nodeId);
		if (feaureFunReqVector == null) {
			feaureFunReqVector = new Vector<FeatureSpineFunctionReq>();
		}
		feaureFunReqVector.add(featureFunctionReq);
		featureSetting.put(nodeId, feaureFunReqVector);
	}

	/**
	 * Save data set.
	 */
	public void saveDataSet(String dataSetFile) {
		
		String dataSetType;
		String nameDataSetFile = dataSetFile;
		String nameDataSetStored = "";
		int indEst;
		boolean errorDataSetStore = false;

		Vector featuresVector = null;
		Feature[] features = null;
		Feature feature;
		int i, j, k;

		Vector sensorSetVector = null;
		SpineSetupSensor setupSensor;

		Vector functionSetVector = null;
		FeatureSpineSetupFunction setupFunction;

		Vector featureSetVector = null;
		FeatureSpineFunctionReq setupFeature;

		Vector<String> vClassLabels = new Vector<String>();
		String allClassLabels = "";
		String origAllClassLabels = "";
		String classLabel;
		String notes = "";
		String startTime = "";

		propertControll = PropertiesController.getInstance();

		try {
			propertControll.load();
			// Read CLASS_LABEL
			allClassLabels = propertControll.getProperty("CLASS_LABEL");
			origAllClassLabels = allClassLabels;
			// Read DATASET_NOTES
			notes = propertControll.getProperty("DATASET_NOTES");
			notes = notes.replaceAll("\\n", " -- ");
			// Read START_TIME
			startTime = propertControll.getProperty("START_TIME");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		while ((allClassLabels.contains(","))) {
			classLabel = allClassLabels.substring(0, allClassLabels
					.indexOf(","));
			allClassLabels = allClassLabels.substring(allClassLabels
					.indexOf(",") + 1);
			vClassLabels.add(classLabel);
		}
		vClassLabels.add(allClassLabels);

		String[] classLabels = new String[vClassLabels.size()];

		for (i = 0; i < vClassLabels.size(); i++) {
			classLabels[i] = (String) vClassLabels.get(i);
		}

		if (dataSetFile.endsWith(".arff")) {
			// *** CASE ARFF FILE ***
			dataSetType = "arff";

			Enumeration ek = data.keys();

			while (ek.hasMoreElements()) {
				k = (Integer) ek.nextElement();

				// 1 Arff file for each node
				featuresVector = data.get(k);

				ArffFile fileArf = new ArffFile();
				// COMMENT
				String comment = "";
				comment = "DB SPINE - DataColletor Application" + NEWLINE;

				comment = comment + notes + NEWLINE;
				
				comment = comment + "First packet data timestamps: " + startTime + NEWLINE;

				comment = comment + NEWLINE + STARTLINECHR + "Node phyID:"
						+ getNodeFromId(k).getPhysicalID().getAsInt();

				// SENSOR SETTING
				sensorSetVector = sensorSetting.get(k);

				for (i = 0; i < sensorSetVector.size(); i++) {
					setupSensor = (SpineSetupSensor) sensorSetVector.get(i);
					System.out.println("*** setupSensor :"
							+ logic.Utils
									.setupSensorsToStringWithCode(setupSensor));
					comment = comment
							+ NEWLINE
							+ STARTLINECHR
							+ logic.Utils
									.setupSensorsToStringWithCode(setupSensor);
				}

				// FUNCTION SETTING
				functionSetVector = functionSetting.get(k);

				for (i = 0; i < functionSetVector.size(); i++) {
					setupFunction = (FeatureSpineSetupFunction) functionSetVector
							.get(i);

					System.out
							.println("*** setupFunction :"
									+ logic.Utils
											.setupFunciontStringWithCode(setupFunction));
					comment = comment
							+ NEWLINE
							+ STARTLINECHR
							+ logic.Utils
									.setupFunciontStringWithCode(setupFunction);
				}

				// FEATURE SETTING
				featureSetVector = featureSetting.get(k);

				for (i = 0; i < featureSetVector.size(); i++) {
					setupFeature = (FeatureSpineFunctionReq) featureSetVector
							.get(i);

					System.out
							.println("*** setupFeature :"
									+ logic.Utils
											.setupFeaturetoStringWithCode(setupFeature));
					comment = comment
							+ NEWLINE
							+ STARTLINECHR
							+ logic.Utils
									.setupFeaturetoStringWithCode(setupFeature);
				}

				fileArf.setComment(comment);

				// RELATION
				fileArf.setRelation("spine");

				// ATTRIBUTE
				fileArf.defineAttribute("CLASS_LABEL", "nominal", classLabels);
				fileArf.defineAttribute("featureDataId", "numeric");
				fileArf.defineAttribute("featureId", "numeric");

				fileArf.defineAttribute("sensorCode_featureCode_chNum",
						"string");
				fileArf.defineAttribute("featureValue", "numeric");

				// DATA
				featuresVector = data.get(k);

				for (i = 0; i < featuresVector.size(); i++) {
					features = (Feature[]) featuresVector.get(i);
					for (j = 0; j < features.length; j++) {
						feature = (Feature) features[j];

						if (SPINESensorConstants.chPresent(0, feature
								.getChannelBitmask())) {
							Object[] featuresData = new Object[5];

							System.out.println("CLASS_LABEL:"
									+ feature.getFeatureLabel() + " "
									+ "FeatureDataId:" + i + " " + "FeatureId:"
									+ j + " " + feature.getSensorCode() + "_"
									+ feature.getFeatureCode() + "_"
									+ SPINESensorConstants.CH1 + " "
									+ feature.getCh1Value());

							featuresData[0] = feature.getFeatureLabel();
							featuresData[1] = i;
							featuresData[2] = j;

							featuresData[3] = feature.getSensorCode() + "_"
									+ feature.getFeatureCode() + "_"
									+ SPINESensorConstants.CH1;
							featuresData[4] = feature.getCh1Value();
							fileArf.addData(featuresData);
						}
						if (SPINESensorConstants.chPresent(1, feature
								.getChannelBitmask())) {
							Object[] featuresData = new Object[5];

							System.out.println("CLASS_LABEL:"
									+ feature.getFeatureLabel() + " "
									+ "FeatureDataId:" + i + " " + "FeatureId:"
									+ j + " " + feature.getSensorCode() + "_"
									+ feature.getFeatureCode() + "_"
									+ SPINESensorConstants.CH2 + " "
									+ feature.getCh2Value());

							featuresData[0] = feature.getFeatureLabel();
							featuresData[1] = i;
							featuresData[2] = j;

							featuresData[3] = feature.getSensorCode() + "_"
									+ feature.getFeatureCode() + "_"
									+ SPINESensorConstants.CH2;
							featuresData[4] = feature.getCh2Value();
							fileArf.addData(featuresData);
						}
						if (SPINESensorConstants.chPresent(2, feature
								.getChannelBitmask())) {
							Object[] featuresData = new Object[5];

							System.out.println("CLASS_LABEL:"
									+ feature.getFeatureLabel() + " "
									+ "FeatureDataId:" + i + " " + "FeatureId:"
									+ j + " " + feature.getSensorCode() + "_"
									+ feature.getFeatureCode() + "_"
									+ SPINESensorConstants.CH3 + " "
									+ feature.getCh3Value());

							featuresData[0] = feature.getFeatureLabel();
							featuresData[1] = i;
							featuresData[2] = j;

							featuresData[3] = feature.getSensorCode() + "_"
									+ feature.getFeatureCode() + "_"
									+ SPINESensorConstants.CH3;
							featuresData[4] = feature.getCh3Value();
							fileArf.addData(featuresData);
						}
					}
				}

				try {

					if (data.size() > 1) {
						indEst = dataSetFile.indexOf(".");
						nameDataSetFile = dataSetFile.substring(0, indEst) + k
								+ "." + dataSetType;
						System.out.println("*** nameDataSetFile:"
								+ nameDataSetFile);
					}
					nameDataSetStored = nameDataSetStored + "  "
							+ nameDataSetFile;
					fileArf.save(nameDataSetFile);

				} catch (IOException e1) {
					errorDataSetStore = true;
					e1.printStackTrace();
				}

			}
		} else if ((dataSetFile.endsWith(".csv"))
				|| (dataSetFile.endsWith(".txt"))) {
			// *** CASE CSV FILE ***
			if (dataSetFile.endsWith(".csv")) {
				dataSetType = "csv";
			} else {
				dataSetType = "txt";
			}

			// Set char column separator
			String separator = (dataSetType.equals("csv")) ? ";" : " ";

			String dataSetCommentFile = "";

			String headerData = "";

			// 2 files (data-csv + comment-txt or data-txt +
			// comment-txt ) for each node

			Enumeration ek = data.keys();

			while (ek.hasMoreElements()) {
				k = (Integer) ek.nextElement();

				featuresVector = data.get(k);

				// data-csv or comment-txt

				BufferedWriter dataFile;

				// comment-txt
				BufferedWriter commentFile;

				// dataSetCommentFile

				indEst = dataSetFile.indexOf(".");
				if (data.size() > 1) {
					dataSetCommentFile = dataSetFile.substring(0, indEst) + k
							+ "Comment" + "." + dataSetType;
				} else {
					dataSetCommentFile = dataSetFile.substring(0, indEst)
							+ "Comment" + "." + dataSetType;
				}

				try {

					commentFile = new BufferedWriter(new FileWriter(
							dataSetCommentFile));
					// COMMENT
					String comment = "";
					comment = "% DB SPINE - DataColletor Application" + NEWLINE;

					comment = comment + notes + NEWLINE;
					
					comment = comment + "First packet data timestamps: " + startTime + NEWLINE;

					comment = comment + NEWLINE + STARTLINECHR + "Node phyID:"
							+ getNodeFromId(k).getPhysicalID().getAsInt();

					// SENSOR SETTING
					sensorSetVector = sensorSetting.get(k);

					for (i = 0; i < sensorSetVector.size(); i++) {
						setupSensor = (SpineSetupSensor) sensorSetVector.get(i);
						System.out
								.println("*** setupSensor :"
										+ logic.Utils
												.setupSensorsToStringWithCode(setupSensor));
						comment = comment
								+ NEWLINE
								+ STARTLINECHR
								+ logic.Utils
										.setupSensorsToStringWithCode(setupSensor);
					}

					// FUNCTION SETTING
					functionSetVector = functionSetting.get(k);

					for (i = 0; i < functionSetVector.size(); i++) {
						setupFunction = (FeatureSpineSetupFunction) functionSetVector
								.get(i);
						System.out
								.println("*** setupFunction :"
										+ logic.Utils
												.setupFunciontStringWithCode(setupFunction));
						comment = comment
								+ NEWLINE
								+ STARTLINECHR
								+ logic.Utils
										.setupFunciontStringWithCode(setupFunction);
					}

					// FEATURE SETTING
					featureSetVector = featureSetting.get(k);

					for (i = 0; i < featureSetVector.size(); i++) {
						setupFeature = (FeatureSpineFunctionReq) featureSetVector
								.get(i);
						System.out
								.println("*** setupFeature :"
										+ logic.Utils
												.setupFeaturetoStringWithCode(setupFeature));
						comment = comment
								+ NEWLINE
								+ STARTLINECHR
								+ logic.Utils
										.setupFeaturetoStringWithCode(setupFeature);
					}

					try {
						commentFile.write(comment + "\n");
						// RELATION
						commentFile.append("@relation spine" + "\r\n");
						// ATTRIBUTE
						commentFile.append("@attribute CLASS_LABEL {"
								+ origAllClassLabels + "} \r\n");
						commentFile.append("@attribute featureDataId numeric"
								+ "\r\n");
						commentFile.append("@attribute featureId numeric"
								+ "\r\n");
						commentFile
								.append("@attribute sensorCode_featureCode_chNum string"
										+ "\r\n");
						commentFile.append("@attribute featureValue numeric"
								+ "\r\n");
						commentFile.flush();
						commentFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// DATA

				if (data.size() > 1) {
					indEst = dataSetFile.indexOf(".");
					nameDataSetFile = dataSetFile.substring(0, indEst) + k
							+ "." + dataSetType;
					System.out
							.println("*** nameDataSetFile:" + nameDataSetFile);
				}

				try {
					dataFile = new BufferedWriter(new FileWriter(
							nameDataSetFile));

					headerData = "CLASS_LABEL" + separator + "featureDataId"
							+ separator + "featureId" + separator
							+ "sensorCode_featureCode_chNum" + separator
							+ "featureValue";

					dataFile.write(headerData + "\n");

					featuresVector = data.get(k);

					for (i = 0; i < featuresVector.size(); i++) {
						features = (Feature[]) featuresVector.get(i);
						for (j = 0; j < features.length; j++) {
							feature = (Feature) features[j];

							if (SPINESensorConstants.chPresent(0, feature
									.getChannelBitmask())) {
								Object[] featuresData = new Object[5];
								System.out.println("CLASS_LABEL:"
										+ feature.getFeatureLabel() + " "
										+ "FeatureDataId:" + i + " "
										+ "FeatureId:" + j + " "
										+ feature.getSensorCode() + "_"
										+ feature.getFeatureCode() + "_"
										+ SPINESensorConstants.CH1 + " "
										+ feature.getCh1Value());
								featuresData[0] = feature.getFeatureLabel();
								featuresData[1] = i;
								featuresData[2] = j;
								featuresData[3] = feature.getSensorCode() + "_"
										+ feature.getFeatureCode() + "_"
										+ SPINESensorConstants.CH1;
								featuresData[4] = feature.getCh1Value();
								dataFile.append(featuresData[0].toString()
										+ separator
										+ featuresData[1].toString()
										+ separator
										+ featuresData[2].toString()
										+ separator
										+ featuresData[3].toString()
										+ separator
										+ featuresData[4].toString() + "\n");

							}
							if (SPINESensorConstants.chPresent(1, feature
									.getChannelBitmask())) {
								Object[] featuresData = new Object[5];
								System.out.println("CLASS_LABEL:"
										+ feature.getFeatureLabel() + " "
										+ "FeatureDataId:" + i + " "
										+ "FeatureId:" + j + " "
										+ feature.getSensorCode() + "_"
										+ feature.getFeatureCode() + "_"
										+ SPINESensorConstants.CH2 + " "
										+ feature.getCh2Value());
								featuresData[0] = feature.getFeatureLabel();
								featuresData[1] = i;
								featuresData[2] = j;
								featuresData[3] = feature.getSensorCode() + "_"
										+ feature.getFeatureCode() + "_"
										+ SPINESensorConstants.CH2;
								featuresData[4] = feature.getCh2Value();
								dataFile.append(featuresData[0].toString()
										+ separator
										+ featuresData[1].toString()
										+ separator
										+ featuresData[2].toString()
										+ separator
										+ featuresData[3].toString()
										+ separator
										+ featuresData[4].toString() + "\n");

							}
							if (SPINESensorConstants.chPresent(2, feature
									.getChannelBitmask())) {
								Object[] featuresData = new Object[5];
								System.out.println("CLASS_LABEL:"
										+ feature.getFeatureLabel() + " "
										+ "FeatureDataId:" + i + " "
										+ "FeatureId:" + j + " "
										+ feature.getSensorCode() + "_"
										+ feature.getFeatureCode() + "_"
										+ SPINESensorConstants.CH3 + " "
										+ feature.getCh3Value());
								featuresData[0] = feature.getFeatureLabel();
								featuresData[1] = i;
								featuresData[2] = j;
								featuresData[3] = feature.getSensorCode() + "_"
										+ feature.getFeatureCode() + "_"
										+ SPINESensorConstants.CH3;
								featuresData[4] = feature.getCh3Value();
								dataFile.append(featuresData[0].toString()
										+ separator
										+ featuresData[1].toString()
										+ separator
										+ featuresData[2].toString()
										+ separator
										+ featuresData[3].toString()
										+ separator
										+ featuresData[4].toString() + "\n");

							}
						}
					}

					dataFile.flush();
					dataFile.close();

				} catch (IOException e) {
					e.printStackTrace();
				}

				nameDataSetStored = nameDataSetStored + "  " + nameDataSetFile;

			}

		}

		if (errorDataSetStore) {
			JOptionPane.showMessageDialog(null, "Failed to save Data Set on "
					+ nameDataSetStored, "DATA SET", JOptionPane.ERROR_MESSAGE);
		} else
			JOptionPane.showMessageDialog(null, "Data Set has been saved on "
					+ nameDataSetStored, "DATA SET",
					JOptionPane.INFORMATION_MESSAGE);

	}

}
