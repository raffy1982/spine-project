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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import exceptions.FunctionNotSupportedException;
import exceptions.SensorNotPresentException;
import exceptions.SensorSetupException;

import spine.datamodel.Feature;
import spine.datamodel.Sensor;
import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;
import spine.datamodel.functions.SpineSetupSensor;
import spine.datamodel.functions.FeatureSpineSetupFunction;
import spine.datamodel.functions.FeatureSpineFunctionReq;

/**
 * Payload Manager: encode and decode payload message.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */

public class PayloadManager {

	public PayloadManager() {

	}

	/** Create Payload for Service Advertisement message. */
	public byte[] createSerAdvPayload(Vector sensorsList, Hashtable functionsList) {

		ArrayList<Byte> payloadTmp = new ArrayList<Byte>();

		Sensor sensor;

		int libreriesListSize;

		Vector functionParam = new Vector();

		int arrayLength;

		// sensorsNr
		payloadTmp.add((byte) sensorsList.size());

		// get elements of sensorsList
		for (int i = 0; i < sensorsList.size(); i++) {
			sensor = (Sensor) (sensorsList.get(i));
			payloadTmp.add(sensor.getCode());
			payloadTmp.add(sensor.getChannelBitmask());
		}
		// libreriesListSize
		libreriesListSize = functionsList.size() * 2;

		for (Enumeration e = functionsList.keys(); e.hasMoreElements();) {
			byte functionCode = (Byte) e.nextElement();
			functionParam = (Vector) (functionsList.get(functionCode));
			libreriesListSize = libreriesListSize + functionParam.size();
		}

		payloadTmp.add((byte) libreriesListSize);

		for (Enumeration e = functionsList.keys(); e.hasMoreElements();) {
			byte functionCode = (Byte) e.nextElement();
			payloadTmp.add((byte) functionCode);
			functionParam = (Vector) (functionsList.get(functionCode));
			payloadTmp.add((byte) functionParam.size());
			// get elements of functionParam
			for (int i = 0; i < functionParam.size(); i++) {
				payloadTmp.add((Byte) (functionParam.get(i)));
			}
		}

		arrayLength = payloadTmp.size();
		byte[] payload = new byte[arrayLength];

		for (int i = 0; i < arrayLength; i++)
			payload[i] = payloadTmp.get(i).byteValue();

		return payload;
	}

	/** Create Payload for data packets. */
	public byte[] createDataPayload(ArrayList<Feature> featureData) {
		ArrayList<Byte> payloadTmp = new ArrayList<Byte>();
		int arrayLength;

		Feature feature = new Feature();

		String featureLabel = "";
		byte featureLabelLength = 0;

		int featureCount = featureData.size();

		int ch1Value;
		int ch2Value;
		int ch3Value;
		int ch4Value;
		byte channelBitmask;

		for (int j = 0; j < featureCount; j++) {
			feature = (Feature) featureData.get(j);

			System.out.println(feature.toString());

			if (j == 0) {
				payloadTmp.add(feature.getFunctionCode()); // Function Code
				payloadTmp.add(feature.getSensorCode()); // Sensor Code
				payloadTmp.add((byte) featureCount); // Feature count
			}
			payloadTmp.add(feature.getFeatureCode()); // Feature Code
			channelBitmask = feature.getChannelBitmask();
			payloadTmp.add(channelBitmask); // ChannelBitmask
			if (SPINESensorConstants.chPresent(SPINESensorConstants.CH1, channelBitmask)) {
				ch1Value = feature.getCh1Value();
				payloadTmp.add((byte) (ch1Value >> 24));
				payloadTmp.add((byte) ((ch1Value << 8) >> 24));
				payloadTmp.add((byte) ((ch1Value << 16) >> 24));
				payloadTmp.add((byte) ((ch1Value << 24) >> 24));
			}
			if (SPINESensorConstants.chPresent(SPINESensorConstants.CH2, channelBitmask)) {
				ch2Value = feature.getCh2Value();
				payloadTmp.add((byte) (ch2Value >> 24));
				payloadTmp.add((byte) ((ch2Value << 8) >> 24));
				payloadTmp.add((byte) ((ch2Value << 16) >> 24));
				payloadTmp.add((byte) ((ch2Value << 24) >> 24));
			}
			if (SPINESensorConstants.chPresent(SPINESensorConstants.CH3, channelBitmask)) {
				ch3Value = feature.getCh3Value();
				payloadTmp.add((byte) (ch3Value >> 24));
				payloadTmp.add((byte) ((ch3Value << 8) >> 24));
				payloadTmp.add((byte) ((ch3Value << 16) >> 24));
				payloadTmp.add((byte) ((ch3Value << 24) >> 24));
			}
			if (SPINESensorConstants.chPresent(SPINESensorConstants.CH4, channelBitmask)) {
				ch4Value = feature.getCh4Value();
				payloadTmp.add((byte) (ch4Value >> 24));
				payloadTmp.add((byte) ((ch4Value << 8) >> 24));
				payloadTmp.add((byte) ((ch4Value << 16) >> 24));
				payloadTmp.add((byte) ((ch4Value << 24) >> 24));
			}

			// featureLabel
			featureLabel = feature.getFeatureLabel();
			// featureLabel.lenght
			if (!(featureLabel == null)) {
				featureLabelLength = (byte) feature.getFeatureLabel().length();
			}
			payloadTmp.add(featureLabelLength);
			if (!(featureLabel == null)) {
				for (int k = 0; k < featureLabel.length(); k++) {
					payloadTmp.add((byte) featureLabel.charAt(k));
				}
			}
		}

		arrayLength = payloadTmp.size();

		byte[] payload = new byte[arrayLength];

		for (int i = 0; i < arrayLength; i++)
			payload[i] = payloadTmp.get(i).byteValue();

		return payload;
	}

