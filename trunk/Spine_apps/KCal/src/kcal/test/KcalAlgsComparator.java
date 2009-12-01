/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
�
GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.Â  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MAÂ  02111-1307, USA.
*****************************************************************/

/**
 *
 *  This class runs over multiple data-sets two version of the energy expenditure algorithm:
 *  - a floating point math version
 *  - an integer math version (which also use an integer square root)
 *  
 *  and provides with summary results of the difference between the two 
 *
 * @author Raffaele Gravina <rgravina@wsnlabberkeley.com>
 *
 * @version 1.3
 */

package kcal.test;

import java.util.Arrays;

public class KcalAlgsComparator {

	private static String APP_PROPERTY_PATH = "humanEnergyExp/resources/dataSets/";

	static final String[] FILES = { APP_PROPERTY_PATH + "Edmund_runing.csv",
									APP_PROPERTY_PATH + "Edmund_biking.csv",
									APP_PROPERTY_PATH + "Edmund_sitting.csv",
									APP_PROPERTY_PATH + "Edmund_stairs.csv",
									APP_PROPERTY_PATH + "Edmund_walking.csv",
									APP_PROPERTY_PATH + "Raf_running.csv",
									APP_PROPERTY_PATH + "Raf_sitting.csv",
									APP_PROPERTY_PATH + "Raf_stairs.csv",
									APP_PROPERTY_PATH + "Raf_walking.csv"		
								  };
	
	public static void main(String[] args) {

		SPINEPhysActEE_Sim_Integer kcalInt;
		SPINEPhysActEE_Sim kcal;

		double allFilesDiff = 0;
		
		for (int i = 0; i<FILES.length; i++) {
			System.out.println("DATA -> " + FILES[i]);
			kcalInt = new SPINEPhysActEE_Sim_Integer(FILES[i]);
			System.out.println("Int: " + Arrays.toString((kcalInt.results.toArray())));
			kcal = new SPINEPhysActEE_Sim(FILES[i]);
			System.out.println("Dbl: " + Arrays.toString((kcal.results.toArray())));
			
			double totKCalInt = 0;
			double totKCal = 0;
			for(int j = 0; j<kcalInt.results.size(); j++) {
				totKCalInt += ((Double)kcalInt.results.elementAt(j)).doubleValue();
				totKCal += ((Double)kcal.results.elementAt(j)).doubleValue();
				
			}
			
			allFilesDiff += (totKCalInt - totKCal);
			
			System.out.println("KcalInt TOT: " + (int)totKCalInt + " KCal" + ", on " + kcalInt.results.size() + " min.");
			System.out.println("KcalDbl TOT: " + (int)totKCal + " KCal" + ", on " + kcalInt.results.size() + " min.");
			System.out.println("TOT Difference: " + (int)(totKCalInt - totKCal) + " KCal" + ", on " + kcalInt.results.size() + " min.");
			
			System.out.println();
		}
		
		System.out.println("OVERALL DIFFERENCE (ON " + FILES.length + " GIVEN DATA-SETS) FROM intAlg to DblAlg: " + (int)allFilesDiff + " KCal.");

	}

}
