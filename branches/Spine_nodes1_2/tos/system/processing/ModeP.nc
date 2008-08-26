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
* This component calculate the Mode of a set of elements.
*
* @author Raffaele Gravina
*
* @version 1.2
*/

module ModeP {

       provides interface Feature;
       
       uses {
          interface Boot;
          interface FeatureEngine;
          interface Sort;
       }
}

implementation {
       
       bool registered = FALSE;

       event void Boot.booted() {
          if (!registered) {
             // the feature self-registers to the FeatureEngine at boot time
             call FeatureEngine.registerFeature(MODE);
             registered = TRUE;
          }
       }

       int32_t calculate(int16_t* data, uint16_t elemCount) {
            uint16_t i = 0, j;
            uint16_t iMax = 0;
            int16_t orderedData[elemCount];
            uint16_t tmp[elemCount];

            memset(tmp, 0x00, sizeof tmp);

            // to boost the algorithm, we first sort the array (mergeSort takes O(nlogn))
            memcpy(orderedData, data, sizeof orderedData);
            call Sort.mergeSort(orderedData, elemCount, 0, elemCount-1);

            // now we look for the max number of occurences per each value
            while(i<elemCount-1)
               for (j = i+1; j<elemCount; j++)
        	  if(orderedData[i] == orderedData[j]) {
        	     tmp[i] = j-i+1;
        	     if (j==elemCount-1) i = elemCount-1; // exit condition
        	  }
          	  else {
        	     i = j;
        	     break;
                  }

            // we choose the overall max
            for(i = 1; i < elemCount; i++)
               if( tmp[i] > tmp[iMax])
                  iMax = i;

            return orderedData[iMax];
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
         return 2;    // uint16_t = 2bytes
       }
}




