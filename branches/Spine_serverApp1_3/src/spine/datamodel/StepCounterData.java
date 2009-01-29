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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

/**
* This class represents the StepCounterData entity.
* It contains the decode method for converting low level Step-Counter type data into an high level object.
*
* @author Raffaele Gravina
*
* @version 1.3
*/

package spine.datamodel;

import spine.SPINEFunctionConstants;

public class StepCounterData extends Data {
	
	private int stepsCount;
		
	/**
	 * Getter method of the steps count
	 * @return the steps count
	 */
	public int getStepsCount() {
		return this.stepsCount;
	}
	
	/**
	 * 
	 * Returns a string representation of the StepCounter object.
	 * 
	 */
	public String toString() {
		return "From node: " + this.nodeID + " - " + SPINEFunctionConstants.STEP_COUNTER_LABEL + 
				" update: "	+ this.stepsCount;
	}

	/**
	 * @param stepsCount the stepsCount to set
	 */
	public void setStepsCount(int stepsCount) {
		this.stepsCount = stepsCount;
	}
}
