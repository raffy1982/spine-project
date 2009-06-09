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

/**
 *
 * Represents the function 'Buffered Raw-Data' specs
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

package spine.datamodel.functions;

import spine.SPINEFunctionConstants;

import spine.datamodel.functions.Exception.*;

public class BufferedRawDataFunction extends Function {

	public void init(byte[] spec) throws BadFunctionSpecException {
		this.functionCode = SPINEFunctionConstants.BUFFERED_RAW_DATA;
	}
	
	public String toString() {
		return SPINEFunctionConstants.BUFFERED_RAW_DATA_LABEL;
	}

}
