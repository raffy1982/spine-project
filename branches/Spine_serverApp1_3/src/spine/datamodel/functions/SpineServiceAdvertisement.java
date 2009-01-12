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
 * This class contains the static method to parse (decompress) a 
 * TinyOS SPINE Service Advertisement packet payload into a platform independent one.
 *
 * Note that this class is only used internally at the framework. 
 *
 * @author Raffaele Gravina
 * @author Alessia Salmeri
 *
 * @version 1.3
 */

package spine.datamodel.functions;


public interface SpineServiceAdvertisement {
		
	/**
	 * Decompress Service Advertisement packet payload into a platform independent packet payload
	 * 
	 * @param payload the low level byte array containing the payload of the Spine Service Advertisement packet to parse (decompress)
	 * @return still a byte array representing the platform independent Spine Service Advertisement packet payload.
	 */
	public byte[] decode(byte[] payload);

}
