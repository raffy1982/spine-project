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

generic module CircularBufferC(uint16_t dim_buffer)
{
   provides interface Init;
   provides interface CircularBuffer;
}
implementation
{
       int16_t buffer[dim_buffer];
       uint16_t shift = dim_buffer;

       norace uint16_t i_bufferStart = 0;
       uint16_t i_curr = 0;

       norace uint8_t i;
       
       async command int16_t CircularBuffer.firstElement() {
            return buffer[i_bufferStart];
       }

       async command int16_t CircularBuffer.lastElement() {
            if (i_curr == 0)
                return buffer[dim_buffer - 1];
            else
                return buffer[(i_curr - 1)];
       }

       async command uint16_t CircularBuffer.getWindow() {
            int16_t win[dim_buffer];

            for(i=0; i<dim_buffer; i++) {
                 win[i] = buffer[(i_bufferStart + i) % dim_buffer];
            }
            i_bufferStart = (i_bufferStart + shift) % dim_buffer;
            return (uint16_t)(&win[0]);
       }

       command void CircularBuffer.putElem(int16_t put) {
            atomic {
                   buffer[i_curr] = (int16_t)put;
                   i_curr = (i_curr + 1) % dim_buffer;
            }

       }

       command void CircularBuffer.putArray(uint16_t* put, uint16_t size) {
            for(i=0; i<size; i++) {
                buffer[i_curr] = (int16_t)(*(put + i) );
                i_curr = (i_curr + 1) % dim_buffer;
            }
       }

       //command void CircularBuffer.setShift(uint16_t shiftV) {
      //      shift = shiftV;
      // }

       async command uint16_t CircularBuffer.size() {
            return dim_buffer;
       }

       command error_t Init.init() {
            memset(buffer, 0, sizeof(buffer));
            return SUCCESS;
      }

 }




