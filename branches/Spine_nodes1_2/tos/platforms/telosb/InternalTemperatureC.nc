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
 * InternalTemperatureC is a common name for the Msp430InternalTemperatureC temperature
 * diode available on the telosb platform.
 *
 * The MSP430 microcontroller has internal temperature and voltage sensors that may be used
 * through the microcontroller’s ADC interface. The temperature input is a temperature diode connected to internal ADC port 10. 
 * When using the temperature sensor, the sample period must be greater than 30 microseconds.
 * The temperature sensor offset can be as much as ±20°C.
 * A single-point calibration is recommended in order to minimize the offset error of the built-in temperature sensor.
 *
 * To convert from ADC counts to temperature, convert to voltage by
 * dividing by 4096 and multiplying by Vref (1.5V). Then subtract
 * 0.986 from voltage and divide by 0.00355 to get degrees C.
 *
 *
 * @author Raffaele Gravina <raffaele.gravina@gmail.com>
 * @version 1.0
 */

generic configuration InternalTemperatureC() {
  provides interface Read<uint16_t>;
}

implementation {
  components new Msp430InternalTemperatureC();
  Read = Msp430InternalTemperatureC.Read;
}