	/** Decode Setup Sensor message. */
	public SpineSetupSensor decodeSetupSensor(short[] shortPayload, Vector sensorsList, Vector sensorCodeSetupEx, Hashtable dsSensorTimeSampling, String computeFeature)
			throws SensorNotPresentException, SensorSetupException {
		SpineSetupSensor setupSensor = new SpineSetupSensor();

		Sensor sensor;

		byte sensorCode;

		boolean isSensorPresent = false;

		byte[] payload = new byte[shortPayload.length];

		payload = convertPayloadShortToPayloadByte(shortPayload);

		sensorCode = (byte) (payload[0] >> 4);

		for (int j = 0; j < sensorsList.size(); j++) {
			sensor = (Sensor) sensorsList.get(j);
			if (sensor.getCode() == sensorCode) {
				isSensorPresent = true;
			}
		}

		if (!isSensorPresent) {

			throw new SensorNotPresentException("Setup Sensor", sensorCode);
		} else {

			for (int j = 0; j < sensorCodeSetupEx.size(); j++) {
				if ((Byte) sensorCodeSetupEx.get(j) == sensorCode) {
					sensorCodeSetupEx.remove(j);
				}
			}

			//
			byte sampleTimeScale = (byte) ((payload[0] & 0x0C) >> 2);
			int sampleTime = convertTwoBytesToInt(payload, 1);
			// set sensor, sampligTime and timeScale
			setupSensor.setSensor(sensorCode);
			setupSensor.setTimeScale(sampleTimeScale);
			setupSensor.setSamplingTime(sampleTime);

			if (computeFeature.equalsIgnoreCase("ON")) {

				if (sampleTimeScale == SPINESensorConstants.SEC) {
					sampleTime = sampleTime * 1000;
				} else if (sampleTimeScale == SPINESensorConstants.MIN) {
					sampleTime = sampleTime * 60000;
				}

				int dsSampleTime = (Integer) dsSensorTimeSampling.get(sensorCode);

				float sampleRateRemainder = sampleTime % dsSampleTime;

				if (sampleRateRemainder != 0) {
					sensorCodeSetupEx.add(sensorCode);

					throw new SensorSetupException("Setup Sensor", sensorCode, sampleTimeScale, sampleTime);
				}

			}
		}

		return setupSensor;

	}

	/** Decode Setup Function message (Feature function). */
	// Manager only Feature function
	public FeatureSpineSetupFunction decodeSetupFunction(short[] shortPayload, Vector sensorsList, Vector sensorCodeSetupEx) throws FunctionNotSupportedException {
		FeatureSpineSetupFunction featureSetupFunct = new FeatureSpineSetupFunction();

		Sensor sensor;

		boolean isSensorPresent = false;

		byte sensorCode;
		byte functionCode;

		byte[] payload = new byte[shortPayload.length];

		payload = convertPayloadShortToPayloadByte(shortPayload);

		functionCode = payload[0];

		if (functionCode != SPINEFunctionConstants.FEATURE) {

			throw new FunctionNotSupportedException("Setup Function", SPINEFunctionConstants.functionCodeToString(functionCode));
		} else {

			sensorCode = (byte) (payload[2] >> 4);

			for (int j = 0; j < sensorsList.size(); j++) {
				sensor = (Sensor) sensorsList.get(j);
				if (sensor.getCode() == sensorCode) {
					isSensorPresent = true;
				}
			}

			for (int k = 0; k < sensorCodeSetupEx.size(); k++) {
				if ((Byte) sensorCodeSetupEx.get(k) == sensorCode) {
					isSensorPresent = false;
				}
			}

			if (!isSensorPresent) {

				throw new FunctionNotSupportedException("Setup Function", SPINEFunctionConstants.functionCodeToString(functionCode) + " (" + SPINESensorConstants.sensorCodeToString(sensorCode) + ")");
			} else {
				// set sensor, windowSize and shiftSize
				featureSetupFunct.setSensor(sensorCode);
				featureSetupFunct.setWindowSize((byte) payload[3]);
				featureSetupFunct.setShiftSize((byte) payload[4]);
			}
		}

		return featureSetupFunct;
	}

