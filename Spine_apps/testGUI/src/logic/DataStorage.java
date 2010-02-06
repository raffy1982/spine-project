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

package logic;

import java.util.Calendar;
// import java.util.Date;
import java.util.GregorianCalendar;
// import java.util.Hashtable;
import java.util.LinkedList;

import spine.SPINESensorConstants;
import spine.datamodel.Data;
import spine.datamodel.Feature;
import spine.datamodel.FeatureData;
import spine.datamodel.Node;

/**
 * DataStorage manager.
 * 
 * @author Luigi Buondonno : luigi.buondonno@gmail.com
 * @author Antonio Giordano : antoniogior@hotmail.com
 * 
 * @version 1.0
 */

public class DataStorage {

	private LinkedList<Node> node;

	private LinkedList featureTable;

	private CsvFile cvs;

	private int numOfline = 0;

	private final int maxNumberOfLine = 1000;

	private static DataStorage instance = null;

	private DataStorage() {
		this.node = new LinkedList<Node>();
		cvs = new CsvFile();
		featureTable = new LinkedList();
	}

	public static DataStorage getInstance() {
		if (instance == null) {
			instance = new DataStorage();
		}
		return instance;
	}

	public void setNode(LinkedList<Node> node) {
		this.node = node;
	}

	public LinkedList<Node> getNode() {
		return node;
	}

	/**
	 * Get node from physical ID.
	 * 
	 */
	public Node getNodeFromId(int idn) {
		for (Node n : node) {
			if (n.getPhysicalID().getAsInt() == idn)
				return n;
		}
		return null;
	}

	public void addNode(Node n) {
		if (getNodeFromId(n.getPhysicalID().getAsInt()) == null)
			node.add(n);
		return;
	}
	
	/**
	 * Store feature data 
	 * 
	 */

	public void dataIn(int node, Data data) {

		if (data instanceof FeatureData) {
			for (Feature fe : ((FeatureData) data).getFeatures()) {
				String label = this.buildLabel(node, fe);
				LinkedList la = new LinkedList();
				la.addFirst(label);
				la.addLast(fe);
				Calendar cal = new GregorianCalendar();
				String date = "DATE: " + cal.get(Calendar.DAY_OF_MONTH) + "-" + ((cal.get(Calendar.MONTH)) + 1) + "-" + cal.get((Calendar.YEAR)) + " HOUR: " + cal.get(Calendar.HOUR) + ":"
						+ cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + " " + (cal.get(Calendar.AM_PM) == 0 ? "A.M." : "P.M.");
				la.addLast(date);
				featureTable.add(la);
				numOfline++;
			}
			System.out.println("data memorized");
		}
		if (numOfline >= maxNumberOfLine) {
			commitInCsvFile("tmpFile.csv");
			numOfline = 0;
			featureTable.clear();
			System.gc();
		}
		return;
	}

	/**
	 * Create label (<node ID ,sensor, feature Code, ch num >)
	 * 
	 */
	private String buildLabel(int node, Feature data) {
		// label: <node ID ,sensor, feature Code, ch num >
		return node + "_" + data.getSensorCode() + "_" + data.getFeatureCode() + "_" + (SPINESensorConstants.channelBitmaskToString(data.getChannelBitmask()));
	}


	/**
	 * Write feature datain CSV file 
	 * 
	 */
	public void commitInCsvFile(String path) {
		for (int i = 0; i < featureTable.size(); i++) {
			LinkedList l = (LinkedList) featureTable.get(i);
			LinkedList fin = new LinkedList();
			fin.add(l.getFirst());// label
			fin.add(((Feature) l.get(1)).getCh1Value());
			fin.add(((Feature) l.get(1)).getCh2Value());
			fin.add(((Feature) l.get(1)).getCh3Value());
			fin.add(((Feature) l.get(1)).getCh4Value());
			fin.add(l.getLast());
			cvs.writeLine(fin);
		}
		cvs.commitNow();
		cvs.saveAs(path);
		featureTable.clear();
		System.gc();
	}

}
