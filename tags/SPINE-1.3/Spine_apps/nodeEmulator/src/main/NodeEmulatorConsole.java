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

package main;

import logic.Command;
import logic.VirtualNode;
import gui.NodeEmulatorGUI;

/**
 * Node Emulator - application to emulate a SPINE sensor node (run with console
 * command).
 *  
 * Parameters (no case sensitive and no positional): 
 * dataSetFile: dataSet path and fileName (OPTIONAL) 
 * connectToWSN: true or false (OPTIONAL default false)
 * computeFeature: ON or OFF (OPTIONAL default compute_feature in configuration.properties) 
 * labelAlgorithm: ALLWITHFREQ or MOREFREQ or LAST (OPTIONAL default label_algorithm in configuration.properties)
 * 
 * @author Alessia Salmeri
 * 
 * @version 1.0
 * 
 */

public class NodeEmulatorConsole {

	public static void main(String[] args) {
		String dataSetFile = null;
		boolean connectToWSN = false;
		String computeFeature = null;
		String labelAlgorithm = null;
		String paramName;
		String paramValue;

		Command c = VirtualNode.getInstance();
		NodeEmulatorGUI instance = new NodeEmulatorGUI(c);
		instance.setLocationRelativeTo(null);
		instance.setVisible(true);
		VirtualNode vNode = (VirtualNode) c;
		((VirtualNode) vNode).addObserver(instance);

		for (int j = 0; j < args.length; j++) {
			System.out.println(args[j]);
			paramName = args[j].substring(0, args[j].indexOf("="));
			paramValue = args[j].substring(args[j].indexOf("=") + 1, args[j].length());
			if (paramName.equalsIgnoreCase("dataSetFile")) {
				dataSetFile = paramValue;
			} else if (paramName.equalsIgnoreCase("connectToWSN")) {
				connectToWSN = Boolean.parseBoolean(paramValue);
			} else if (paramName.equalsIgnoreCase("computeFeature")) {
				computeFeature = paramValue;
			} else if (paramName.equalsIgnoreCase("labelAlgorithm")) {
				labelAlgorithm = paramValue;
			}
		}
		if (computeFeature != null) {
			if (computeFeature.equalsIgnoreCase("ON") || computeFeature.equalsIgnoreCase("OFF")) {
				vNode.computeFeature = computeFeature;
			}
		}
		if (labelAlgorithm != null) {
			if (labelAlgorithm.equalsIgnoreCase("ALLWITHFREQ") || labelAlgorithm.equalsIgnoreCase("MOREFREQ") || labelAlgorithm.equalsIgnoreCase("LAST")) {
				vNode.labelAlgorithm = labelAlgorithm;
			}
		}

		if (dataSetFile != null) {
			vNode.connectToWSN = connectToWSN;
			vNode.loadDataSensor(dataSetFile);
		}
	}
}
