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
 * This component calculates the Mean of each channel and returns the magnitude
 * of the vector mean.
 *
 * @author Philip Kuryloski
 *
 * @version 1.0
 */
 
module VectorMagnitudeP {
      
	provides interface Feature;
      
	uses {
		interface Boot;
		interface FeatureEngine;
		interface MathUtils;
	}
}

implementation {
     
	bool registered = FALSE;

	event void Boot.booted() {
		if (!registered) {
			call FeatureEngine.registerFeature(VECTOR_MAGNITUDE);
			registered = TRUE;
		}
	}

	command uint8_t Feature.calculate(int16_t** data, uint8_t channelMask, uint16_t dataLen, int8_t* result) {
		uint8_t i;
		uint16_t j;
		uint8_t mask = 0x08;
	
		int32_t sum[4] = {0, 0, 0, 0};
		uint32_t avg2[4] = {0, 0, 0, 0};
		
		uint32_t tmp1, tmp2, mag;
		
		for (i = 0; i<4; i++) {
			if ( (channelMask & (mask>>i)) == (mask>>i)) {
				for (j=0; j<dataLen; j++) {
					sum[i] += data[i][j];
				}
				
				sum[i] = sum[i]/dataLen;
				
				avg2[i] = sum[i]*sum[i];
			}
		}
		
		tmp1 = call MathUtils.isqrt(avg2[0] + avg2[1]);
		tmp2 = call MathUtils.isqrt(avg2[2] + avg2[3]);
		mag = call MathUtils.isqrt((tmp1*tmp1) + (tmp2*tmp2));
		
		((uint16_t *)result)[0] = (uint16_t)mag;

		return BM_CH1_ONLY;
	}
	
	command uint8_t Feature.getResultSize() {
		return 2;
	}
}




