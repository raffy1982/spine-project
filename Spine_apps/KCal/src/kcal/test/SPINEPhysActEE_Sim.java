/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
 
GNU Lesser General Public License
 
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
 
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.Ã‚Â  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MAÃ‚Â  02111-1307, USA.
*****************************************************************/

/**
 *
 *  This class measures physical activity and estimates 
 *	energy expenditure using FLOATING POINT MATH 
 *
 * 
 * @author Edmund Seto <seto@berkeley.edu>
 * @author Raffaele Gravina <rgravina@wsnlabberkeley.com>
 *
 * @version 1.3
 */

package kcal.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public class SPINEPhysActEE_Sim {

	static final String FILE_NAME = "humanEnergyExp/resources/dataSets/Edmund_sitting.csv";
	
	private int counter = 0;
	private double accum_minute_V = 0;
	private double accum_minute_H = 0;
	
	public Vector results = new Vector();
	
	public static void main(String[] args) {
		new SPINEPhysActEE_Sim(FILE_NAME);
	}
	
	public SPINEPhysActEE_Sim(String fileName) {
		runKcalFeature(loadData(fileName));
	}
	
	
	public void dataReceived(double ch1, double ch2) {
		// This application processes the computed features on the server side to summarize Physical Activity and Estimate
		// Energy Expenditure
		
		double EE_minute;

		// Process the KCAL features
		// KCAL computes a vertical activity count (ch 2) and two horizontal activity counts (ch 1 and 3)
		//System.out.println("KCAL 1 2: " + ch1 + " " + ch2 );

		accum_minute_V += (ch1/30)/1024.0;  // rescale the sensor data by 1024.  // note, divide by 30 because of 30 samples within a sec.
		accum_minute_H += (ch2/30)/1024.0;
		counter++;
		
		// EEact(k) = a*H^p1 + b*V^p2
		//
		// assume:  mass(kg) = 80 kg
		// 			gender = 1 (male)
		//
		// a = (12.81 * mass(kg) + 843.22) / 1000 = 1.87
		// b = (38.90 * mass(kg) - 682.44 * gender(1=male,2=female) + 692.50)/1000 = 3.12
		// p1 = (2.66 * mass(kg) + 146.72)/1000 = 0.36
		// p2 = (-3.85 * mass(kg) + 968.28)/1000 = 0.66
		
		if (counter>=60) {
			EE_minute = 1.87 * java.lang.Math.pow(accum_minute_H, 0.36) + 3.12 * java.lang.Math.pow(accum_minute_V, 0.66);
			//System.out.println("Activity count per minute: " + accum_minute_V);
			accum_minute_V = 0;
			accum_minute_H = 0;
			counter = 0;
			
			//System.out.println("*************************************");
			//System.out.println("EE in units kcal/minute: " + EE_minute/4.184);		// the 4.184 is to convert from KJ to kilocalories 
			results.addElement(new Double(EE_minute/4.184));
			//System.out.println("*************************************");
		}
	}	
	
	public int[][] loadData(String fileName) {
		Vector dataxV = new Vector();
		Vector datayV = new Vector();
		Vector datazV = new Vector();
		int totSamples = 0;
		try {
			String line = null;
			StringTokenizer st = null;
			
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			try {	
				line = br.readLine();
				while (line != null) {
					++totSamples;
					
					st = new StringTokenizer(line,";, \t", false);
					
					dataxV.addElement(new Integer((String)st.nextToken())); 
					datayV.addElement(new Integer((String)st.nextToken()));
					datazV.addElement(new Integer((String)st.nextToken()));
					
					line = br.readLine();
				}
			} catch (IOException e) {e.printStackTrace();}
			
			br.close();
		} catch (IOException e) { e.printStackTrace(); }
		
		int[][] data = new int[3][totSamples];
		
		for (int i=0; i<dataxV.size(); i++)
			try {
				data[0][i] = ((Integer)(dataxV.elementAt(i))).intValue();
				data[1][i] = ((Integer)(datayV.elementAt(i))).intValue();
				data[2][i] = ((Integer)(datazV.elementAt(i))).intValue();
			} catch (NumberFormatException e) {}	
			  catch (ClassCastException e) {}
			  
	    //System.out.print("Finished loading " + totSamples + " samples.\n");
		
		return data;
	}	
	
	
	public void runKcalFeature(int[][] dataAll) {
		int[][] data = new int[3][30];
		int counter = 0;
		for (int j=0; j<dataAll[0].length; j++){
			if(((counter+1)%31)!=0) {
				data[0][counter] = dataAll[0][j];
				data[1][counter] = dataAll[1][j];
				data[2][counter++] = dataAll[2][j];
			}
			else {
				j--;
				counter = 0;
				double[] result = calculateKcal(data);
				dataReceived(result[0], result[1]);				
			}
		}
	}
	
	public double[] calculateKcal(int[][] data) {
		double history[] = new double[3];
		
		double res[] = new double[2];
		double d[] = new double[3];
		double p[] = new double[3];

		
		// this is historical average of the entire data package of 30 samples
        for (int i=0; i<3; i++) {
            for (int j=0; j<data[0].length; j++) {
                history[i] += (data[i][j]/30.0);
            }
        }

	    // v is history
	    // compute d = (ax - vx, ay-vy, az-vz)
	    // compute p = ((d dot v)/(v dot v)) v     		p is the vertical component of d
	    // compute h = d - p				h is the horizontal component of d	
	
	    for (int j=0; j<data[0].length; j++) {
	
	    	// compute d as moving average if possible.
	    	for (int i=0; i<3; i++) {
			    	d[i] = history[i] - data[i][j];
			}
	
	    	double num = 0;
	    	double den = 0;
	    	double value = 0;
	    	for (int i=0; i<3; i++) {
	    		num = (d[0]*history[0] + d[1]*history[1] + d[2]*history[2]);
	    		den = (history[0]*history[0] + history[1]*history[1] + history[2]*history[2]);
	    		value = (num/den)*history[i];
	    		p[i] = value;
	    	}
		
	    	double pMagn = p[0]*p[0] + p[1]*p[1] + p[2]*p[2];
	    	res[0] += Math.sqrt(pMagn);
	
			// subtract for horizontal component
			// compute h = d - p				h is the horizontal component of d
			res[1] += Math.sqrt( (d[0]-p[0])*(d[0]-p[0]) + (d[1]-p[1])*(d[1]-p[1]) + (d[2]-p[2])*(d[2]-p[2]) );	
	    }  
	    
	    return res;
	}
	
}
