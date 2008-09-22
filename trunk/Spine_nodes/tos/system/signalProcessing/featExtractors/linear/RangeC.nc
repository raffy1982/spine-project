/*****************************************************************
SPINE - Signal Processing In-Note Environment is a framework that 
allows dynamic configuration of feature extraction capabilities 
of WSN nodes via an OtA protocol

Copyright (C) 2007 Telecom Italia S.p.A. 
�
GNU Lesser General Public License
�
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
�
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the GNU
Lesser General Public License for more details.
�
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA� 02111-1307, USA.
*****************************************************************/

/**
 * This component calculate the Range of a set of elements.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 *
 * @param elemCount : the lenght of the given array
 */

generic module RangeC(uint16_t elemCount) {

       provides interface Range;
}

implementation {
       uint16_t i;

       int16_t min;
       int16_t max;

       /*
       * Calculates the Range of the given data array
       *
       * @param data : the pointer to the array
       *
       * @return 'int32_t' : the calculated value of Range
       */
       command int32_t Range.calculate(uint16_t* data) {
            min = (int16_t)(*(data) );
            max = min;
            for(i = 1; i < elemCount; i++){
                 if( (int16_t)(*(data + i) ) < min)
                      min = (int16_t)(*(data + i));
                 if( (int16_t)(*(data + i) ) > max)
                      max = (int16_t)(*(data + i) );
            }
            return max - min;
       }

       command error_t Range.init() {
            return SUCCESS;
      }

}



