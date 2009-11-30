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
 * Module component of the 'Hamamatsu' 
 * light sensors driver for the motive tmote sky platform.
 *
 * This driver implementation offer two channels:
 * - first channel (CH_1), carries the PAR (Photosynthetically Active Radiation) sensor signals
 * - second channel (CH_2), carries the TSR (Total Solar Radiation) sensor signals
 *
 * The TSR and PAR sensors are also measured using the microcontrollers 12-bit ADC with Vref=1.5V. 
 * The photodiodes create a current through a 100kOhm resistor. 
 * By calculating the raw voltage using equation ( Vsensor = (raw_value/4096) * Vref ),
 * convert the voltage into a current using V = I * R:
 * I = Vsensor / 100,000
 *
 * The Moteiv datasheet includes curves for converting the photodiode's current into light values (Lux).
 * Based on the graphs available in the Hamamatsu S1087 datasheet, the current of the sensor, I, may be converted to Lux.
 * PAR    lx = 0.625 * 1e6 * I * 1000
 * TSR    lx = 0.769 * 1e5 * I * 1000
 *
 *
 *
 * @author Carlo Caione <carlo.caione@unibo.it>
 *
 * @version 1.3
 */

module HilLightSensorP {
  uses {
     interface Read<uint16_t> as Visible;
     interface Read<uint16_t> as Spectrum;

     interface Boot;
     interface SensorsRegistry;
  }

  provides interface Sensor;
}
implementation {
  
    uint16_t val_visible = 0;
    uint16_t val_spectrum = 0;
    uint8_t acqType;
    bool VisibleReady;
    bool SpectrumReady;
    uint8_t valueTypesList[2];
    uint8_t acquireTypesList[3];   
    bool registered = FALSE;


    event void Boot.booted() {
       if (!registered) {
          valueTypesList[0] = CH_1;
          valueTypesList[1] = CH_2;
          acquireTypesList[0] = CH_1_ONLY;
          acquireTypesList[1] = CH_2_ONLY;
          acquireTypesList[2] = ALL;

          // the driver self-registers to the sensor registry
          call SensorsRegistry.registerSensor(LIGHT_SENSOR);

          registered = TRUE;
       }
    }

    command uint8_t Sensor.getSignificantBits() {
        return 12;
    }

    command error_t Sensor.acquireData(enum AcquireTypes acquireType) {
        VisibleReady = FALSE;
        SpectrumReady = FALSE;

        acqType = acquireType;

        if(acquireType == ALL || acquireType == CH_1_ONLY)
            call Visible.read();
        if(acquireType == ALL || acquireType == CH_2_ONLY)
            call Spectrum.read();

        return SUCCESS;
    }
    
    command uint16_t Sensor.getValue(enum ValueTypes valueType) {
        switch (valueType) {
            case CH_1 : return val_visible;
            case CH_2 : return val_spectrum;
            default : return 0xffff;
        }
    }

    command void Sensor.getAllValues(uint16_t* buffer, uint8_t* valuesNr) {
        *valuesNr = sizeof valueTypesList;
        buffer[0] = val_visible;
        buffer[1] = val_spectrum;
    }

    command enum SensorCode Sensor.getSensorCode() {
        return LIGHT_SENSOR;
    }

    command uint16_t Sensor.getSensorID() {
        return 0xdead; // the ID has been randomly choosen
    }

    event void Visible.readDone(error_t result, uint16_t data) {
        VisibleReady = TRUE;

        val_visible = data;

        if ((acqType == ALL && SpectrumReady) || acqType == CH_1_ONLY)  // the acquisitionDone is not signaled until every channel values are ready
           signal Sensor.acquisitionDone(result, acqType);
    }

    event void Spectrum.readDone(error_t result, uint16_t data) {
        SpectrumReady = TRUE;

        val_spectrum = data;

        if ((acqType == ALL && VisibleReady) || acqType == CH_2_ONLY)  // the acquisitionDone is not signaled until every channel values are ready
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