
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
 *
 * @param dim_buffer : the size of the buffer
 * @param elemSize : the number of bytes of the elements to be stored in.
 */

generic module GenericCircularBufferC(uint16_t dim_buffer, uint8_t elemSize)
{
   provides interface GenericCircularBuffer;
}
implementation
{
       int8_t buffer[dim_buffer * elemSize];
       int8_t win[dim_buffer * elemSize];
       uint16_t shift = dim_buffer * elemSize;

       uint16_t i_bufferStart = 0; // indicates the position on the buffer where the window starts
       uint16_t i_curr = 0; // indicates the next writing cell in the buffer

       // to support generic data types, an array of bytes is required to store
       // the actual value of the first and the last element of the buffer
       int8_t retF[elemSize];
       int8_t retL[elemSize];
       
       uint16_t i;  


       /*
       * Returns the first element of the buffer
       *
       * @return the pointer to the element
       */
       command void* GenericCircularBuffer.firstElement() {
            memcpy(&retF, &buffer[i_bufferStart * elemSize], elemSize);
            return &retF;
       }
       
       /*
       * Returns the last element of the buffer
       *
       * @return the pointer to the element
       */
       command void* GenericCircularBuffer.lastElement() {
            if (i_curr == 0)
                 memcpy(&retL, &buffer[((dim_buffer - 1) * elemSize)], elemSize);
            else
                 memcpy(&retL, &buffer[((i_curr - 1) * elemSize)], elemSize);
            return &retL;
       }

       /*
       * Returns the pointer to the current window
       *
       * @return the pointer to the current window
       */
       command void* GenericCircularBuffer.getWindow() {
            memcpy(&win, &buffer[i_bufferStart * elemSize], (dim_buffer - i_bufferStart) * elemSize);
            memcpy(&win[(dim_buffer - i_bufferStart) * elemSize], &buffer, i_bufferStart * elemSize);

            i_bufferStart = (i_bufferStart + shift) % dim_buffer;

            return &win;
       }

       /*
       * Inserts the given value in the position pointed by the current writing index
       *
       * @param elem : the pointer to the elem to be stored
       *
       * @return void
       */
       command void GenericCircularBuffer.putElem(void* elem) {
            atomic {
                   memcpy(&buffer[(i_curr * elemSize)], elem, elemSize);
                   i_curr = (i_curr + 1) % dim_buffer;
            }
       }

       /*
       * Sets the shift value.
       * Shift is defined as the number of elements the window slides.
       *
       * @param shiftV : the new shift value
       *
       * @return void
       */
       command void GenericCircularBuffer.setShift(uint16_t shiftV) {
            atomic shift = shiftV;
       }

       /*
       * Returns the length of the buffer
       *
       * @return the size of the buffer
       */
       command uint16_t GenericCircularBuffer.size() {
            return dim_buffer;
       }

       /*
       * Initializes the buffer, putting all zero values
       *
       * @return always <code>SUCCESS</code>
       */
       command error_t GenericCircularBuffer.init() {
            memset(buffer, '\0', dim_buffer * elemSize);
            return SUCCESS;
      }

 }




