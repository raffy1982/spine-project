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
 * This component calculate the cross-axial istantaneus Total Energy over the sensor axis.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
 
module TotalEnergyC {
       provides interface TotalEnergy;
}

implementation {

       int32_t energy;
       uint8_t i;

       /*
       * Calculates the current Total Energy of the given data array,
       * which has to contain the values over all sensor axis
       *
       * @param data : the pointer to the array
       * @param elemCount : the lenght of the given array
       *
       * @return 'int32_t' : the calculated value of cross-axial Total Energy
       */
       command int32_t TotalEnergy.calculate(int16_t* data, uint16_t elemCount) {

            energy = 0;
            for(i=0; i<elemCount; i++)
                  energy += ( ((int32_t)(*(data + i))) * ((int32_t)(*(data + i))) );

            return  energy;
       }

}




