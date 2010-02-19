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

package gui;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;
import spine.datamodel.Node;
import spine.datamodel.Sensor;
import spine.datamodel.functions.FeatureFunction;
import spine.datamodel.functions.Function;

/**
 * NodeInfoPanel.
 * 
 * @author Luigi Buondonno : luigi.buondonno@gmail.com
 * @author Antonio Giordano : antoniogior@hotmail.com
 * 
 * @version 1.0
 */

public class NodeInfoPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 1L;

	private JPanel jSensorPanel;

	private JScrollPane jScrollPane;

	private JList jFunctionsList;

	private JTabbedPane jFeatureTabbedPane;

	private JList jListSensor;

	private JPanel jFunctionPanel;

	private Node node;

	private JScrollPane jScrollPaneFunc;

	/**
	 * Node Info panel.
	 * 
	 */
	public NodeInfoPanel(Node n) {
		super();
		this.node = n;
		initGUI();
	}

	private void initGUI() {
		try {
			this.setPreferredSize(new java.awt.Dimension(253, 355));
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			this.setBorder(BorderFactory.createTitledBorder("Node Info"));
			{
				jSensorPanel = new JPanel();
				BorderLayout jSensorPanelLayout = new BorderLayout();
				jSensorPanel.setLayout(jSensorPanelLayout);
				this.add(jSensorPanel, BorderLayout.NORTH);
				jSensorPanel.setPreferredSize(new java.awt.Dimension(243, 96));
				jSensorPanel.setBorder(BorderFactory.createTitledBorder("Sensor"));
				{
					jScrollPane = new JScrollPane();
					jSensorPanel.add(jScrollPane, BorderLayout.CENTER);
					{
						Vector sensor = node.getSensorsList();
						String[] s = new String[sensor.size()];
						for (int i = 0; i < sensor.size(); i++) {
							s[i] = SPINESensorConstants.sensorCodeToString(((Sensor) sensor.get(i)).getCode());
						}
						ListModel jListSensorModel = new DefaultComboBoxModel(s);
						jListSensor = new JList();
						jListSensor.setModel(jListSensorModel);
						jScrollPane.setViewportView(jListSensor);
					}
				}
			}

			jFunctionPanel = new JPanel();
			BorderLayout jFunctionPanelLayout = new BorderLayout();
			jFunctionPanel.setLayout(jFunctionPanelLayout);
			this.add(jFunctionPanel, BorderLayout.CENTER);
			jFunctionPanel.setPreferredSize(new java.awt.Dimension(243, 137));
			jFunctionPanel.setBorder(BorderFactory.createTitledBorder("Function "));

			jScrollPaneFunc = new JScrollPane();
			jFunctionPanel.add(jScrollPaneFunc, BorderLayout.CENTER);

			Vector fl = node.getFunctionsList();
			String f[] = new String[fl.size()];
			for (int i = 0; i < fl.size(); i++) {
				f[i] = SPINEFunctionConstants.functionCodeToString(((Function) fl.get(i)).functionCode);

				ListModel jFunctionsListModel = new DefaultComboBoxModel(f);
				jFunctionsList = new JList();
				jFunctionsList.setModel(jFunctionsListModel);
				jScrollPaneFunc.setViewportView(jFunctionsList);
			}
			{
				jFeatureTabbedPane = new JTabbedPane();
				this.add(jFeatureTabbedPane, BorderLayout.SOUTH);
				jFeatureTabbedPane.setPreferredSize(new java.awt.Dimension(243, 132));
				jFeatureTabbedPane.setBorder(BorderFactory.createTitledBorder("Feature"));

				for (int i = 0; i < f.length; i++) {
					if (((Function) fl.get(i)).functionCode == SPINEFunctionConstants.FEATURE) {
						FeatureFunction vec = (FeatureFunction) fl.get(i);
						String[] fc = new String[vec.getFeatures().size()];
						for (int k = 0; k < fc.length; k++) {
							fc[k] = SPINEFunctionConstants.functionalityCodeToString(SPINEFunctionConstants.FEATURE, SPINEFunctionConstants.functionalityCodeByString(
									SPINEFunctionConstants.FEATURE_LABEL, ((String) vec.getFeatures().get(k))));
						}
						jFeatureTabbedPane.add(f[i], new JScrollPane(new JList(fc)));
					} else {
						jFeatureTabbedPane.add(f[i], new JScrollPane((new JList())));

					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
