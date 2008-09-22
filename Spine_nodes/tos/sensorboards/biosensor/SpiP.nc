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

module SpiP {

  provides {
    interface Spi;
  }

  uses {
    interface HplMsp430GeneralIO as DataP;  // Data pin
    interface HplMsp430GeneralIO as ClockP; // Clock pin
    interface HplMsp430GeneralIO as CsP;    // Chip Select pin
  }
}

implementation {

  async command void Spi.sendData(uint8_t *dataBuffer, uint8_t bufferSize,
				  bool csPinControl) {
    
    uint8_t mask = 0;
    uint8_t i = 0, k = 0;

    call DataP.makeOutput();
    call ClockP.makeOutput();
    call CsP.makeOutput();

    call ClockP.set(); // This chooses between rising or falling
                       // edge of the clock signal. If ClockP is
                       // set() here, each databit will be ready on
                       // the bus on FALLING edge of the  clock signal.
                       // Thus, clr() means RISING edge.

    // If user wants sendData() to control chip select pin
    if( csPinControl ) {
      call CsP.set(); // To make sure we start as CS high
      call CsP.clr(); // CS low to start data transfer
    }

    // these two loops do the actual sending
    for( k = 1; k <= bufferSize; k++ ) {

      for( mask = 0x80, i = 0; i < 8; i++ ) {
	
	// Set data pin
	if( dataBuffer[bufferSize-k] & mask ) { call DataP.set(); }
	else { call DataP.clr(); }

	// Then toggle clock pin twice
	call ClockP.toggle(); call ClockP.toggle();

	mask >>= 1; // Shift masking byte one left
      }
    }

    if( csPinControl ) {
      call CsP.set(); // CS high to end transfer
    }

  } 

}
