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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.Â  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MAÂ  02111-1307, USA.
*****************************************************************/

/**
 *
 *  This simple application can be used to save to a file raw data of multiple transmitting SPINE motes 
 *  equipped with ACCELEROMETER sensors. 
 *  
 *  The application uses the BufferedRawDataFunction introduces in SPINE 1.3
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

package test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import spine.SPINEFactory;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.SPINESensorConstants;
import spine.datamodel.BufferedRawData;
import spine.datamodel.Data;
import spine.datamodel.Node;
import spine.datamodel.Sensor;
import spine.datamodel.ServiceMessage;
import spine.datamodel.functions.BufferedRawDataSpineFunctionReq;
import spine.datamodel.functions.BufferedRawDataSpineSetupFunction;
import spine.datamodel.functions.SpineSetupSensor;

public class BufferedRawDataToFile implements SPINEListener {

	private static final int SAMPLING_TIME = 50;
	
	private static final short BUFFER_SIZE = 16;	
	private static final short SHIFT_SIZE = 16;
	
	private static SPINEManager manager;
	private static final long SLEEP_TIME = 200;
		
	private static final String FILE_NAME = "rawData.csv";	
	private static final String SEPARATOR = ";";
	
	
	private BufferedWriter out;
	
	
	public static void main(String[] args) {		
		new BufferedRawDataToFile();
	}
	
	public BufferedRawDataToFile() {
		try {
			manager = SPINEFactory.createSPINEManager("resources/SPINETestApp.properties");				
		
			manager.addListener(this);	
			
			manager.discoveryWsn();
		} catch (InstantiationException e) {e.printStackTrace();}
	}
	
	
	public void discoveryCompleted(Vector activeNodes) {
		Node curr = null;
		for (int j = 0; j<activeNodes.size(); j++) {
			
			curr = (Node)activeNodes.elementAt(j);
			
			System.out.println(curr);			
			
			for (int i = 0; i < curr.getSensorsList().size(); i++) {
				
				byte sensor = ((Sensor)curr.getSensorsList().elementAt(i)).getCode();
				
				if (sensor == SPINESensorConstants.ACC_SENSOR) {
					
					SpineSetupSensor sss = new SpineSetupSensor();
					sss.setSensor(sensor);
					sss.setTimeScale(SPINESensorConstants.MILLISEC);
					sss.setSamplingTime(SAMPLING_TIME);
					manager.setup(curr, sss);

					try { Thread.sleep(SLEEP_TIME); } catch (InterruptedException e) {}
					
					BufferedRawDataSpineSetupFunction brdsf = new BufferedRawDataSpineSetupFunction();
					brdsf.setSensor(sensor);
					brdsf.setBufferSize(BUFFER_SIZE);
					brdsf.setShiftSize(SHIFT_SIZE);
					manager.setup(curr, brdsf);
					
					try { Thread.sleep(SLEEP_TIME); } catch (InterruptedException e) {}
					
					BufferedRawDataSpineFunctionReq brdfr = new BufferedRawDataSpineFunctionReq();
					brdfr.setSensor(sensor);
					brdfr.setChannelsBitmask(SPINESensorConstants.CH1_CH2_CH3_ONLY);					
					manager.activate(curr, brdfr);
					
					try { Thread.sleep(SLEEP_TIME); } catch (InterruptedException e) {}
				}
			}			
		}			
		
		if (activeNodes.size() > 0) {
			manager.startWsn(false, false);
			try {
		        out = new BufferedWriter(new FileWriter(FILE_NAME));
		    } catch (IOException e) {e.printStackTrace();}
		}
	}

	Hashtable superFrame = new Hashtable();
	public void received(Data data) {
		
		if (data instanceof BufferedRawData && ((BufferedRawData)data).getSensorCode() == SPINESensorConstants.ACC_SENSOR) {
			int[][] values = ((BufferedRawData)data).getValues();
			
			superFrame.put(new Integer(data.getNode().getPhysicalID().getAsInt()), values);
			
			if( manager.getActiveNodes().size() == superFrame.keySet().size() ) {
				
				Object[] keyset = superFrame.keySet().toArray();
				Arrays.sort(keyset);			
			
				int[][] superFrameMatrix = new int[3*manager.getActiveNodes().size()][BUFFER_SIZE];
				int i = 0; 
				for (int p = 0; p<keyset.length; p++) {
					int[][] temp = (int[][])superFrame.get((Integer)keyset[p]);
					for (int n = 0; n<temp.length; n++) {
						if(temp[n] != null) 
							System.arraycopy(temp[n], 0, superFrameMatrix[i++], 0, temp[n].length);
					}
				}				
				
				long[] sample = new long[superFrameMatrix.length];
				for (int k = 0; k<superFrameMatrix[0].length; k++) {
					for (int j = 0; j<superFrameMatrix.length; j++) {
						if (superFrameMatrix[j][k] > 32768) 
							superFrameMatrix[j][k] -= 65535;
						sample[j] = superFrameMatrix[j][k]; 						
					}
					
					try {
						String feats = generateStringToWrite(sample);
						out.write(feats + "\r\n");
						out.flush();
					} catch (IOException e) {e.printStackTrace();}
					
				}
				superFrame = new Hashtable(); 
			}
		}
	}
	
	
	public void received(ServiceMessage msg) { System.out.println(msg); }
	
	
	public void newNodeDiscovered(Node newNode) {}
	
	
	private String generateStringToWrite(long features[]) {
		String feats = "";
		
		for (int i = 0; i<features.length-1; i++) 
			feats += features[i] + SEPARATOR;
		
		return feats += features[features.length-1];
	}
	
}
