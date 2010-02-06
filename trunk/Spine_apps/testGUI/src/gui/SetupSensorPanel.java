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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import spine.SPINESensorConstants;
import spine.datamodel.Node;
import spine.datamodel.Sensor;
import spine.datamodel.functions.SpineSetupSensor;

/**
 * Setup Sensor Panel.
 * 
 * @author Luigi Buondonno : luigi.buondonno@gmail.com
 * @author Antonio Giordano : antoniogior@hotmail.com
 * 
 * @version 1.0
 */

public class SetupSensorPanel extends javax.swing.JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JPanel jPanelNord;

	private JPanel jPanelCenter;

	private JButton ExitButton;

	private JScrollPane jScrollPane;

	private JTextArea jNodeInfoTextArea;

	private JComboBox jSensorCodeComboBox;

	private JPanel jPanelNodeInfo;

	private JLabel sensorCodeLabel;

	private JComboBox typeComboBox;

	private JLabel typeLabel;

	private JTextField sampleTimeTextField;

	private JLabel sampleTimeLabel;

	private JButton jButtonConfirm;

	private JPanel jPanelSensorSetting;

	private SpineGUI sg;

	private Node node;

	/**
	 * Setup Sensor Panel.
	 * 
	 */

	public SetupSensorPanel(SpineGUI sg, Node n) {
		super();
		this.sg = sg;
		this.node = n;
		initGUI();
	}

	private void initGUI() {
		try {
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(631, 349));
			{
				jPanelNord = new JPanel();
				URL imageURL = SetupFunctionPanel.class.getResource("/img/SetupSensor.gif");
				System.out.println(imageURL);
				jPanelNord.setBorder(new MatteBorder(new ImageIcon(imageURL)));
				this.add(jPanelNord, BorderLayout.NORTH);
				jPanelNord.setPreferredSize(new java.awt.Dimension(631, 57));
				jPanelNord.setVisible(true);
			}
			{
				jPanelCenter = new JPanel();
				GroupLayout jPanelCenterLayout = new GroupLayout((JComponent) jPanelCenter);
				jPanelCenter.setLayout(jPanelCenterLayout);
				this.add(jPanelCenter, BorderLayout.CENTER);
				{
					jPanelNodeInfo = new JPanel();
					BorderLayout jPanelNodeInfoLayout = new BorderLayout();
					jPanelNodeInfo.setLayout(jPanelNodeInfoLayout);
					jPanelNodeInfo.setBorder(BorderFactory.createTitledBorder(null, "Node Info", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
					{
						jScrollPane = new JScrollPane();
						jPanelNodeInfo.add(jScrollPane, BorderLayout.CENTER);
						{
							jNodeInfoTextArea = new JTextArea();
							jScrollPane.setViewportView(jNodeInfoTextArea);
							jNodeInfoTextArea.setEditable(false);
							jNodeInfoTextArea.setText(node.toString());
						}
					}
				}
				{
					jPanelSensorSetting = new JPanel();
					GridLayout jPanelSensorSettingLayout = new GridLayout(3, 2);
					jPanelSensorSettingLayout.setHgap(5);
					jPanelSensorSettingLayout.setVgap(5);
					jPanelSensorSettingLayout.setColumns(1);
					jPanelSensorSetting.setLayout(jPanelSensorSettingLayout);
					jPanelSensorSetting.setBorder(BorderFactory.createTitledBorder("Sensor Setting"));
					{
						sampleTimeLabel = new JLabel();
						jPanelSensorSetting.add(sampleTimeLabel);
						sampleTimeLabel.setText("Sample Time");
					}
					{
						sampleTimeTextField = new JTextField();
						jPanelSensorSetting.add(sampleTimeTextField);
					}
					{
						sensorCodeLabel = new JLabel();
						jPanelSensorSetting.add(sensorCodeLabel);
						sensorCodeLabel.setText("Sensor Code");
					}
					{
						Vector sensor = node.getSensorsList();
						String[] s = new String[sensor.size()];
						for (int i = 0; i < sensor.size(); i++) {
							s[i] = SPINESensorConstants.sensorCodeToString(((Sensor) sensor.get(i)).getCode());
						}
						ComboBoxModel jSensorCodeComboBoxModel = new DefaultComboBoxModel(s);
						jSensorCodeComboBox = new JComboBox();
						jPanelSensorSetting.add(jSensorCodeComboBox);
						jSensorCodeComboBox.setModel(jSensorCodeComboBoxModel);
					}
					{
						typeLabel = new JLabel();
						jPanelSensorSetting.add(typeLabel);
						typeLabel.setText("Time Type");
					}
					{
						ComboBoxModel typeComboBoxModel = new DefaultComboBoxModel(new String[] { "ms", "sec", "min" });
						typeComboBox = new JComboBox();
						jPanelSensorSetting.add(typeComboBox);
						typeComboBox.setModel(typeComboBoxModel);
					}
				}
				{
					ExitButton = new JButton();
					ExitButton.setText("Exit");
					ExitButton.addActionListener(this);
				}
				{
					jButtonConfirm = new JButton();
					jButtonConfirm.setText("OK");
					jButtonConfirm.addActionListener(this);
				}
				jPanelCenterLayout.setHorizontalGroup(jPanelCenterLayout.createSequentialGroup().addContainerGap(48, 48).addGroup(
						jPanelCenterLayout.createParallelGroup().addComponent(jPanelNodeInfo, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 536, GroupLayout.PREFERRED_SIZE).addComponent(
								jPanelSensorSetting, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 536, GroupLayout.PREFERRED_SIZE).addGroup(
								GroupLayout.Alignment.LEADING,
								jPanelCenterLayout.createSequentialGroup().addGap(111).addComponent(jButtonConfirm, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE).addGap(87)
										.addComponent(ExitButton, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE).addGap(135))).addContainerGap(47, 47));
				jPanelCenterLayout.setVerticalGroup(jPanelCenterLayout.createSequentialGroup().addComponent(jPanelNodeInfo, 0, 139, Short.MAX_VALUE).addPreferredGap(
						LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jPanelSensorSetting, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE).addPreferredGap(
						LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
						jPanelCenterLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jButtonConfirm, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(ExitButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)).addContainerGap());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(ExitButton)) {
			sg.closeFrame();
		}

		if (e.getSource().equals(jButtonConfirm)) {

			try {

				SpineSetupSensor ss = new SpineSetupSensor();
				int samplingTime = Integer.parseInt(sampleTimeTextField.getText());
				ss.setSamplingTime(samplingTime);

				// byte sensor;
				ss.setSensor(SPINESensorConstants.sensorCodeByString((String) jSensorCodeComboBox.getSelectedItem()));

				// byte timeScale;
				String type = (String) typeComboBox.getSelectedItem();
				ss.setTimeScale(SPINESensorConstants.timeScaleByString(type));

				int res = JOptionPane.showConfirmDialog(this, "Are you sure about the parameters?  \n" + ss, "Confirm", JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.OK_OPTION) {
					sg.setupSensor(node, ss);
					sg.closeFrame();
					return;
				}

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Data Error", "Error", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				return;
			}
		}

	}

}
