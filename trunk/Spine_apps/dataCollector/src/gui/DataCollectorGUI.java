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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Cursor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
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
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import spine.datamodel.Data;
import spine.datamodel.Node;
import spine.datamodel.functions.SpineFunctionReq;
import spine.datamodel.functions.SpineSetupFunction;
import spine.datamodel.functions.SpineSetupSensor;
import logic.Command;
import logic.PropertiesController;

/**
 * Data Collector GUI - SPINE application to store Sensors Data Set.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */

public class DataCollectorGUI extends javax.swing.JFrame implements ActionListener, TreeSelectionListener, MouseListener, Observer {

	private static final long serialVersionUID = 1L;

	Command command;

	private JTabbedPane jTabbedPaneRight;

	private JTabbedPane jTabbedPaneLeft;

	private JSplitPane jSplitPaneCentral;

	private JToolBar jToolBar;

	private JTree jNodeTree;

	private JSeparator jSeparator1;

	private JToolBar jToolBarDown;

	private JTabbedPane jTabbedPaneLog;

	private JMenuBar guiMenuBar;

	private JMenu applicationMenu;

	private JMenuItem dataSetConfigMenuItem;

	private JMenuItem dataSetNotesMenuItem;

	private JMenuItem exitMenuItem;

	private JMenu wsnCommandMenu;

	private JMenuItem discoveryMenuItem;

	private JMenuItem resetMenuItem;

	private JMenuItem startCompMenuItem;

	private JMenuItem changeLabelMenuItem;

	private JMenuItem aboutMenuItem;

	private JMenuItem settingIt;

	private JFrame frame;

	private JScrollPane jLogscrollpane;

	private JTextArea jLogTextArea;

	private PropertiesController propertControll;

	private String lastClassLabel;

	private ProgressMonitor progressMonitor;

	private Task task;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd, HH:mm:ss");

