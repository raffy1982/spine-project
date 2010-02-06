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

import java.util.Vector;
import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;
import spine.datamodel.Feature;
import spine.datamodel.functions.FeatureSpineFunctionReq;
import spine.datamodel.functions.FeatureSpineSetupFunction;
import spine.datamodel.functions.SpineSetupSensor;

/**
 * Utility methods to obtain string representation of the SPINE object.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */

public class Utils {

	/**
	 * Returns a string representation of this SpineSetupSensor object (with
	 * sensorCode).
	 * 
	 * @return the String representation of this SpineSetupSensor object (with
	 *         sensorCode)
	 */
	public static String setupSensorsToStringWithCode(
			SpineSetupSensor setupSensor) {
		String s = "Sensor_Setup: ";

		s += "sensor="
				+ SPINESensorConstants.sensorCodeToString(setupSensor
						.getSensor()) + "(" + setupSensor.getSensor() + "), ";
		s += "timeScale="
				+ SPINESensorConstants.timeScaleToString(setupSensor
						.getTimeScale()) + ", ";
		s += "samplingTime=" + setupSensor.getSamplingTime();

		return s;
	}

	/**
	 * 
	 * Returns a string representation of the FeatureSpineSetupFunction object
	 * (with sensorCode).
	 * 
	 */
	public static String setupFunciontStringWithCode(
			FeatureSpineSetupFunction setupFunction) {
		String s = "Feature_Setup: ";

		s += "sensor="
				+ SPINESensorConstants.sensorCodeToString(setupFunction
						.getSensor()) + "(" + setupFunction.getSensor() + "), ";
		s += "window=" + setupFunction.getWindowSize() + ", ";
		s += "shift =" + setupFunction.getShiftSize();

		return s;
	}

	/**
	 * 
	 * Returns a string representation of the FeatureSpineFunctionReq object
	 * (with sensorCode, featureCode and channelCode).
	 * 
	 */
	public static String setupFeaturetoStringWithCode(
			FeatureSpineFunctionReq setupFeature) {

		byte featureCode;
		Vector features = new Vector();

		features = setupFeature.getFeatures();

		String s = "Feature_";

		s += (setupFeature.getActivationFlag()) ? "Activation: "
				: "Deactivation: ";

		s += "sensor="
				+ SPINESensorConstants.sensorCodeToString(setupFeature
						.getSensor()) + "(" + setupFeature.getSensor() + "), ";

		for (int i = 0; i < setupFeature.getFeatures().size(); i++) {
			featureCode = ((Feature) features.elementAt(i)).getFeatureCode();
			s += "feature="
					+ SPINEFunctionConstants.functionalityCodeToString(
							SPINEFunctionConstants.FEATURE, featureCode) + "{"
					+ featureCode + "} ";
			s += "channels="
					+ SPINESensorConstants.channelBitmaskToString(
							((Feature) features.elementAt(i))
									.getChannelBitmask()).replace(",", "")
					+ channelBitmaskToCode(((Feature) features.elementAt(i))
							.getChannelBitmask());
			if (i < features.size() - 1)
				s += ", ";
		}
		return s;
	}

	public static String channelBitmaskToCode(byte channelBitmask) {

		switch (channelBitmask) {
		case SPINESensorConstants.ALL:
			return "[" + SPINESensorConstants.CH1 + " "
					+ SPINESensorConstants.CH2 + " " + SPINESensorConstants.CH3
					+ " " + SPINESensorConstants.CH4 + "]";
		case SPINESensorConstants.NONE:
			return "[]";

		case SPINESensorConstants.CH1_ONLY:
			return "[" + SPINESensorConstants.CH1 + "]";
		case SPINESensorConstants.CH1_CH2_ONLY:
			return "[" + SPINESensorConstants.CH1 + " "
					+ SPINESensorConstants.CH2 + "]";
		case SPINESensorConstants.CH1_CH2_CH3_ONLY:
			return "[" + SPINESensorConstants.CH1 + " "
					+ SPINESensorConstants.CH2 + " " + SPINESensorConstants.CH3
					+ "]";
		case SPINESensorConstants.CH1_CH2_CH4_ONLY:
			return "[" + SPINESensorConstants.CH1 + " "
					+ SPINESensorConstants.CH2 + SPINESensorConstants.CH4 + "]";
		case SPINESensorConstants.CH1_CH3_ONLY:
			return "[" + SPINESensorConstants.CH1 + " "
					+ SPINESensorConstants.CH3 + "]";
		case SPINESensorConstants.CH1_CH3_CH4_ONLY:
			return "[" + SPINESensorConstants.CH1 + " "
					+ SPINESensorConstants.CH3 + " " + SPINESensorConstants.CH4
					+ "]";
		case SPINESensorConstants.CH1_CH4_ONLY:
			return "[" + SPINESensorConstants.CH1 + " "
					+ SPINESensorConstants.CH4 + "]";

		case SPINESensorConstants.CH2_ONLY:
			return "[" + SPINESensorConstants.CH2 + "]";
		case SPINESensorConstants.CH2_CH3_ONLY:
			return "[" + SPINESensorConstants.CH2 + " "
					+ SPINESensorConstants.CH3 + "]";
		case SPINESensorConstants.CH2_CH3_CH4_ONLY:
			return "[" + SPINESensorConstants.CH2 + " "
					+ SPINESensorConstants.CH3 + " " + SPINESensorConstants.CH4
					+ "]";
		case SPINESensorConstants.CH2_CH4_ONLY:
			return "[" + SPINESensorConstants.CH2 + SPINESensorConstants.CH4
					+ "]";

		case SPINESensorConstants.CH3_ONLY:
			return "[" + SPINESensorConstants.CH3 + "]";
		case SPINESensorConstants.CH3_CH4_ONLY:
			return "[" + SPINESensorConstants.CH3 + " "
					+ SPINESensorConstants.CH4 + "]";

		case SPINESensorConstants.CH4_ONLY:
			return "[" + SPINESensorConstants.CH4 + "]";
		default:
			return "?";
		}
	}

}
