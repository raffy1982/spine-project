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
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import spine.datamodel.Node;

/**
 * Property Node panel.
 * 
 * @author Luigi Buondonno : luigi.buondonno@gmail.com
 * @author Antonio Giordano : antoniogior@hotmail.com
 * 
 * @version 1.0
 */
public class PropertyNodePanel extends javax.swing.JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JPanel jPanelNord;

	private JPanel jPanelCenter;

	private JButton ExitButton;

	private NodeInfoPanel jPanelNodeInfo;

	private SpineGUI sg;

	private Node node;

	/**
	 * Property Node panel.
	 * 
	 */
	public PropertyNodePanel(SpineGUI sg, Node n) {
		super();
		this.node = n;
		this.sg = sg;
		initGUI();
	}

	private void initGUI() {
		try {
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(419, 467));
			{
				jPanelNord = new JPanel();
				this.add(jPanelNord, BorderLayout.NORTH);
				jPanelNord.setPreferredSize(new java.awt.Dimension(587, 57));
			}
			{
				jPanelCenter = new JPanel();
				GroupLayout jPanelCenterLayout = new GroupLayout((JComponent) jPanelCenter);
				jPanelCenter.setLayout(jPanelCenterLayout);
				this.add(jPanelCenter, BorderLayout.CENTER);
				jPanelCenter.setPreferredSize(new java.awt.Dimension(410, 407));
				{
					jPanelNodeInfo = new NodeInfoPanel(node);
				}
				{
					ExitButton = new JButton();
					ExitButton.setText("OK");
					ExitButton.addActionListener(this);
				}
				jPanelCenterLayout.setHorizontalGroup(jPanelCenterLayout.createSequentialGroup().addContainerGap(30, 30).addGroup(
						jPanelCenterLayout.createParallelGroup().addComponent(jPanelNodeInfo, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 341, GroupLayout.PREFERRED_SIZE).addGroup(
								GroupLayout.Alignment.LEADING,
								jPanelCenterLayout.createSequentialGroup().addGap(129).addComponent(ExitButton, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE).addGap(113)))
						.addContainerGap(48, 48));
				jPanelCenterLayout.setVerticalGroup(jPanelCenterLayout.createSequentialGroup().addComponent(jPanelNodeInfo, GroupLayout.PREFERRED_SIZE, 326, GroupLayout.PREFERRED_SIZE).addGap(0, 54,
						Short.MAX_VALUE).addComponent(ExitButton, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE).addContainerGap());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(ExitButton)) {
			sg.closeFrame();
		}

	}

}
