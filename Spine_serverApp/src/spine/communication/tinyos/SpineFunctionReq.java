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
 * This class represents the generic SPINE Function requests and must be extended
 * by 'function specific' classes.
 *  
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

package spine.communication.tinyos;

public abstract class SpineFunctionReq {
	
	protected boolean isActivationRequest;

	/**
	 * This method must be implemented by any class extending SpineFunctionReq
	 * and must contain the logic of converting an high level function request into 
	 * an actual SPINE Ota message of the request, in terms of a byte[] array
	 * 
	 * @return the actual SPINE Ota message of the request
	 */
	public abstract byte[] encode();

	/**
	 * Sets the control flag of the request indicating if it's an activation or a deactivation.
	 * 
	 * @param isActivationRequest 'true' if the current request is of activation; 
	 * 'false' if it's a deactivation request 
	 */
	public void setActivationFlag(boolean isActivationRequest) {
		this.isActivationRequest = isActivationRequest;		
	}
	
}
