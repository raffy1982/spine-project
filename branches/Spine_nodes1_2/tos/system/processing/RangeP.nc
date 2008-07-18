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
 * This component calculate the Range (Max - Min) of a set of elements.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
 
 module RangeP {

       provides interface Feature;
       
       uses {
          interface Boot;
          interface FeatureEngine;
       }
}

implementation {
       
       bool registered = FALSE;

       event void Boot.booted() {
          if (!registered) {
             call FeatureEngine.registerFeature(RANGE);
             registered = TRUE;
          }
       }

       int32_t calculate(int16_t* data, uint16_t elemCount) {
            uint16_t i;
            int16_t min = *data;
            int16_t max = min;
            
            for(i = 1; i < elemCount; i++) {
                 if( *(data + i) < min)
                      min = *(data + i);
                 if( *(data + i) > max)
                      max = *(data + i);
            }

            return (max - min);
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
         return 2;
       }
}




