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
 *  Every actual incoming SPINE packet should provide this interface in order to be SPINE v1.2 compliant
 * 
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */ 
interface BufferPool {

    command uint8_t getAvailableBuffer();
    
    command void releaseBuffer(uint8_t bufferID);

    command void getData(uint8_t bufferID, uint16_t windowSize, uint16_t* buffer);
    
    command void putElem(uint8_t bufferID, uint16_t elem);
    
    command uint16_t getBufferSize(uint8_t bufferID);

    command uint8_t getBufferPoolSize();

    command void getBufferedData(uint8_t bufferID, uint16_t firstToNow, uint16_t lastToNow, uint16_t* buffer);
    
    command void getBufferPoolCopy(uint16_t* buffer);

    event void newElem(uint8_t bufferID, uint16_t elem);

}
