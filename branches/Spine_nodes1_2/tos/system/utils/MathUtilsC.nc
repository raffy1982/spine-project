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
 
#include <stdlib.h>
#define NEXT(n, i)  (((n) + (i)/(n)) >> 1)

module MathUtilsC {
   provides interface MathUtils;
}
implementation
{
      command uint16_t MathUtils.max(int16_t* data, uint16_t elemCount) {
            uint16_t i;
            int16_t max = data[0];

            for(i = 1; i < elemCount; i++)
                  if( data[i] > max)
                       max = data[i];

            return max;
      }
      
      command uint16_t MathUtils.min(int16_t* data, uint16_t elemCount) {
            uint16_t i;
            int16_t min = data[0];

            for(i = 1; i < elemCount; i++)
                  if( data[i] < min)
                       min = data[i];

            return min;
      }
      
      command uint16_t MathUtils.mean(int16_t* data, uint16_t dataLen) {
            uint16_t i;
            int32_t mu = 0;

            for(i = 0; i < dataLen; i++)
                mu += data[i];

            return  (mu / dataLen);
      }
      
      command uint32_t MathUtils.varianceOld(int16_t* data, uint16_t elemCount) {
          uint32_t var = 0;
          uint16_t i;
          int16_t mu = call MathUtils.mean(data, elemCount);

          for(i = 0; i<elemCount; i++) {
                var += (  ((int32_t)data[i] - mu) * ((int32_t)data[i] - mu)  );
            }
		  
          return  (var/elemCount);
       }
       //variance implementation changed to have computation time linear with # samples
       command uint32_t MathUtils.variance(int16_t* data, uint16_t elemCount) {
          uint32_t var = 0;
          uint16_t i;
          //int16_t mu = call MathUtils.mean(data, elemCount);
		  int16_t mu = 0;	
          int32_t val = 0;
          for(i = 0; i<elemCount; i++) {
                //var += (  ((int32_t)data[i] - mu) * ((int32_t)data[i] - mu)  );
                val = (int32_t)data[i];
                mu += val;
                var += (  val*val );
            }
          mu /= elemCount;  
          var /= elemCount;
          var -= (mu*mu); 
		  return (var);
          //return  (var/elemCount);
       }

      /*
      * Returns the truncated integer square root of the given parameter.
      *
      * This is the most stable method of the ones here provided.
      *
      * @param y : the value to be processed
      *
      * @return 'uint32_t' the truncated integer square root of the value
      */
      command uint32_t MathUtils.isqrt(uint32_t y)
      {
              uint32_t x_old, x_new;
              uint32_t testy;
              uint16_t nbits;
              uint16_t i;

              if (y <= 0) {
                  if (y != 0)
                      return -1L;
                  return 0L;
              }

              /* select a good starting value using binary logarithms: */
              nbits = (sizeof(y) * 8) - 1;    /* subtract 1 for sign bit */
              for (i = 4, testy = 16L;; i += 2, testy <<= 2L) {
                  if (i >= nbits || y <= testy) {
                      x_old = (1L << (i / 2L));       /* x_old = sqrt(testy) */
                      break;
                  }
              }
              /* x_old >= sqrt(y) */
              /* use the Babylonian method to arrive at the integer square root: */
              for (;;) {
                  x_new = (y / x_old + x_old) / 2L;
                  if (x_old <= x_new)
                          break;
                  x_old = x_new;
              }
              /* make sure that the answer is right: */
              if ((long long) x_old * x_old > y || ((long long) x_old + 1) * ((long long) x_old + 1) <= y)
                  return -1L;
              return x_old;
       }

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
       * @param number : the value to be processed
       *
       * @return 'uint32_t' the truncated integer square root of the value
       */
       command uint32_t MathUtils.isqrt_Buchanan(uint32_t number) {
           uint32_t n = 1;
           uint32_t n1 = NEXT(n, number);

           while(abs(n1 - n) > 1) {
               n  = n1;
               n1 = NEXT(n, number);
           }
           while((n1*n1) > number) {
               n1 -= 1;
           }

           return n1;
       }
       


       /*
       * Returns the truncated integer square root of the given parameter.
       *
       * Contributors include Arne Steinarson for the basic approximation idea,
       * Dann Corbit and Mathew Hendry for the first cut at the algorithm,
       * Lawrence Kirby for the rearrangement, improvments and range optimization
       * and Paul Hsieh for the round-then-adjust idea.
       *
       * @param x : the value to be processed
       *
       * @return 'uint32_t' the truncated integer square root of the value
       */
       command uint32_t MathUtils.isqrt_Steinarson(uint32_t x) {

         uint8_t sqq_table[] = {
               0,  16,  22,  27,  32,  35,  39,  42,  45,  48,  50,  53,  55,  57, 59,  61,  64,  65,  67,  69,  71,  73,  75,  76,  78,  80,  81,  83,
              84,  86,  87,  89,  90,  91,  93,  94,  96,  97,  98,  99, 101, 102, 103, 104, 106, 107, 108, 109, 110, 112, 113, 114, 115, 116, 117, 118,
             119, 120, 121, 122, 123, 124, 125, 126, 128, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 144, 145,
             146, 147, 148, 149, 150, 150, 151, 152, 153, 154, 155, 155, 156, 157, 158, 159, 160, 160, 161, 162, 163, 163, 164, 165, 166, 167, 167, 168,
             169, 170, 170, 171, 172, 173, 173, 174, 175, 176, 176, 177, 178, 178, 179, 180, 181, 181, 182, 183, 183, 184, 185, 185, 186, 187, 187, 188,
             189, 189, 190, 191, 192, 192, 193, 193, 194, 195, 195, 196, 197, 197, 198, 199, 199, 200, 201, 201, 202, 203, 203, 204, 204, 205, 206, 206,
             207, 208, 208, 209, 209, 210, 211, 211, 212, 212, 213, 214, 214, 215, 215, 216, 217, 217, 218, 218, 219, 219, 220, 221, 221, 222, 222, 223,
             224, 224, 225, 225, 226, 226, 227, 227, 228, 229, 229, 230, 230, 231, 231, 232, 232, 233, 234, 234, 235, 235, 236, 236, 237, 237, 238, 238,
             239, 240, 240, 241, 241, 242, 242, 243, 243, 244, 244, 245, 245, 246, 246, 247, 247, 248, 248, 249, 249, 250, 250, 251, 251, 252, 252, 253,
             253, 254, 254, 255
         };

         uint32_t xn;

            if (x >= 0x10000)
                if (x >= 0x1000000)
                    if (x >= 0x10000000)
                        if (x >= 0x40000000) {
                            if (x >= 65535UL*65535UL)
                                return 0xFFFF;
                            xn = sqq_table[x>>24] << 8;
                        } else
                            xn = sqq_table[x>>22] << 7;
                    else
                        if (x >= 0x4000000)
                            xn = sqq_table[x>>20] << 6;
                        else
                            xn = sqq_table[x>>18] << 5;
                else {
                    if (x >= 0x100000)
                        if (x >= 0x400000)
                            xn = sqq_table[x>>16] << 4;
                        else
                            xn = sqq_table[x>>14] << 3;
                    else
                        if (x >= 0x40000)
                            xn = sqq_table[x>>12] << 2;
                        else
                            xn = sqq_table[x>>10] << 1;

                    goto nr1;
                }
            else
                if (x >= 0x100) {
                    if (x >= 0x1000)
                        if (x >= 0x4000)
                            xn = (sqq_table[x>>8] >> 0) + 1;
                        else
                            xn = (sqq_table[x>>6] >> 1) + 1;
                    else
                        if (x >= 0x400)
                            xn = (sqq_table[x>>4] >> 2) + 1;
                        else
                            xn = (sqq_table[x>>2] >> 3) + 1;

                    goto adj;
                } else
                    return sqq_table[x] >> 4;

        /* Run two iterations of the standard convergence formula */

            xn = (xn + 1 + x / xn) / 2;
        nr1:
            xn = (xn + 1 + x / xn) / 2;
        adj:

            if (xn * xn > x) /* Correct rounding if necessary */
               xn--;

            return xn;
       }

}




