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

import java.util.Hashtable;
import java.util.Vector;

import exceptions.FunctionNotSupportedException;
import exceptions.SensorNotPresentException;
import exceptions.SensorSetupException;

import spine.communication.emu.EMUMessage;
import spine.datamodel.functions.FeatureSpineSetupFunction;
import spine.datamodel.functions.SpineSetupSensor;

/**
 * Command interface.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */
public interface Command {
	
	public void commandReceived(int arg0, EMUMessage emumsg);
	
	public String createSocket();
	
	public Vector getFeatureDataVector();
	
	public Hashtable getFeatureSetUpInfo();
	
	public Hashtable getFunctionsList() ;

	public int getNodeID();
	
	public Hashtable getSensorSetUpInfo();
	
	public Vector getSensorsList();
	
	public void setfeatureSetUpInfo(Hashtable<Integer, Vector> featureSetUpInfo);
	
	public void setFunctionsList(Hashtable functionsList);
	
	public void setNodeID(int nodeID);
	
	public void setSensorSetUpInfo(Hashtable<Integer, Vector> sensorSetUpInfo);
	
	public void setSensorsList(Vector sensorsList);
	
	public FeatureSpineSetupFunction setupFunction(short[] shortPayload, Vector sensorsList, Vector sensorCodeSetupEx)throws FunctionNotSupportedException;
	
	public SpineSetupSensor setupSensor(short[] shortPayload, Vector sensorsList, Vector sensorCodeSetupEx, Hashtable dsSensorTimeSampling)throws SensorNotPresentException, SensorSetupException;
	
	public void portReceived(int arg0, String valSSPort);
	
	public void loadDataSensor(String dataSetFile);

	public void start();

	public void reset();
}
