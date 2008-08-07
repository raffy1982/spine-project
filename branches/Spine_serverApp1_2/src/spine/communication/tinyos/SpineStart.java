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
 * This class represents a SPINE Start command.
 * It contains the logic for encoding a start command from an high level Start object.
 * 
 * Note that this class is only used internally at the framework.   
 *  
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

package spine.communication.tinyos;

public class SpineStart extends spine.communication.tinyos.SpineTOSMessage {
	
	private final static int LENGTH = 4;
	
	private int activeNodesCount = -1;
	private boolean radioAlwaysOn;
	private boolean enableTDMA;
	
	/**
	 * This method is used internally by the framework and encodes the high level start command 
	 * into an actual SPINE Ota message of the request, in terms of a byte[] array
	 * @return
	 */
	public byte[] encode() {
		byte[] data = new byte[LENGTH];
		
		data[0] = (byte)(this.activeNodesCount>>8);
		data[1] = (byte)this.activeNodesCount;
		data[2] = (this.radioAlwaysOn)? (byte)1: 0;
		data[3] = (this.enableTDMA)? (byte)1: 0;
		
		return data;
	}

	/**
	 * Sets the the size of the discovered WSN.
	 * Note that this info is actually used by the node only if it's requested 
	 * to operate in TDMA mode.  
	 * 
	 * @param activeNodesCount the size of the discovered WSN
	 */
	public void setActiveNodesCount(int activeNodesCount) {
		this.activeNodesCount = activeNodesCount;
	}

	/**
	 *  Sets the control flag for enabling the radio low-power mode.
	 *  
	 * @param radioAlwaysOn 'true' for keeping the radio always turned on;
	 * 'false' to let the node optimizing the radio consumption by turning the radio off
	 * when it's not needed.
	 */
	public void setRadioAlwaysOn(boolean radioAlwaysOn) {
		this.radioAlwaysOn = radioAlwaysOn;
	}

	/**
	 * Sets the control flag for enabling the on-node TDMA radio access scheme
	 * 
	 * @param enableTDMA 'true' if the radio access scheme must be TDMA; 
	 * 'false' to rely on the default one.
	 */
	public void setEnableTDMA(boolean enableTDMA) {
		this.enableTDMA = enableTDMA;
	}
}
