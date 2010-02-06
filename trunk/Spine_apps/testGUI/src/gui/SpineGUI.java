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
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import spine.datamodel.Data;
import spine.datamodel.Node;
import spine.datamodel.ServiceMessage;
import spine.datamodel.functions.SpineFunctionReq;
import spine.datamodel.functions.SpineSetupFunction;
import spine.datamodel.functions.SpineSetupSensor;
import spine.datamodel.serviceMessages.ServiceErrorMessage;

import logic.Command;

/**
 * GUI for testing SPINE sensor node.
 * 
 * @author Luigi Buondonno : luigi.buondonno@gmail.com
 * @author Antonio Giordano : antoniogior@hotmail.com
 * 
 * @version 1.0
 */

public class SpineGUI extends javax.swing.JFrame implements ActionListener, TreeSelectionListener, MouseListener, Observer {

	private static final long serialVersionUID = 1L;

	Command command;

	private JMenuItem helpMenuItem;

	private JMenu jMenu5;

	private JTabbedPane jTabbedPaneCentralUp;

	private JTabbedPane jTabbedPaneCentralDw;

	private JSplitPane jSplitPane1;

	private JTabbedPane jTabbedPaneLeft;

	private JPanel jPanelCentral;

	private JSplitPane jSplitPaneCentral;

	private JToolBar jToolBar;

	private JTree jNodeTree;

	private JMenuItem exitMenuItem;

	private JSeparator jSeparator1;

	private JSeparator jSeparator2;

	private JMenuItem startCompMenuItem;

	private JTabbedPane jTabbedPaneError;

	private JScrollPane jErrorScrollPane;

	private JTabbedPane jNodePropertyTabbedPane;

	private JTextArea jErrorTextArea;

	private JScrollPane jNodePropertyScrollPane;

	private JTabbedPane jNodesStatusTabbedPane;

	private JToolBar jToolBarDown;

	private JScrollPane nodeSettingPane;

	private JTabbedPane jTabbedPaneWar;

	private JTabbedPane jTabbedPaneLog;

	private JMenuItem resetMenuItem;

	private JMenuItem discoveryMenuItem;

	private JMenu jMenu3;

	private JMenuBar jMenuBar1;

	private JMenuItem settingIt, prop;

	private JFrame frame;

	private JScrollPane jLogscrollpane;

	private JTextArea jLogTextArea;

	private JScrollPane jWarscrollpane;

	private JTextArea jWarTextArea;

	private ProgressMonitor progressMonitor;

	static final private String OPTIONS = "Option";

	static final private String STARTDISCO = "Discovery";

	static final private String STOP = "Stop";

	private static final String STARTCOMP = "Start Comp";

	private Task task;

	private JFileChooser fc;

	private JButton jbim;

	private JButton jbtex;

	private JMenu save;

	private JMenuItem simpleText;

	private JMenuItem csvFile;

	public SpineGUI(Command command) {
		super(" G.U.I. for Testing ");
		this.command = command;
		initGUI();
	}

