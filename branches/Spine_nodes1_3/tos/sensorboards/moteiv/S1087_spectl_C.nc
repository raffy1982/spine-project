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
 * Configuration component of the 'Hamamatsu S1087-01' 
 *  light spectrum sensor driver for the motive tmote sky platform
 *
 * @author Carlo Caione <carlo.caione@unibo.it>
 *
 * @version 1.3
 */

generic configuration S1087_spectl_C() {
  provides interface Read<uint16_t>;
  provides interface ReadStream<uint16_t>;
}
implementation {
  components new AdcReadClientC();
  components new AdcReadStreamClientC();
  components S1087_spectl_P;

  
  ReadStream = AdcReadStreamClientC;
  Read = AdcReadClientC;
  AdcReadClientC.AdcConfigure -> S1087_spectl_P;
  AdcReadStreamClientC.AdcConfigure -> S1087_spectl_P;
}
