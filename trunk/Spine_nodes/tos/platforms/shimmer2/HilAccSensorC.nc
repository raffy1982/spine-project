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
 * @author Trevor Pering <trevor.pering@intel.com>
 *
 * @version 1.2
 */

configuration HilAccSensorC
{
  provides interface Sensor;
}

implementation
{
  components MainC, Mma7260P;
  components new Msp430Adc12ClientAutoRVGC() as ADC;
  components HilAccSensorP, SensorsRegistryC;

  ADC.AdcConfigure -> HilAccSensorP;
  HilAccSensorP.Resource -> ADC.Resource;
  HilAccSensorP.ADC -> ADC;
  MainC.SoftwareInit -> Mma7260P.Init;
  MainC.SoftwareInit -> HilAccSensorP.Init;

  HilAccSensorP.Boot -> MainC;

  Sensor = HilAccSensorP;

  HilAccSensorP.Mma_Accel -> Mma7260P;

  HilAccSensorP.SensorsRegistry -> SensorsRegistryC;
}
