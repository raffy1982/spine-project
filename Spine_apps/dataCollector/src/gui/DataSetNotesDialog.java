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
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import logic.PropertiesController;

/**
 * Dialog box to insert notes.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */
public class DataSetNotesDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel panel;

	private JTextArea notes;

	private PropertiesController propertControll;

	private String dataSetNotes;

	private String newDataSetNotes;

	/** Construct a Dialog box to insert notes. */
	public DataSetNotesDialog(String title, String initialDescr) {

		getContentPane().setLayout(null);
		setModal(true);
		setResizable(false);
		setTitle(title);
		setBounds(100, 100, 477, 235);

		propertControll = PropertiesController.getInstance();

		try {
			propertControll.load();
			// Read DATASET_NOTES
			dataSetNotes = propertControll.getProperty("DATASET_NOTES");
			propertControll.store();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(7, 7, 454, 187);

		final JLabel notesLabel = new JLabel();
		notesLabel.setText("Notes:");
		notesLabel.setBounds(13, 81, 66, 14);
		panel.add(notesLabel);

		notes = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(notes,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(80, 25, 350, 100);
		panel.add(scrollPane);
		notes.setText(dataSetNotes);

		final JButton saveWizardButton = new JButton();
		saveWizardButton.setText("Confirm");
		saveWizardButton.setBounds(242, 152, 93, 23);
		saveWizardButton.setFocusable(false);
		saveWizardButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				newDataSetNotes = notes.getText();
				if (!(newDataSetNotes.contains("%"))
						&& !(newDataSetNotes.contains("@"))
						&& !(newDataSetNotes.contains("!"))) {
					// Set DATASET_PATH
					propertControll.setProperty("DATASET_NOTES",
							newDataSetNotes);
					DataSetNotesDialog.this.dispose();

				} else
					JOptionPane
							.showMessageDialog(
									null,
									"Please specify a valid notes! (Don't use character: ! @ and % )",
									"ERROR", JOptionPane.ERROR_MESSAGE);
			}
		});
		panel.add(saveWizardButton);

		final JButton cancelWizardButton = new JButton();
		cancelWizardButton.setText("Cancel");
		cancelWizardButton.setBounds(341, 152, 93, 23);
		cancelWizardButton.setFocusable(false);
		cancelWizardButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				DataSetNotesDialog.this.dispose();
			}
		});
		panel.add(cancelWizardButton);

		getContentPane().add(panel);
	}
}
