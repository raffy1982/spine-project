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
 * Implementation of the Buffer Manager. This is an ad-hoc component, developed for the 
 * Feature Selection Agent application, that handles (initializes, allocates, fills and releases)
 * the 50 buffers needed by the appplication.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
 
 interface BuffersManager {
   
   /*
   * This command ask the given buffer to get its current window.
   *
   * @param ID : the ID of the buffer
   *
   * @return 'int16_t*' : the pointer to the window array
   */
   command int16_t* getWindow(uint8_t ID);
   
   /*
   * This command insert into the proper data structure the given element.
   * The data structure in which the element will be stored is determined by the sensor and axis code
   *
   * @param sensorCode : the sensor code
   * @param axisCode : the axis of the sensor
   * @param elem : the pointer to the element
   *
   * @return void
   */
   command void putElem(uint8_t sensorCode, uint8_t axisCode, void* elem);
   
   /*
   * This command allocates a buffer, if there any proper one is available, returns an error code otherwise.
   *
   * @param sensorCode : the sensor code
   * @param axisCode : the axis of the sensor
   * @param window : the pointer to the element
   * @param shift : the pointer to the element
   *
   * @return 'int8_t' : positive numbers indicate the linked buffer ID instead negative numbers are
   *                    code for well known errors
   *                    (such as "Requested window size Too Big" or "no more free buffers")
   */
   command int8_t bufferAllocReq(uint8_t sensorCode, uint8_t axisCode, uint16_t window,  uint16_t shift);
   
   /*
   * This command releases the buffer corresponding the given ID.
   *
   * @param id : the ID of the buffer to be released
   *
   * @return void
   */
   command void bufferRelease(uint8_t id);
   
   /*
   * This command returns the lenght of the window associated to the given buffer ID.
   *
   * @param id : the ID of the buffer
   *
   * @return 'uint16_t' : the window size
   */
   command uint16_t getWindowSize(uint8_t ID);
   
   /*
   * This command sets the number of Motes in the buffer.
   *
   * @param nM : the number of Motes in the network
   *
   * @return void
   */
   command void setNumMotes(uint8_t nM);

 }