	private void initGUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			{
				BorderLayout thisLayout = new BorderLayout();
				getContentPane().setLayout(thisLayout);
				{
					jSplitPaneCentral = new JSplitPane();
					{
						jPanelCentral = new JPanel();
						BorderLayout jPanelCentralLayout = new BorderLayout();
						jSplitPaneCentral.add(jPanelCentral, JSplitPane.RIGHT);
						jPanelCentral.setLayout(jPanelCentralLayout);
						{
							jSplitPane1 = new JSplitPane();
							jPanelCentral.add(jSplitPane1, BorderLayout.CENTER);
							jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
							{
								jTabbedPaneCentralDw = new JTabbedPane();
								jSplitPane1.add(jTabbedPaneCentralDw, JSplitPane.RIGHT);
								jTabbedPaneCentralDw.setPreferredSize(new java.awt.Dimension(581, 47));
								{
									jTabbedPaneLog = new JTabbedPane();
									jTabbedPaneCentralDw.addTab("Log", null, jTabbedPaneLog, null);
									jLogscrollpane = new JScrollPane();
									jLogTextArea = new JTextArea();
									jLogscrollpane.setViewportView(jLogTextArea);

									jTabbedPaneLog.setLayout(new BorderLayout());
									jTabbedPaneLog.add(jLogscrollpane);
								}
								{
									jTabbedPaneError = new JTabbedPane();
									jTabbedPaneCentralDw.addTab("Error", null, jTabbedPaneError, null);
									jTabbedPaneError.setLayout(new BorderLayout());
									jTabbedPaneError.add(getJErrorScrollPane());
								}
								{
									jTabbedPaneWar = new JTabbedPane();
									jTabbedPaneCentralDw.addTab("Warning", null, jTabbedPaneWar, null);

									jWarscrollpane = new JScrollPane();
									jWarTextArea = new JTextArea();
									jWarscrollpane.setViewportView(jWarTextArea);

									jTabbedPaneWar.setLayout(new BorderLayout());
									jTabbedPaneWar.add(jWarscrollpane);

								}
							}
							{
								jTabbedPaneCentralUp = new JTabbedPane();
								jSplitPane1.add(jTabbedPaneCentralUp, JSplitPane.LEFT);
								jTabbedPaneCentralUp.setPreferredSize(new java.awt.Dimension(372, 238));
								{
									nodeSettingPane = new JScrollPane();

									jTabbedPaneCentralUp.addTab("Nodes Status", null, nodeSettingPane, null);
									jTabbedPaneCentralUp.addTab("Nodes Property", null, getJNodePropertyScrollPane(), null);
									nodeSettingPane.setViewportView(getJNodesStatusTabbedPane());

								}
							}
						}
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
					addButtons(jToolBar);
					getContentPane().add(jSplitPaneCentral, BorderLayout.CENTER);
					jSplitPaneCentral.setPreferredSize(new java.awt.Dimension(695, 369));

					{
						jToolBarDown = new JToolBar();
						String imgLocation = "/img/Sensore.gif";
						URL imageURL = SpineGUI.class.getResource(imgLocation);
						jbim = new JButton(new ImageIcon(imageURL));
						jbim.setBorderPainted(false);

						jbtex = new JButton("");
						jbtex.setBorderPainted(false);

						jToolBarDown.add(jbim);
						jToolBarDown.add(jbtex);

						jbim.setVisible(false);
						jbtex.setVisible(false);
						getContentPane().add(jToolBarDown, BorderLayout.SOUTH);
						jToolBarDown.setPreferredSize(new java.awt.Dimension(695, 30));
					}
				}
			}
			this.setSize(703, 476);
			{
				jMenuBar1 = new JMenuBar();
				ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
				JPopupMenu.setDefaultLightWeightPopupEnabled(false);
				setJMenuBar(jMenuBar1);

				jMenu3 = new JMenu();
				jMenu3.setFocusTraversalKeysEnabled(false);
				jMenuBar1.add(jMenu3);
				jMenu3.setText("Application");

				discoveryMenuItem = new JMenuItem();
				jMenu3.add(discoveryMenuItem);
				discoveryMenuItem.setText("Discovery");
				discoveryMenuItem.addActionListener(this);

				startCompMenuItem = new JMenuItem();
				jMenu3.add(startCompMenuItem);
				startCompMenuItem.setText("Start Computation");
				startCompMenuItem.addActionListener(this);
				startCompMenuItem.setEnabled(false);

				resetMenuItem = new JMenuItem();
				jMenu3.add(resetMenuItem);
				resetMenuItem.setText("Reset");
				resetMenuItem.addActionListener(this);
				resetMenuItem.setEnabled(false);

				jSeparator1 = new JSeparator();
				jMenu3.add(jSeparator1);

				save = new JMenu();
				save.setText("Save Log area as");
				save.addActionListener(this);
				save.setEnabled(false);

				simpleText = new JMenuItem();
				simpleText.setText("Simple txt file");
				simpleText.addActionListener(this);
				save.add(simpleText);

				csvFile = new JMenuItem();
				csvFile.setText("Csv file");
				csvFile.addActionListener(this);
				save.add(csvFile);

				jMenu3.add(save);

				jSeparator2 = new JSeparator();
				jMenu3.add(jSeparator2);

				exitMenuItem = new JMenuItem();
				exitMenuItem.addActionListener(this);
				jMenu3.add(exitMenuItem);
				exitMenuItem.setText("Exit");
				{
					jMenu5 = new JMenu();
					jMenuBar1.add(jMenu5);
					jMenu5.setText("Help");
					{
						helpMenuItem = new JMenuItem();
						jMenu5.add(helpMenuItem);
						helpMenuItem.setText("About");
						helpMenuItem.addActionListener(this);
					}
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
			DefaultMutableTreeNode n11 = new DefaultMutableTreeNode(nId + " Setup Sensor");
			DefaultMutableTreeNode n12 = new DefaultMutableTreeNode(nId + " Setup Function");
			DefaultMutableTreeNode n13 = new DefaultMutableTreeNode(nId + " Activate Function");
			DefaultMutableTreeNode n14 = new DefaultMutableTreeNode(nId + " Disable Function");
			n1.add(n11);
			n1.add(n12);
			n1.add(n13);
			n1.add(n14);

			n.add(n1);
		}

		return n;
	}

	public void actionPerformed(ActionEvent arg0) {

		String cmd = arg0.getActionCommand();

		// Handle each button.
		if (OPTIONS.equals(cmd)) {

			if (jNodeTree == null) {
				JOptionPane.showMessageDialog(this, "Discovery is not yet done", "Discovery Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			buildFrameOptions();

		} else if (STARTDISCO.equals(cmd)) {
			avviaProgressMonitor();
			command.discoveryWsn();
			startCompMenuItem.setEnabled(true);
			return;

		} else if (STOP.equals(cmd)) {
			command.resetWsn();
			jbim.setVisible(false);
			jbtex.setVisible(false);
			save.setEnabled(true);
			return;
		} else if (STARTCOMP.equals(cmd)) {

			if (jNodeTree != null) {
				command.startWsn();
				resetMenuItem.setEnabled(true);
				return;
			} else {
				JOptionPane.showMessageDialog(this, "Discovery is not yet done", "Start Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

		}

		if (arg0.getSource().equals(exitMenuItem)) {
			System.exit(NORMAL);
		}

		if (arg0.getSource().equals(resetMenuItem)) {
			jbim.setVisible(false);
			jbtex.setVisible(false);
			command.resetWsn();
			save.setEnabled(true);
			return;
		}

		if (arg0.getSource().equals(startCompMenuItem)) {
			if (jNodeTree != null) {
				command.startWsn();
				jbim.setEnabled(false);
				resetMenuItem.setEnabled(true);
				return;
			} else {
				JOptionPane.showMessageDialog(this, "Discovery is not yet done", "Start Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		if (arg0.getSource().equals(discoveryMenuItem)) {
			avviaProgressMonitor();
			command.discoveryWsn();
			return;
		}

		if (arg0.getSource().equals(settingIt)) {
			buildFrameOptions();
		}

		if (arg0.getSource().equals(prop)) {
			buildFrameProp();
		}

		if (arg0.getSource().equals(helpMenuItem)) {
			JOptionPane.showMessageDialog(this, "Autori :\n" + "Luigi Buondonno : luigi.buondonno@gmail.com \n" + "Antonio Giordano : antoniogior@hotmail.com \n", "Autori",
					JOptionPane.INFORMATION_MESSAGE);
		}

		if (arg0.getSource().equals(simpleText)) {

			fc = new JFileChooser();
			fc.showSaveDialog(this);
			File f = fc.getSelectedFile();
			command.storeDatainSTF(f.getAbsolutePath(), jLogTextArea.getText());

		}

		if (arg0.getSource().equals(csvFile)) {
			fc = new JFileChooser();
			fc.showSaveDialog(this);
			File f = fc.getSelectedFile();
			command.storeDatainCsv(f.getAbsolutePath());
			return;
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

	private List<Integer> nodeMonitored = new LinkedList<Integer>();

	/**
	 * Tree selection event handler.
	 * 
	 */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jNodeTree.getLastSelectedPathComponent();

		if (node == null)
			// Nothing is selected.
			return;

		Object nodeInfo = node.getUserObject();
		nodePanelInfo = (String) nodeInfo;
		System.out.println(nodePanelInfo);
		if (nodePanelInfo.equalsIgnoreCase("Setup Sensor")) {
			System.out.println("Setup Sensor");

		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() >= 2) {
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

	protected JButton makeNavigationButton(String imageName, String actionCommand, String toolTipText, String altText) {

		String imgLocation = "/img/" + imageName + ".gif";
		URL imageURL = SpineGUI.class.getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.setPreferredSize(new Dimension(40, 30));
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}

		return button;
	}

	protected void addButtons(JToolBar toolBar) {
		JButton button = null;

		// first button
		button = makeNavigationButton("option", OPTIONS, "Selected node properties", "Option");
		toolBar.add(button);

		// separator
		toolBar.addSeparator();

		// second button block
		button = makeNavigationButton("discovery", STARTDISCO, "Start Dicovery", "Start Dicovery");
		toolBar.add(button);

		button = makeNavigationButton("startComp", STARTCOMP, "Start Computation", "Start Computation");
		toolBar.add(button);

		button = makeNavigationButton("stop", STOP, "Reset WSN", "Reset WSN");
		toolBar.add(button);

	}

	private void buildFrameProp() {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jNodeTree.getLastSelectedPathComponent();
		if (node == null || node.getParent() == null) {
			return;
		}
		Object nodeInfo = node.getUserObject();
		Object nodeInfop = ((DefaultMutableTreeNode) node.getParent()).getUserObject();
		nodePanelInfo = (String) nodeInfo;
		String par = (String) nodeInfop;

		if (nodePanelInfo.contains("Node") || par.contains("Node")) {
			String id = nodePanelInfo.replaceAll("[a-zA-Zàòèéùì’ ]+", "");
			int idn = Integer.parseInt(id.trim());
			Node n = command.getNodeFromId(idn);
			if (n == null)
				return;
			frame = new JFrame("Property Node " + id);
			frame.getContentPane().add(new PropertyNodePanel(this, n));
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
			frame.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2), 50);
			this.setEnabled(false);
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
			// System.out.println(n);
			if (n == null)
				return;
			frame = new JFrame("Property Node " + id);
			PropertyNodePanel pn = new PropertyNodePanel(this, n);
			pn.setVisible(true);
			frame.getContentPane().add(pn);
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
			frame.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2), 50);
			this.setEnabled(false);
		}

		if (nodePanelInfo.contains("Setup Sensor")) {
			String id = nodePanelInfo.replaceAll("[a-zA-Zàòèéùì’ ]+", "");

			int idn = Integer.parseInt(id.trim());
			Node n = command.getNodeFromId(idn);
			if (n == null)
				return;
			frame = new JFrame("Setup Sensor Node " + id);
			frame.getContentPane().add(new SetupSensorPanel(this, n));
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
			frame.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2), 50);
			this.setEnabled(false);
		}

		if (nodePanelInfo.contains(" Setup Function")) {
			String id = nodePanelInfo.replaceAll("[a-zA-Zàòèéùì’ ]+", "");

			int idn = Integer.parseInt(id.trim());
			Node n = command.getNodeFromId(idn);
			if (n == null)
				return;
			frame = new JFrame("Setup Function Node " + id);
			frame.getContentPane().add(new SetupFunctionPanel(this, n));
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
			frame.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2), 50);
			this.setEnabled(false);
		}

		if (nodePanelInfo.contains(" Activate Function")) {
			String id = nodePanelInfo.replaceAll("[a-zA-Zàòèéùì’ ]+", "");

			int idn = Integer.parseInt(id.trim());
			Node n = command.getNodeFromId(idn);
			if (n == null)
				return;
			frame = new JFrame("Activate Function Node " + id);
			frame.getContentPane().add(new ActivateFunctionPanel(this, n));
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
			frame.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2), 50);
			this.setEnabled(false);
		}

