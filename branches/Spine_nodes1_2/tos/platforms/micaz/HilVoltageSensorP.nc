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
 * 
 *
 * @author Raffaele Gravina <rgravina@wsnlabberkeley.com>
 * @version 1.0
 */      

 module HilVoltageSensorP {
  uses {
     interface Boot;
     interface SplitControl;
     interface Read<uint16_t> as Volt;
  }
  provides interface VoltageSensor as VoltSensor;
}
implementation {
  
    norace uint16_t volt=0;

    /**
    * Init the voltage sensor
    *
    * @return void
    */
    void initVoltSensor() {

    }

    event void Boot.booted() {
        call SplitControl.start();
    }

    event void SplitControl.startDone(error_t err) {
        if (err == SUCCESS) {
            initVoltSensor();
        }
        else
            call SplitControl.start();
    }

    event void SplitControl.stopDone(error_t err) {}

    /**
    * Reads the current voltage level and make it
    * ready to get using the related commands.
    *
    * @return void
    */
    command void VoltSensor.readVolt() {
        call Volt.read();
    }
    
    /**
    * Gets the last voltage level stored using the command readVolt.
    *
    * @return 'uint16_t' the last voltage level stored
    */
    async command uint16_t VoltSensor.getVolt() {
        return volt;
    }
    
    event void Volt.readDone(error_t result, uint16_t data) {
       if (result != SUCCESS)
	   volt = 0;
       else
           volt = data;
    }
}


