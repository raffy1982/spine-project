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
 * Configuration component of the 'Sensirion AG SHT11' environmental
 * temperature sensor driver for the motive tmote sky platform
 *
 * @author Carlo Caione <carlo.caione@unibo.it>
 *
 * @version 1.3
 */

configuration HilTemperatureSensorC 
{
	provides interface Sensor;
}

implementation {

   components new SensirionSht11C();
   components MainC;
   components SensorsRegistryC;
   components HilTemperatureSensorP;

   Sensor = HilTemperatureSensorP;
   HilTemperatureSensorP.SensorsRegistry -> SensorsRegistryC;
   HilTemperatureSensorP.Boot -> MainC;
   HilTemperatureSensorP.Temp -> SensirionSht11C.Temperature;
   //HilTemperatureSensorP.TemperatureMetadata -> SensirionSht11C.TemperatureMetadata;

}
 
 
 