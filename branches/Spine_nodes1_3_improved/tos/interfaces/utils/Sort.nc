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
 * @version 1.2
 */  
 interface Sort {

    /**
    * Sorts the given array using the Bubble Sort algorithm
    *
    * @param array : the pointer to the array to be sorted
    * @param length : the lenght of the array
    *
    * @return void
    */
    command void bubbleSort(uint16_t* array, uint16_t length);

    /**
    * Sorts the given array using the Insertion Sort algorithm
    *
    * @param array : the pointer to the array to be sorted
    * @param length : the lenght of the array
    *
    * @return void
    */
    command void insertionSort(uint16_t* array, uint16_t length);

    /**
    * Sorts the given array using the Selection Sort algorithm
    *
    * @param array : the pointer to the array to be sorted
    * @param length : the lenght of the array
    *
    * @return void
    */
    command void selectionSort(uint16_t* array, uint16_t length);
    
    /**
    * Sorts the given array using the Recursive Quick Sort algorithm
    *
    * @param array : the pointer to the array to be sorted
    * @param length : the lenght of the array
    * @param low : beginning index ( put 0)
    * @param high : end index ( put array length - 1)
    *
    * @return void
    */
    command void quickSort(uint16_t* array, uint16_t length, uint16_t low, uint16_t high);

    /**
    * Sorts the given array using the Recursive Merge Sort algorithm
    *
    * @param array : the pointer to the array to be sorted
    * @param length : the lenght of the array
    * @param low : beginning index ( put 0)
    * @param high : end index ( put array length - 1)
    *
    * @return void
    */
    command void mergeSort(uint16_t* array, uint16_t length, uint16_t low, uint16_t high);

 }




