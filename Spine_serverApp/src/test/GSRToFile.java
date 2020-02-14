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
 *  This program saves GSR data (ADC raw values, resistance signal, and other features to CSV file)
 *
 * @author Matteo Aloi
 * @author Enrico Ubaldino
 *
 * @version 1.3
 */

package test;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import com.fazecast.jSerialComm.SerialPort;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import spine.SPINEFactory;
import spine.SPINEFunctionConstants;
import spine.SPINEListener;
import spine.SPINEManager;
import spine.SPINESensorConstants;

import spine.datamodel.*;

//functions sub-package to import for running apps on SPINE1.3 
import spine.datamodel.functions.*;

public class GSRToFile implements SPINEListener {

	private static final int SAMPLING_TIME = 100; //frequenza tra 5Hz e 15Hz
	
	private static final short WINDOW_SIZE = 80;

	private static final short SHIFT_SIZE = 40;

	private static SPINEManager manager;
	private static final long SLEEP_TIME = 200;
	
	private static final String FILE_NAME = "GSRrawData.csv";	
	private static final String SEPARATOR = ";";//cambia colonna
	
	private static int counter = 0;
	private static int counter_alarm = 0;
	private BufferedWriter out;
	
	static boolean recive = false;
	static SerialPort chosenPort;
	static int x = 0;
	int adcvalue=0;
	int payload=0;
	int media =0;
	int mediana =0;
	float phasic2 = 0;
	int minvalue =0;
	int maxvalue =0;
	int devst=0;
	int phasic=0;
	static int Rf;
    static int  j = 0;
    int resistance = 0;
    int range = 0;
    
	public static void main(String[] args) {

		// create and configure the window
		JFrame window = new JFrame();
		window.setTitle("Sensor Graph GUI");
		window.setSize(600, 400);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// create a drop-down box and connect button, then place them at the top of the window
		JComboBox<String> portList = new JComboBox<String>();
		JButton connectButton = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.add(portList);
		topPanel.add(connectButton);
		window.add(topPanel, BorderLayout.NORTH);
		
		// populate the drop-down box
		SerialPort[] portNames = SerialPort.getCommPorts();
		for(int i = 0; i < portNames.length; i++)
			portList.addItem(portNames[i].getSystemPortName());
		
		// create the line graph
		XYSeries series = new XYSeries("Acquire DATA");
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		JFreeChart chart = ChartFactory.createXYLineChart("Phasic Driver", "Time (seconds)", "GSR Reading", dataset);
		window.add(new ChartPanel(chart), BorderLayout.CENTER);
		
		// configure the connect button and use another thread to listen for data
		connectButton.addActionListener(new Action());	
		System.out.println("SENSE EMOTIONS");
		System.out.println("Author: Aloi&Ubaldino");
		window.setVisible(true);
	
		
	}
	
	static class Action implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println(x);						
			new GSRToFile();
			
		}
		
	}
	
	
	public GSRToFile() {

		try {	
			// Initialize SPINE by passing the fileName with the configuration properties
			manager = SPINEFactory.createSPINEManager("resources/SPINETestApp.properties");
			
			// ... then we need to register a SPINEListener implementation to the SPINE manager instance
			// (I register myself since I'm a SPINEListener implementation!)
			manager.addListener(this);	
			
			// we could change the logging level to avoid e.g. prints for the ongoing and incoming low level messages.
			SPINEManager.getLogger().setLevel(Level.ALL);
			
			// We could even decide to change the default discoveryProcedureTimeout; after that: ok ...
			/* manager.setDiscoveryProcedureTimeout(1000); */
			
			// ... let's start playing! 
			manager.discoveryWsn();
			
		} catch (InstantiationException e) {
			// if we are here, then the SPINEManager initialization did not work properly
			e.printStackTrace();
		}
		

	}

	
	
	//public void newNodeDiscovered(Node newNode) {
		// after my 'discoveryWsn' request we should receive this event one or more times.  
		// However, we prefer to wait for the 'discoveryCompleted' event		
