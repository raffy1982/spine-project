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
 * This component provides an implementation of a generic circular buffer.
 * Basically, it can handle 8, 16, 32 bit values, simply instancing it in the proper manner 
 * at compile time.
 *
 * It's also possible to define over the buffer a window frame and a window shift.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */  
 interface GenericCircularBuffer {

       /*
       * Returns the first element of the buffer
       *
       * @return the pointer to the element
       */
       command void* firstElement();

       /*
       * Returns the last element of the buffer
       *
       * @return the pointer to the element
       */
       command void* lastElement();

       /*
       * Returns the pointer to the current window
       *
       * @return the pointer to the current window
       */
       command void* getWindow();

       /*
       * Insert the given value in the position pointed by the current writing index
       *
       * @param elem : the pointer to the elem to be stored
       *
       * @return void
       */
       command void putElem(void* elem);

       /*
       * Sets the shift value.
       * Shift is defined as the number of elements the window slides.
       *
       * @param shiftV : the new shift value
       *
       * @return void
       */
       command void setShift(uint16_t shiftV);

       /*
       * Returns the length of the buffer
       *
       * @return the size of the buffer
       */
       command uint16_t size();

       /*
       * Initializes the buffer, putting all zero values
       *
       * @return always <code>SUCCESS</code>
       */
       command error_t init();

 }




