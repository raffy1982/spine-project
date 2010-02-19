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

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

/**
 * SensorLabelManager: set feature label for calculate feature.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */

public class SensorLabelManager {

	int sensorCodeKey;

	String labelAlgorithm;

	short windowSize;

	short shiftSize;

	Vector sensorLabelValue;

	/**
	 * Constructor of an SensorDataManager.
	 * 
	 * @param sensorCodeKey is a sensor code.
	 * @param sensorLabelValue is the set of sensor raw data Label.
	 * @param windowSize is the windows feature setup info.
	 * @param shiftSize is the shift feature setup info.
	 * 
	 */

	public SensorLabelManager(int sensorCodeKey, Vector sensorLabelValue, short windowSize, short shiftSize) {

		this.sensorCodeKey = sensorCodeKey;
		this.sensorLabelValue = sensorLabelValue;
		this.windowSize = windowSize;
		this.shiftSize = shiftSize;

	};

	/**
	 * Calculate feature Label.
	 * 
	 * @param labelAlgorithm is the algorithm for calculate the label  
	 *            "ALLWITHFREQ" - return all labels in the window with
	 *            occurrence number or "MOREFREQ" - return the label with
	 *            highest occurence in the window - or "LAST"- return the last
	 *            label (more recent in the window).
	 * 
	 */
	public Vector calculateLabel(String labelAlgorithm) {

		this.labelAlgorithm = labelAlgorithm;

		Vector sensorLabelCalculateValue = new Vector();

		String[] labelData;

		if (windowSize <= 0 || shiftSize <= 0 || shiftSize > windowSize) {
			System.out.println("WINDOW and/or SHIFT INVALID.");
		}

		labelData = new String[sensorLabelValue.size()];
		for (int i = 0; i < sensorLabelValue.size(); i++) {
			labelData[i] = (String) sensorLabelValue.get(i);
		}
		if (labelData.length < windowSize) {
			System.out.println("WINDOW > rawData.lenght");
		} else {

			sensorLabelCalculateValue = calculate(labelData, labelAlgorithm);
		}

		return sensorLabelCalculateValue;

	}

	private Vector calculate(String[] labelData, String labelAlgorithm) {

		String[] dataWindow = new String[windowSize];

		Vector currInstance = new Vector();
		int startIndex = 0;
		int j = 0;

		try {

			while (true) {
				System.arraycopy(labelData, startIndex, dataWindow, 0, windowSize);

				currInstance.add((String) calculate(labelAlgorithm, dataWindow));

				startIndex = shiftSize * ++j;
			}
		} catch (Exception e) {
			System.err.println("No more data from labelData to dataWindow");
		}

		return currInstance;

	}

	private static String calculate(String labelAlgorithm, String[] data) {

		String labelCalculate = "";

		if (labelAlgorithm.equalsIgnoreCase("ALLWITHFREQ")) {
			labelCalculate = allWithFreq(data);
		} else if (labelAlgorithm.equalsIgnoreCase("MOREFREQ")) {
			labelCalculate = moreFreq(data);
		} else if (labelAlgorithm.equalsIgnoreCase("LAST")) {
			labelCalculate = last(data);
		}

		return labelCalculate;

	}

	// allWithFreq calculate
	private static String allWithFreq(String[] data) {
		Hashtable<String, Integer> labelsFreq = new Hashtable<String, Integer>();
		int freq = 0;
		String label = "";
		String allWithFreq = "";

		for (int i = 0; i < data.length; i++) {

			if (labelsFreq.containsKey(data[i])) {
				freq = labelsFreq.get(data[i]);
				freq = freq + 1;
			} else {
				freq = 1;
			}
			labelsFreq.put(data[i], freq);
		}

		Enumeration e = labelsFreq.keys();

		while (e.hasMoreElements()) {

			label = (String) e.nextElement();
			freq = labelsFreq.get(label);

			allWithFreq = allWithFreq + " " + label + "(" + freq + ")";

		}
		return allWithFreq;
	}

	// moreFreq calculate
	private static String moreFreq(String[] data) {
		Hashtable<String, Integer> labelsFreq = new Hashtable<String, Integer>();
		Hashtable<Integer, String> freqLabels = new Hashtable<Integer, String>();
		int freq = 0;
		String label = "";
		String newLabel = "";
		String moreFreq = "";

		for (int i = 0; i < data.length; i++) {

			if (labelsFreq.containsKey(data[i])) {
				freq = labelsFreq.get(data[i]);
				freq = freq + 1;
			} else {
				freq = 1;
			}
			labelsFreq.put(data[i], freq);
		}

		Enumeration e = labelsFreq.keys();

		while (e.hasMoreElements()) {

			label = (String) e.nextElement();
			freq = labelsFreq.get(label);

			if (freqLabels.containsKey(freq)) {
				newLabel = freqLabels.get(freq);
				newLabel = newLabel + " " + label;
			} else {
				newLabel = label;
			}
			freqLabels.put(freq, newLabel);
		}

		int highestFreq = (int) java.util.Collections.max(freqLabels.keySet());

		moreFreq = freqLabels.get(highestFreq);

		return moreFreq;
	}

	// last calculate: the last label in a window
	private static String last(String[] data) {
		int indexLastValue = data.length - 1;
		String last = data[indexLastValue];
		return last;
	}

}