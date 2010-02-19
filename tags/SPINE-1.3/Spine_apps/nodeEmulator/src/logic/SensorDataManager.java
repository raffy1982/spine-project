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

import java.util.Arrays;
import java.util.Vector;

import spine.SPINEFunctionConstants;

/**
 * SensorDataManager: calculate feature on sensor data.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * @author Raffaele Gravina
 * 
 * @version 1.0
 */
public class SensorDataManager {

	int sensorCodeKey;

	byte featureCode;

	short windowSize;

	short shiftSize;

	Vector[] sensorRawValue;

	Vector ch1RawValue;

	Vector ch2RawValue;

	Vector ch3RawValue;

	Vector ch4RawValue;

	/**
	 * Constructor of an SensorDataManager.
	 * 
	 * @param sensorCodeKey is a sensor code.
	 * @param sensorRawValue is the set of sensor raw data.
	 * @param windowSize is the windows feature setup info.
	 * @param shiftSize is the shift feature setup info.
	 * 
	 */
	public SensorDataManager(int sensorCodeKey, Vector[] sensorRawValue, short windowSize, short shiftSize) {

		this.sensorCodeKey = sensorCodeKey;
		this.sensorRawValue = sensorRawValue;
		this.ch1RawValue = sensorRawValue[0];
		this.ch2RawValue = sensorRawValue[1];
		this.ch3RawValue = sensorRawValue[2];
		this.ch4RawValue = sensorRawValue[3];
		this.windowSize = windowSize;
		this.shiftSize = shiftSize;

	};

	/**
	 * Calculate feature (RAW_DATA, MAX, MIN, RANGE, MEAN, AMPLITUDE, RMS,
	 * ST_DEV, TOTAL_ENERGY, VARIANCE, MODE, MEDIAN).
	 * 
	 * @param featureCode is a feature code (SPINEFunctionConstants).
	 * 
	 */

	public Vector[] calculateFeature(byte featureCode) {

		this.featureCode = featureCode;

		Vector[] sensorFeatureValue = new Vector[4];
		Vector ch1FeatureValue = new Vector();
		Vector ch2FeatureValue = new Vector();
		Vector ch3FeatureValue = new Vector();
		Vector ch4FeatureValue = new Vector();
		int[] rawData;

		if (windowSize <= 0 || shiftSize <= 0 || shiftSize > windowSize) {
			System.out.println("WINDOW and/or SHIFT INVALID.");
		}

		// ch1FeatureValue
		rawData = new int[ch1RawValue.size()];
		for (int i = 0; i < ch1RawValue.size(); i++) {
			rawData[i] = (Integer) ch1RawValue.get(i);
		}
		if (rawData.length < windowSize) {
			System.out.println("WINDOW > rawData.lenght");
		} else {
			ch1FeatureValue = calculate(rawData, featureCode);
		}

		// ch2FeatureValue
		rawData = new int[ch2RawValue.size()];
		for (int i = 0; i < ch2RawValue.size(); i++) {
			rawData[i] = (Integer) ch2RawValue.get(i);
		}
		// ch2FeatureValue = calculate(rawData, featureCode);
		if (rawData.length < windowSize) {
			System.out.println("WINDOW > rawData.lenght");
		} else {
			ch2FeatureValue = calculate(rawData, featureCode);
		}

		// ch3FeatureValue
		rawData = new int[ch3RawValue.size()];
		for (int i = 0; i < ch3RawValue.size(); i++) {
			rawData[i] = (Integer) ch3RawValue.get(i);
		}
		// ch3FeatureValue = calculate(rawData, featureCode);
		if (rawData.length < windowSize) {
			System.out.println("WINDOW > rawData.lenght");
		} else {
			ch3FeatureValue = calculate(rawData, featureCode);
		}

		// ch4FeatureValue
		rawData = new int[ch4RawValue.size()];
		for (int i = 0; i < ch4RawValue.size(); i++) {
			rawData[i] = (Integer) ch4RawValue.get(i);
		}
		// ch4FeatureValue = calculate(rawData, featureCode);
		if (rawData.length < windowSize) {
			System.out.println("WINDOW > rawData.lenght");
		} else {
			ch4FeatureValue = calculate(rawData, featureCode);
		}

		sensorFeatureValue[0] = ch1FeatureValue;
		sensorFeatureValue[1] = ch2FeatureValue;
		sensorFeatureValue[2] = ch3FeatureValue;
		sensorFeatureValue[3] = ch4FeatureValue;

		return sensorFeatureValue;

	}

