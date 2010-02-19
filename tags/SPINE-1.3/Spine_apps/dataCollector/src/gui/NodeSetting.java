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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import javax.swing.WindowConstants;

import spine.SPINEFunctionConstants;
import spine.SPINESensorConstants;
import spine.datamodel.Feature;
import spine.datamodel.Node;
import spine.datamodel.Sensor;
import spine.datamodel.functions.FeatureSpineFunctionReq;
import spine.datamodel.functions.FeatureSpineSetupFunction;
import spine.datamodel.functions.SpineSetupSensor;

/**
 * Dialog box to set node configuration.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */

public class NodeSetting extends javax.swing.JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JPanel function_jPanel;

	private JComboBox minComboBox;

	private JCheckBox MaxCheckBox;

	private JComboBox modeComboBox;

	private JPanel noswInfo_jPanel;

	private JTextPane nodeInfo_jTextPane;

	private JScrollPane nodeInfo_jScrollPane;

	private JTextField shift_jTextField;

	private JTextField window_jTextField;

	private JButton exit_jButton;

	private JButton ok_jButton;

	private JPanel com_jPanel;

	private JLabel shift_jLabel;

	private JLabel window_jLabel;

	private JComboBox functionCode_jComboBox;

	private JLabel funcCode_jLabel;

	private JComboBox typeComboBox;

	private JLabel typeLabel;

	private JTextField sampleTimeTextField;

	private JLabel sampleTimeLabel;

	private JPanel jPanelSensorSetting;

	private JComboBox sensorCode_jComboBox;

	private JLabel sensorCode_jLabel;

	private JPanel sensorType_jPanel;

	private JComboBox totEneComboBox;

	private JCheckBox TotEnergyCheckBox;

	private JComboBox vectMagniComboBox;

	private JCheckBox vectorMagnitudeCheckBox;

	private JComboBox pitchComboBox;

	private JCheckBox pitchCheckBox;

	private JComboBox medianaComboBox;

	private JCheckBox MedianCheckBox;

	private JCheckBox ModeCheckBox;

	private JComboBox varianceComboBox;

	private JCheckBox varianceCheckBox;

	private JComboBox stdDevComboBox;

	private JCheckBox stdDevCheckBox;

	private JComboBox rmsComboBox;

	private JCheckBox RmsCheckBox;

	private JComboBox amplitudeComboBox;

	private JCheckBox AmplitudeCheckBox;

	private JComboBox meanComboBox;

	private JCheckBox MeanCheckBox;

	private JComboBox rangeComboBox;

	private JCheckBox RangeCheckBox;

	private JComboBox maxComboBox;

	private JCheckBox minCheckBox;

	private JComboBox rawDataComboBox;

	private JCheckBox RawDataCheckBox;

	private JPanel jPanelFeature;

	private JScrollPane jScrollPaneFeature;

	private JPanel setting_jPanel;

	private DataCollectorGUI sg;

	private Node node;

	/** Construct a Dialog box to set node configuration. */
	public NodeSetting(DataCollectorGUI sg, Node n) {
		super("Node: " + n.getPhysicalID().toString());
		this.node = n;
		this.sg = sg;
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			{
				noswInfo_jPanel = new JPanel();
				getContentPane().add(noswInfo_jPanel, BorderLayout.NORTH);
				noswInfo_jPanel.setBorder(BorderFactory
						.createTitledBorder("Node Info"));
				{

					nodeInfo_jScrollPane = new JScrollPane();
					noswInfo_jPanel.add(nodeInfo_jScrollPane);
					{
						nodeInfo_jTextPane = new JTextPane();
						nodeInfo_jScrollPane
								.setViewportView(nodeInfo_jTextPane);
						nodeInfo_jTextPane
								.setPreferredSize(new java.awt.Dimension(519,
										75));
						nodeInfo_jTextPane.setBackground(new java.awt.Color(
								255, 255, 255));
						nodeInfo_jTextPane.setText(node.toString());
						nodeInfo_jTextPane.setEditable(false);
					}

				}
			}
			{
				setting_jPanel = new JPanel();
				getContentPane().add(setting_jPanel, BorderLayout.CENTER);
				setting_jPanel.setLayout(null);
				setting_jPanel
						.setPreferredSize(new java.awt.Dimension(531, 419));
				{
					sensorType_jPanel = new JPanel();
					BorderLayout sensorType_jPanelLayout = new BorderLayout();
					sensorType_jPanel.setLayout(sensorType_jPanelLayout);
					setting_jPanel.add(sensorType_jPanel);
					sensorType_jPanel.setBounds(12, 5, 502, 24);
					{
						sensorCode_jLabel = new JLabel();
						FlowLayout sensorCode_jLabelLayout = new FlowLayout();
						sensorCode_jLabel.setLayout(sensorCode_jLabelLayout);
						sensorType_jPanel.add(sensorCode_jLabel,
								BorderLayout.WEST);
						sensorCode_jLabel.setText("Sensor Code");
						sensorCode_jLabel
								.setPreferredSize(new java.awt.Dimension(229,
										24));
					}
					{
						Vector sensor = node.getSensorsList();
						String[] s = new String[sensor.size()];
						for (int i = 0; i < sensor.size(); i++) {
							s[i] = SPINESensorConstants
									.sensorCodeToString(((Sensor) sensor.get(i))
											.getCode());
						}
						ComboBoxModel jSensorCodeComboBoxModel = new DefaultComboBoxModel(
								s);
						sensorCode_jComboBox = new JComboBox();
						sensorType_jPanel.add(sensorCode_jComboBox,
								BorderLayout.EAST);
						sensorCode_jComboBox.setModel(jSensorCodeComboBoxModel);
						sensorCode_jComboBox
								.setPreferredSize(new java.awt.Dimension(209,
										41));
					}
				}
				{
					jPanelSensorSetting = new JPanel();
					setting_jPanel.add(jPanelSensorSetting);
					GridLayout jPanelSensorSettingLayout = new GridLayout(2, 1);
					jPanelSensorSettingLayout.setHgap(5);
					jPanelSensorSettingLayout.setVgap(5);
					jPanelSensorSettingLayout.setColumns(1);
					jPanelSensorSettingLayout.setRows(2);
					jPanelSensorSetting.setBorder(BorderFactory
							.createTitledBorder("Sensor Setting"));
					jPanelSensorSetting.setLayout(jPanelSensorSettingLayout);
					jPanelSensorSetting.setBounds(7, 35, 512, 73);
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
						typeLabel = new JLabel();
						jPanelSensorSetting.add(typeLabel);
						typeLabel.setText("Time Type");
					}
					{
						ComboBoxModel typeComboBoxModel = new DefaultComboBoxModel(
								new String[] { "ms", "sec", "min" });
						typeComboBox = new JComboBox();
						jPanelSensorSetting.add(typeComboBox);
						typeComboBox.setModel(typeComboBoxModel);
					}
				}
				{
					function_jPanel = new JPanel();
					GridLayout function_jPanelLayout = new GridLayout(3, 1);
					function_jPanelLayout.setHgap(5);
					function_jPanelLayout.setVgap(5);
					function_jPanelLayout.setColumns(1);
					function_jPanel.setLayout(function_jPanelLayout);
					setting_jPanel.add(function_jPanel);
					function_jPanel.setBorder(BorderFactory
							.createTitledBorder("Function Setting"));
					function_jPanel.setBounds(7, 114, 512, 117);
					{
						funcCode_jLabel = new JLabel();
						function_jPanel.add(funcCode_jLabel);
						funcCode_jLabel.setText("Function Code");
					}
					{

						ComboBoxModel functionCode_jComboBoxModel = new DefaultComboBoxModel(
								new String[] { SPINEFunctionConstants.FEATURE_LABEL });
						functionCode_jComboBox = new JComboBox();
						function_jPanel.add(functionCode_jComboBox);
						functionCode_jComboBox
								.setModel(functionCode_jComboBoxModel);
					}
					{
						window_jLabel = new JLabel();
						function_jPanel.add(window_jLabel);
						window_jLabel.setText("window");
					}
					{
						window_jTextField = new JTextField();
						function_jPanel.add(window_jTextField);
					}
					{
						shift_jLabel = new JLabel();
						function_jPanel.add(shift_jLabel);
						shift_jLabel.setText("Shift");
					}
					{
						shift_jTextField = new JTextField();
						function_jPanel.add(shift_jTextField);
					}
				}
				{
					jScrollPaneFeature = new JScrollPane();
					setting_jPanel.add(jScrollPaneFeature);
					jScrollPaneFeature.setBorder(BorderFactory
							.createTitledBorder("Feature Setting"));
					jScrollPaneFeature.setBounds(7, 231, 516, 159);
					{
						LinkedList<String> ll = new LinkedList<String>();
						for (int i = 0; i < 8; i++)
							ll.addLast(encodeSetup(i));

						jPanelFeature = new JPanel();
						jScrollPaneFeature.setViewportView(jPanelFeature);
						GridLayout jPanelFeatureLayout = new GridLayout(14, 1);
						jPanelFeatureLayout.setHgap(5);
						jPanelFeatureLayout.setVgap(5);
						jPanelFeatureLayout.setColumns(1);
						jPanelFeatureLayout.setRows(14);
						jPanelFeature.setLayout(jPanelFeatureLayout);
						{
							RawDataCheckBox = new JCheckBox();
							jPanelFeature.add(RawDataCheckBox);
							RawDataCheckBox.setText("Raw Data");
						}
						{
							ComboBoxModel rawDataComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							rawDataComboBox = new JComboBox();
							jPanelFeature.add(rawDataComboBox);
							rawDataComboBox.setModel(rawDataComboBoxModel);
						}
						{
							minCheckBox = new JCheckBox();
							jPanelFeature.add(minCheckBox);
							minCheckBox.setText("Min");
						}
						{
							ComboBoxModel minComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							minComboBox = new JComboBox();
							jPanelFeature.add(minComboBox);
							minComboBox.setModel(minComboBoxModel);
						}
						{
							MaxCheckBox = new JCheckBox();
							jPanelFeature.add(MaxCheckBox);
							MaxCheckBox.setText("Max");
						}
						{
							ComboBoxModel maxComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							maxComboBox = new JComboBox();
							jPanelFeature.add(maxComboBox);
							maxComboBox.setModel(maxComboBoxModel);
						}
						{
							RangeCheckBox = new JCheckBox();
							jPanelFeature.add(RangeCheckBox);
							RangeCheckBox.setText("Range");
						}
						{
							ComboBoxModel rangeComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							rangeComboBox = new JComboBox();
							jPanelFeature.add(rangeComboBox);
							rangeComboBox.setModel(rangeComboBoxModel);
						}
						{
							MeanCheckBox = new JCheckBox();
							jPanelFeature.add(MeanCheckBox);
							MeanCheckBox.setText("Mean");
						}
						{
							ComboBoxModel meanComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							meanComboBox = new JComboBox();
							jPanelFeature.add(meanComboBox);
							meanComboBox.setModel(meanComboBoxModel);
						}
						{
							AmplitudeCheckBox = new JCheckBox();
							jPanelFeature.add(AmplitudeCheckBox);
							AmplitudeCheckBox.setText("Amplitude");
						}
						{
							ComboBoxModel amplitudeComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							amplitudeComboBox = new JComboBox();
							jPanelFeature.add(amplitudeComboBox);
							amplitudeComboBox.setModel(amplitudeComboBoxModel);
						}
						{
							RmsCheckBox = new JCheckBox();
							jPanelFeature.add(RmsCheckBox);
							RmsCheckBox.setText("Rms");
						}
						{
							ComboBoxModel rmsComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							rmsComboBox = new JComboBox();
							jPanelFeature.add(rmsComboBox);
							rmsComboBox.setModel(rmsComboBoxModel);
						}
						{
							stdDevCheckBox = new JCheckBox();
							jPanelFeature.add(stdDevCheckBox);
							stdDevCheckBox.setText("Std Dev");
						}
						{
							ComboBoxModel stdDevComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							stdDevComboBox = new JComboBox();
							jPanelFeature.add(stdDevComboBox);
							stdDevComboBox.setModel(stdDevComboBoxModel);
						}
						{
							varianceCheckBox = new JCheckBox();
							jPanelFeature.add(varianceCheckBox);
							varianceCheckBox.setText("Variance");
						}
						{
							ComboBoxModel varianceComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							varianceComboBox = new JComboBox();
							jPanelFeature.add(varianceComboBox);
							varianceComboBox.setModel(varianceComboBoxModel);
						}
						{
							ModeCheckBox = new JCheckBox();
							jPanelFeature.add(ModeCheckBox);
							ModeCheckBox.setText("Mode");
						}
						{
							ComboBoxModel modeComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							modeComboBox = new JComboBox();
							jPanelFeature.add(modeComboBox);
							modeComboBox.setModel(modeComboBoxModel);
						}
						{
							MedianCheckBox = new JCheckBox();
							jPanelFeature.add(MedianCheckBox);
							MedianCheckBox.setText("Mediana");
						}
						{
							ComboBoxModel medianaComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							medianaComboBox = new JComboBox();
							jPanelFeature.add(medianaComboBox);
							medianaComboBox.setModel(medianaComboBoxModel);
						}
						{
							pitchCheckBox = new JCheckBox();
							jPanelFeature.add(pitchCheckBox);
							pitchCheckBox.setText("Pitch & Roll");
						}
						{
							ComboBoxModel pitchComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							pitchComboBox = new JComboBox();
							jPanelFeature.add(pitchComboBox);
							pitchComboBox.setModel(pitchComboBoxModel);
						}
						{
							vectorMagnitudeCheckBox = new JCheckBox();
							jPanelFeature.add(vectorMagnitudeCheckBox);
							vectorMagnitudeCheckBox.setText("Vector Magnitude");
						}
						{
							ComboBoxModel vectMagniComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							vectMagniComboBox = new JComboBox();
							jPanelFeature.add(vectMagniComboBox);
							vectMagniComboBox.setModel(vectMagniComboBoxModel);
						}
						{
							TotEnergyCheckBox = new JCheckBox();
							jPanelFeature.add(TotEnergyCheckBox);
							TotEnergyCheckBox.setText("Total Energy");
						}
						{
							ComboBoxModel totEneComboBoxModel = new DefaultComboBoxModel(
									ll.toArray());
							totEneComboBox = new JComboBox();
							jPanelFeature.add(totEneComboBox);
							totEneComboBox.setModel(totEneComboBoxModel);
						}
					}
				}
			}
			{
				com_jPanel = new JPanel();
				getContentPane().add(com_jPanel, BorderLayout.SOUTH);
				com_jPanel.setPreferredSize(new java.awt.Dimension(531, 48));
				{
					ok_jButton = new JButton();
					com_jPanel.add(ok_jButton);
					ok_jButton.setText("OK");
					ok_jButton.setPreferredSize(new java.awt.Dimension(73, 21));
					ok_jButton.addActionListener(this);
				}
				{
					exit_jButton = new JButton();
					com_jPanel.add(exit_jButton);
					exit_jButton.setText("Exit");
					exit_jButton
							.setPreferredSize(new java.awt.Dimension(63, 21));
					exit_jButton.addActionListener(this);
				}
			}
			pack();
			this.setSize(539, 605);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String funcCode;

	int fCode;

	byte sensor;

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(ok_jButton)) {

			SpineSetupSensor ss = new SpineSetupSensor();
			FeatureSpineSetupFunction fs = new FeatureSpineSetupFunction();
			FeatureSpineFunctionReq fsfr = new FeatureSpineFunctionReq();

			try {
				int samplingTime = Integer.parseInt(sampleTimeTextField
						.getText());
				ss.setSamplingTime(samplingTime);

				ss.setSensor(SPINESensorConstants
						.sensorCodeByString((String) sensorCode_jComboBox
								.getSelectedItem()));

				String type = (String) typeComboBox.getSelectedItem();
				ss.setTimeScale(SPINESensorConstants.timeScaleByString(type));

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error in Sensor Setting",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Setup Function
			funcCode = (String) functionCode_jComboBox.getSelectedItem();
			fCode = SPINEFunctionConstants.functionCodeByString(funcCode);

			if (fCode != SPINEFunctionConstants.FEATURE) {
				JOptionPane.showMessageDialog(this,
						"Setup non implmentato per questa Function", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				short windowSize = (short) Integer.parseInt(window_jTextField
						.getText());
				short shiftSize = (short) Integer.parseInt(shift_jTextField
						.getText());
				sensor = SPINESensorConstants
						.sensorCodeByString((String) sensorCode_jComboBox
								.getSelectedItem());

				switch (fCode) {

				case SPINEFunctionConstants.FEATURE: {
					fs.setSensor(sensor);
					fs.setShiftSize(shiftSize);
					fs.setWindowSize(windowSize);

					break;
				}

				default:
					break;
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this,
						"Error in Function Setting", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Activate Function
			try {

				funcCode = (String) functionCode_jComboBox.getSelectedItem();
				fCode = SPINEFunctionConstants.functionCodeByString(funcCode);
				sensor = SPINESensorConstants
						.sensorCodeByString((String) sensorCode_jComboBox
								.getSelectedItem());

				switch (fCode) {

				case SPINEFunctionConstants.FEATURE: {
					boolean functIsSelected = false;
					fsfr.setActivationFlag(true);
					fsfr.setSensor(sensor);
					byte ch;
					if (MaxCheckBox.isSelected()) {
						ch = (byte) decodeSetup(maxComboBox.getSelectedItem()
								.toString());
						if (ch != 0) {
							fsfr
									.add(new Feature(
											SPINEFunctionConstants.MAX, ch));
							functIsSelected = true;
						}
					}
					if (minCheckBox.isSelected()) {
						ch = (byte) decodeSetup(minComboBox.getSelectedItem()
								.toString());
						if (ch != 0) {
							fsfr
									.add(new Feature(
											SPINEFunctionConstants.MIN, ch));
							functIsSelected = true;
						}
					}
					if (RawDataCheckBox.isSelected()) {
						ch = (byte) decodeSetup(rawDataComboBox
								.getSelectedItem().toString());
						if (ch != 0) {
							fsfr.add(new Feature(
									SPINEFunctionConstants.RAW_DATA, ch));
							functIsSelected = true;
						}
					}
					if (RangeCheckBox.isSelected()) {
						ch = (byte) decodeSetup(rangeComboBox.getSelectedItem()
								.toString());
						if (ch != 0) {
							fsfr.add(new Feature(SPINEFunctionConstants.RANGE,
									ch));
							functIsSelected = true;
						}
					}
					if (MeanCheckBox.isSelected()) {
						ch = (byte) decodeSetup(meanComboBox.getSelectedItem()
								.toString());
						if (ch != 0) {
							fsfr.add(new Feature(SPINEFunctionConstants.MEAN,
									ch));
							functIsSelected = true;
						}
					}
					if (AmplitudeCheckBox.isSelected()) {
						ch = (byte) decodeSetup(amplitudeComboBox
								.getSelectedItem().toString());
						if (ch != 0) {
							fsfr.add(new Feature(
									SPINEFunctionConstants.AMPLITUDE, ch));
							functIsSelected = true;
						}
					}
					if (RmsCheckBox.isSelected()) {
						ch = (byte) decodeSetup(rmsComboBox.getSelectedItem()
								.toString());
						if (ch != 0) {
							fsfr
									.add(new Feature(
											SPINEFunctionConstants.RMS, ch));
							functIsSelected = true;
						}
					}
					if (varianceCheckBox.isSelected()) {
						ch = (byte) decodeSetup(varianceComboBox
								.getSelectedItem().toString());
						if (ch != 0) {
							fsfr.add(new Feature(
									SPINEFunctionConstants.VARIANCE, ch));
							functIsSelected = true;
						}
					}
					if (ModeCheckBox.isSelected()) {
						ch = (byte) decodeSetup(modeComboBox.getSelectedItem()
								.toString());
						if (ch != 0) {
							fsfr.add(new Feature(SPINEFunctionConstants.MODE,
									ch));
							functIsSelected = true;
						}
					}
					if (MedianCheckBox.isSelected()) {
						ch = (byte) decodeSetup(medianaComboBox
								.getSelectedItem().toString());
						if (ch != 0) {
							fsfr.add(new Feature(SPINEFunctionConstants.MEDIAN,
									ch));
							functIsSelected = true;
						}
					}
					if (pitchCheckBox.isSelected()) {
						ch = (byte) decodeSetup(pitchComboBox.getSelectedItem()
								.toString());
						if (ch != 0) {
							fsfr.add(new Feature(
									SPINEFunctionConstants.PITCH_ROLL, ch));
							functIsSelected = true;
						}
					}
					if (vectorMagnitudeCheckBox.isSelected()) {
						ch = (byte) decodeSetup(vectMagniComboBox
								.getSelectedItem().toString());
						if (ch != 0) {
							fsfr
									.add(new Feature(
											SPINEFunctionConstants.VECTOR_MAGNITUDE,
											ch));
							functIsSelected = true;
						}
					}
					if (stdDevCheckBox.isSelected()) {
						ch = (byte) decodeSetup(stdDevComboBox
								.getSelectedItem().toString());
						if (ch != 0) {
							fsfr.add(new Feature(SPINEFunctionConstants.ST_DEV,
									ch));
							functIsSelected = true;
						}
					}
					if (TotEnergyCheckBox.isSelected()) {
						ch = (byte) decodeSetup(totEneComboBox
								.getSelectedItem().toString());
						if (ch != 0) {
							fsfr.add(new Feature(
									SPINEFunctionConstants.TOTAL_ENERGY, ch));
							functIsSelected = true;
						}
					}

					if (!functIsSelected) {
						JOptionPane.showMessageDialog(this,
								"Error in Feature Setting", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					break;
				}

				default:
					break;
				}

				int res = JOptionPane.showConfirmDialog(this,
						"Have you set correct parameters? \n" + ss + "\n" + fs
								+ "\n" + fsfr, "Confirm",
						JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.OK_OPTION) {
					sg.setupSensor(node, ss);
					sg.setupFunction(node, fs);
					sg.activateFunction(node, fsfr);
					this.dispose();
				} else
					return;

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error in Feature Setting",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

		}

		if (e.getSource().equals(exit_jButton)) {
			this.dispose();
		}
	}

	private String encodeSetup(int i) {
		String s = "";
		switch (i) {
		case 0:
			s = "NULL";
			break;
		case 1:
			s = "X axis";
			break;
		case 2:
			s = "Y axis";
			break;
		case 3:
			s = "X Y axis";
			break;
		case 4:
			s = "Z axis";
			break;
		case 5:
			s = "Z X axis";
			break;
		case 6:
			s = "Z Y axis";
			break;
		case 7:
			s = "Z Y X axis";
			break;
		default:
			s = "null";
			break;
		}
		return s;
	}

	private int decodeSetup(String g) {
		if (g.equals("NULL") || g.equals("null"))
			return 0;
		if (g.equals("X axis"))
			return 8;
		if (g.equals("Y axis"))
			return 4;
		if (g.equals("X Y axis"))
			return 12;
		if (g.equals("Z axis"))
			return 2;
		if (g.equals("Z X axis"))
			return 10;
		if (g.equals("Z Y axis"))
			return 6;
		if (g.equals("Z Y X axis"))
			return 14;
		return 0;
	}
}
