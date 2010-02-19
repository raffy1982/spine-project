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
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog box to choose data set file (extension arff, txt or csv).
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */

public class dataSetOpenDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private File folderName;

	private JPanel panel;

	private JTextField dsPathFile;

	private JFileChooser jf = new JFileChooser();

	public String dataSetFile;

	/** Construct a Dialog box to choose data set file. */
	public dataSetOpenDialog(String title, String initialDescr) {

		getContentPane().setLayout(null);
		setModal(true);
		setResizable(false);
		setTitle(title);
		setBounds(100, 100, 477, 235);

		panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(7, 7, 454, 187);

		final JLabel initialStepLabel = new JLabel();
		initialStepLabel.setBounds(13, 37, 450, 14);
		initialStepLabel.setText(initialDescr);
		panel.add(initialStepLabel);

		final JLabel dataSetFileLabel = new JLabel();
		dataSetFileLabel.setText("Data Set:");
		dataSetFileLabel.setBounds(13, 81, 66, 14);
		panel.add(dataSetFileLabel);

		dsPathFile = new JTextField();
		dsPathFile.setBounds(85, 79, 289, 19);
		panel.add(dsPathFile);

		final JButton browseButton = new JButton();
		browseButton.setText("...");
		browseButton.setBounds(380, 77, 54, 23);
		browseButton.setFocusable(false);
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = jf.showDialog(dataSetOpenDialog.this, "Data Set");
				if (returnVal == JFileChooser.APPROVE_OPTION)
					folderName = jf.getSelectedFile();
				try {
					dsPathFile.setText(folderName.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		panel.add(browseButton);

		final JButton saveWizardButton = new JButton();
		saveWizardButton.setText("Confirm");
		saveWizardButton.setBounds(242, 152, 93, 23);
		saveWizardButton.setFocusable(false);
		saveWizardButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				dataSetFile = dsPathFile.getText();
				if (dataSetFile.endsWith(".arff") || dataSetFile.endsWith(".csv") || dataSetFile.endsWith(".txt")) {

					dataSetOpenDialog.this.dispose();

				} else
					JOptionPane.showMessageDialog(null, "Please specify a valid file path/name!", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		});
		panel.add(saveWizardButton);

		final JButton cancelWizardButton = new JButton();
		cancelWizardButton.setText("Cancel");
		cancelWizardButton.setBounds(341, 152, 93, 23);
		cancelWizardButton.setFocusable(false);
		cancelWizardButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				dataSetFile = "";
				dataSetOpenDialog.this.dispose();
			}
		});
		panel.add(cancelWizardButton);
		getContentPane().add(panel);
	}
}
