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
 * This component calculate the istantaneus Total Energy.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
 
module TotalEnergyP {
       uses {
          interface Boot;
          interface FeatureEngine;
       }

       provides interface Feature;
}

implementation {
       
       bool registered = FALSE;

       event void Boot.booted() {
          if (!registered) {
             call FeatureEngine.registerFeature(TOTAL_ENERGY);
             registered = TRUE;
          }
       }

       command uint8_t Feature.calculate(int16_t** data, uint8_t channelMask, uint16_t dataLen, int8_t* result) {
          uint8_t i;
	  uint16_t j;
	  uint8_t mask = 0x08;

	  uint32_t enCh[4] = {0, 0, 0, 0};

	  uint32_t totEn;

	  for (i = 0; i<4; i++) 
	     if ( (channelMask & (mask>>i)) == (mask>>i))
	        for (j=0; j<dataLen; j++)
		   enCh[i] += (int32_t)data[i][j]*(int32_t)data[i][j]/dataLen;

          totEn = enCh[0] + enCh[1] + enCh[2] + enCh[3];

	  ((uint32_t *)result)[0] = totEn;

	  return BM_CH1_ONLY;
       }
       
       command uint8_t Feature.getResultSize() {
          return 4;
       }
}




