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
 *
 * Implementation for TinyOS platforms of the GAL Message interface.
 * 
 * Note that this class is only used internally at the framework. 
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

package spine.communication.emule;

import java.io.Serializable;

public class EMUMessage extends com.tilab.gal.Message implements Serializable {
	
	public void setSourceURL(String sourceID) {
		this.sourceURL = sourceID;
	}
	
	public void setSeqNo(byte seqNo) {
		this.transSeqNumber =  seqNo;
		if (this.transSeqNumber < 0) 
			this.transSeqNumber += 256;
	}
	
	/*public String toString() {
		short[] payload = this.getPayload();
		String valPayload= "";
		if (payload == null || payload.length == 0){
		    valPayload="empty payload";}
		else {
			for (int i = 0; i < payload.length; i++) {
				short b = payload[i];
				if (b < 0)
					b += 256;
				valPayload=valPayload + Integer.toHexString(b) + " ";
			}
		}
		
		return "From node: " + this.getSourceURL() + " - " + valPayload;		
	}*/

}