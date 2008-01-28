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
 * This component evaluates the following linear expression: result = aBX + c
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */   
 interface Linear {

       /*
       * Calculates the following linear expression: result = aBX + c
       *
       * @param data : the pointer to the data array (X)
       * @param elemCount : the length of the array   ( |X| )
       * @param coeff : the pointer to the coefficient array (B)
       * @param multiplierNum : the numerator part of the rational expression of the multiplier (a)
       * @param multiplierDen : the denominator part of the rational expression of the multiplier (a)
       * @param shift : the shift of the expression (c)
       *
       * Note: the split of the multiplier 'a' is required, to allow the use of float values
       *
       * @return 'int32_t' : the result of the evaluated linear expression
       */
       command int32_t calculate(uint16_t* data, uint16_t elemCount, uint16_t* coeff, int16_t multiplierNum, int16_t multiplierDen, int16_t shift);

 }




