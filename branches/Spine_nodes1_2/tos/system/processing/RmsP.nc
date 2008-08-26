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
 * This component calculate the Root Mean Square (RMS) of a set of elements.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.2
 */
 
 module RmsP {
       
       uses {
          interface Boot;
          interface FeatureEngine;
          
          interface MathUtils;
       }

       provides interface Feature;
 }

 implementation {
       
       bool registered = FALSE;

       event void Boot.booted() {
          if (!registered) {
             // the feature self-registers to the FeatureEngine at boot time
             call FeatureEngine.registerFeature(RMS);
             registered = TRUE;
          }
       }

       int32_t calculate(int16_t* data, uint16_t elemCount) {
            int32_t rms = 0;
            uint16_t i;

            for(i = 0; i<elemCount; i++)
                  rms += ( (int32_t)data[i] * (int32_t)data[i] );

            rms /= elemCount;

            return call MathUtils.isqrt(rms);
       }
       
       command uint8_t Feature.calculate(int16_t** data, uint8_t channelMask, uint16_t dataLen, int8_t* result) {
            uint8_t i;
            uint8_t mask = 0x08;
            uint8_t rChCount = 0;

            for (i = 0; i<MAX_VALUE_TYPES; i++)
               if ( (channelMask & (mask>>i)) == (mask>>i))
                  ((uint16_t *) result)[rChCount++] = calculate(data[i], dataLen);

            return channelMask;
       }
       
       command uint8_t Feature.getResultSize() {
         return 2;   // uint16_t = 2bytes
       }
 }




