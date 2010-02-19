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
 * Module component of the 'STMicroelectronics LIS3LV02DQ Accelerometer'
 * tri-axial sensor driver for the telosb platform.
 *
 * NOTE that the raw value 1024 = +1g,
 * and negative acceleration are given using two's complement format 
 *
 * @author Raffaele Gravina <rgravina@wsnlabberkeley.com>
 * @author Antonio Guerrieri <aguerrieri@wsnlabberkeley.com>
 * @author Filippo Tempia <filippo.tempia@telecomitalia.it>
 *
 * @version 1.2
 */      

#include "Msp430Adc12.h"

module HilAccSensorP {

    uses {
       interface HplMsp430GeneralIO as CS_accel_port;
       interface HplMsp430GeneralIO as CLK_port;
       interface HplMsp430GeneralIO as DIN_port;
       interface HplMsp430GeneralIO as DOUT_port;

       interface Boot;
       interface SensorsRegistry;
    }

    provides interface Sensor;
}

implementation {
  
    uint8_t writeBuff;
    uint8_t i;
    bool temp = FALSE;

    uint16_t accX;
    uint16_t accY;
    uint16_t accZ;
    
    uint8_t valueTypesList[3];

    uint8_t acquireTypesList[1];
    
    bool registered = FALSE;

    
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

	//writeBuff = 0xc7;  // 11000111 = 0xc7 ENABLE ACCELEROMETER REGISTER VALUE   max 40Hz
	//writeBuff = 0xd7;  // 11010111 = 0xd7 ENABLE ACCELEROMETER REGISTER VALUE     max 160Hz
	writeBuff = 0xe7;  // 11100111 = 0xd7 ENABLE ACCELEROMETER REGISTER VALUE     max 640Hz
	//writeBuff = 0xf7;  // 11110111 = 0xd7 ENABLE ACCELEROMETER REGISTER VALUE     max 2560Hz
        for (i=8; i>=1; i--) {
  	    call CLK_port.clr();
  	    //WRITE
  	    if ((writeBuff>>(i-1) & 0x01) != 0)
  	        call DIN_port.set();
            else
  	        call DIN_port.clr();
            call CLK_port.set();
  	}

  	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        call CS_accel_port.set();
	call CLK_port.set();
	call CS_accel_port.clr();

	writeBuff = 0x21;  // 0100001= 0x21 ENABLE ACCELEROMETER REGISTER ADDRESS
	for (i=8; i>=1; i--) {
	    call CLK_port.clr();
	    //WRITE
	    if ((writeBuff>>(i-1) & 0x01) != 0)
	        call DIN_port.set();
            else
	        call DIN_port.clr();
	    call CLK_port.set();
	}

	writeBuff = 0x80;  // 10000000 = 0x80 ENABLE ACCELEROMETER REGISTER VALUE
        for (i=8; i>=1; i--) {
  	    call CLK_port.clr();
  	    //WRITE
  	    if ((writeBuff>>(i-1) & 0x01) != 0)
  	        call DIN_port.set();
            else
  	        call DIN_port.clr();
            call CLK_port.set();
        }
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    }

    event void Boot.booted() {
        if (!registered) {
           call DOUT_port.makeInput();
	   call CS_accel_port.makeOutput();
	   call CS_accel_port.set();
	   call CLK_port.makeOutput();
	   call DIN_port.makeOutput();

           // this specific accelerometer requires an inital bootstrap
           initAccel();

           valueTypesList[0] = CH_1;
           valueTypesList[1] = CH_2;
           valueTypesList[2] = CH_3;
           acquireTypesList[0] = ALL;

           call SensorsRegistry.registerSensor(ACC_SENSOR);
           
           // the driver self-registers to the sensor registry
           registered = TRUE;
        }
    }

    command uint8_t Sensor.getSignificantBits() {
        return 12;
    }

    command error_t Sensor.acquireData(enum AcquireTypes acquireType) {
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

       // this driver is implemented as a synchronous component; 
       // hence, at the end of the acuireData command, it's possible to signal the acquisitionDone event 
       // because the raw data values are already available
       signal Sensor.acquisitionDone(SUCCESS, acquireType);

       return SUCCESS;
    }

    command uint16_t Sensor.getValue(enum ValueTypes valueType) {
        switch (valueType) {
            case CH_1 : return accX;
            case CH_2 : return accY;
            case CH_3 : return accZ;
            default : return 0xffff;
        }
    }

    command void Sensor.getAllValues(uint16_t* buffer, uint8_t* valuesNr) {
        *valuesNr = sizeof valueTypesList;
        buffer[0] = accX;
        buffer[1] = accY;
        buffer[2] = accZ;
    }

    command enum SensorCode Sensor.getSensorCode() {
        return ACC_SENSOR;
    }

    command uint16_t Sensor.getSensorID() {
        return 0x2143; // the ID has been randomly choosen
    }

    command uint8_t* Sensor.getValueTypesList(uint8_t* valuesTypeNr) {
        *valuesTypeNr = sizeof valueTypesList;
        return valueTypesList;
    }

    command uint8_t* Sensor.getAcquireTypesList(uint8_t* acquireTypesNr) {
        *acquireTypesNr = sizeof acquireTypesList;
        return acquireTypesList;
    }
}


