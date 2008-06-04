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
 * 
 * 
 *
 * @author Raffaele Gravina <rgravina@wsnlabberkeley.com>
 * @version 1.0
 */      

 module HilAccSensorP {
  uses {
     interface Boot;
     interface SplitControl;
     interface Read<uint16_t> as AccelX;
     interface Read<uint16_t> as AccelY;
  }
  provides interface AccSensor as Acc;
}
implementation {
  
    norace uint16_t accX=0;
    norace uint16_t accY=0;
    norace uint16_t accZ=0;

    /**
    * Init the accelerometer sensor and enable its registers
    *
    * @return void
    */
    void initAccel() {

    }

    event void Boot.booted() {
        call SplitControl.start();
    }

    event void SplitControl.startDone(error_t err) {
        if (err == SUCCESS) {
            initAccel();
        }
        else
            call SplitControl.start();
    }

    event void SplitControl.stopDone(error_t err) {}

    /**
    * Reads the current accelerometer values over all three axis
    * and make them ready to get using the related commands.
    *
    * @return void
    */
    command void Acc.readAccel() {
        call AccelX.read();
        call AccelY.read();
    }
    
    /**
    * Gets the last x-axis value stored using the command readAccel.
    *
    * @return 'uint16_t' the last x-axis value stored
    */
    async command uint16_t Acc.getAccelX() {
        return accX;
    }

    /**
    * Gets the last y-axis value stored using the command readAccel.
    *
    * @return 'uint16_t' the last y-axis value stored
    */
    async command uint16_t Acc.getAccelY() {
        return accY;
    }

    /**
    * Gets the last z-axis value stored using the command readAccel.
    *
    * @return 'uint16_t' the last z-axis value stored
    */
    async command uint16_t Acc.getAccelZ() {
        return accZ;
    }

    event void AccelX.readDone(error_t result, uint16_t data) {
       if (result != SUCCESS)
	   accX = 0;
       else
           accX = data;
    }
    
    event void AccelY.readDone(error_t result, uint16_t data) {
       if (result != SUCCESS)
	   accY = 0;
       else
           accY = data;
    }
}


