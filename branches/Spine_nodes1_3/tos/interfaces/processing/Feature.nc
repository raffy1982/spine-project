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
 * Interface of the SPINE Feature.
 *
 * @author Raffaele Gravina
 * @author Philip Kuryloski
 *
 * @version 1.2
 */

interface Feature {

	/**
	 * Commands the execution of a feature
	 *
	 * @param	data	two dimensional array containing data to be processed referenced by channel then sample
	 * @param	channelMask	channel mask for the data array
	 * @param	dataLen	the number of samples per channel
	 * @param	res	buffer to store the calculated result (must have size returnedChannelCount x resultSize bytes)
	 *
	 * @return	the channels written into the 'result', expressed as a channel Bitmask
	 */
	command uint8_t calculate(int16_t** data, uint8_t channelMask, uint16_t dataLen, uint8_t* res);

	/**
	 * Returns the result word lenght (number of bytes of the result)
	 *
	 * @return the number of bytes of the result
	 */
	command uint8_t getResultSize();
}
