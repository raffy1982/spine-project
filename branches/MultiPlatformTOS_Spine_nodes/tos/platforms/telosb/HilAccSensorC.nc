/*****************************************************************
SPINE - Signal Processing In-Note Environment is a framework that 
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
 * Implementation of the 'STMicroelectronics LIS3LV02DQ Accelerometer' tri-axial sensor
 * for the telosb platform
 *
 * @author Raffaele Gravina <rgravina@wsnlabberkeley.com>
 * @author Antonio Guerrieri <aguerrieri@wsnlabberkeley.com>
 * @author Filippo Tempia <filippo.tempia@telecomitalia.it>
 * @version 1.0
 */      
 
 configuration HilAccSensorC {
  provides interface AccSensor;
}
implementation {
    components MainC;

    components ActiveMessageC;
    
    components HplMsp430GeneralIOC;

    components HilAccSensorP;

    AccSensor = HilAccSensorP;
    
    HilAccSensorP.Boot -> MainC;

    HilAccSensorP.SplitControl -> ActiveMessageC;
    
    HilAccSensorP.CS_accel_port -> HplMsp430GeneralIOC.Port26;
    HilAccSensorP.CLK_port      -> HplMsp430GeneralIOC.Port66;
    HilAccSensorP.DIN_port      -> HplMsp430GeneralIOC.Port67;
    HilAccSensorP.DOUT_port     -> HplMsp430GeneralIOC.Port23;
}



