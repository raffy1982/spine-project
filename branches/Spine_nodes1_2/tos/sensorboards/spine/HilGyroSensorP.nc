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
 * @version 1.0
 */

 module HilGyroSensorP {
  uses {
     interface Boot;

     interface SplitControl;
     
     interface Read<uint16_t> as GyroX;
     interface Read<uint16_t> as GyroY;
  }
  provides interface GyroSensor as Gyro;
}
implementation {
  
    norace uint16_t gyroX=0;
    norace uint16_t gyroY=0;
    norace uint16_t gyroZ=0;

    /**
    * Init the gyroscope sensor
    *
    * @return void
    */
    void initGyro() {
        call GyroX.read();
        call GyroY.read();
    }

    event void Boot.booted() {
        call SplitControl.start();
    }

    event void SplitControl.startDone(error_t err) {
        if (err == SUCCESS) {
            initGyro();
        }
        else
            call SplitControl.start();
    }

    event void SplitControl.stopDone(error_t err) {}

    /**
    * Reads the current gyroscope values over all axis
    * and make them ready to get using the related commands.
    *
    * @return void
    */
    command void Gyro.readGyro() {

    }
    
    /**
    * Gets the last x-axis value stored using the command readGyro.
    *
    * @return 'uint16_t' the last x-axis value stored
    */
    async command uint16_t Gyro.getGyroX() {
        return gyroX;
    }

    /**
    * Gets the last y-axis value stored using the command readGyro.
    *
    * @return 'uint16_t' the last y-axis value stored
    */
    async command uint16_t Gyro.getGyroY() {
        return gyroY;
    }

    /**
    * Gets the last z-axis value stored using the command readGyro.
    *
    * @return 'uint16_t' the last z-axis value stored
    */
    async command uint16_t Gyro.getGyroZ() {
        return gyroZ;
    }

      /*
      * The event is thrown when a new gyroscope x-axis reading is ready.
      * It sets a global variable and put the reading in the system buffer.
      *
      * @param result : indicates whether the reading process has succeed or not.
      * @param data : if the result is <code>SUCCESS</code>, it contains a consistent reading.
      *
      * @return void
      */
      event void GyroX.readDone(error_t result, uint16_t data) {
	    gyroX = data;
      }

      /*
      * The event is thrown when a new gyroscope y-axis reading is ready.
      * It sets a global variable and put the reading in the system buffer.
      *
      * @param result : indicates whether the reading process has succeed or not.
      * @param data : if the result is <code>SUCCESS</code>, it contains a consistent reading.
      *
      * @return void
      */
      event void GyroY.readDone(error_t result, uint16_t data) {
	    gyroY = data;
      }
}