	/** Construct a Data Collector GUI. */
	public DataCollectorGUI(Command command) {
		super(" Data Collector - SPINE application to store Sensors Data Set ");
		this.command = command;
		initGUI();

		propertControll = PropertiesController.getInstance();

		try {
			propertControll.load();

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	private void initGUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			{
				BorderLayout thisLayout = new BorderLayout();
				getContentPane().setLayout(thisLayout);
				{
					jSplitPaneCentral = new JSplitPane();

					jTabbedPaneRight = new JTabbedPane();
					jSplitPaneCentral.add(jTabbedPaneRight, JSplitPane.RIGHT);
					jTabbedPaneRight.setPreferredSize(new java.awt.Dimension(581, 47));
					// Log panel
					{
						jTabbedPaneLog = new JTabbedPane();
						jTabbedPaneRight.addTab("Log", null, jTabbedPaneLog, null);
						jLogscrollpane = new JScrollPane();
						jLogTextArea = new JTextArea();
						jLogTextArea.setEditable(false);
						jLogscrollpane.setViewportView(jLogTextArea);
						jTabbedPaneLog.setLayout(new BorderLayout());
						jTabbedPaneLog.add(jLogscrollpane);
					}

					{
						jTabbedPaneLeft = new JTabbedPane();
						jSplitPaneCentral.add(jTabbedPaneLeft, JSplitPane.LEFT);
						jTabbedPaneLeft.setPreferredSize(new java.awt.Dimension(103, 349));
					}
				}

				{
					jToolBar = new JToolBar();
					getContentPane().add(jToolBar, BorderLayout.NORTH);
					getContentPane().add(jSplitPaneCentral, BorderLayout.CENTER);
					jSplitPaneCentral.setPreferredSize(new java.awt.Dimension(695, 369));
					{
						jToolBarDown = new JToolBar();
						getContentPane().add(jToolBarDown, BorderLayout.SOUTH);
						jToolBarDown.setPreferredSize(new java.awt.Dimension(695, 23));
					}
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
						dataSetConfigMenuItem = new JMenuItem();
						applicationMenu.add(dataSetConfigMenuItem);
						dataSetConfigMenuItem.setText("Data Set Configuration");
						dataSetConfigMenuItem.addActionListener(this);
					}
					{
						dataSetNotesMenuItem = new JMenuItem();
						applicationMenu.add(dataSetNotesMenuItem);
						dataSetNotesMenuItem.setText("Data Set Notes");
						dataSetNotesMenuItem.addActionListener(this);
					}
					{
						jSeparator1 = new JSeparator();
						applicationMenu.add(jSeparator1);
					}
					{
						aboutMenuItem = new JMenuItem();
						applicationMenu.add(aboutMenuItem);
						aboutMenuItem.setText("About");
						aboutMenuItem.addActionListener(this);
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
					// Menu WSN Command
					wsnCommandMenu = new JMenu();
					guiMenuBar.add(wsnCommandMenu);
					wsnCommandMenu.setText("WSN Command");
					{
						discoveryMenuItem = new JMenuItem();
						wsnCommandMenu.add(discoveryMenuItem);
						discoveryMenuItem.setText("Discovery");
						discoveryMenuItem.addActionListener(this);
					}
					{
						startCompMenuItem = new JMenuItem();
						wsnCommandMenu.add(startCompMenuItem);
						startCompMenuItem.setText("Start Computation");
						startCompMenuItem.addActionListener(this);
						startCompMenuItem.setEnabled(false);
					}
					{
						resetMenuItem = new JMenuItem();
						wsnCommandMenu.add(resetMenuItem);
						resetMenuItem.setText("Stop Computation (Reset and Save Data Set)");
						resetMenuItem.addActionListener(this);
						resetMenuItem.setEnabled(false);
					}
				}

				{
					// Menu Set Class Label
					changeLabelMenuItem = new JMenuItem();
					guiMenuBar.add(changeLabelMenuItem);
					changeLabelMenuItem.setText("Change Class Label");
					changeLabelMenuItem.addActionListener(this);
					changeLabelMenuItem.setEnabled(false);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private MutableTreeNode buildNodeTree(LinkedList<Node> list) {

		DefaultMutableTreeNode n = new DefaultMutableTreeNode("Nodes");

		for (Node snode : list) {

			int nId = snode.getPhysicalID().getAsInt();

			DefaultMutableTreeNode n1 = new DefaultMutableTreeNode("Node " + nId);

			n.add(n1);
		}

		return n;
	}

	public void actionPerformed(ActionEvent arg0) {

		if (arg0.getSource().equals(exitMenuItem)) {
			System.exit(NORMAL);
		}

		if (arg0.getSource().equals(resetMenuItem)) {
			command.resetWsn();
			discoveryMenuItem.setEnabled(false);
			startCompMenuItem.setEnabled(false);
			changeLabelMenuItem.setEnabled(false);
			resetMenuItem.setEnabled(false);
		}

		if (arg0.getSource().equals(startCompMenuItem)) {
			if (jNodeTree != null) {
				ClassLabelDialog wiz = new ClassLabelDialog("Class Label", "Start");
				gui.Utils.onScreenCentered(wiz);
				wiz.setVisible(true);
				// Read CLASS_LABEL
				lastClassLabel = propertControll.getProperty("CLASS_LABEL");
				if (lastClassLabel.contains(",")) {
					lastClassLabel = lastClassLabel.substring(lastClassLabel.lastIndexOf(",") + 2);
				}
				command.setFeatureLabel(lastClassLabel);
				command.startWsn();
				startCompMenuItem.setEnabled(false);
				changeLabelMenuItem.setEnabled(true);
				resetMenuItem.setEnabled(true);
			} else {
				JOptionPane.showMessageDialog(this, "Discovery not yet done", "Start Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		if (arg0.getSource().equals(changeLabelMenuItem)) {
			command.pause(true);
			ClassLabelDialog wiz = new ClassLabelDialog("Class Label", "");
			gui.Utils.onScreenCentered(wiz);
			wiz.setVisible(true);
			// Read CLASS_LABEL
			lastClassLabel = propertControll.getProperty("CLASS_LABEL");
			if (lastClassLabel.contains(",")) {
				lastClassLabel = lastClassLabel.substring(lastClassLabel.lastIndexOf(",") + 2);
			}
			command.setFeatureLabel(lastClassLabel);
			command.pause(false);
		}

		if (arg0.getSource().equals(discoveryMenuItem)) {
			if (jNodeTree == null) {
				avviaProgressMonitor();
				command.discoveryWsn();
				discoveryMenuItem.setEnabled(false);
				startCompMenuItem.setEnabled(true);
				resetMenuItem.setEnabled(false);
			} else {
				JOptionPane.showMessageDialog(this, "Discovery is already done", "Discovery Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		if (arg0.getSource().equals(settingIt)) {

			buildFrameOptions();

		}

		if (arg0.getSource().equals(dataSetConfigMenuItem)) {
			DataSetConfDialog wiz = new DataSetConfDialog("Data Set Configuration", "Data Set format managed: ARFF, CSV and TXT");
			gui.Utils.onScreenCentered(wiz);
			wiz.setVisible(true);
		}

		if (arg0.getSource().equals(dataSetNotesMenuItem)) {
			DataSetNotesDialog wiz = new DataSetNotesDialog("Data Set Notes", "");
			gui.Utils.onScreenCentered(wiz);
			wiz.setVisible(true);
		}

		if (arg0.getSource().equals(aboutMenuItem)) {
			JOptionPane.showMessageDialog(this, "Author :\n" + "Alessia Salmeri : Alessia.Salmeri@telecomitalia.it \n" + "Luigi Buondonno : luigi.buondonno@gmail.com \n"
					+ "Antonio Giordano : antoniogior@hotmail.com \n", "Autori", JOptionPane.INFORMATION_MESSAGE);
		}

	}

	private void avviaProgressMonitor() {
		progressMonitor = new ProgressMonitor(this, "Discovery in progress....", "", 0, 6);
		progressMonitor.setProgress(0);
		task = new Task();
		task.execute();
	}

	public void closeFrame() {
		frame.dispose();
		this.setEnabled(true);
		this.setVisible(true);
	}

	String nodePanelInfo = "";

	JPopupMenu popup;

	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jNodeTree.getLastSelectedPathComponent();

		if (node == null)
			// Nothing is selected.
			return;

		Object nodeInfo = node.getUserObject();
		nodePanelInfo = (String) nodeInfo;
		System.out.println(nodePanelInfo);
		if (nodePanelInfo.equalsIgnoreCase("Setup Sensor")) {
			System.out.println("In Setup Sensor");

		}
	}

	public void mouseClicked(MouseEvent arg0) {
		maybeShowPopup(arg0);
		if (arg0.getClickCount() >= 2) {
			buildFrameOptions();
		}

	}

	public void mouseEntered(MouseEvent arg0) {
		maybeShowPopup(arg0);

	}

	public void mouseExited(MouseEvent arg0) {
		maybeShowPopup(arg0);

	}

	public void mousePressed(MouseEvent arg0) {
		maybeShowPopup(arg0);

	}

	public void mouseReleased(MouseEvent arg0) {
		maybeShowPopup(arg0);

	}

	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	private void buildFrameOptions() {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jNodeTree.getLastSelectedPathComponent();

		if (node == null) {
			return;
		}

		Object nodeInfo = node.getUserObject();
		nodePanelInfo = (String) nodeInfo;

		if (nodePanelInfo.equalsIgnoreCase("Nodes")) {
			return;
		}

		if (nodePanelInfo.contains("Node")) {
			String id = nodePanelInfo.replace("Node ", " ");
			if (id.equalsIgnoreCase(" "))
				return;
			int idn = Integer.parseInt(id.trim());
			Node n = command.getNodeFromId(idn);
			if (n == null)
				return;

			NodeSetting ns = new NodeSetting(this, n);
			ns.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2), 50);
			ns.setVisible(true);
		}

	}

	private void buildNode(LinkedList<Node> list) {

		MutableTreeNode n = buildNodeTree(list);
		DefaultTreeModel treeModel = new DefaultTreeModel(n);
		jNodeTree = new JTree(treeModel);
		jNodeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		jNodeTree.addTreeSelectionListener(DataCollectorGUI.this);
		jTabbedPaneLeft.addTab("Node", null, jNodeTree, null);
		jNodeTree.setPreferredSize(new java.awt.Dimension(307, 351));

		popup = new JPopupMenu();
		settingIt = new JMenuItem("Setting");
		settingIt.addActionListener(this);
		popup.add(settingIt);
		jNodeTree.addMouseListener(this);
	}

	public void update(Observable arg0, Object o) {
		
		boolean isFirstDataReceived=true;

		if (o instanceof String) {
			if (o.equals("StartSave")){
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
			else if (o.equals("EndSave")){
				setCursor(Cursor.getDefaultCursor());			
			}
			else {
			jLogTextArea.append(((String) o).toString() + "\n");
			jLogTextArea.append("\n");
			jLogTextArea.append("\n");
			jLogTextArea.setCaretPosition(jLogTextArea.getText().length());
			}
		}

		if (o instanceof LinkedList && ((LinkedList) o).size() == 0) {
			JOptionPane.showMessageDialog(this, "No node discovered", "Discovery Error", JOptionPane.INFORMATION_MESSAGE);
			discoveryMenuItem.setEnabled(true);
			startCompMenuItem.setEnabled(false);
			return;
		}

		if (o instanceof LinkedList && ((LinkedList) o).getFirst() instanceof Node) {
			System.out.println("notifica arriv");
			if (jNodeTree == null) {
				// msg di discovery
				buildNode((LinkedList<Node>) o);
			} else {
				JOptionPane.showMessageDialog(this, "Discovery already done", "Discovery Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

		}

		if (o instanceof Data) {
			Data data = (Data) o;
			if (isFirstDataReceived){
				propertControll.setProperty("START_TIME", dateTime());
				propertControll.store();
				isFirstDataReceived=false;
			}
			jLogTextArea.append(data.toString() + "\n");
			jLogTextArea.setCaretPosition(jLogTextArea.getText().length());
		}

	}

	public void setupSensor(Node node, SpineSetupSensor setupSensor) {
		command.setupSensor(node, setupSensor);
	}

	public void setupFunction(Node node, SpineSetupFunction setupFunction) {
		command.setupFunction(node, setupFunction);
	}

	public void activateFunction(Node node, SpineFunctionReq functionReq) {
		command.activateFunction(node, functionReq);
	}
	
	static String dateTime() {
		return sdf.format(new Date(System.currentTimeMillis()));
	}

	class Task extends SwingWorker<Void, Void> {

		public Void doInBackground() {

			int progress = 0;
			try {
				while (progress < 6 && !isCancelled()) {
					Thread.sleep(500);
					progress += 1;
					progressMonitor.setProgress(progress);
				}
			} catch (InterruptedException ignore) {
			}
			return null;
		}

		public void done() {
			Toolkit.getDefaultToolkit().beep();
			progressMonitor.close();
		}
	}
}
