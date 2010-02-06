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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import logic.VirtualNode;
import logic.Command;

/**
 * Node Emulator GUI - application to emule a sensor node.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */

public class NodeEmulatorGUI extends javax.swing.JFrame implements ActionListener, Observer {

	Command command;

	final static String NEWLINE = "\n";

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd, HH:mm:ss '- '");

	private static final long serialVersionUID = 1L;

	private JTabbedPane jTabbedPaneCentralDw;

	private JToolBar jToolBar;

	private JSeparator jSeparator1;

	private JToolBar jToolBarDown;

	private JTabbedPane jTabbedPaneLog;

	private JMenuBar guiMenuBar;

	private JMenu applicationMenu;

	private JMenuItem dataSetOpenMenuItem;

	private JMenuItem exitMenuItem;

	private JMenu nodeCommandMenu;

	private JMenuItem resetMenuItem;

	private JMenuItem connectToWSNMenuItem;

	private JMenu helpMenu;

	private JMenuItem aboutMenuItem;

	private JFrame frame;

	private JScrollPane jLogscrollpane;

	private JTextArea jLogTextArea;

	JPopupMenu popup;

	VirtualNode vNode;

	private String connectResult = null;

	/** Construct a Node Emulator GUI. */
	public NodeEmulatorGUI(Command command) {
		super(" Node Emulator - Application for emulate sensors node");
		this.vNode = (VirtualNode) command;
		initGUI();
	}

	private void initGUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			{
				BorderLayout thisLayout = new BorderLayout();
				getContentPane().setLayout(thisLayout);
				jTabbedPaneCentralDw = new JTabbedPane();
				jTabbedPaneCentralDw.setPreferredSize(new java.awt.Dimension(581, 47));
				jTabbedPaneLog = new JTabbedPane();
				jTabbedPaneCentralDw.addTab("Log", null, jTabbedPaneLog, null);
				jLogscrollpane = new JScrollPane();
				jLogTextArea = new JTextArea();
				jLogscrollpane.setViewportView(jLogTextArea);
				jTabbedPaneLog.setLayout(new BorderLayout());
				jTabbedPaneLog.add(jLogscrollpane);
			}
			{
				jToolBar = new JToolBar();
				getContentPane().add(jToolBar, BorderLayout.NORTH);
				getContentPane().add(jTabbedPaneCentralDw, BorderLayout.CENTER);
				{
					jToolBarDown = new JToolBar();
					getContentPane().add(jToolBarDown, BorderLayout.SOUTH);
					jToolBarDown.setPreferredSize(new java.awt.Dimension(695, 23));
				}
			}
			this.setSize(703, 476);
			{
				guiMenuBar = new JMenuBar();
				setJMenuBar(guiMenuBar);
				{
					// Menu Application
					applicationMenu = new JMenu();
					guiMenuBar.add(applicationMenu);
					applicationMenu.setText("Application");
					{
						dataSetOpenMenuItem = new JMenuItem();
						applicationMenu.add(dataSetOpenMenuItem);
						dataSetOpenMenuItem.setText("Data Set Open");
						dataSetOpenMenuItem.addActionListener(this);
					}
					{
						jSeparator1 = new JSeparator();
						applicationMenu.add(jSeparator1);
					}
					{
						exitMenuItem = new JMenuItem();
						exitMenuItem.addActionListener(this);
						applicationMenu.add(exitMenuItem);
						exitMenuItem.setText("Exit");
					}
				}

				{
					// Menu Node Command
					nodeCommandMenu = new JMenu();
					guiMenuBar.add(nodeCommandMenu);
					nodeCommandMenu.setText("Node Command");
					{
						connectToWSNMenuItem = new JMenuItem();
						nodeCommandMenu.add(connectToWSNMenuItem);
						connectToWSNMenuItem.setText("Connect to WSN");
						connectToWSNMenuItem.addActionListener(this);
						connectToWSNMenuItem.setEnabled(false);
					}
					{
						jSeparator1 = new JSeparator();
						nodeCommandMenu.add(jSeparator1);
					}
					{
						resetMenuItem = new JMenuItem();
						nodeCommandMenu.add(resetMenuItem);
						resetMenuItem.setText("Reset");
						resetMenuItem.addActionListener(this);
						resetMenuItem.setEnabled(false);
					}

				}
				// Menu Help
				{
					helpMenu = new JMenu();
					guiMenuBar.add(helpMenu);
					helpMenu.setText("Help");
					{
						aboutMenuItem = new JMenuItem();
						helpMenu.add(aboutMenuItem);
						aboutMenuItem.setText("About");
						aboutMenuItem.addActionListener(this);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent arg0) {

		if (arg0.getSource().equals(dataSetOpenMenuItem)) {
			dataSetOpenDialog wiz = new dataSetOpenDialog("Data Set Open", "Data Set format managed: ARFF, CSV and TXT");
			wiz.setModal(true);
			gui.Utils.onScreenCentered(wiz);
			wiz.setVisible(true);
			System.out.println("*** Data File File scelto:" + wiz.dataSetFile);
			String dataSetFile = wiz.dataSetFile;
			if (dataSetFile != "") {
				vNode.loadDataSensor(dataSetFile);
			}
			;
		}

		if (arg0.getSource().equals(exitMenuItem)) {
			System.exit(NORMAL);
		}

		if (arg0.getSource().equals(connectToWSNMenuItem)) {
			connectResult = vNode.createSocket();
		}

		if (arg0.getSource().equals(resetMenuItem)) {
			vNode.reset();
			resetMenuItem.setEnabled(false);
		}

		if (arg0.getSource().equals(aboutMenuItem)) {
			JOptionPane.showMessageDialog(this, "Autori :\n" + "Alessia Salmeri : Alessia.Salmeri@telecomitalia.it \n", "Autori", JOptionPane.INFORMATION_MESSAGE);
		}

	}

	public void closeFrame() {
		frame.dispose();
		this.setEnabled(true);
		this.setVisible(true);
	}

	public void update(Observable arg0, Object o) {
		if (o instanceof String) {

			if (o.equals("StartLoading")) {
				dataSetOpenMenuItem.setEnabled(false);
				connectToWSNMenuItem.setEnabled(false);

			} else if (o.equals("EndLoading")) {
				connectToWSNMenuItem.setEnabled(true);

			} else if (o.equals("Start Node")) {
				resetMenuItem.setEnabled(true);
			} else {
				jLogTextArea.append(dateTime() + ((String) o).toString() + "\n");
				jLogTextArea.setCaretPosition(jLogTextArea.getText().length() - 1);
				if (o.equals("Connection successful")) {
					connectToWSNMenuItem.setEnabled(false);
				}
			}
		}
	}

	static String dateTime() {
		return sdf.format(new Date(System.currentTimeMillis()));
	}

}
