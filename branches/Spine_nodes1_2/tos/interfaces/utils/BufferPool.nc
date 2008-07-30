/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that
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
 *  Interface for the BufferPool.
 *  The Buffer pool is composed of several logical buffers mapped on a single physical array. 
 *  Each byffer is handled in a circular fashion: 
 *  once the end of the single buffer is reached, new elements are stored from the beginning, overwriting the old ones
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */ 
interface BufferPool {

    /**
    * Returns the first available buffer ID
    *
    *
    * @return the first available buffer ID
    */
    command uint8_t getAvailableBuffer();

    /**
    * Releases the buffer 'bufferID'
    *
    * @param 'bufferID' the ID of the buffer to be released
    *
    * @return void
    */
    command void releaseBuffer(uint8_t bufferID);

    /**
    * Returns a pointer to an array containing the last 'windowSize' elements of the buffer 'bufferID',
    * ordered from the newest to the oldest inserted
    *
    * @param 'bufferID' the ID of the buffer from where to take the window
    * @param 'windowSize' the number of elements to return
    * @param 'buffer' the pointer variable in which to store the pointer to the returning window
    *
    * @return void
    */
    command void getData(uint8_t bufferID, uint16_t windowSize, uint16_t* buffer);
    
    /**
    * Puts a new element 'elem' into the buffer 'bufferID'
    *
    * @param 'bufferID' the ID of the buffer where to put the new element
    * @param 'elem' the new element to store in the given buffer
    *
    * @return  void
    */
    command void putElem(uint8_t bufferID, uint16_t elem);
    
    /**
    * Returns the number of element the buffer 'bufferID' is able to store
    *
    * @param 'bufferID' the ID of the buffer
    *
    * @return the number of element the given buffer is able to store
    */
    command uint16_t getBufferSize(uint8_t bufferID);

    /**
    * Returns the total number of buffers the pool is composed of
    *
    *
    * @return the total number of buffers the pool is composed of
    */
    command uint8_t getBufferPoolSize();

    /**
    * Returns a pointer to an array containing the last 'firstToNow - lastToNow' elements of the buffer 'bufferID',
    * ordered from the newest to the oldest inserted
    *
    * @param 'bufferID' the ID of the buffer from where to take the window
    * @param 'firstToNow' the position of the first element w.r.t. the current buffer index
    * @param 'lastToNow' the position of the last element w.r.t. the current buffer index
    * @param 'buffer' the pointer variable in which to store the pointer to the returning window
    *
    * @return void
    */
    command void getBufferedData(uint8_t bufferID, uint16_t firstToNow, uint16_t lastToNow, uint16_t* buffer);

    /**
    * Returns the full copy of the whole bufferPool
    *
    * @param 'buffer' the variable pointer where to store the pointer to the bufferPoll copy array
    *
    * @return void
    */
    command void getBufferPoolCopy(uint16_t* buffer);

    /**
    * Clear the buffer pool and its indexes.
    *
    *
    * @return void
    */
    command void clear();

    /**
    * This event is signaled when a new element 'elem' has been inserted into the buffer 'bufferID'
    *
    * @param 'bufferID' the ID of the buffer that has stored the new elem
    * @param 'elem' the new elem
    *
    * @return void
    */
    event void newElem(uint8_t bufferID, uint16_t elem);

}
