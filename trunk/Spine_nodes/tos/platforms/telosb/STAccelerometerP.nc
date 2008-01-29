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
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 * @author Filippo Tempia <filippo.tempia@telecomitalia.it>
 * @author Roozbeh Jafari <r.jafari@utdallas.edu>
 *
 * @version 1.0
 */  
 module STAccelerometerP {
  uses {
     interface Boot;

     interface SplitControl as AMControl;
     interface HplMsp430GeneralIO as CS_accel_port;
     interface HplMsp430GeneralIO as CLK_port;
     interface HplMsp430GeneralIO as DIN_port;
     interface HplMsp430GeneralIO as DOUT_port;
  }
  provides interface STAccelerometer;
}
implementation {
  
    uint8_t writeBuff;
    uint8_t i;
    bool temp = FALSE;

    norace uint16_t accX;
    norace uint16_t accY;
    norace uint16_t accZ;

    /**
    * Init the accelerometer sensor and enable its registers
    *
    * @return void
    */
    void initAccel() {
        call CS_accel_port.set();
	call CLK_port.set();
	call CS_accel_port.clr();

	writeBuff = 0x20;  // 00100000 = 0x20 ENABLE ACCELEROMETER REGISTER ADDRESS
	for (i=8; i>=1; i--) {
	    call CLK_port.clr();

	    //WRITE
	    if ((writeBuff>>(i-1) & 0x01) != 0)
	        call DIN_port.set();
            else
	        call DIN_port.clr();
	    call CLK_port.set();
	}

	writeBuff = 0xc7;  // 11000111 = 0xc7 ENABLE ACCELEROMETER REGISTER VALUE
	//writeBuff = 0xcf;  // 11001111 = 0xcf ENABLE ACCELEROMETER REGISTER VALUE
  
        for (i=8; i>=1; i--) {
  	    call CLK_port.clr();

  	    //WRITE
  	    if ((writeBuff>>(i-1) & 0x01) != 0)
  	        call DIN_port.set();
            else
  	        call DIN_port.clr();
            call CLK_port.set();
  	}
    }

    event void Boot.booted() {
        call AMControl.start();
    }

    event void AMControl.startDone(error_t err) {
        if (err == SUCCESS) {
            call DOUT_port.makeInput();
	    call CS_accel_port.makeOutput();
	    call CS_accel_port.set();
	    call CLK_port.makeOutput();
	    call DIN_port.makeOutput();

            initAccel();
        }
        else
            call AMControl.start();
    }

    event void AMControl.stopDone(error_t err) {}

    /**
    * Reads the current accelerometer values over all three axis
    * and make them ready to get using the related commands.
    *
    * @return void
    */
    command void STAccelerometer.readAccel() {
        call CS_accel_port.set();
        call CLK_port.set();
        call CS_accel_port.clr();

        accX = 0;
        accY = 0;
        accZ = 0;
        writeBuff = 0xe8;  // 11101000 = 0xe8 READ ACCELERATION DATA

        for (i=8; i>=1; i--) {
           call CLK_port.clr();

           //WRITE
	   if ((writeBuff>>(i-1) & 0x01) != 0)
	       call DIN_port.set();
	   else
	       call DIN_port.clr();
	   call CLK_port.set();
	}

        //LOW X-AXIS BYTE
	for (i=8; i>=1; i--) {
	    call CLK_port.clr();

            //READ
	    temp = call DOUT_port.get();
	        if (temp)
		    accX = accX + (0x0001<<(i-1));
		call CLK_port.set();
	}

        //HIGH X-AXIS BYTE
	for (i=8; i>=1; i--) {
	    call CLK_port.clr();

            //READ
	    temp = call DOUT_port.get();
     	    if (temp)
	        accX = accX + (0x0100<<(i-1));
            call CLK_port.set();
	}

	//LOW Y-AXIS BYTE
	for (i=8; i>=1; i--) {
	    call CLK_port.clr();

            //READ
	    temp = call DOUT_port.get();
	        if (temp)
		    accY = accY + (0x0001<<(i-1));
		call CLK_port.set();
	}

        //HIGH Y-AXIS BYTE
	for (i=8; i>=1; i--) {
	    call CLK_port.clr();

            //READ
	    temp = call DOUT_port.get();
     	    if (temp)
	        accY = accY + (0x0100<<(i-1));
            call CLK_port.set();
	}
	
	//LOW Z-AXIS BYTE
	for (i=8; i>=1; i--) {
	    call CLK_port.clr();

            //READ
	    temp = call DOUT_port.get();
	        if (temp)
		    accZ = accZ + (0x0001<<(i-1));
		call CLK_port.set();
	}

        //HIGH Z-AXIS BYTE
	for (i=8; i>=1; i--) {
	    call CLK_port.clr();

            //READ
	    temp = call DOUT_port.get();
     	    if (temp)
	        accZ = accZ + (0x0100<<(i-1));
            call CLK_port.set();
	}

	call CS_accel_port.set();
    }
    
    /**
    * Gets the last x-axis value stored using the command readAccel.
    *
    * @return 'uint16_t' the last x-axis value stored
    */
    async command uint16_t STAccelerometer.getAccelX() {
        return accX;
    }

    /**
    * Gets the last y-axis value stored using the command readAccel.
    *
    * @return 'uint16_t' the last y-axis value stored
    */
    async command uint16_t STAccelerometer.getAccelY() {
        return accY;
    }

    /**
    * Gets the last z-axis value stored using the command readAccel.
    *
    * @return 'uint16_t' the last z-axis value stored
    */
    async command uint16_t STAccelerometer.getAccelZ() {
        return accZ;
    }

}
