/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that
allows dynamic configuration of feature extraction capabilities 
of WSN nodes via an OtA protocol

Copyright (C) 2007 Telecom Italia S.p.A. 
�
GNU Lesser General Public License
�
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
�
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the GNU
Lesser General Public License for more details.
�
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA� 02111-1307, USA.
*****************************************************************/

/**
 * Configuration component of the ST Microelectronics LIS3L06AL
 * accelerometer sensor driver for the telosb platform
 *
 * @author Ville-Pekka Seppa <ville-pekka.seppa@tut.fi>
 *
 * @version 1.2
 */

configuration HilAccSensorC {
  provides interface Sensor;
}

implementation {
    components HilAccSensorP;

    components new AccXSensorC() as AccX;
    components new AccYSensorC() as AccY;
    components new AccZSensorC() as AccZ;

    components MainC;

    components SensorsRegistryC;
    Sensor = HilAccSensorP;

    HilAccSensorP.AccX -> AccX;
    HilAccSensorP.AccY -> AccY;
    HilAccSensorP.AccZ -> AccZ;
    
    HilAccSensorP.Boot -> MainC;

    HilAccSensorP.SensorsRegistry -> SensorsRegistryC;
}



