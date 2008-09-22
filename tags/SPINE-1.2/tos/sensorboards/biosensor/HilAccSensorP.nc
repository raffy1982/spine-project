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
 * Module component of the ST Microelectronics LIS3L06AL
 * accelerometer sensor driver for the telosb platform
 *
 * @author Ville-Pekka Seppa <ville-pekka.seppa@tut.fi>
 *
 * @version 1.2
 */

module HilAccSensorP {
  uses {
     interface Read<uint16_t> as AccX;
     interface Read<uint16_t> as AccY;
     interface Read<uint16_t> as AccZ;

     interface Boot;
     interface SensorsRegistry;
  }

  provides interface Sensor;
}
implementation {
  
    uint16_t accX = 0;
    uint16_t accY = 0;
    uint16_t accZ = 0;

    uint8_t acqType;
    bool xReady;
    bool yReady;
    bool zReady;

    uint8_t valueTypesList[3];

    uint8_t acquireTypesList[4];
    
    bool registered = FALSE;


    event void Boot.booted() {
       if (!registered) {
          valueTypesList[0] = CH_1;
          valueTypesList[1] = CH_2;
          valueTypesList[2] = CH_3;
          acquireTypesList[0] = CH_1_ONLY;
          acquireTypesList[1] = CH_2_ONLY;
          acquireTypesList[2] = CH_3_ONLY;
          acquireTypesList[3] = ALL;

          call SensorsRegistry.registerSensor(ACC_SENSOR);

          registered = TRUE;
       }
    }

    command uint8_t Sensor.getSignificantBits() {
        return 12;
    }

    command error_t Sensor.acquireData(enum AcquireTypes acquireType) {
        xReady = FALSE;
        yReady = FALSE;
        zReady = FALSE;

        acqType = acquireType;

        if(acquireType == ALL || acquireType == CH_1_ONLY)
            call AccX.read();
        if(acquireType == ALL || acquireType == CH_2_ONLY)
            call AccY.read();
        if(acquireType == ALL || acquireType == CH_3_ONLY)
            call AccZ.read();

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
        memcpy(buffer, &accX, 2);
        memcpy(buffer+1, &accY, 2);
        memcpy(buffer+2, &accZ, 2);
    }

    command enum SensorCode Sensor.getSensorCode() {
        return ACC_SENSOR;
    }

    command uint16_t Sensor.getSensorID() {
        return 0x9283; // the ID has been randomly choosen
    }

    /*
    * The event is thrown when a new accelerometer x-axis reading is ready.
    * It sets a global variable and put the reading in the system buffer.
    *
    * @param result : indicates whether the reading process has succeed or not.
    * @param data : if the result is <code>SUCCESS</code>, it contains a consistent reading.
    *
    * @return void
    */
    event void AccX.readDone(error_t result, uint16_t data) {
        xReady = TRUE;

        accX = data;

        if ((acqType == ALL && xReady && yReady && zReady)
	    || acqType == CH_1_ONLY)
           signal Sensor.acquisitionDone(result, acqType);
    }

    /*
    * The event is thrown when a new accelerometer y-axis reading is ready.
    * It sets a global variable and put the reading in the system buffer.
    *
    * @param result : indicates whether the reading process has succeed or not.
    * @param data : if the result is <code>SUCCESS</code>, it contains a consistent reading.
    *
    * @return void
    */
    event void AccY.readDone(error_t result, uint16_t data) {
        yReady = TRUE;

        accY = data;

        if ((acqType == ALL && xReady && yReady && zReady) 
	    || acqType == CH_2_ONLY)
           signal Sensor.acquisitionDone(result, acqType);
    }

    /*
    * The event is thrown when a new accelerometer z-axis reading is ready.
    * It sets a global variable and put the reading in the system buffer.
    *
    * @param result : indicates whether the reading process has succeed or not.
    * @param data : if the result is <code>SUCCESS</code>, it contains a consistent reading.
    *
    * @return void
    */
    event void AccZ.readDone(error_t result, uint16_t data) {
        zReady = TRUE;

        accZ = data;

        if ((acqType == ALL && xReady && yReady && zReady)
	    || acqType == CH_3_ONLY)
           signal Sensor.acquisitionDone(result, acqType);
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


