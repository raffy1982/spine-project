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

import javax.swing.JScrollPane;
import javax.swing.JTable;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;
import spine.datamodel.AlarmData;
import spine.datamodel.Data;
import spine.datamodel.Feature;
import spine.datamodel.FeatureData;
import spine.datamodel.StepCounterData;

/**
 * Node Monitored Info panel.
 * 
 * @author Luigi Buondonno : luigi.buondonno@gmail.com
 * @author Antonio Giordano : antoniogior@hotmail.com
 * 
 * @version 1.0
 */

public class NodeMonitoredInfoPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 1L;

	private Data data;

	private JTable jstatusTable;

	/**
	 * Node Monitored Info panel.
	 * 
	 */
	public NodeMonitoredInfoPanel(Data d) {
		super();
		this.data = d;
		initGUI();
	}

	private void initGUI() {
		try {
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);

			{
				Feature[] feat = null;
				int nodeId = 0;
				if (data instanceof FeatureData) {
					FeatureData f = (FeatureData) data;
					feat = f.getFeatures();
					nodeId = f.getNode().getPhysicalID().getAsInt();
				}
				if (data instanceof AlarmData) {
					AlarmData f = (AlarmData) data;
					feat = new Feature[1];
					feat[0] = new Feature();
					feat[0].setFeatureCode(f.getDataType());
					feat[0].setChannelBitmask(f.getValueType());
					nodeId = f.getNode().getPhysicalID().getAsInt();
				}

				if (data instanceof StepCounterData) {
					StepCounterData f = (StepCounterData) data;
					feat = new Feature[1];
					feat[0] = new Feature();
					feat[0].setFeatureCode(SPINEFunctionConstants.RAW_DATA);
					feat[0].setChannelBitmask(SPINESensorConstants.CH3_ONLY);
					nodeId = f.getNode().getPhysicalID().getAsInt();
				}

				String[][] mod = new String[feat.length][3];
				Vector<Vector<String>> model = new Vector<Vector<String>>();
				int cont = 0;
				for (Feature ff : feat) {
					String[] riga = new String[3];
					Vector<String> r = new Vector<String>();
					r.add(nodeId + "");
					riga[0] = nodeId + "";
					r.add(SPINEFunctionConstants.functionalityCodeToString(SPINEFunctionConstants.FEATURE, ff.getFeatureCode()));
					riga[1] = SPINEFunctionConstants.functionalityCodeToString(SPINEFunctionConstants.FEATURE, ff.getFeatureCode());
					r.add(SPINESensorConstants.channelBitmaskToString(ff.getChannelBitmask()));
					riga[2] = SPINESensorConstants.channelBitmaskToString(ff.getChannelBitmask());
					mod[cont++] = riga;
					model.add(r);
				}
				Vector<String> names = new Vector<String>();
				String name[] = new String[] { "Node ID", "Feature code", "Act Channel" };
				for (String na : name) {
					names.add(na);
				}

				jstatusTable = new JTable(model, names);
				jstatusTable.setAutoCreateColumnsFromModel(true);

				JScrollPane sp = new JScrollPane(jstatusTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

				this.add(sp);

				jstatusTable.setAutoCreateRowSorter(true);
				jstatusTable.setEnabled(false);
				jstatusTable.setCellSelectionEnabled(true);

				jstatusTable.setColumnSelectionAllowed(true);
				jstatusTable.setFillsViewportHeight(true);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
