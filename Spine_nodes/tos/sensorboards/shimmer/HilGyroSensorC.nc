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
 * Configuration component of the 'Sparkfun Integrated Dual-Axis Gyro IDG-300'
 * sensor driver for the shimmer platform
 *
 * @author Marco Arena <marco.arena@guest.telecomitalia.it>
 *
 * @version 1.0
 */

configuration HilGyroSensorC {
  provides interface Sensor;
}

implementation {
	components MainC;
	components new Msp430Adc12ClientAutoRVGC() as ADC;
	components HilGyroSensorP, SensorsRegistryC;
	
	ADC.AdcConfigure -> HilGyroSensorP;
	HilGyroSensorP.Resource -> ADC.Resource;
	HilGyroSensorP.ADC -> ADC;
	MainC.SoftwareInit -> HilGyroSensorP.Init;
	
	HilGyroSensorP.Boot -> MainC;

	Sensor = HilGyroSensorP;

	HilGyroSensorP.SensorsRegistry -> SensorsRegistryC;

}