//	}
	
	public void discoveryCompleted(Vector activeNodes) { // quici entro dopo che ho scoperto i nodi e faccio stup frequenze di campionamento e feature che chiedo al nodo media media ecc perch� ste cose se le calcola direttamente il telosb
		
		// we loop over the discovered nodes (hopefully, at least a node's showed up!)
		Node curr = null;
		for (int j = 0; j<activeNodes.size(); j++) {
			
			curr = (Node)activeNodes.elementAt(j);
			
			// we print for each node its details (nodeID, sensors and functions provided)
			System.out.println(curr);			
			
			// for each node, we look for specific services
			for (int i = 0; i < curr.getSensorsList().size(); i++) {
				
				byte sensor = ((Sensor)curr.getSensorsList().elementAt(i)).getCode();
				
				// if the current node has an accelerometer, then...
				if (sensor == SPINESensorConstants.GSR_SENSOR) {
					System.out.println("SENSOR GSR FOUND!!");	
                    /*try { Thread.sleep(SLEEP_TIME); } catch (InterruptedException e) {}*/
					SpineSetupSensor sss = new SpineSetupSensor();
					sss.setSensor(sensor);
					sss.setTimeScale(SPINESensorConstants.MILLISEC);
					sss.setSamplingTime(SAMPLING_TIME);
					manager.setup(curr, sss);
					
					/*BufferedRawDataSpineSetupFunction brdsf = new BufferedRawDataSpineSetupFunction();
					brdsf.setSensor(sensor);
					brdsf.setBufferSize(BUFFER_SIZE);
					brdsf.setShiftSize(SHIFT_SIZE);
					manager.setup(curr, brdsf);*/
					
					FeatureSpineSetupFunction ssf = new FeatureSpineSetupFunction();
					ssf.setSensor(sensor);
					ssf.setWindowSize(WINDOW_SIZE);
					ssf.setShiftSize(SHIFT_SIZE);
					manager.setup(curr, ssf);
				
					try { Thread.sleep(SLEEP_TIME); } catch (InterruptedException e) {}
					
					/*BufferedRawDataSpineFunctionReq brdfr = new BufferedRawDataSpineFunctionReq();
					brdfr.setSensor(sensor);
					brdfr.setChannelsBitmask(SPINESensorConstants.CH1_ONLY);//all invece di ch1_only					
					manager.activate(curr, brdfr);*/
					
					/*try { Thread.sleep(SLEEP_TIME); } catch (InterruptedException e) {}*/
					
					FeatureSpineFunctionReq sfr = new FeatureSpineFunctionReq();
					sfr.setSensor(sensor);
					sfr.add(new Feature(SPINEFunctionConstants.RAW_DATA,SPINESensorConstants.CH1_ONLY));
					sfr.add(new Feature(SPINEFunctionConstants.MIN,SPINESensorConstants.CH1_ONLY));
					sfr.add(new Feature(SPINEFunctionConstants.MAX,SPINESensorConstants.CH1_ONLY));
					sfr.add(new Feature(SPINEFunctionConstants.MEAN, SPINESensorConstants.CH1_ONLY));
					sfr.add(new Feature(SPINEFunctionConstants.MEDIAN,SPINESensorConstants.CH1_ONLY)); 	
					sfr.add(new Feature(SPINEFunctionConstants.ST_DEV, SPINESensorConstants.CH1_ONLY));
					//sfr.add(new Feature(SPINEFunctionConstants.PHASIC_DRIVER, SPINESensorConstants.CH1_ONLY));
					manager.activate(curr, sfr);	
					
					
				}	
								
			}			
		}
		
		

		
		// ... start the sensor network sensing and computing our aforeactivated services.
		if (activeNodes.size() > 0) {
			manager.startWsn(true, true); // we can tune a few node parameters at run-time for reducing the power consumption and the packets drop. 
		try {
	        out = new BufferedWriter(new FileWriter(FILE_NAME));
	        out.write("ADC Value");
	        out.write(";");
	        out.write("Resistance [Ohm]");
	        out.write(";");
	        out.write("Min");
	        out.write(";");
	        out.write("Max");
	        out.write(";");
	        out.write("Media");
	        out.write(";");
	        out.write("Mediana");
	        out.write(";");
	        out.write("Dev.Standard");
	        out.write(";");
	        out.write("Phasic Driver");
	        out.write(";");
	        out.write("\r\n");	        
	    } catch (IOException e) {e.printStackTrace();}}
	

		}
	//private Feature[] features;

    
	public void received(Data data) {

			Feature[] features;
			features = ((FeatureData)data).getFeatures();
			System.out.println(data);
			
			//FEATURE
			payload=features[0].getCh1Value();
			range = Range(payload);
			adcvalue = (payload&0x0fff);
			minvalue=(features[1].getCh1Value()&0x0fff);
			//System.out.println(features[1].getFeatureCode());
			maxvalue = (features[2].getCh1Value()&0x0fff);
			//System.out.println(features[2].getFeatureCode());
			media = (features[3].getCh1Value()&0x0fff);
			//System.out.println(features[3].getFeatureCode());
			mediana = (features[4].getCh1Value()&0x0fff);
			devst = (features[5].getCh1Value()&0x0fff);
			//phasic = (features[6].getCh1Value()&0x0fff);						
			phasic2 = cal_ph(mediana , media, range);
			
			System.out.println("Payload = " + payload);
			System.out.println("ADC = " + adcvalue);
			
			//Calcoli

			System.out.println("Min = " + ADCtoR(range,(minvalue)));
			System.out.println("Max = " + ADCtoR(range,(maxvalue)));
			System.out.println("Media = " + ADCtoR(range,(media)));
			System.out.println("Mediana = " + ADCtoR(range,(mediana)));
			System.out.println("Dev_Standard = " + ADCtoR(range,(devst)));
			System.out.println("Resistenza � = " + ADCtoR(range,adcvalue));
			//System.out.println("Phasic = " + ADCtoR(range,(phasic)));
			System.out.println("Phasic2 = " + phasic2);

			//Scrittura su file
			try {
		    out.write(adcvalue + ";");
			out.write(ADCtoR(range,adcvalue) + ";");
			out.write(ADCtoR(range,minvalue) + ";");
			out.write(ADCtoR(range,maxvalue) + ";");
			out.write(ADCtoR(range,media) + ";");
			out.write(ADCtoR(range,mediana) + ";");
			out.write(ADCtoR(range,devst) + ";");
			out.write(phasic2 + "\r\n");
			out.flush(); 

			} catch (IOException e) {e.printStackTrace();}
		
        
	}	
	
	public void received(ServiceMessage msg) {
		// for this simple application, I just like to print the service message received
		System.out.println(msg);
		
	}	
