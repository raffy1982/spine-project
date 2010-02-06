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

package dataSetIO;

import java.util.Vector;
import java.util.Hashtable;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;
import spine.datamodel.Sensor;

/**
 * Comment Manager: parse comment section.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */

public class CommentManager {

	private int nodeID;

	private Vector<Sensor> sensorsList = new Vector<Sensor>(); // <values:Sensor>

	private Hashtable functionsList = new Hashtable();

	private Hashtable sensorSetUpInfo = new Hashtable(); // key sensorCode

	private Hashtable featureSetUpInfo = new Hashtable(); // key sensorCode

	private Hashtable dsSensorTimeSampling = new Hashtable(); // key

	// sensorCode

	/** Construct. */
	public CommentManager() {

	}

	/**
	 * Parse comment.
	 * 
	 * @param comment is the String representation of the comment.
	 * @param computeFeature (ON or OFF).
	 * 
	 */
	public void parseComment(String comment, String computeFeature) {

		String[] commentList = comment.split("\r|\n|\r\n");

		String newCommentLine;

		Pattern codeSensorValuePat = Pattern.compile("\\((.*?)\\)");
		Pattern codeChannelValuePat = Pattern.compile("\\[(.*?)\\]");
		Pattern codeFunctionValuePat = Pattern.compile("\\{(.*?)\\}");
		Pattern chRawValuePat = Pattern.compile("Raw(.*?)\\]");

		Matcher codeSensorValueMat;
		Matcher codeChannelValueMat;
		Matcher codeFunctionValueMat;
		Matcher chRawValueMat;

		String commentLine;
		int nodeID = 0;

		boolean[] channelBitmask = new boolean[4];
		boolean hasCh1;
		boolean hasCh2;
		boolean hasCh3;
		boolean hasCh4;

		byte sensorCode = -1;
		byte sensorChannelBitmask;

		byte timeScale;
		int samplingTime;
		int window;
		int shift;

		Vector<Sensor> sensorsList = new Vector<Sensor>(); // <values:Sensor>
		Hashtable sensorSetUpInfo = new Hashtable(); // key sensorCode
		Vector sensorSetUpParam = new Vector(); // timeScale samplingTime
		Hashtable featureSetUpInfo = new Hashtable(); // key sensorCode
		Vector featureSetUpParam = new Vector(); // window shift

		Hashtable functionsList = new Hashtable();

		Vector<Byte> functionParam = new Vector<Byte>();

		System.out.println("COMMENT: ");
		System.out.println(comment);
		// CASE: compute_feature=ON
		if (computeFeature.equalsIgnoreCase("ON")) {
			for (int k = 0; k < commentList.length; k++) {
				commentLine = commentList[k];
				newCommentLine = "";
				if (commentLine.startsWith("!")) {
					// nodeID = Node phyID
					if (commentLine.contains("phyID")) {
						nodeID = Integer.parseInt(commentLine.substring(commentLine.indexOf(":") + 1));
						System.out.println("*** phyID: " + nodeID);
					} else if (commentLine.contains("Feature_Activation")) {

						int stInd;
						int endInd;

						newCommentLine = "";

						codeFunctionValueMat = codeFunctionValuePat.matcher(commentLine);

						while (codeFunctionValueMat.find()) {
							System.out.println("Match: " + codeFunctionValueMat.group());
							newCommentLine = newCommentLine + codeFunctionValueMat.group();
						}

						System.out.println("newCommentLine: " + newCommentLine);

						if (newCommentLine.contains("{" + SPINEFunctionConstants.RAW_DATA + "}")) {

							// Initialization channelBitmask
							for (int i = 0; i < 4; i++) {
								channelBitmask[i] = false;
							}
							// sensorsList -- sensorCode and
							// sensorChannelBitmask

							codeSensorValueMat = codeSensorValuePat.matcher(commentLine);

							while (codeSensorValueMat.find()) {
								System.out.println("Match: " + codeSensorValueMat.group());
								newCommentLine = newCommentLine + codeSensorValueMat.group();
							}

							System.out.println("newCommentLine: " + newCommentLine);

							sensorCode = Byte.parseByte(newCommentLine.substring(newCommentLine.indexOf("(") + 1, newCommentLine.indexOf(")")));

							System.out.println("sensorCode: " + sensorCode);

							newCommentLine = "";

							chRawValueMat = chRawValuePat.matcher(commentLine);

							while (chRawValueMat.find()) {
								System.out.println("Match: " + chRawValueMat.group());
								newCommentLine = newCommentLine + chRawValueMat.group();
							}

							stInd = newCommentLine.indexOf("[") + 1;
							endInd = newCommentLine.indexOf("]");
							newCommentLine = newCommentLine.substring(stInd, endInd);

							System.out.println("newCommentLine: " + newCommentLine);

							newCommentLine = newCommentLine.replace(" ", "");

							System.out.println("newCommentLine: " + newCommentLine + "  " + newCommentLine.length());

							for (int z = 0; z < newCommentLine.length(); z++) {
								channelBitmask[Integer.parseInt(String.valueOf(newCommentLine.charAt(z)))] = true;
							}

							hasCh1 = channelBitmask[0];
							hasCh2 = channelBitmask[1];
							hasCh3 = channelBitmask[2];
							hasCh4 = channelBitmask[3];
							sensorChannelBitmask = SPINESensorConstants.getValueTypesCodeByBitmask(hasCh1, hasCh2, hasCh3, hasCh4);

							System.out.println("sensorChannelBitmask: " + sensorChannelBitmask);

							sensorsList.add(new Sensor((byte) sensorCode, (byte) sensorChannelBitmask));

						}
					} else if (commentLine.contains("Sensor_Setup")) {
						sensorSetUpParam.clear();
						sensorCode = Byte.parseByte(commentLine.substring(commentLine.indexOf("(") + 1, commentLine.indexOf(")")));
						newCommentLine = commentLine.substring(commentLine.indexOf("timeScale"), commentLine.indexOf("samplingTime"));
						timeScale = SPINESensorConstants.timeScaleByString(newCommentLine.substring(newCommentLine.indexOf("=") + 1, newCommentLine.indexOf(",")));
						sensorSetUpParam.add(timeScale);
						newCommentLine = commentLine.substring(commentLine.indexOf("samplingTime"));
						samplingTime = Integer.parseInt(newCommentLine.substring(newCommentLine.indexOf("=") + 1, newCommentLine.length()));
						sensorSetUpParam.add(samplingTime);
						sensorSetUpInfo.put(sensorCode, sensorSetUpParam.clone());
						// Store dsSensorTimeSampling
						if (timeScale == SPINESensorConstants.SEC) {
							samplingTime = samplingTime * 1000;
						} else if (timeScale == SPINESensorConstants.MIN) {
							samplingTime = samplingTime * 60000;
						}
						dsSensorTimeSampling.put(sensorCode, samplingTime);

					} else if (commentLine.contains("Feature_Setup")) {
						featureSetUpParam.clear();
						sensorCode = Byte.parseByte(commentLine.substring(commentLine.indexOf("(") + 1, commentLine.indexOf(")")));
						newCommentLine = commentLine.substring(commentLine.indexOf("window"), commentLine.indexOf("shift"));
						window = Integer.parseInt(newCommentLine.substring(newCommentLine.indexOf("=") + 1, newCommentLine.indexOf(",")));
						featureSetUpParam.add(window);
						newCommentLine = commentLine.substring(commentLine.indexOf("shift"));
						shift = Integer.parseInt(newCommentLine.substring(newCommentLine.indexOf("=") + 1, newCommentLine.length()));
						featureSetUpParam.add(shift);
						featureSetUpInfo.put(sensorCode, featureSetUpParam.clone());
						// Store dsSensorTimeSampling
						samplingTime = (Integer) dsSensorTimeSampling.get(sensorCode);
						samplingTime = samplingTime * shift;
						dsSensorTimeSampling.put(sensorCode, samplingTime);

					}
				}
			}

			// Function List <FunctionCode, <FunctionParam>>
			functionParam.clear();
			functionParam.add(SPINEFunctionConstants.RAW_DATA);
			functionParam.add(SPINEFunctionConstants.MEDIAN);
			functionParam.add(SPINEFunctionConstants.MODE);
			functionParam.add(SPINEFunctionConstants.VARIANCE);
			functionParam.add(SPINEFunctionConstants.TOTAL_ENERGY);
			functionParam.add(SPINEFunctionConstants.ST_DEV);
			functionParam.add(SPINEFunctionConstants.RMS);
			functionParam.add(SPINEFunctionConstants.AMPLITUDE);
			functionParam.add(SPINEFunctionConstants.MEAN);
			functionParam.add(SPINEFunctionConstants.RANGE);
			functionParam.add(SPINEFunctionConstants.MIN);
			functionParam.add(SPINEFunctionConstants.MAX);
			functionsList.put((byte) 0x1, functionParam.clone());

		} else {

			// CASE: compute_feature=OFF
			functionParam.clear();
			for (int k = 0; k < commentList.length; k++) {
				commentLine = commentList[k];
				newCommentLine = "";
				if (commentLine.startsWith("!")) {
					// nodeID = Node phyID
					if (commentLine.contains("phyID")) {
						nodeID = Integer.parseInt(commentLine.substring(commentLine.indexOf(":") + 1));
						System.out.println("*** phyID: " + nodeID);
					} else if (commentLine.contains("Feature_Activation")) {

						// Initialization channelBitmask
						for (int i = 0; i < 4; i++) {
							channelBitmask[i] = false;
						}
						// sensorsList -- sensorCode and sensorChannelBitmask

						codeSensorValueMat = codeSensorValuePat.matcher(commentLine);

						while (codeSensorValueMat.find()) {
							System.out.println("Match: " + codeSensorValueMat.group());
							newCommentLine = newCommentLine + codeSensorValueMat.group();
						}

						System.out.println("newCommentLine: " + newCommentLine);

						sensorCode = Byte.parseByte(newCommentLine.substring(newCommentLine.indexOf("(") + 1, newCommentLine.indexOf(")")));

						System.out.println("sensorCode: " + sensorCode);

						newCommentLine = "";

						codeChannelValueMat = codeChannelValuePat.matcher(commentLine);

						while (codeChannelValueMat.find()) {
							System.out.println("Match: " + codeChannelValueMat.group());
							newCommentLine = newCommentLine + codeChannelValueMat.group();
						}

						System.out.println("newCommentLine: " + newCommentLine);

						newCommentLine = newCommentLine.replace("[", "");

						newCommentLine = newCommentLine.replace("]", "");

						newCommentLine = newCommentLine.replace(" ", "");

						System.out.println("newCommentLine: " + newCommentLine + "  " + newCommentLine.length());

						for (int z = 0; z < newCommentLine.length(); z++) {
							channelBitmask[Integer.parseInt(String.valueOf(newCommentLine.charAt(z)))] = true;
						}

						hasCh1 = channelBitmask[0];
						hasCh2 = channelBitmask[1];
						hasCh3 = channelBitmask[2];
						hasCh4 = channelBitmask[3];
						sensorChannelBitmask = SPINESensorConstants.getValueTypesCodeByBitmask(hasCh1, hasCh2, hasCh3, hasCh4);

						System.out.println("sensorChannelBitmask: " + sensorChannelBitmask);

						sensorsList.add(new Sensor((byte) sensorCode, (byte) sensorChannelBitmask));

						// Function List <FunctionCode, <FunctionParam>>
						int stInd;
						int endInd;
						byte featureCode;
						boolean featureCodeIsPresent = false;

						newCommentLine = "";

						codeFunctionValueMat = codeFunctionValuePat.matcher(commentLine);

						while (codeFunctionValueMat.find()) {
							System.out.println("Match: " + codeFunctionValueMat.group());
							newCommentLine = newCommentLine + codeFunctionValueMat.group();
						}

						System.out.println("newCommentLine: " + newCommentLine);

						while (newCommentLine.length() > 0) {
							stInd = newCommentLine.indexOf("{") + 1;
							endInd = newCommentLine.indexOf("}");
							featureCode = Byte.parseByte(newCommentLine.substring(stInd, endInd));
							for (int j = 0; j < functionParam.size(); j++) {
								if (featureCode == (Byte) functionParam.get(j)) {
									featureCodeIsPresent = true;
								}
							}
							if (!featureCodeIsPresent) {
								functionParam.add(featureCode);
							}
							newCommentLine = newCommentLine.substring(endInd + 1);
						}

					} else if (commentLine.contains("Sensor_Setup")) {
						sensorSetUpParam.clear();
						sensorCode = Byte.parseByte(commentLine.substring(commentLine.indexOf("(") + 1, commentLine.indexOf(")")));
						newCommentLine = commentLine.substring(commentLine.indexOf("timeScale"), commentLine.indexOf("samplingTime"));
						timeScale = SPINESensorConstants.timeScaleByString(newCommentLine.substring(newCommentLine.indexOf("=") + 1, newCommentLine.indexOf(",")));
						sensorSetUpParam.add(timeScale);
						newCommentLine = commentLine.substring(commentLine.indexOf("samplingTime"));
						samplingTime = Integer.parseInt(newCommentLine.substring(newCommentLine.indexOf("=") + 1, newCommentLine.length()));
						sensorSetUpParam.add(samplingTime);
						sensorSetUpInfo.put((int) sensorCode, sensorSetUpParam.clone());
						// Store dsSensorTimeSampling
						if (timeScale == SPINESensorConstants.SEC) {
							samplingTime = samplingTime * 1000;
						} else if (timeScale == SPINESensorConstants.MIN) {
							samplingTime = samplingTime * 60000;
						}
						dsSensorTimeSampling.put(sensorCode, samplingTime);
					} else if (commentLine.contains("Feature_Setup")) {
						featureSetUpParam.clear();
						sensorCode = Byte.parseByte(commentLine.substring(commentLine.indexOf("(") + 1, commentLine.indexOf(")")));
						newCommentLine = commentLine.substring(commentLine.indexOf("window"), commentLine.indexOf("shift"));
						window = Integer.parseInt(newCommentLine.substring(newCommentLine.indexOf("=") + 1, newCommentLine.indexOf(",")));
						featureSetUpParam.add(window);
						newCommentLine = commentLine.substring(commentLine.indexOf("shift"));
						shift = Integer.parseInt(newCommentLine.substring(newCommentLine.indexOf("=") + 1, newCommentLine.length()));
						featureSetUpParam.add(shift);
						featureSetUpInfo.put((int) sensorCode, featureSetUpParam.clone());
						// Store dsSensorTimeSampling
						samplingTime = (Integer) dsSensorTimeSampling.get(sensorCode);
						samplingTime = samplingTime * shift;
						dsSensorTimeSampling.put(sensorCode, samplingTime);
					}
				}
			}
			functionsList.put((byte) 0x1, functionParam.clone());
		}

		this.nodeID = nodeID;
		this.sensorsList = sensorsList;
		this.functionsList = functionsList;
		this.sensorSetUpInfo = sensorSetUpInfo;
		this.featureSetUpInfo = featureSetUpInfo;
		this.dsSensorTimeSampling = dsSensorTimeSampling;
	}

	/**
	 * Get the sensors list.
	 */
	public Vector getSensorsList() {

		return sensorsList;
	}

	/**
	 * Get the functions list.
	 */
	public Hashtable getFunctionsList() {

		return functionsList;
	}

	/**
	 * Get the sensor setup info.
	 */
	public Hashtable getSensorSetUpInfo() {

		return sensorSetUpInfo;
	}

	/**
	 * Get the feature setup info.
	 */
	public Hashtable getFeatureSetUpInfo() {

		return featureSetUpInfo;
	}

	/**
	 * Get the sensor time sampling.
	 */
	public Hashtable getDsSensorTimeSampling() {

		return dsSensorTimeSampling;
	}

	/**
	 * Get the node Physical ID.
	 */
	public int getNodeID() {
		return nodeID;
	}
}
