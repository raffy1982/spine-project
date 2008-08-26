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
 * This utility component contains useful math functions
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.2
 */ 
 interface MathUtils {

        /*
        * Returns the MAX value of the given 'data' array of size 'elemCount'.
        *
        *
        * @param data the pointer to the array
        * @param elemCount the size of the array
        *
        * @return the MAX value of the 'data' array
        */
        command uint16_t max(int16_t* data, uint16_t elemCount);
        
        /*
        * Returns the MIN value of the given 'data' array of size 'elemCount'.
        *
        *
        * @param data the pointer to the array
        * @param elemCount the size of the array
        *
        * @return the MIN value of the 'data' array
        */
        command uint16_t min(int16_t* data, uint16_t elemCount);

        /*
        * Returns the MEAN value of the given 'data' array of size 'elemCount'.
        *
        *
        * @param data the pointer to the array
        * @param elemCount the size of the array
        *
        * @return the MEAN value of the 'data' array
        */
        command uint16_t mean(int16_t* data, uint16_t elemCount);

        /*
        * Returns the VARIANCE value of the given 'data' array of size 'elemCount'.
        *
        *
        * @param data the pointer to the array
        * @param elemCount the size of the array
        *
        * @return the VARIANCE value of the 'data' array
        */
        command uint32_t variance(int16_t* data, uint16_t elemCount);

        /*
        * Returns the truncated integer square root of the given parameter.
        *
        * This is the most stable method of the ones here provided.
        *
        * @param y the value to be processed
        *
        * @return the truncated integer square root of the value
        */
        command uint32_t isqrt(uint32_t y);

        /*
        * Returns the truncated integer square root of the given parameter.
        *
        * This function implements the Buchanan algorithm.
        *
        * Technical details: If number is the maximum unsigned int value, call it MAX_VAL,
        *                    then the first evaluation of NEXT(n, number), with n == 1,
        *                    produces an overflow when 1 + MAX_VAL/1 is evaluated.
        *                    For an unsigned type the overflow typically wraps around and yields zero
        *                    as the macro result and zero as the overall function result.
        *
        * @param number the value to be processed
        *
        * @return the truncated integer square root of the value
        */
        command uint32_t isqrt_Buchanan(uint32_t number);

        /*
        * Returns the truncated integer square root of the given parameter.
        *
        * Contributors include Arne Steinarson for the basic approximation idea,
        * Dann Corbit and Mathew Hendry for the first cut at the algorithm,
        * Lawrence Kirby for the rearrangement, improvments and range optimization
        * and Paul Hsieh for the round-then-adjust idea.
        *
        * @param x the value to be processed
        *
        * @return the truncated integer square root of the value
        */
        command uint32_t isqrt_Steinarson(uint32_t x);

 }




