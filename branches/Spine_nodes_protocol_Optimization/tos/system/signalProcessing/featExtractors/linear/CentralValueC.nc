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
 * This component calculate the Central Value of a set of elements.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 *
 * @param elemCount : the lenght of the given array
 */
 
generic module CentralValueC(uint16_t elemCount) {
       uses interface Linear as Lin;

       provides interface CentralValue;
}

implementation {
       
       int16_t coeff[elemCount];
       int16_t multiplierNum, multiplierDen;
       int16_t shift;
       uint16_t i;

       /*
       * Calculates the Central Value of the given data array
       *
       * @param data : the pointer to the array
       *
       * @return 'int32_t' : the calculated value of Central Value
       */
       command int32_t CentralValue.calculate(uint16_t* data) {
            return call Lin.calculate(data, elemCount, coeff, multiplierNum, multiplierDen, shift);
       }

       command error_t CentralValue.init() {
            //memset(coeff, '\0', sizeof(coeff));
            for(i=0; i<elemCount; i++)
                 coeff[i] = 0;
            multiplierNum = 1;
            shift = 0;

            if(elemCount % 2 == 0){
                 i = elemCount/2;
                 coeff[i] = 1;
                 coeff[i-1] = 1;
                 multiplierDen = 2;
            }
            else {
                 i = (elemCount + 1)/2;
                 coeff[i] = 1;
                 multiplierDen = 1;
            }


            return SUCCESS;
      }

}




