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
 * With respect to the 'GenericCircularBuffer' component, this one requires as parameter for its 
 * initialization the array in which the elements will be stored. This allows to share the array
 * among several GenericSharedCircularBuffer.
 *
 * It's also possible to define over the buffer a window frame and a window shift.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 *
 * @param elemSize : the number of bytes of the elements to be stored in.
 */

generic module GenericSharedCircularBufferC(uint8_t elemSize)
{
   provides interface GenericSharedCircularBuffer;
}
implementation
{
       int8_t* buffer;

       uint16_t shift;
       uint16_t window;
       uint16_t dim_buffer;

       uint16_t i_bufferStart = 0; // indicates the position on the buffer where the window starts
       uint16_t i_curr; // indicates the next writing cell in the buffer

       uint16_t* i_curPunt; // just the pointer to i_curr variable.
                                   // This is needed for implementation concerns.

       // to support generic data types, an array of bytes is required to store
       // the actual value of the first and the last element of the buffer
       int8_t retF[elemSize];
       int8_t retL[elemSize];

       uint16_t tdmaAdvance;

       uint8_t nodeNumber = 1;

       /*
       * Returns the first element of the buffer
       *
       * @return the pointer to the element
       */
       command void* GenericSharedCircularBuffer.firstElement() {
            memcpy(&retF, (buffer+(i_bufferStart * elemSize)), elemSize);
            return &retF;
       }

       /*
       * Returns the last element of the buffer
       *
       * @return the pointer to the element
       */
       command void* GenericSharedCircularBuffer.lastElement() {
            memcpy(&i_curr, i_curPunt, 2);

            if (i_curr == 0)
                 memcpy(&retL, (buffer+((dim_buffer - 1) * elemSize)), elemSize);
            else
                 memcpy(&retL, (buffer+((i_curr - 1) * elemSize)), elemSize);
            return &retL;
       }

       /*
       * Stores in the given array the current window over the buffer
       *
       * @return void
       */
       command void GenericSharedCircularBuffer.getWindow(void* win) {

            if( (i_bufferStart+window-tdmaAdvance) > dim_buffer) {
                 memcpy(win, (buffer+((i_bufferStart-tdmaAdvance) * elemSize)), (dim_buffer - (i_bufferStart-tdmaAdvance)) * elemSize);
                 memcpy(win+((dim_buffer - (i_bufferStart-tdmaAdvance)) * elemSize), buffer, (window - (dim_buffer - (i_bufferStart-tdmaAdvance))) * elemSize);
            }

            else {
              
                 if(i_bufferStart < tdmaAdvance){
                     memcpy(win, (buffer+((dim_buffer - (tdmaAdvance - i_bufferStart)) * elemSize)), (tdmaAdvance - i_bufferStart) * elemSize);
                     memcpy(win+((tdmaAdvance - i_bufferStart) * elemSize), buffer, (window - (tdmaAdvance - i_bufferStart)) * elemSize);
                 }
                 else{
                      memcpy(win, (buffer+((i_bufferStart-tdmaAdvance) * elemSize)), window * elemSize);
                 }

            }
            
            i_bufferStart = (i_bufferStart + shift) % dim_buffer;
       }

       /*
       * Inserts the given value in the position pointed by the current writing index
       *
       * @param elem : the pointer to the elem to be stored
       *
       * @return void
       */
       command void GenericSharedCircularBuffer.putElem(void* elem) {
            atomic {
                   memcpy(&i_curr, i_curPunt, 2);
                   memcpy((buffer+(i_curr * elemSize)), elem, elemSize);
                   i_curr = (i_curr + 1) % dim_buffer;
                   memcpy(i_curPunt, &i_curr, 2);
            }
       }

       /*
       * Sets the size of the window over the buffer
       *
       * @param wS : the new size of the window
       *
       * @return void
       */
       command void GenericSharedCircularBuffer.setWindowSize(uint16_t wS) {
            atomic window = wS;
       }

       /*
       * Sets the shift value.
       * Shift is defined as the number of elements the window slides.
       *
       * @param shiftV : the new shift value
       *
       * @return void
       */
       command void GenericSharedCircularBuffer.setShift(uint16_t shiftV) {
            atomic shift = shiftV;
       }

       /*
       * Returns the length of the buffer
       *
       * @return the size of the buffer
       */
       command uint16_t GenericSharedCircularBuffer.size() {
            return dim_buffer;
       }
       
       /*
       * Returns the length of the window over the buffer
       *
       * @return the size of the window over the buffer
       */
       command uint16_t GenericSharedCircularBuffer.getWindowSize() {
            return window;
       }
       
       /*
       * Initializes the buffer.
       *
       * @param buf : the pointer to the buffer to be used
       * @param curr : the pointer to the current writing position of the given buffer 'buf'
       * @param dim : the size of the given buffer 'buf'
       * @param w : the size of the new window over the buffer
       * @param sh : the shift value (number of elements the window slides)
       *
       * @return always <code>SUCCESS</code>
       */
       command error_t GenericSharedCircularBuffer.init(int8_t* buf, uint16_t* curr, uint16_t dim, uint16_t w, uint16_t sh) {
            buffer = buf;
            i_curPunt = curr;
            shift = sh;
            window = w;
            dim_buffer = dim;
            tdmaAdvance = (window / nodeNumber) * (nodeNumber - 1);
            memcpy(&i_bufferStart, i_curPunt, 2);
            if((i_bufferStart - w + sh) < 0)
                 i_bufferStart = dim_buffer + (i_bufferStart - w + sh);
            else
                 i_bufferStart = i_bufferStart - w + sh;

            return SUCCESS;
      }
      
      /*
       * Sets the number of Nodes in the Network.
       *
       * @param nM : the number of Nodes in the Network
       *
       * @return void
       */
       command void GenericSharedCircularBuffer.setNodeNumber(uint8_t nM){
             nodeNumber = nM;
       }

 }