public void newNodeDiscovered(Node newNode) {}
	
	private static int Range(int valore) {
		System.out.println("valore: " +valore);
		//private static resistance;
		switch (valore&0xf000) { 
		  case 49152: 
			  Rf = 3300000; 
			  break; 
	      case 32768:
		      Rf = 1000000; 
		       break; 
		  case 16384:
			  Rf = 287000; 
			  break; 
	      case 0:
		       Rf = 40000;
		       break; 
		  }
		 System.out.println("Rf: "+Rf);
		 //resistance=(int) (Rf/((float)((valore&0x0fff)*3.0/4095.0/0.5) -1.0));
		 //System.out.println("Resistenza: "+resistance);
		 
		return Rf;
	}
	
	private static int ADCtoR(int Rfed, int val) {
		int R = 0;
		
		R=(int) (Rfed/((float)((val&0x0fff)*0.0014652) -1.0)); //equivale alla seguente formula R=(int) (Rfed/((float)((val&0x0fff)*3.0/4095.0/0.5) -1.0));
		return R;
	}
	private static float cal_ph(int median, int mean, int range) {
		float ph;
		float median2, mean2;
		median2 = ADCtoR(range,median);
		mean2 = ADCtoR(range,mean);
		ph = ((mean2 - median2)/(median2*mean2))*1000000;
		
		return ph;
	}
	
/*	private String generateStringToWrite(long features[]) {
		String feats = "";
		
		for (int i = 0; i<features.length-1; i++) 
			feats += features + SEPARATOR;
			
		return feats += features[features.length-1];
	}*/
}





