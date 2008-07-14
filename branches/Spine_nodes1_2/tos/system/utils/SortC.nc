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
       /**
       * Sorts the given array using the Bubble Sort algorithm
       *
       * @param array : the pointer to the array to be sorted
       * @param length : the lenght of the array
       *
       * @return void
       */
       command void Sort.bubbleSort(uint16_t* array, uint16_t length) {
            uint16_t i, j;
            int16_t temp;

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
            uint16_t i, j;
            int16_t temp;

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
       command void Sort.selectionSort(uint16_t* array, uint16_t length) {
            uint16_t i, j;
            int16_t min, temp;

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


       command void Sort.quickSort(uint16_t* array, uint16_t length, uint16_t low, uint16_t high) {
          //  'lo' is the lower index, 'hi' is the upper index
          //  of the region of array 'array' that is to be sorted
          uint16_t i = low, j = high, h;
          uint16_t x = array[(low+high)/2];
      
          //  partition
          do {
              while (array[i] < x) i++;
              while (array[j] > x) j--;
              if (i <= j) {
                  h = array[i]; array[i] = array[j]; array[j] = h;
                  i++; j--;
              }
          } while (i <= j);
      
          //  recursion
          if (low < j) call Sort.quickSort(array, length, low, j);
          if (i < high) call Sort.quickSort(array, length, i, high);
      }

      
      void merge(uint16_t* array, uint16_t length, uint16_t low, uint16_t mid, uint16_t high) {
         uint16_t aux[ length ];
         //uint16_t aux[ high - low + 1 ];

         uint16_t i = low;
         uint16_t j = mid + 1;
         uint16_t k = low;
         
         while(i <= mid && j <= high)
            if(array[i] < array[j])
               aux[k++] = array[i++];
            else
              aux[k++] = array[j++];

         while(i <= mid)
            aux[k++] = array[i++];

         while(j <= high)
            aux[k++] = array[j++];
         
         for(i = low; i<k; i++)
            array[i] = aux[i];
      }
      
      command void Sort.mergeSort(uint16_t* array, uint16_t length, uint16_t low, uint16_t high) {
         uint16_t mid;
         if(low < high) {
            mid = (low + high)/2;
            call Sort.mergeSort(array, length, low, mid);
            call Sort.mergeSort(array, length, mid+1, high);
            merge(array, length, low, mid, high);
         }
      }
}