	/** Decode Function Request message (Feature function). */
	// Manager only Feature function
	public FeatureSpineFunctionReq decodeFunctionReq(short[] shortPayload, Vector sensorsList, Vector sensorCodeSetupEx, Hashtable functionsList) throws FunctionNotSupportedException {

		FeatureSpineFunctionReq featureFunctReq = new FeatureSpineFunctionReq();

		Vector functionParam;

		String featureNotSupportedList = "";

		Vector<Feature> featureNotSupported = new Vector<Feature>();

		Sensor sensor;

		byte sensorCode;

		byte featureCode;

		boolean isSensorPresent = false;

		boolean isFeatPresent = false;

		byte functionCode;

		byte[] payload = new byte[shortPayload.length];

		payload = convertPayloadShortToPayloadByte(shortPayload);

		functionCode = payload[0];

		if (functionCode != SPINEFunctionConstants.FEATURE) {

			throw new FunctionNotSupportedException("Function request", SPINEFunctionConstants.functionCodeToString(functionCode));
		} else {

			sensorCode = payload[3];

			for (int j = 0; j < sensorsList.size(); j++) {
				sensor = (Sensor) sensorsList.get(j);
				if (sensor.getCode() == sensorCode) {
					isSensorPresent = true;
				}
			}

			for (int k = 0; k < sensorCodeSetupEx.size(); k++) {
				if ((Byte) sensorCodeSetupEx.get(k) == sensorCode) {
					isSensorPresent = false;
				}
			}

			if (!isSensorPresent) {

				throw new FunctionNotSupportedException("Function request", SPINEFunctionConstants.functionCodeToString(functionCode) + " (" + SPINESensorConstants.sensorCodeToString(sensorCode)
						+ ")");

			} else {

				functionParam = (Vector) functionsList.get(functionCode);

				if (payload[1] == 1) {
					featureFunctReq.setActivationFlag(true);
				} else {
					featureFunctReq.setActivationFlag(false);
				}
				featureFunctReq.setSensor(sensorCode);
				for (int i = 0; i <= payload[4] * 2 - 2; i++) {
					// Feature(byte featureCode, byte channelBitmask)
					featureCode = payload[5 + i];
					if (isFeatureCodePresent(featureCode, functionParam)) {
						isFeatPresent = true;
						featureFunctReq.add(new Feature(featureCode, payload[5 + i + 1]));

					} else {
						featureNotSupported.add(new Feature(featureCode, payload[5 + i + 1]));
					}
					i++;
				}
			}

			if (featureNotSupported.size() != 0) {
				for (int j = 0; j < featureNotSupported.size(); j++) {
					featureCode = ((Feature) featureNotSupported.get(j)).getFeatureCode();
					featureNotSupportedList = featureNotSupportedList + " " + SPINEFunctionConstants.functionalityCodeToString(functionCode, featureCode);
				}
			}

			if (!isFeatPresent) {
				throw new FunctionNotSupportedException("Function request", featureNotSupportedList);

			}
		}

		return featureFunctReq;

	}

	private boolean isFeatureCodePresent(byte featureCode, Vector functionParam) {
		boolean isFeatPresent = false;

		for (int j = 0; j < functionParam.size(); j++) {
			if (featureCode == (Byte) functionParam.get(j)) {
				isFeatPresent = true;
			}
		}

		return isFeatPresent;
	}

	/**
	 * Converts the two following bytes in the array 'bytes' starting from the
	 * index 'index' into the corresponding integer
	 * 
	 * @param bytes
	 *            the byte array from where to take the 2 bytes to be converted
	 *            to an integer
	 * @param index
	 *            the starting index on the interested portion to convert
	 * 
	 * @return the converted integer
	 */
	private int convertTwoBytesToInt(byte[] bytes, int index) {
		if (bytes.length < 2)
			return 0;

		return (bytes[index + 1] & 0xFF) | ((bytes[index] & 0xFF) << 8);
	}

	private byte[] convertPayloadShortToPayloadByte(short[] payloadShort) {
		byte[] payload = new byte[payloadShort.length];
		for (int h = 0; h < payload.length; h++)
			payload[h] = (byte) payloadShort[h];
		return payload;
	}

}
