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

package main;

import gui.DataCollectorGUI;
import logic.Command;
import logic.Manager;

/**
 * Data Collector - SPINE application to store Sensors Data Set (ARFF, CSV or
 * TXT).
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */
public class DataCollector {

	public static void main(String[] args) {
		Command c = Manager.getInstance();
		DataCollectorGUI instance = new DataCollectorGUI(c);
		instance.setLocationRelativeTo(null);
		instance.setVisible(true);
		((Manager) c).addObserver(instance);
	}
}
