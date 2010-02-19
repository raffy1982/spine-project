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
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import spine.datamodel.Feature;
import spine.datamodel.Node;
import spine.datamodel.Sensor;
import spine.datamodel.functions.AlarmSpineFunctionReq;
import spine.datamodel.functions.FeatureSpineFunctionReq;
import spine.datamodel.functions.Function;
// Used in STEP_COUNTER
//import spine.datamodel.functions.StepCounterSpineFunctionReq;

/**
 * Activate function panel.
 * 
 * @author Luigi Buondonno : luigi.buondonno@gmail.com
 * @author Antonio Giordano : antoniogior@hotmail.com
 * 
 * @version 1.0
 */

public class ActivateFunctionPanel extends javax.swing.JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JPanel jPanelNord;

	private JPanel jPanelCenter;

	private JButton ExitButton;

	private JComboBox modeComboBox;

	private JComboBox jSensorCodeComboBox;

	private JComboBox jFunctionCodeComboBox;

	private JComboBox totEneComboBox;

	private JComboBox vectMagniComboBox;

	private JComboBox pitchComboBox;

	private JComboBox medianaComboBox;

	private JComboBox varianceComboBox;

	private JComboBox stdDevComboBox;

	private JComboBox rmsComboBox;

	private JComboBox amplitudeComboBox;

	private JComboBox meanComboBox;

	private JComboBox rangeComboBox;

	private JComboBox maxComboBox;

	private JComboBox minComboBox;

	private JComboBox rawDataComboBox;

	private JCheckBox TotEnergyCheckBox;

	private JCheckBox vectorMagnitudeCheckBox;

	private JCheckBox pitchCheckBox;

	private JCheckBox MedianCheckBox;

	private JCheckBox ModeCheckBox;

	private JCheckBox varianceCheckBox;

	private JCheckBox stdDevCheckBox;

	private JCheckBox RmsCheckBox;

	private JCheckBox AmplitudeCheckBox;

	private JCheckBox MeanCheckBox;

	private JCheckBox RangeCheckBox;

	private JCheckBox MaxCheckBox;

	private JCheckBox minCheckBox;

	private JCheckBox RawDataCheckBox;

	private JPanel jPanelFeature;

	private JScrollPane jScrollPaneFeature;

	private JPanel jPanelNodeInfo;

	private JLabel sensorCodeLabel;

	private JLabel sampleTimeLabel;

	private JButton jButtonConfirm;

	private JPanel jPanelSensorSetting;

	private SpineGUI sg;

	private Node node;

	private JScrollPane jScrollPane;

	private JTextArea jNodeInfoTextArea;

	private JPanel jPanelAlarmSetting;

	private JLabel jdataTypeLabel;

	private JComboBox jDataTypeComboBox;

	private JComboBox jValueTypeComboBox;

	private JLabel jValueTypeLabel;

	private JLabel jAlarmTypeLabel;

	private JComboBox jAlarmTypeComboBox;

	private JLabel jUthLabel;

	private JTextField jUthTextField;

	private JLabel jLthLabel;

	private JTextField jLthTextField;

	private JPanel jPanelStepCounterSetting;

	private JLabel jUthLabelStepCounter;

	private JTextField jUthTextFieldStepCounter;

	private JLabel jLthLabelStepCounter;

	private JTextField jLthTextFieldStepCounter;

	/**
	 * Activate function panel.
	 * 
	 */

	public ActivateFunctionPanel(SpineGUI sg, Node n) {
		super();
		this.node = n;
		this.sg = sg;
		initGUI();
	}

	private void initGUI() {
		try {
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(587, 529));
			{
				jPanelNord = new JPanel();
				URL imageURL = SetupFunctionPanel.class.getResource("/img/ActivateFunction.gif");
				System.out.println(imageURL);
				jPanelNord.setBorder(new MatteBorder(new ImageIcon(imageURL)));
				this.add(jPanelNord, BorderLayout.NORTH);
				jPanelNord.setPreferredSize(new java.awt.Dimension(587, 57));
				jPanelNord.setVisible(true);
			}
			{
				jPanelCenter = new JPanel();
				this.add(jPanelCenter, BorderLayout.CENTER);
				GroupLayout jPanelCenterLayout = new GroupLayout((JComponent) jPanelCenter);
				jPanelCenter.setLayout(jPanelCenterLayout);
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
					GridLayout jPanelSensorSettingLayout = new GridLayout(3, 2);
					jPanelSensorSettingLayout.setHgap(5);
					jPanelSensorSettingLayout.setVgap(5);
					jPanelSensorSettingLayout.setColumns(1);
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
						byte fCode;
						for (int i = 0; i < fl.size(); i++) {
							fCode = ((Function) fl.get(i)).functionCode;
							f[i] = SPINEFunctionConstants.functionCodeToString(fCode);
						}
						ComboBoxModel jFunctionCodeComboBoxModel = new DefaultComboBoxModel(f);
						jFunctionCodeComboBox = new JComboBox();
						jFunctionCodeComboBox.addActionListener(this);
						jPanelSensorSetting.add(jFunctionCodeComboBox);
						jFunctionCodeComboBox.setModel(jFunctionCodeComboBoxModel);
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
				}
				{
					LinkedList<String> ll = new LinkedList<String>();
					for (int i = 0; i < 8; i++)
						ll.addLast(encodeSetup(i));

					jScrollPaneFeature = new JScrollPane();
					jScrollPaneFeature.setBorder(BorderFactory.createTitledBorder("Feature Setting"));
					{

						// stepCounter panel

						jPanelStepCounterSetting = new JPanel();
						GridLayout jPanelStepCounterSettingLayout = new GridLayout(2, 2);
						jPanelStepCounterSetting.setLayout(jPanelStepCounterSettingLayout);

						jUthLabelStepCounter = new JLabel("Avg Acceleration");
						jPanelStepCounterSetting.add(jUthLabelStepCounter);

						jUthTextFieldStepCounter = new JTextField();
						jPanelStepCounterSetting.add(jUthTextFieldStepCounter);

						jLthLabelStepCounter = new JLabel("Step Threshold");
						jPanelStepCounterSetting.add(jLthLabelStepCounter);

						jLthTextFieldStepCounter = new JTextField();
						jPanelStepCounterSetting.add(jLthTextFieldStepCounter);

						jPanelAlarmSetting = new JPanel();
						GridLayout jPanelAlarmSettingLayout = new GridLayout(5, 2);
						jPanelAlarmSetting.setLayout(jPanelAlarmSettingLayout);

						jdataTypeLabel = new JLabel("Data type");
						jPanelAlarmSetting.add(jdataTypeLabel);

						ComboBoxModel dataTypeComboBoxModel = new DefaultComboBoxModel(new String[] { SPINEFunctionConstants.AMPLITUDE_LABEL, SPINEFunctionConstants.MAX_LABEL,
								SPINEFunctionConstants.MEAN_LABEL, SPINEFunctionConstants.MEDIAN_LABEL, SPINEFunctionConstants.MIN_LABEL, SPINEFunctionConstants.MODE_LABEL,
								SPINEFunctionConstants.PITCH_ROLL_LABEL, SPINEFunctionConstants.RANGE_LABEL, SPINEFunctionConstants.RAW_DATA_LABEL, SPINEFunctionConstants.RMS_LABEL,
								SPINEFunctionConstants.ST_DEV_LABEL, SPINEFunctionConstants.TOTAL_ENERGY_LABEL, SPINEFunctionConstants.VARIANCE_LABEL, SPINEFunctionConstants.VECTOR_MAGNITUDE_LABEL });
						jDataTypeComboBox = new JComboBox();
						jDataTypeComboBox.setModel(dataTypeComboBoxModel);
						jPanelAlarmSetting.add(jDataTypeComboBox);

						jValueTypeLabel = new JLabel("Value type");
						jPanelAlarmSetting.add(jValueTypeLabel);

						ComboBoxModel alarmDataComboBoxModel = new DefaultComboBoxModel(ll.toArray());
						jValueTypeComboBox = new JComboBox();
						jValueTypeComboBox.setModel(alarmDataComboBoxModel);
						jPanelAlarmSetting.add(jValueTypeComboBox);

						jAlarmTypeLabel = new JLabel("Alarm type");
						jPanelAlarmSetting.add(jAlarmTypeLabel);

						ComboBoxModel alarmTypeComboBoxModel = new DefaultComboBoxModel(new String[] { SPINEFunctionConstants.ABOVE_THRESHOLD_LABEL, SPINEFunctionConstants.BELOW_THRESHOLD_LABEL,
								SPINEFunctionConstants.IN_BETWEEN_THRESHOLDS_LABEL, SPINEFunctionConstants.OUT_OF_THRESHOLDS_LABEL });
						jAlarmTypeComboBox = new JComboBox();
						jAlarmTypeComboBox.setModel(alarmTypeComboBoxModel);
						jPanelAlarmSetting.add(jAlarmTypeComboBox);

						jUthLabel = new JLabel("Upper threshold");
						jPanelAlarmSetting.add(jUthLabel);

						jUthTextField = new JTextField();
						jUthTextField.setText("0");
						jPanelAlarmSetting.add(jUthTextField);

						jLthLabel = new JLabel("Lower threshold");
						jPanelAlarmSetting.add(jLthLabel);

						jLthTextField = new JTextField();
						jLthTextField.setText("0");
						jPanelAlarmSetting.add(jLthTextField);

						jPanelFeature = new JPanel();
						GridLayout jPanelFeatureLayout = new GridLayout(14, 2);
						jPanelFeatureLayout.setHgap(5);
						jPanelFeatureLayout.setVgap(5);
						jPanelFeatureLayout.setColumns(1);

						jScrollPaneFeature.setViewportView(jPanelFeature);

						jPanelFeature.setLayout(jPanelFeatureLayout);

						{
							RawDataCheckBox = new JCheckBox();
							jPanelFeature.add(RawDataCheckBox);
							RawDataCheckBox.setText("Raw Data");
						}

						{
							ComboBoxModel rawDataComboBoxModel = new DefaultComboBoxModel(ll.toArray());
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
							ComboBoxModel minComboBoxModel = new DefaultComboBoxModel(ll.toArray());
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
							ComboBoxModel maxComboBoxModel = new DefaultComboBoxModel(ll.toArray());
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
							ComboBoxModel rangeComboBoxModel = new DefaultComboBoxModel(ll.toArray());
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
							ComboBoxModel meanComboBoxModel = new DefaultComboBoxModel(ll.toArray());
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
							ComboBoxModel amplitudeComboBoxModel = new DefaultComboBoxModel(ll.toArray());
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
							ComboBoxModel rmsComboBoxModel = new DefaultComboBoxModel(ll.toArray());
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
							ComboBoxModel stdDevComboBoxModel = new DefaultComboBoxModel(ll.toArray());
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
							ComboBoxModel varianceComboBoxModel = new DefaultComboBoxModel(ll.toArray());
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
							ComboBoxModel modeComboBoxModel = new DefaultComboBoxModel(ll.toArray());
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
							ComboBoxModel medianaComboBoxModel = new DefaultComboBoxModel(ll.toArray());
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
							ComboBoxModel pitchComboBoxModel = new DefaultComboBoxModel(ll.toArray());
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
							ComboBoxModel vectMagniComboBoxModel = new DefaultComboBoxModel(ll.toArray());
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
							ComboBoxModel totEneComboBoxModel = new DefaultComboBoxModel(ll.toArray());
							totEneComboBox = new JComboBox();
							jPanelFeature.add(totEneComboBox);
							totEneComboBox.setModel(totEneComboBoxModel);
						}
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
								jPanelSensorSetting, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 494, GroupLayout.PREFERRED_SIZE).addComponent(jScrollPaneFeature,
								GroupLayout.Alignment.LEADING, 0, 495, Short.MAX_VALUE).addGroup(
								GroupLayout.Alignment.LEADING,
								jPanelCenterLayout.createSequentialGroup().addGap(105).addComponent(jButtonConfirm, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE).addGap(67)
										.addComponent(ExitButton, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE).addGap(120))).addContainerGap(44, 44));
				jPanelCenterLayout.setVerticalGroup(jPanelCenterLayout.createSequentialGroup().addComponent(jPanelNodeInfo, 0, 108, Short.MAX_VALUE).addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanelSensorSetting, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE).addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPaneFeature, GroupLayout.PREFERRED_SIZE, 206, GroupLayout.PREFERRED_SIZE).addPreferredGap(
						LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
						jPanelCenterLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jButtonConfirm, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(ExitButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)).addContainerGap(15, 15));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//
		String func = (String) jFunctionCodeComboBox.getSelectedItem();
		if (func.equalsIgnoreCase(SPINEFunctionConstants.ALARM_LABEL)) {
			jScrollPaneFeature.setViewportView(jPanelAlarmSetting);
		}
		if (func.equalsIgnoreCase(SPINEFunctionConstants.FEATURE_LABEL)) {
			jScrollPaneFeature.setViewportView(jPanelFeature);
		}

		if (func.equalsIgnoreCase(SPINEFunctionConstants.STEP_COUNTER_LABEL) || func.equalsIgnoreCase(SPINEFunctionConstants.BUFFERED_RAW_DATA_LABEL) ) {
			jScrollPaneFeature.setViewportView(null);
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

	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(ExitButton)) {
			sg.closeFrame();
		}

		if (e.getSource().equals(jButtonConfirm)) {

			try {

				String s = (String) jFunctionCodeComboBox.getSelectedItem();
				int funcCode = SPINEFunctionConstants.functionCodeByString(s);
				byte sensor = SPINESensorConstants.sensorCodeByString((String) jSensorCodeComboBox.getSelectedItem());

				switch (funcCode) {

				case SPINEFunctionConstants.ALARM: {
					AlarmSpineFunctionReq afr = new AlarmSpineFunctionReq();
					afr.setActivationFlag(true);
					afr.setAlarmType(SPINEFunctionConstants.functionalityCodeByString(SPINEFunctionConstants.ALARM_LABEL, (String) jAlarmTypeComboBox.getSelectedItem()));
					afr.setDataType(SPINEFunctionConstants.functionalityCodeByString(SPINEFunctionConstants.FEATURE_LABEL, (String) jDataTypeComboBox.getSelectedItem()));
					afr.setLowerThreshold(Integer.parseInt(jLthTextField.getText()));
					afr.setSensor(sensor);
					afr.setUpperThreshold(Integer.parseInt(jUthTextField.getText()));
					;
					afr.setValueType((byte) decodeSetup(jValueTypeComboBox.getSelectedItem().toString()));

					int res = JOptionPane.showConfirmDialog(this, "Are you sure about the parameters? \n" + afr, "Confirm", JOptionPane.YES_NO_OPTION);
					if (res == JOptionPane.OK_OPTION) {
						sg.actFunction(node, afr);
						sg.closeFrame();
						return;
					}

					break;
				}
				case SPINEFunctionConstants.FEATURE: {
					FeatureSpineFunctionReq fsfr = new FeatureSpineFunctionReq();
					fsfr.setActivationFlag(true);
					fsfr.setSensor(sensor);
					if (MaxCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.MAX, (byte) decodeSetup(maxComboBox.getSelectedItem().toString())));
					}
					if (minCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.MIN, (byte) decodeSetup(minComboBox.getSelectedItem().toString())));
					}
					if (RawDataCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.RAW_DATA, (byte) decodeSetup(rawDataComboBox.getSelectedItem().toString())));
					}
					if (RangeCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.RANGE, (byte) decodeSetup(rangeComboBox.getSelectedItem().toString())));
					}
					if (MeanCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.MEAN, (byte) decodeSetup(meanComboBox.getSelectedItem().toString())));
					}
					if (AmplitudeCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.AMPLITUDE, (byte) decodeSetup(amplitudeComboBox.getSelectedItem().toString())));
					}
					if (RmsCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.RMS, (byte) decodeSetup(rmsComboBox.getSelectedItem().toString())));
					}
					if (varianceCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.VARIANCE, (byte) decodeSetup(varianceComboBox.getSelectedItem().toString())));
					}
					if (ModeCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.MODE, (byte) decodeSetup(modeComboBox.getSelectedItem().toString())));
					}
					if (MedianCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.MEDIAN, (byte) decodeSetup(medianaComboBox.getSelectedItem().toString())));
					}
					if (pitchCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.PITCH_ROLL, (byte) decodeSetup(pitchComboBox.getSelectedItem().toString())));
					}
					if (vectorMagnitudeCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.VECTOR_MAGNITUDE, (byte) decodeSetup(vectMagniComboBox.getSelectedItem().toString())));
					}
					if (stdDevCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.ST_DEV, (byte) decodeSetup(stdDevComboBox.getSelectedItem().toString())));
					}
					if (TotEnergyCheckBox.isSelected()) {
						fsfr.add(new Feature(SPINEFunctionConstants.TOTAL_ENERGY, (byte) decodeSetup(totEneComboBox.getSelectedItem().toString())));
					}

					int res = JOptionPane.showConfirmDialog(this, "Are you sure about the parameters? \n" + fsfr, "Confirm", JOptionPane.YES_NO_OPTION);
					if (res == JOptionPane.OK_OPTION) {
						sg.actFunction(node, fsfr);
						sg.closeFrame();
						return;
					}

					break;
				}

				/* NOT YET SUPPORTED
				case SPINEFunctionConstants.STEP_COUNTER: {

					StepCounterSpineFunctionReq svfr = new StepCounterSpineFunctionReq();
					svfr.setActivationFlag(true);
					int res = JOptionPane.showConfirmDialog(this, "Are you sure about the parameters? \n" + svfr, "Confirm", JOptionPane.YES_NO_OPTION);
					if (res == JOptionPane.OK_OPTION) {
						sg.actFunction(node, svfr);
						sg.closeFrame();
						return;
					}

					break;
				}*/
				default:
					JOptionPane.showMessageDialog(this, SPINEFunctionConstants.functionCodeToString((byte)funcCode) + ": function not supported", "Error", JOptionPane.ERROR_MESSAGE);
					break;
				}

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Data Error", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

		}

		if (e.getSource().equals(jFunctionCodeComboBox)) {
			String func = (String) jFunctionCodeComboBox.getSelectedItem();
			if (func.equalsIgnoreCase(SPINEFunctionConstants.ALARM_LABEL)) {
				jScrollPaneFeature.setViewportView(jPanelAlarmSetting);
			}
			if (func.equalsIgnoreCase(SPINEFunctionConstants.FEATURE_LABEL)) {
				jScrollPaneFeature.setViewportView(jPanelFeature);
			}

			if (func.equalsIgnoreCase(SPINEFunctionConstants.STEP_COUNTER_LABEL) || func.equalsIgnoreCase(SPINEFunctionConstants.BUFFERED_RAW_DATA_LABEL) ) {
				jScrollPaneFeature.setViewportView(null);
			}
		}

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
