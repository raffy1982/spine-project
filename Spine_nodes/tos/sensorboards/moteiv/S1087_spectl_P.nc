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
 * Module component of the 'Hamamatsu S1087-01' 
 * light spectrum sensor driver for the motive tmote sky platform
 *
 * @author Carlo Caione <carlo.caione@unibo.it>
 *
 * @version 1.3
 */
#include "Msp430Adc12.h"

module S1087_spectl_P {
  provides interface AdcConfigure<const msp430adc12_channel_config_t*>;
}
implementation {

  const msp430adc12_channel_config_t config = {
      inch: INPUT_CHANNEL_A5,
      sref: REFERENCE_VREFplus_AVss,
      ref2_5v: REFVOLT_LEVEL_NONE,
      adc12ssel: SHT_SOURCE_ACLK,
      adc12div: SHT_CLOCK_DIV_1,
      sht: SAMPLE_HOLD_4_CYCLES,
      sampcon_ssel: SAMPCON_SOURCE_SMCLK,
      sampcon_id: SAMPCON_CLOCK_DIV_1
  };
  
  async command const msp430adc12_channel_config_t* AdcConfigure.getConfiguration()
  {
    return &config;
  }
}
