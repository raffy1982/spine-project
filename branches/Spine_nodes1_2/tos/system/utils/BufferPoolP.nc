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
 * Module component for the Buffer Pool.
 *
 * @author Raffaele Gravina
 * @author Roberta Giannantonio
 *
 * @version 1.0
 */

#ifndef BUFFER_POOL_SIZE
#define BUFFER_POOL_SIZE 6
#endif

#ifndef BUFFER_LENGTH
#define BUFFER_LENGTH 80
#endif

module BufferPoolP {
       
       provides interface BufferPool;
}

implementation {

       uint16_t bufferPool[BUFFER_POOL_SIZE * BUFFER_LENGTH];
       uint8_t buffersIndexes[BUFFER_POOL_SIZE];

       uint16_t tmpBuffer[BUFFER_LENGTH];
       
       uint8_t nextAvailableBufferID = 0;


       command uint8_t BufferPool.getAvailableBuffer() {
          buffersIndexes[nextAvailableBufferID] = 0;

          if (nextAvailableBufferID == (BUFFER_POOL_SIZE-1))
             return nextAvailableBufferID;
          return nextAvailableBufferID++;
       }
       
       command void BufferPool.releaseBuffer(uint8_t bufferID) {
           // TODO
       }

       command void BufferPool.getData(uint8_t bufferID, uint8_t windowSize, uint16_t* buffer) {
          call BufferPool.getBufferedData(bufferID, windowSize, 0, buffer);
       }

       command void BufferPool.putElem(uint8_t bufferID, uint16_t elem) {
          bufferPool[ (BUFFER_LENGTH * bufferID) + buffersIndexes[bufferID] ] = elem;

          buffersIndexes[bufferID] = (buffersIndexes[bufferID]+1)%BUFFER_LENGTH;
          
          signal BufferPool.newElem(bufferID, elem);
       }

       command uint16_t BufferPool.getBufferSize(uint8_t bufferID) {
          return BUFFER_LENGTH;
       }

       command uint8_t BufferPool.getBufferPoolSize() {
          return BUFFER_POOL_SIZE;
       }

       command void BufferPool.getBufferedData(uint8_t bufferID, uint8_t firstToNow, uint8_t lastToNow, uint16_t* buffer) {
          uint8_t i=0;
          uint8_t j=0;
          uint8_t k;
          uint8_t windowSize = firstToNow - lastToNow;

          if (windowSize > BUFFER_LENGTH)
            windowSize = BUFFER_LENGTH;

          if (windowSize <= buffersIndexes[bufferID])
             for (i = 0; i<windowSize; i++)
                tmpBuffer[i] = bufferPool[(BUFFER_LENGTH * bufferID) + (buffersIndexes[bufferID]-1 - i)];
          else {
             for (i = 0; i<(buffersIndexes[bufferID]); i++)
                tmpBuffer[i] = bufferPool[(BUFFER_LENGTH * bufferID) + (buffersIndexes[bufferID]-1 - i)];

             k = (BUFFER_LENGTH-1);
             for (j = (BUFFER_LENGTH-(windowSize-buffersIndexes[bufferID])); j<BUFFER_LENGTH; j++)
                tmpBuffer[i++] = bufferPool[(BUFFER_LENGTH * bufferID) + k--];
          }

          memcpy(buffer, tmpBuffer, windowSize*2);
       }
       
       command void BufferPool.getBufferPoolCopy(uint16_t* buffer) {
          atomic {
            uint8_t currBufID;
            for (currBufID = 0; currBufID<BUFFER_POOL_SIZE; currBufID++)
               call BufferPool.getData(currBufID, BUFFER_LENGTH, buffer+(currBufID * BUFFER_LENGTH));
          }     
       }
}




