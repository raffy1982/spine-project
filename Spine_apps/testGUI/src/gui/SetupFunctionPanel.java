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
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;
import spine.datamodel.Node;
import spine.datamodel.Sensor;
import spine.datamodel.functions.AlarmSpineSetupFunction;
import spine.datamodel.functions.FeatureSpineSetupFunction;
import spine.datamodel.functions.Function;
//Used in STEP_COUNTER
//import spine.datamodel.functions.StepCounterSpineSetupFunction;

/**
 * Setup Function panel.
 * 
 * @author Luigi Buondonno : luigi.buondonno@gmail.com
 * @author Antonio Giordano : antoniogior@hotmail.com
 * 
 * @version 1.0
 */

public class SetupFunctionPanel extends javax.swing.JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JPanel jPanelNord;

	private JPanel jPanelCenter;

	private JButton ExitButton;

	private JPanel jPanelNodeInfo;

	private JLabel sensorCodeLabel;

	private JLabel typeLabel;

	private JLabel sampleTimeLabel;

	private JButton jButtonConfirm;

	private JPanel jPanelSensorSetting;

	private SpineGUI sg;

	private JTextField windowTextField;

	private JTextField shiftTextField;

	private JLabel shiftLabel;

	private Node node;

	private JScrollPane jScrollPane;

	private JComboBox jSensorCodeComboBox;

	private JComboBox jFunctionCodeComboBox;

	private JTextArea jNodeInfoTextArea;

	/**
	 * Setup Function panel.
	 * 
	 */

	public SetupFunctionPanel(SpineGUI sg, Node n) {
		super();
		this.node = n;
		this.sg = sg;
		initGUI();
	}

	private void initGUI() {
		try {
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(587, 413));
			{
				jPanelNord = new JPanel();
				URL imageURL = SetupFunctionPanel.class.getResource("../img/SetupFunction.gif");
				System.out.println(imageURL);
				jPanelNord.setBorder(new MatteBorder(new ImageIcon(imageURL)));
				this.add(jPanelNord, BorderLayout.NORTH);
				jPanelNord.setPreferredSize(new java.awt.Dimension(587, 57));
				jPanelNord.setVisible(true);
			}
			{
				jPanelCenter = new JPanel();
				GroupLayout jPanelCenterLayout = new GroupLayout((JComponent) jPanelCenter);
				jPanelCenter.setLayout(jPanelCenterLayout);
				this.add(jPanelCenter, BorderLayout.CENTER);
				jPanelCenter.setPreferredSize(new java.awt.Dimension(587, 249));
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
					GridLayout jPanelSensorSettingLayout = new GridLayout(5, 2);
					jPanelSensorSetting.setLayout(jPanelSensorSettingLayout);
					jPanelSensorSetting.setBorder(BorderFactory.createTitledBorder("Function Setting"));
					{
						sampleTimeLabel = new JLabel();
						jPanelSensorSetting.add(sampleTimeLabel);
						sampleTimeLabel.setText("Function Code");
					}
					{
						Vector fl = node.getFunctionsList();
						String f[] = new String[fl.size()];
						for (int i = 0; i < fl.size(); i++) {
							f[i] = SPINEFunctionConstants.functionCodeToString(((Function) fl.get(i)).functionCode);
						}
						ComboBoxModel jFunctionCodeComboBoxModel = new DefaultComboBoxModel(f);
						jFunctionCodeComboBox = new JComboBox();
						jPanelSensorSetting.add(jFunctionCodeComboBox);
						jFunctionCodeComboBox.setModel(jFunctionCodeComboBoxModel);
						jFunctionCodeComboBox.addActionListener(this);
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
						typeLabel.setText("Window");
					}
					{
						windowTextField = new JTextField();
						jPanelSensorSetting.add(windowTextField);
					}
					{
						shiftLabel = new JLabel();
						jPanelSensorSetting.add(shiftLabel);
						shiftLabel.setText("Shift");
					}
					{
						shiftTextField = new JTextField();
						jPanelSensorSetting.add(shiftTextField);
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
						jPanelCenterLayout.createParallelGroup().addComponent(jPanelNodeInfo, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 495, GroupLayout.PREFERRED_SIZE).addComponent(
								jPanelSensorSetting, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 495, GroupLayout.PREFERRED_SIZE).addGroup(
								GroupLayout.Alignment.LEADING,
								jPanelCenterLayout.createSequentialGroup().addGap(101).addComponent(jButtonConfirm, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE).addGap(103)
										.addComponent(ExitButton, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE).addGap(88))).addContainerGap(44, 44));
				jPanelCenterLayout.setVerticalGroup(jPanelCenterLayout.createSequentialGroup().addContainerGap().addComponent(jPanelNodeInfo, GroupLayout.PREFERRED_SIZE, 133,
						GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanelSensorSetting, 0, 156, Short.MAX_VALUE).addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED).addGroup(
						jPanelCenterLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jButtonConfirm, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(ExitButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)).addContainerGap(22, 22));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String func = (String) jFunctionCodeComboBox.getSelectedItem();

		if (func.equalsIgnoreCase(SPINEFunctionConstants.STEP_COUNTER_LABEL) || func.equalsIgnoreCase(SPINEFunctionConstants.BUFFERED_RAW_DATA_LABEL)) {
			//typeLabel.setText("Avg Acceleration");
			//shiftLabel.setText("Step Threshold");
			typeLabel.setVisible(false);
			shiftLabel.setVisible(false);
			windowTextField.setVisible(false);
			shiftTextField.setVisible(false);

		} else {
			//typeLabel.setText("Window");
			//shiftLabel.setText("Shift");
			typeLabel.setVisible(true);
			shiftLabel.setVisible(true);
			windowTextField.setVisible(true);
			shiftTextField.setVisible(true);
		}
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(ExitButton)) {
			sg.closeFrame();
		}

		if (e.getSource().equals(jButtonConfirm)) {

			String funcCode = (String) jFunctionCodeComboBox.getSelectedItem();
			int fCode = SPINEFunctionConstants.functionCodeByString(funcCode);

			try {
				byte sensor = SPINESensorConstants.sensorCodeByString((String) jSensorCodeComboBox.getSelectedItem());

				switch (fCode) {

				case SPINEFunctionConstants.FEATURE: {
					short windowSize = (short) Integer.parseInt(windowTextField.getText());
					short shiftSize = (short) Integer.parseInt(shiftTextField.getText());
					FeatureSpineSetupFunction fs = new FeatureSpineSetupFunction();
					fs.setSensor(sensor);
					fs.setShiftSize(shiftSize);
					fs.setWindowSize(windowSize);
					int res = JOptionPane.showConfirmDialog(this, "Are you sure about the parameters? \n" + fs, "Confirm", JOptionPane.YES_NO_OPTION);
					if (res == JOptionPane.OK_OPTION) {
						sg.setupFunction(node, fs);
						sg.closeFrame();
						return;
					}
					break;
				}

				case SPINEFunctionConstants.ALARM: {
					short windowSize = (short) Integer.parseInt(windowTextField.getText());
					short shiftSize = (short) Integer.parseInt(shiftTextField.getText());
					AlarmSpineSetupFunction al = new AlarmSpineSetupFunction();
					al.setSensor(sensor);
					al.setShiftSize(shiftSize);
					al.setWindowSize(windowSize);
					int res = JOptionPane.showConfirmDialog(this, "Are you sure about the parameters? \n" + al, "Confirm", JOptionPane.YES_NO_OPTION);
					if (res == JOptionPane.OK_OPTION) {
						sg.setupFunction(node, al);
						sg.closeFrame();
						return;
					}

					break;
				}

				/* NOT YET SUPPORTED
				case SPINEFunctionConstants.STEP_COUNTER: {

					StepCounterSpineSetupFunction al = new StepCounterSpineSetupFunction();
					int res = JOptionPane.showConfirmDialog(this, "Are you sure about the parameters? \n" + al, "Confirm", JOptionPane.YES_NO_OPTION);
					if (res == JOptionPane.OK_OPTION) {
						sg.setupFunction(node, al);
						sg.closeFrame();
						return;
					}
					typeLabel.setText("Window");
					shiftLabel.setText("Shift");
					break;
				}*/

				default:
					JOptionPane.showMessageDialog(this, SPINEFunctionConstants.functionCodeToString((byte)fCode) + ": function not supported", "Error", JOptionPane.ERROR_MESSAGE);
					break;
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Data Error", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

		}

		if (e.getSource().equals(jFunctionCodeComboBox)) {
			String func = (String) jFunctionCodeComboBox.getSelectedItem();

			if (func.equalsIgnoreCase(SPINEFunctionConstants.STEP_COUNTER_LABEL) || func.equalsIgnoreCase(SPINEFunctionConstants.BUFFERED_RAW_DATA_LABEL)) {
				//typeLabel.setText("Avg Acceleration");
				//shiftLabel.setText("Step Threshold");
				typeLabel.setVisible(false);
				shiftLabel.setVisible(false);
				windowTextField.setVisible(false);
				shiftTextField.setVisible(false);

			} else {
				//typeLabel.setText("Window");
				//shiftLabel.setText("Shift");
				typeLabel.setVisible(true);
				shiftLabel.setVisible(true);
				windowTextField.setVisible(true);
				shiftTextField.setVisible(true);
			}
		}

	}

}
