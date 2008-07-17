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
 * This component calculates the Mean of accelerometer data and estimates Pitch and Roll.
 *
 * @author Philip Kuryloski
 *
 * @version 1.0
 */
 
module PitchRollP {
      
	provides interface MultiChannelFeature;
      
	uses {
		interface Boot;
		interface MultiChannelFeatureEngine;
	}
}

implementation {
     
	bool registered = FALSE;

	event void Boot.booted() {
		if (!registered) {
			call MultiChannelFeatureEngine.registerMultiChannelFeature(PITCH_ROLL);
			registered = TRUE;
		}
	}

	command error_t MultiChannelFeature.calculate(int16_t** data, uint8_t channelMask, uint16_t dataLen, int8_t* result) {
		uint16_t i;
		int32_t sum_x = 0;
		int32_t sum_y = 0;
		int32_t sum_z = 0;
		
		int16_t pitch, roll;
		float x, y, z;
		float hyp;
		float p, r;
		
		if (channelMask == 0x0E) {	// binary 0000 1110
	
			// calculate Mean values for X, Y & Z channels
			for(i = 0; i < dataLen; i++) {
				sum_x += data[0][i];
				sum_y += data[1][i];
				sum_z += data[2][i];
			}
		
			x = sum_x / dataLen;
			y = sum_y / dataLen;
			z = sum_z / dataLen;
			
			// derive Pitch & Roll
			if(y == 0 && z == 0) {
				p = M_PI_2;
			} else {
				hyp = sqrtf((y*y)+(z*z));
				p = atan2f(x, hyp);
			}
			
			if(y == 0 && z == 0) {
				r = 0.0;
			} else {
				r = atan2f(-y, z);
			}
			
			// convert radians to degrees
			pitch = p*57.295779513082322;
			roll = r*57.295779513082322;
			
			// store the result
			((int16_t *)result)[0] = pitch;
			((int16_t *)result)[1] = roll;
	
			return SUCCESS;
		} else {
			return FAIL;
		}
		
	}
	
	command uint8_t MultiChannelFeature.getReturnedChannelCount() {
		return 2;
	}
	
	command uint8_t MultiChannelFeature.getResultSize() {
		return 2;
	}
}




