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
 * Module component for the Electrocardiogram (ECG)
 * sensor developed in Tampere University of Technology,
 * Tampere, Finland
 *
 * @author  Ville-Pekka Seppa <ville-pekka.seppa@tut.fi>
 *
 * @version 1.2
 */      

#include "Msp430Adc12.h"

module HilEcgSensorP {
  uses {
    interface Read<uint16_t> as EcgSensor;
    
    interface Boot;
    interface SensorsRegistry;

    interface HplMsp430GeneralIO as BiosensorPowerCtl;
  }

  provides interface Sensor;
}

implementation {

  uint16_t eipData = 0;
  uint8_t acqType;
  bool eipDataReady;
  uint8_t valueTypesList[1];
  uint8_t acquireTypesList[1];
  bool registered = FALSE;

  /*
   * Set the biosensor power on
   *
   * @return void
   */
  void initBiosensor() {

    call BiosensorPowerCtl.makeOutput();
    call BiosensorPowerCtl.set(); // Set biosensor power supply ON

    // Initializing the DDS chip here might be needed also
    // for ECG measurement?

  }


  event void Boot.booted() {

    if (!registered) {

      initBiosensor();

      valueTypesList[0] = CH_1;
      acquireTypesList[0] = CH_1_ONLY;

      call SensorsRegistry.registerSensor(ECG_SENSOR);
           
      registered = TRUE;
    }
  }

  command uint8_t Sensor.getSignificantBits() {
    return 12;
  }

  command error_t Sensor.acquireData(enum AcquireTypes acquireType) {
    eipDataReady = FALSE;

    acqType = acquireType;

    if(  acquireType == ALL || acquireType == CH_1_ONLY ) {
      call EcgSensor.read();
    }

    return SUCCESS;

  }
    
  command uint16_t Sensor.getValue(enum ValueTypes valueType) {
    switch (valueType) {
    case CH_1 : return eipData;
    default : return 0xffff;
    }
  }

  command void Sensor.getAllValues(uint16_t* buffer, uint8_t* valuesNr) {

    *valuesNr = sizeof valueTypesList;
    memcpy(buffer, &eipData, 2);
  }

  command enum SensorCode Sensor.getSensorCode() {
    return ECG_SENSOR;
  }

  command uint16_t Sensor.getSensorID() {
    return 0x0509; // the ID has been randomly choosen
  }

  /*
   * The event is thrown when an ECG sensor reading is ready.
   * It sets a global variable and puts the reading in the system buffer.
   *
   * @param result : indicates whether the reading process has succeed or not.
   * @param data : if the result is <code>SUCCESS</code>, it contains a consistent reading.
   *
   * @return void
   */
  event void EcgSensor.readDone(error_t result, uint16_t data) {
    eipDataReady = TRUE;

    eipData = data;

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