	private Vector calculate(int[] rawData, byte featureCode) {

		int[] dataWindow = new int[windowSize];

		Vector currInstance = new Vector();
		int startIndex = 0;
		int j = 0;

		try {

			while (true) {
				System.arraycopy(rawData, startIndex, dataWindow, 0, windowSize);
				currInstance.add((int) calculate(featureCode, dataWindow));
				startIndex = shiftSize * ++j;
			}
		} catch (Exception e) {
			System.err.println("No more data from rawData to dataWindow");
		}

		return currInstance;

	}

	private static int calculate(byte featurecode, int[] data) {
		switch (featurecode) {
		case SPINEFunctionConstants.RAW_DATA:
			return raw(data);
		case SPINEFunctionConstants.MAX:
			return max(data);
		case SPINEFunctionConstants.MIN:
			return min(data);
		case SPINEFunctionConstants.RANGE:
			return range(data);
		case SPINEFunctionConstants.MEAN:
			return mean(data);
		case SPINEFunctionConstants.AMPLITUDE:
			return amplitude(data);
		case SPINEFunctionConstants.RMS:
			return rms(data);
		case SPINEFunctionConstants.ST_DEV:
			return stDev(data);
		case SPINEFunctionConstants.TOTAL_ENERGY:
			return totEnergy(data);
		case SPINEFunctionConstants.VARIANCE:
			return variance(data);
		case SPINEFunctionConstants.MODE:
			return mode(data);
		case SPINEFunctionConstants.MEDIAN:
			return median(data);
		default:
			return 0;
		}

	}

	// RAW_DATA calculate: the last raw_data in a window
	private static int raw(int[] data) {
		int indexLastValue = data.length - 1;
		int raw = data[indexLastValue];
		return raw;
	}

	private static int max(int[] data) {
		int max = data[0];
		for (int i = 1; i < data.length; i++)
			if (data[i] > max)
				max = data[i];
		return max;
	}

	private static int min(int[] data) {
		int min = data[0];
		for (int i = 1; i < data.length; i++)
			if (data[i] < min)
				min = data[i];
		return min;
	}

	private static int range(int[] data) {
		int min = data[0];
		int max = min;
		// we don't use the methods 'max' and 'min';
		// instead, to boost the alg, we can compute both using one single for
		// loop ( O(n) vs O(2n) )

		for (int i = 1; i < data.length; i++) {
			if (data[i] < min)
				min = data[i];
			if (data[i] > max)
				max = data[i];
		}
		return (max - min);
	}

	private static int mean(int[] data) {
		double mean = 0;

		for (int i = 0; i < data.length; i++)
			mean += data[i];

		return (int) (Math.round(mean / data.length));
	}

	private static int amplitude(int[] data) {
		return (max(data) - mean(data));
	}

	private static int rms(int[] data) {
		double rms = 0;
		for (int i = 0; i < data.length; i++)
			rms += (data[i] * data[i]);
		rms /= data.length;
		return (int) Math.round(Math.sqrt(rms));
	}

	private static int variance(int[] data) {
		double var = 0, mu = 0;
		int val = 0;

		for (int i = 0; i < data.length; i++) {
			val = data[i];
			mu += val;
			var += (val * val);
		}

		mu /= data.length;
		var /= data.length;
		var -= (mu * mu);
		return (int) Math.round(var);
	}

	private static int stDev(int[] data) {
		return (int) (Math.round(Math.sqrt(variance(data))));
	}

	private static int mode(int[] data) {
		int iMax = 0;
		int[] orderedData = new int[data.length];
		System.arraycopy(data, 0, orderedData, 0, data.length);
		int[] tmp = new int[data.length];

		// to boost the algorithm, we first sort the array (mergeSort takes
		// O(nlogn))
		Arrays.sort(orderedData);

		int i = 0;
		// now we look for the max number of occurences per each value
		while (i < data.length - 1) {
			for (int j = i + 1; j < data.length; j++)
				if (orderedData[i] == orderedData[j]) {
					tmp[i] = j - i + 1;
					if (j == (data.length - 1))
						i = data.length - 1; // exit condition
				} else {
					i = j;
					break;
				}
		}

		// we choose the overall max
		for (i = 1; i < data.length; i++)
			if (tmp[i] > tmp[iMax])
				iMax = i;

		return orderedData[iMax];
	}

	private static int median(int[] data) {
		int[] sortedData = new int[data.length];

		System.arraycopy(data, 0, sortedData, 0, data.length);
		Arrays.sort(sortedData);

		return (data.length % 2 == 0) ? (sortedData[data.length / 2] + sortedData[(data.length / 2) - 1]) / 2 : sortedData[(data.length - 1) / 2];
	}

	private static int totEnergy(int[] data) {
		double totEn = 0;

		for (int i = 0; i < data.length; i++)
			totEn += (data[i] * data[i]);

		return (int) (totEn / data.length);
	}

}