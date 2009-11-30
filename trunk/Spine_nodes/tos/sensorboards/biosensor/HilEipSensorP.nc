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
 * Module component for the Electrical Impedance Pneumography (EIP)
 * breathing sensor developed in Tampere University of Technology,
 * Tampere, Finland
 *
 * @author  Ville-Pekka Seppa <ville-pekka.seppa@tut.fi>
 *
 * @version 1.2
 */      

#include "Msp430Adc12.h"

module HilEipSensorP {
  uses {
    interface Read<uint16_t> as EipSensor;
    
    interface Boot;
    interface SensorsRegistry;

    interface Spi;
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

  uint8_t spiBuffer[3];
  uint32_t ddsFreqReg = 0;
  uint16_t temp16 = 0;
    
  /**
   * Initialize the DDS chip on the biosensor and
   * set the biosensor power on
   *
   * @return void
   */
  void initBiosensor() {

    call BiosensorPowerCtl.makeOutput();
    call BiosensorPowerCtl.set(); // Set biosensor power supply ON

    /*** Begin DDS Chip Initialization ***/
    
    //    ddsFreqReg = (uint32_t) ((float)268435456 * 
    //                 (float)DDS_OUTPUT_F) / (float)DDS_MCLK;
    //    ddsFreqReg = 2362232;  // 220.0kHz @ MCLK=25MHz
    ddsFreqReg = 4831838;  // 450.0kHz @ MCLK=25MHz

    // Put DDS to reset reset during programming
    spiBuffer[1] = 1, spiBuffer[0] = 0;
    call Spi.sendData( spiBuffer, 2, TRUE );

    // Prepare DDS to receive 28 bit frequency (B28)
    spiBuffer[1] = ( ( 1<<5 ) | 1 ), spiBuffer[0] = 0;
    call Spi.sendData( spiBuffer, 2, TRUE );

    /* Select FREQ0 (= bit 14) and clear the upper 
     * 14 bits of freq_reg then send it as the 14
     * LSBs of FREQ0 register */
    temp16 = (uint16_t) ( ddsFreqReg & 0x3FFF ) | ( 1 << 14);
    spiBuffer[1] = ( (uint8_t) ( temp16 >> 8 ) );
    spiBuffer[0] = (uint8_t) temp16;
    call Spi.sendData( spiBuffer, 2, TRUE );

    /* Select FREQ0 (= bit 14) and clear the lower 
     * 14 of freq_reg by shifting then send it as
     * the 14 MSBs of FREQ0 register */
    temp16 = (uint16_t) ( ( ddsFreqReg >> 14 ) & 0x3FFF ) | ( 1 << 14);
    spiBuffer[1] = ( (uint8_t) ( temp16 >> 8 ) );
    spiBuffer[0] = (uint8_t) temp16;
    call Spi.sendData( spiBuffer, 2, TRUE );

    // End reset state
    spiBuffer[1] = 0, spiBuffer[0] = 0;
    call Spi.sendData( spiBuffer, 2, TRUE );

    /*** End DDS Chip Initialization ***/

  }


  event void Boot.booted() {

    if (!registered) {

      initBiosensor();

      valueTypesList[0] = CH_1;
      acquireTypesList[0] = CH_1_ONLY;

      call SensorsRegistry.registerSensor(EIP_SENSOR);
           
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
      call EipSensor.read();
    }

    return SUCCESS;

    /*
      xReady = FALSE;
      yReady = FALSE;

      acqType = acquireType;

      if(acquireType == ALL || acquireType == CH_1_ONLY)
      call GyroX.read();
      if(acquireType == ALL || acquireType == CH_2_ONLY)
      call GyroY.read();

      return SUCCESS;
    */
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
    return EIP_SENSOR;
  }

  command uint16_t Sensor.getSensorID() {
    return 0x2202; // the ID has been randomly choosen
  }

  /*
   * The event is thrown when an EIP sensor reading is ready.
   * It sets a global variable and puts the reading in the system buffer.
   *
   * @param result : indicates whether the reading process has succeed or not.
   * @param data : if the result is <code>SUCCESS</code>, it contains a consistent reading.
   *
   * @return void
   */
  event void EipSensor.readDone(error_t result, uint16_t data) {
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
