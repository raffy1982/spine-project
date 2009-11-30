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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

/**
 * This component calculate the factors for a Kcal assessment.
 * It assumes all three channels of a 3-axis accelerometer are specified.
 *
 * @author Edmund Seto  <seto@berkeley.edu>
 * @author Raffaele Gravina <rgravina@wsnlabberkeley.com>
 * @author Po Yan <pyan@eecs.berkeley.edu>
 *
 * @version 1.3
*/
 
module KcalP {

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
             call FeatureEngine.registerFeature(KCAL);
             registered = TRUE;
          }
       }


       command uint8_t Feature.calculate(int16_t** data, uint8_t channelMask, uint16_t dataLen, uint8_t* result) {
            uint8_t i = 0, j = 0;

            int32_t history[3] = {0, 0, 0};
            uint32_t res[2] = {0, 0};
	    int32_t d[3] = {0, 0, 0};
	    int32_t p[3] = {0, 0, 0};
	    
            int32_t num = 0;
	    int32_t den = 0;
	    int32_t value = 0;

	    uint32_t pMagn = 0;

	    // this is historical average of the past dataLen samples
            for (i=0; i<3; i++) {
                for (j=0; j<dataLen; j++)
                    history[i] += data[i][j];
		history[i] /= 30;
            }

	    // v is history
	    // compute d = (ax - vx, ay-vy, az-vz)
	    // compute p = ((d dot v)/(v dot v)) v     		p is the vertical component of d
	    // compute h = d - p				h is the horizontal component of d

	    for (j=0; j<dataLen; j++) {

		// compute d as moving average if possible.
            	for (i=0; i<3; i++)
		  d[i] = history[i] - data[i][j];

		num = 0;
		den = 0;
		value = 0;

            	for (i=0; i<3; i++) {
		  num = (d[0]*history[0] + d[1]*history[1] + d[2]*history[2]);
		  den = (history[0]*history[0] + history[1]*history[1] + history[2]*history[2]);
		  value = ((num<<10)/den)*history[i];

		  p[i] = value>>10;
		}

	    	pMagn = p[0]*p[0] + p[1]*p[1] + p[2]*p[2];
		res[0] += call MathUtils.isqrt(pMagn);

		// subtract for horizontal component
		// compute h = d - p				h is the horizontal component of d
		// note, this is squared magnitude
		res[1] +=  call MathUtils.isqrt( (d[0]-p[0])*(d[0]-p[0]) + (d[1]-p[1])*(d[1]-p[1]) + (d[2]-p[2])*(d[2]-p[2]) );
            }   	  
    
            ((uint32_t *)result)[0]  = (uint32_t) res[0];		// vertical component
            ((uint32_t *)result)[1]  = (uint32_t) res[1];		// squared horizontal component
	    
            return BM_CH1_CH2_ONLY;
       }


       command uint8_t Feature.getResultSize() {
         return 4;   // uint32_t = 4bytes
       }
}
