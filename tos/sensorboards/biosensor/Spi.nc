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
 * Generic SPI interface for Tmote Sky
 *
 * @author Ville-Pekka Seppa
 *
 * @version 1.2
 *
 * @date   December 7, 2007
 */

#include <msp430hardware.h>

interface Spi {

  /**
   * SPI Master mode sending command. The first sent byte in the buffer
   * is dataBuffer[bufferSize-1]. The bytes are sent in MSB first order.
   *
   * @param  dataBuffer  Contains the bytes to be sent
   *
   * @param  bufferSize  Number of bytes of data in dataBuffer
   *
   * @param  csPinControl  Defines whether sendData() controls the
   *                       Chip Select pin or not
   */


  async command void sendData(uint8_t *dataBuffer, uint8_t bufferSize,
			      bool csPinControl);


}
