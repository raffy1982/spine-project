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
 * This component calculate the Variance of a set of elements.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
 
module VarC {
       provides interface Var;
}

implementation {

       uint32_t var;
       uint16_t i;
       int32_t mu;

       /*
       * Calculates the variance of the given data array
       *
       * @param data : the pointer to the array
       * @param elemCount : the lenght of the given array
       *
       * @return 'uint32_t' : the calculated value of Variance
       */
       command uint32_t Var.calculate(int16_t* data, uint16_t elemCount) {
            var = 0;
            mu = 0;
            for(i=0; i<elemCount; i++)
                mu += *(data + i);
            mu /= elemCount;

            for(i=0; i<elemCount; i++)
                  var += (  (((int32_t)(*(data + i))) - mu) * (((int32_t)(*(data + i))) - mu)  );

            return  (var/elemCount);
       }

}




