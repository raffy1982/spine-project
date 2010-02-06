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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import logic.PropertiesController;

/**
 * Dialog box to insert Class Label .
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */
public class ClassLabelDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel panel;

	private JTextField classLabelValue;

	private PropertiesController propertControll;

	private String classLabel;

	private String setClassLabel;

	private String lastClassLabel;

	private String action;

	/** Construct a Class Label Dialog box. */
	public ClassLabelDialog(String title, String act) {

		action = act;

		getContentPane().setLayout(null);
		setModal(true);
		setResizable(false);
		setTitle(title);
		setBounds(100, 100, 477, 235);

		propertControll = PropertiesController.getInstance();

		try {
			propertControll.load();
			// Read CLASS_LABEL
			lastClassLabel = propertControll.getProperty("CLASS_LABEL");
			if (lastClassLabel.contains(",")) {
				lastClassLabel = lastClassLabel.substring(lastClassLabel
						.lastIndexOf(",") + 2);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(7, 7, 454, 187);

		final JLabel classLabelFieldLabel = new JLabel();
		classLabelFieldLabel.setText("Class Label:");
		classLabelFieldLabel.setBounds(13, 81, 76, 14);
		panel.add(classLabelFieldLabel);

		classLabelValue = new JTextField();
		classLabelValue.setBounds(105, 79, 289, 19);
		panel.add(classLabelValue);
		classLabelValue.setText(lastClassLabel);

		final JButton saveWizardButton = new JButton();
		saveWizardButton.setText("Confirm");
		saveWizardButton.setBounds(242, 152, 93, 23);
		saveWizardButton.setFocusable(false);
		saveWizardButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				classLabel = classLabelValue.getText();
				// char column separator CSV =";" TXT=" " ARFF=","
				if ((!classLabel.equals("")) && (!classLabel.contains(","))
						&& (!classLabel.contains(" "))
						&& (!classLabel.contains("{"))
						&& (!classLabel.contains("}"))
						&& (!classLabel.contains(";"))) {
					if (!(action.equals("Start"))) {
						setClassLabel = propertControll
								.getProperty("CLASS_LABEL");
						setClassLabel = setClassLabel + ", " + classLabel;
					} else {
						setClassLabel = classLabel;
					}

					// Set CLASS_LABEL
					propertControll.setProperty("CLASS_LABEL", setClassLabel);
					propertControll.store();
					ClassLabelDialog.this.dispose();

				} else
					JOptionPane.showMessageDialog(null,
							"Please specify a valid Class Label!", "ERROR",
							JOptionPane.ERROR_MESSAGE);
			}
		});
		panel.add(saveWizardButton);

		final JButton cancelWizardButton = new JButton();
		cancelWizardButton.setText("Cancel");
		cancelWizardButton.setBounds(341, 152, 93, 23);
		cancelWizardButton.setFocusable(false);
		cancelWizardButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				ClassLabelDialog.this.dispose();
			}
		});
		panel.add(cancelWizardButton);

		getContentPane().add(panel);
	}
}