		if (nodePanelInfo.contains(" Disable Function")) {
			String id = nodePanelInfo.replaceAll("[a-zA-Zàòèéùì’ ]+", "");

			int idn = Integer.parseInt(id.trim());
			Node n = command.getNodeFromId(idn);
			if (n == null)
				return;
			frame = new JFrame("Disable Function Node " + id);
			frame.getContentPane().add(new DisableFunctionPanel(this, n));
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
			frame.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().height / 2), 50);
			this.setEnabled(false);
		}

	}

	private void buildNode(LinkedList<Node> list) {

		MutableTreeNode n = buildNodeTree(list);
		DefaultTreeModel treeModel = new DefaultTreeModel(n);
		jNodeTree = new JTree(treeModel);
		jNodeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		String imgLoc = "/img/telos.gif";
		String imgLocop = "/img/optionfile.gif";

		URL imageURL = SpineGUI.class.getResource(imgLoc);
		URL imageURLop = SpineGUI.class.getResource(imgLocop);

		if (imageURL != null && imageURLop != null) {
			ImageIcon nodeIcon = new ImageIcon(imageURL);
			ImageIcon opIcon = new ImageIcon(imageURLop);
			if (nodeIcon != null) {
				DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
				renderer.setLeafIcon(opIcon);
				renderer.setOpenIcon(nodeIcon);
				renderer.setClosedIcon(nodeIcon);
				jNodeTree.setCellRenderer(renderer);
			}
		} else {
			System.out.println("img nn trovata " + imgLoc);
		}
		jNodeTree.addTreeSelectionListener(SpineGUI.this);
		jTabbedPaneLeft.addTab("Node", null, jNodeTree, null);
		jNodeTree.setPreferredSize(new java.awt.Dimension(307, 351));

		popup = new JPopupMenu();
		settingIt = new JMenuItem("Setting");
		settingIt.addActionListener(this);
		JSeparator js = new JSeparator();
		prop = new JMenuItem("Properties");
		prop.addActionListener(this);
		popup.add(settingIt);
		popup.add(js);
		popup.add(prop);
		jNodeTree.addMouseListener(this);

	}

	boolean first = true;

	public void update(Observable arg0, Object o) {

		if (o instanceof String) {

			jLogTextArea.append(((String) o).toString() + "\n");
			jLogTextArea.append("\n");
			jLogTextArea.append("\n");
			jLogTextArea.setCaretPosition(jLogTextArea.getText().length());

		}

		if (o instanceof LinkedList && ((LinkedList) o).size() == 0) {
			JOptionPane.showMessageDialog(this, "No node discovered", "Discovery Error", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		if (o instanceof LinkedList && ((LinkedList) o).getFirst() instanceof Node) {

			jNodePropertyTabbedPane.removeAll();
			jTabbedPaneLeft.removeAll();
			buildNode((LinkedList<Node>) o);
			for (int i = 0; i < ((LinkedList<Node>) o).size(); i++) {
				jNodePropertyTabbedPane.add(((Node) (((LinkedList<Node>) o).get(i))).getPhysicalID().getAsInt() + " ", new NodeInfoPanel(((Node) (((LinkedList<Node>) o).get(i)))));
			}

		}

		if (o instanceof Data) {
			Data data = (Data) o;
			if (first) {
				jbim.setVisible(true);
				jbtex.setVisible(true);
				jbtex.setText("Receiving messages ....");
				first = false;
			}
			jLogTextArea.append(data.toString() + "\n");
			jLogTextArea.setCaretPosition(jLogTextArea.getText().length());

			if (!nodeMonitored.contains(data.getNode().getPhysicalID().getAsInt())) {
				nodeMonitored.add(data.getNode().getPhysicalID().getAsInt());
				jNodesStatusTabbedPane.add(data.getNode().getPhysicalID().getAsInt() + "", new NodeMonitoredInfoPanel(data));

			}
		}

		if (o instanceof ServiceMessage) {
			ServiceMessage servmes = (ServiceMessage) o;
			if (servmes instanceof ServiceErrorMessage) {
				jErrorTextArea.append(servmes.toString() + "\n");
				jErrorTextArea.setCaretPosition(jErrorTextArea.getText().length());
			} else {
				jWarTextArea.append(servmes.toString() + "\n");
				jWarTextArea.setCaretPosition(jWarTextArea.getText().length());
			}
		}

	}

	private JTabbedPane getJNodesStatusTabbedPane() {
		if (jNodesStatusTabbedPane == null) {
			jNodesStatusTabbedPane = new JTabbedPane();

		}
		return jNodesStatusTabbedPane;
	}

	private JScrollPane getJNodePropertyScrollPane() {
		if (jNodePropertyScrollPane == null) {
			jNodePropertyScrollPane = new JScrollPane();
			jNodePropertyScrollPane.setViewportView(getJNodePropertyTabbedPane());
		}
		return jNodePropertyScrollPane;
	}

	private JTabbedPane getJNodePropertyTabbedPane() {
		if (jNodePropertyTabbedPane == null) {
			jNodePropertyTabbedPane = new JTabbedPane();
		}
		return jNodePropertyTabbedPane;
	}

	/**
	 * Setup sensor command.
	 * 
	 */
	public void setupSensor(Node node, SpineSetupSensor setupSensor) {
		command.setupSensor(node, setupSensor);
	}

	/**
	 * Setup function command.
	 * 
	 */
	public void setupFunction(Node node, SpineSetupFunction setupFunction) {
		command.setupFunction(node, setupFunction);
	}

	

	/**
	 * Activate function command.
	 * 
	 */
	public void actFunction(Node node, SpineFunctionReq functionReq) {
		command.actFunction(node, functionReq);
	}

	/**
	 * Deactivate function command.
	 * 
	 */
	public void deactivateFunction(Node node, SpineFunctionReq functionReq) {
		command.deactivateFunction(node, functionReq);
	}

	private JScrollPane getJErrorScrollPane() {
		if (jErrorScrollPane == null) {
			jErrorScrollPane = new JScrollPane();
			jErrorScrollPane.setViewportView(getJErrorTextArea());
		}
		return jErrorScrollPane;
	}

	private JTextArea getJErrorTextArea() {
		if (jErrorTextArea == null) {
			jErrorTextArea = new JTextArea();
		}
		return jErrorTextArea;
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


