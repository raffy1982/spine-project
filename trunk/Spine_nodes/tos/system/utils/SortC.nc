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
 * This utility component contains several kind of array sorting algorithms
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
 
module SortC
{
   provides interface Sort;
}
implementation
{
       uint16_t i, j;
       int16_t min, temp;

       /**
       * Sorts the given array using the Bubble Sort algorithm
       *
       * @param array : the pointer to the array to be sorted
       * @param length : the lenght of the array
       *
       * @return void
       */
       command void Sort.bubbleSort(uint16_t* array, uint16_t length) {

            for (i = 0; i < length - 1; i++)
                for (j = 0; j < length - 1; j++) {
                    if ((int16_t)(*(array + j) ) > (int16_t)(*(array + j + 1) )) {
                       temp = (int16_t)(*(array + j) );
                       (int16_t)(*(array + j) ) = (int16_t)(*(array + j + 1) );
                       (int16_t)(*(array + j + 1) ) = temp;
                    }
                }
       }

       /**
       * Sorts the given array using the Insertion Sort algorithm
       *
       * @param array : the pointer to the array to be sorted
       * @param length : the lenght of the array
       *
       * @return void
       */
       command void Sort.insertionSort(uint16_t* array, uint16_t length) {

            for (i = 1; i < length; i++) {
                temp = (int16_t)(*(array + i) );
                j = i;
                while ((j > 0) && ((int16_t)(*(array + j - 1) ) > temp)) {
                      (int16_t)(*(array + j) ) = (int16_t)(*(array + j - 1) );
                      j = j - 1;
                }
                (int16_t)(*(array + j) ) = temp;
            }
       }

       /**
       * Sorts the given array using the Selection Sort algorithm
       *
       * @param array : the pointer to the array to be sorted
       * @param length : the lenght of the array
       *
       * @return void
       */
       command void Sort.selectionSort(uint16_t* array, uint16_t length)
       {
            for (i = 0; i < length - 1; i++)
            {
                min = i;
                for (j = i + 1; j < length; j++)
                    if ((int16_t)(*(array + j) ) < (int16_t)(*(array + min) ))
                        min = j;

                temp = (int16_t)(*(array + i) );
                (int16_t)(*(array + i) ) = (int16_t)(*(array + min) );
                (int16_t)(*(array + min) ) = temp;
            }
       }


}




