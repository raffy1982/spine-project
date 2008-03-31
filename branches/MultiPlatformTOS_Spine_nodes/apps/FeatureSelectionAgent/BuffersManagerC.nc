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

#include "FeatureSelectionAgent.h"
#include "SensorCodes.h"
#include "ServiceMessageCodes.h"

module BuffersManagerC {
     uses {
       interface Boot;
       interface Leds;

       interface GenericSharedCircularBuffer as BAccX01;
       interface GenericSharedCircularBuffer as BAccX02;
       interface GenericSharedCircularBuffer as BAccX03;
       interface GenericSharedCircularBuffer as BAccX04;
       interface GenericSharedCircularBuffer as BAccX05;
       interface GenericSharedCircularBuffer as BAccX06;
       interface GenericSharedCircularBuffer as BAccX07;
       interface GenericSharedCircularBuffer as BAccX08;
       interface GenericSharedCircularBuffer as BAccX09;
       interface GenericSharedCircularBuffer as BAccX10;

       interface GenericSharedCircularBuffer as BAccY01;
       interface GenericSharedCircularBuffer as BAccY02;
       interface GenericSharedCircularBuffer as BAccY03;
       interface GenericSharedCircularBuffer as BAccY04;
       interface GenericSharedCircularBuffer as BAccY05;
       interface GenericSharedCircularBuffer as BAccY06;
       interface GenericSharedCircularBuffer as BAccY07;
       interface GenericSharedCircularBuffer as BAccY08;
       interface GenericSharedCircularBuffer as BAccY09;
       interface GenericSharedCircularBuffer as BAccY10;

       interface GenericSharedCircularBuffer as BAccZ01;
       interface GenericSharedCircularBuffer as BAccZ02;
       interface GenericSharedCircularBuffer as BAccZ03;
       interface GenericSharedCircularBuffer as BAccZ04;
       interface GenericSharedCircularBuffer as BAccZ05;
       interface GenericSharedCircularBuffer as BAccZ06;
       interface GenericSharedCircularBuffer as BAccZ07;
       interface GenericSharedCircularBuffer as BAccZ08;
       interface GenericSharedCircularBuffer as BAccZ09;
       interface GenericSharedCircularBuffer as BAccZ10;

       interface GenericSharedCircularBuffer as BGyroX01;
       interface GenericSharedCircularBuffer as BGyroX02;
       interface GenericSharedCircularBuffer as BGyroX03;
       interface GenericSharedCircularBuffer as BGyroX04;
       interface GenericSharedCircularBuffer as BGyroX05;
       interface GenericSharedCircularBuffer as BGyroX06;
       interface GenericSharedCircularBuffer as BGyroX07;
       interface GenericSharedCircularBuffer as BGyroX08;
       interface GenericSharedCircularBuffer as BGyroX09;
       interface GenericSharedCircularBuffer as BGyroX10;

       interface GenericSharedCircularBuffer as BGyroY01;
       interface GenericSharedCircularBuffer as BGyroY02;
       interface GenericSharedCircularBuffer as BGyroY03;
       interface GenericSharedCircularBuffer as BGyroY04;
       interface GenericSharedCircularBuffer as BGyroY05;
       interface GenericSharedCircularBuffer as BGyroY06;
       interface GenericSharedCircularBuffer as BGyroY07;
       interface GenericSharedCircularBuffer as BGyroY08;
       interface GenericSharedCircularBuffer as BGyroY09;
       interface GenericSharedCircularBuffer as BGyroY10;
     }

     provides interface BuffersManager;
}

implementation {

     bool busyAccX[MAX_FEATURES_PER_AXIS];  // buffers IDs:  1 to 10
     bool busyAccY[MAX_FEATURES_PER_AXIS];  // buffers IDs: 11 to 20
     bool busyAccZ[MAX_FEATURES_PER_AXIS];  // buffers IDs: 21 to 30
     bool busyGyroX[MAX_FEATURES_PER_AXIS]; // buffers IDs: 31 to 40
     bool busyGyroY[MAX_FEATURES_PER_AXIS]; // buffers IDs: 41 to 50
     
     int8_t accX[BUF_SIZE];
     int8_t accY[BUF_SIZE];
     int8_t accZ[BUF_SIZE];
     int8_t gyroX[BUF_SIZE];
     int8_t gyroY[BUF_SIZE];

     int16_t window4Return[BUF_SIZE/2];
     
     uint16_t i_currAccX = 0;
     uint16_t* i_currAccXPunt = &i_currAccX;

     uint16_t i_currAccY = 0;
     uint16_t* i_currAccYPunt = &i_currAccY;

     uint16_t i_currAccZ = 0;
     uint16_t* i_currAccZPunt = &i_currAccZ;

     uint16_t i_currGyroX = 0;
     uint16_t* i_currGyroXPunt = &i_currGyroX;

     uint16_t i_currGyroY = 0;
     uint16_t* i_currGyroYPunt = &i_currGyroY;

     uint8_t i;
     
     /*
      * This event is thrown when the mote is booted. 
      * It initializes the internal data structures.
      *
      * @return void
      */
     event void Boot.booted() {
         call BAccX01.init(accX, i_currAccXPunt, BUF_SIZE/2, 0, 0);
         call BAccY01.init(accY, i_currAccYPunt, BUF_SIZE/2, 0, 0);
         call BAccZ01.init(accZ, i_currAccZPunt, BUF_SIZE/2, 0, 0);
         call BGyroX01.init(gyroX, i_currGyroXPunt, BUF_SIZE/2, 0, 0);
         call BGyroY01.init(gyroY, i_currGyroYPunt, BUF_SIZE/2, 0, 0);
     }

     /*
      * This command ask the given buffer to get its current window.
      *
      * @param ID : the ID of the buffer
      *
      * @return 'int16_t*' : the pointer to the window array
      */
     command int16_t* BuffersManager.getWindow(uint8_t ID) {
        switch(ID) {
           case 0: {
              call BAccX01.getWindow(window4Return);
              break;
           }
           case 1: {
              call BAccX02.getWindow(window4Return);
              break;
           }
           case 2: {
              call BAccX03.getWindow(window4Return);
              break;
           }
           case 3: {
              call BAccX04.getWindow(window4Return);
              break;
           }
           case 4: {
              call BAccX05.getWindow(window4Return);
              break;
           }
           case 5: {
              call BAccX06.getWindow(window4Return);
              break;
           }
           case 6: {
              call BAccX07.getWindow(window4Return);
              break;
           }
           case 7: {
              call BAccX08.getWindow(window4Return);
              break;
           }
           case 8: {
              call BAccX09.getWindow(window4Return);
              break;
           }
           case 9: {
              call BAccX10.getWindow(window4Return);
              break;
           }  // follows accY
           case 10: {
              call BAccY01.getWindow(window4Return);
              break;
           }
           case 11: {
              call BAccY02.getWindow(window4Return);
              break;
           }
           case 12: {
              call BAccY03.getWindow(window4Return);
              break;
           }
           case 13: {
              call BAccY04.getWindow(window4Return);
              break;
           }
           case 14: {
              call BAccY05.getWindow(window4Return);
              break;
           }
           case 15: {
              call BAccY06.getWindow(window4Return);
              break;
           }
           case 16: {
              call BAccY07.getWindow(window4Return);
              break;
           }
           case 17: {
              call BAccY08.getWindow(window4Return);
              break;
           }
           case 18: {
              call BAccY09.getWindow(window4Return);
              break;
           }
           case 19: {
              call BAccY10.getWindow(window4Return);
              break;
           } // follows accZ
           case 20: {
              call BAccZ01.getWindow(window4Return);
              break;
           }
           case 21: {
              call BAccZ02.getWindow(window4Return);
              break;
           }
           case 22: {
              call BAccZ03.getWindow(window4Return);
              break;
           }
           case 23: {
              call BAccZ04.getWindow(window4Return);
              break;
           }
           case 24: {
              call BAccZ05.getWindow(window4Return);
              break;
           }
           case 25: {
              call BAccZ06.getWindow(window4Return);
              break;
           }
           case 26: {
              call BAccZ07.getWindow(window4Return);
              break;
           }
           case 27: {
              call BAccZ08.getWindow(window4Return);
              break;
           }
           case 28: {
              call BAccZ09.getWindow(window4Return);
              break;
           }
           case 29: {
              call BAccZ10.getWindow(window4Return);
              break;
           } // follows gyroX
           case 30: {
              call BGyroX01.getWindow(window4Return);
              break;
           }
           case 31: {
              call BGyroX02.getWindow(window4Return);
              break;
           }
           case 32: {
              call BGyroX03.getWindow(window4Return);
              break;
           }
           case 33: {
              call BGyroX04.getWindow(window4Return);
              break;
           }
           case 34: {
              call BGyroX05.getWindow(window4Return);
              break;
           }
           case 35: {
              call BGyroX06.getWindow(window4Return);
              break;
           }
           case 36: {
              call BGyroX07.getWindow(window4Return);
              break;
           }
           case 37: {
              call BGyroX08.getWindow(window4Return);
              break;
           }
           case 38: {
              call BGyroX09.getWindow(window4Return);
              break;
           }
           case 39: {
              call BGyroX10.getWindow(window4Return);
              break;
           } // follows gyroY
           case 40: {
              call BGyroY01.getWindow(window4Return);
              break;
           }
           case 41: {
              call BGyroY02.getWindow(window4Return);
              break;
           }
           case 42: {
              call BGyroY03.getWindow(window4Return);
              break;
           }
           case 43: {
              call BGyroY04.getWindow(window4Return);
              break;
           }
           case 44: {
              call BGyroY05.getWindow(window4Return);
              break;
           }
           case 45: {
              call BGyroY06.getWindow(window4Return);
              break;
           }
           case 46: {
              call BGyroY07.getWindow(window4Return);
              break;
           }
           case 47: {
              call BGyroY08.getWindow(window4Return);
              break;
           }
           case 48: {
              call BGyroY09.getWindow(window4Return);
              break;
           }
           case 49: {
              call BGyroY10.getWindow(window4Return);
              break;
           }
        } // end switch
        
        return window4Return;
     }

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
     command void BuffersManager.putElem(uint8_t sensorCode, uint8_t axisCode, void* elem) {
         switch (sensorCode) {
            case ACCELEROMETER_CODE: {
              switch (axisCode) {
                  case AXIS_X: {
                      call BAccX01.putElem(elem);
                      break;
                  }//case AXIS_X
                  case AXIS_Y: {
                      call BAccY01.putElem(elem);
                      break;
                  }//case AXIS_Y
                  case AXIS_Z: {
                      call BAccZ01.putElem(elem);
                      break;
                  }//case AXIS_Z
                  default: break;
              } // end switch Axis Code
              break;
            }
            case GYROSCOPE_CODE: {
                switch (axisCode) {
                  case AXIS_X: {
                      call BGyroX01.putElem(elem);
                      break;
                  }//case AXIS_X
                  case AXIS_Y: {
                      call BGyroY01.putElem(elem);
                      break;
                  }//case AXIS_Y
                  default: break;
                } // end switch Axis Code
                break;
            }
            default: break;
        }  // end switch Sensor Code
     }

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
     command int8_t BuffersManager.bufferAllocReq(uint8_t sensorCode, uint8_t axisCode, uint16_t window,  uint16_t shift) {

        if (window > BUF_SIZE/2)
           return -WINDOW_SIZE_TOO_BIG_CODE;
        switch (sensorCode) {
            case ACCELEROMETER_CODE: {
              switch (axisCode) {
                  case AXIS_X: {
                      for (i=0; i<MAX_FEATURES_PER_AXIS; i++) {
                         if (!busyAccX[i]) {
                            busyAccX[i] = TRUE;
                            break;
                         }
                      }
                      if (i == MAX_FEATURES_PER_AXIS)
                          return -TEMPORARY_OUT_OF_RESOURCES;
                      else {
                         // we are going to allocate the resources
                         switch (i) {
                            case 0: {
                                call BAccX01.init(accX, i_currAccXPunt, BUF_SIZE/2, window, shift);
                                return i; // returns buffer ID
                            }
                            case 1: {
                                call BAccX02.init(accX, i_currAccXPunt, BUF_SIZE/2, window, shift);
                                return i; // returns buffer ID
                            }
                            case 2: {
                                call BAccX03.init(accX, i_currAccXPunt, BUF_SIZE/2, window, shift);
                                return i; // returns buffer ID
                            }
                            case 3: {
                                call BAccX04.init(accX, i_currAccXPunt, BUF_SIZE/2, window, shift);
                                return i; // returns buffer ID
                            }
                            case 4: {
                                call BAccX05.init(accX, i_currAccXPunt, BUF_SIZE/2, window, shift);
                                return i; // returns buffer ID
                            }
                            case 5: {
                                call BAccX06.init(accX, i_currAccXPunt, BUF_SIZE/2, window, shift);
                                return i; // returns buffer ID
                            }
                            case 6: {
                                call BAccX07.init(accX, i_currAccXPunt, BUF_SIZE/2, window, shift);
                                return i; // returns buffer ID
                            }
                            case 7: {
                                call BAccX08.init(accX, i_currAccXPunt, BUF_SIZE/2, window, shift);
                                return i; // returns buffer ID
                            }
                            case 8: {
                                call BAccX09.init(accX, i_currAccXPunt, BUF_SIZE/2, window, shift);
                                return i; // returns buffer ID
                            }
                            case 9: {
                                call BAccX10.init(accX, i_currAccXPunt, BUF_SIZE/2, window, shift);
                                return i; // returns buffer ID
                            }
                            default: break;
                         } // end switch Axis Code
                      } //else
                      break;
                  }//case AXIS_X
                  case AXIS_Y: {
                    for (i=0; i<MAX_FEATURES_PER_AXIS; i++) {
                         if (!busyAccY[i]) {
                            busyAccY[i] = TRUE;
                            break;
                         }
                      }
                      if (i == MAX_FEATURES_PER_AXIS)
                          return -TEMPORARY_OUT_OF_RESOURCES;
                      else {
                         // we are going to allocate the resources
                         switch (i) {
                            case 0: {
                                call BAccY01.init(accY, i_currAccYPunt, BUF_SIZE/2, window, shift);
                                return i+10; // returns buffer ID
                            }
                            case 1: {
                                call BAccY02.init(accY, i_currAccYPunt, BUF_SIZE/2, window, shift);
                                return i+10; // returns buffer ID
                            }
                            case 2: {
                                call BAccY03.init(accY, i_currAccYPunt, BUF_SIZE/2, window, shift);
                                return i+10; // returns buffer ID
                            }
                            case 3: {
                                call BAccY04.init(accY, i_currAccYPunt, BUF_SIZE/2, window, shift);
                                return i+10; // returns buffer ID
                            }
                            case 4: {
                                call BAccY05.init(accY, i_currAccYPunt, BUF_SIZE/2, window, shift);
                                return i+10; // returns buffer ID
                            }
                            case 5: {
                                call BAccY06.init(accY, i_currAccYPunt, BUF_SIZE/2, window, shift);
                                return i+10; // returns buffer ID
                            }
                            case 6: {
                                call BAccY07.init(accY, i_currAccYPunt, BUF_SIZE/2, window, shift);
                                return i+10; // returns buffer ID
                            }
                            case 7: {
                                call BAccY08.init(accY, i_currAccYPunt, BUF_SIZE/2, window, shift);
                                return i+10; // returns buffer ID
                            }
                            case 8: {
                                call BAccY09.init(accY, i_currAccYPunt, BUF_SIZE/2, window, shift);
                                return i+10; // returns buffer ID
                            }
                            case 9: {
                                call BAccY10.init(accY, i_currAccYPunt, BUF_SIZE/2, window, shift);
                                return i+10; // returns buffer ID
                            }
                            default: break;
                         } // end switch Axis Code
                      }//else
                      break;
                  }//case AXIS_Y
                  case AXIS_Z: {
                      for (i=0; i<MAX_FEATURES_PER_AXIS; i++) {
                         if (!busyAccZ[i]) {
                            busyAccZ[i] = TRUE;
                            break;
                         }
                      }
                      if (i == MAX_FEATURES_PER_AXIS)
                          return -TEMPORARY_OUT_OF_RESOURCES;
                      else {
                         // we are going to allocate the resources
                         switch (i) {
                            case 0: {
                                call BAccZ01.init(accZ, i_currAccZPunt, BUF_SIZE/2, window, shift);
                                return i+20; // returns buffer ID
                            }
                            case 1: {
                                call BAccZ02.init(accZ, i_currAccZPunt, BUF_SIZE/2, window, shift);
                                return i+20; // returns buffer ID
                            }
                            case 2: {
                                call BAccZ03.init(accZ, i_currAccZPunt, BUF_SIZE/2, window, shift);
                                return i+20; // returns buffer ID
                            }
                            case 3: {
                                call BAccZ04.init(accZ, i_currAccZPunt, BUF_SIZE/2, window, shift);
                                return i+20; // returns buffer ID
                            }
                            case 4: {
                                call BAccZ05.init(accZ, i_currAccZPunt, BUF_SIZE/2, window, shift);
                                return i+20; // returns buffer ID
                            }
                            case 5: {
                                call BAccZ06.init(accZ, i_currAccZPunt, BUF_SIZE/2, window, shift);
                                return i+20; // returns buffer ID
                            }
                            case 6: {
                                call BAccZ07.init(accZ, i_currAccZPunt, BUF_SIZE/2, window, shift);
                                return i+20; // returns buffer ID
                            }
                            case 7: {
                                call BAccZ08.init(accZ, i_currAccZPunt, BUF_SIZE/2, window, shift);
                                return i+20; // returns buffer ID
                            }
                            case 8: {
                                call BAccZ09.init(accZ, i_currAccZPunt, BUF_SIZE/2, window, shift);
                                return i+20; // returns buffer ID
                            }
                            case 9: {
                                call BAccZ10.init(accZ, i_currAccZPunt, BUF_SIZE/2, window, shift);
                                return i+20; // returns buffer ID
                            }
                            default: break;
                         } // end switch Axis Code
                      } //else
                      break;
                  }//case AXIS_Z
                  default: return -INVALID_AXIS_CODE;
              } // end switch Axis Code
              break;
            }
            case GYROSCOPE_CODE: {
                switch (axisCode) {
                  case AXIS_X: {
                      for (i=0; i<MAX_FEATURES_PER_AXIS; i++) {
                         if (!busyGyroX[i]) {
                            busyGyroX[i] = TRUE;
                            break;
                         }
                      }
                      if (i == MAX_FEATURES_PER_AXIS)
                          return -TEMPORARY_OUT_OF_RESOURCES;
                      else {
                         // we are going to allocate the resources
                         switch (i) {
                            case 0: {
                                call BGyroX01.init(gyroX, i_currGyroXPunt, BUF_SIZE/2, window, shift);
                                return i+30; // returns buffer ID
                            }
                            case 1: {
                                call BGyroX02.init(gyroX, i_currGyroXPunt, BUF_SIZE/2, window, shift);
                                return i+30; // returns buffer ID
                            }
                            case 2: {
                                call BGyroX03.init(gyroX, i_currGyroXPunt, BUF_SIZE/2, window, shift);
                                return i+30; // returns buffer ID
                            }
                            case 3: {
                                call BGyroX04.init(gyroX, i_currGyroXPunt, BUF_SIZE/2, window, shift);
                                return i+30; // returns buffer ID
                            }
                            case 4: {
                                call BGyroX05.init(gyroX, i_currGyroXPunt, BUF_SIZE/2, window, shift);
                                return i+30; // returns buffer ID
                            }
                            case 5: {
                                call BGyroX06.init(gyroX, i_currGyroXPunt, BUF_SIZE/2, window, shift);
                                return i+30; // returns buffer ID
                            }
                            case 6: {
                                call BGyroX07.init(gyroX, i_currGyroXPunt, BUF_SIZE/2, window, shift);
                                return i+30; // returns buffer ID
                            }
                            case 7: {
                                call BGyroX08.init(gyroX, i_currGyroXPunt, BUF_SIZE/2, window, shift);
                                return i+30; // returns buffer ID
                            }
                            case 8: {
                                call BGyroX09.init(gyroX, i_currGyroXPunt, BUF_SIZE/2, window, shift);
                                return i+30; // returns buffer ID
                            }
                            case 9: {
                                call BGyroX10.init(gyroX, i_currGyroXPunt, BUF_SIZE/2, window, shift);
                                return i+30; // returns buffer ID
                            }
                            default: break;
                         } // end switch Axis Code
                      } //else
                      break;
                  }//case AXIS_X
                  case AXIS_Y: {
                    for (i=0; i<MAX_FEATURES_PER_AXIS; i++) {
                         if (!busyGyroY[i]) {
                            busyGyroY[i] = TRUE;
                            break;
                         }
                      }
                      if (i == MAX_FEATURES_PER_AXIS)
                          return -TEMPORARY_OUT_OF_RESOURCES;
                      else {
                         // we are going to allocate the resources
                         switch (i) {
                            case 0: {
                                call BGyroY01.init(gyroY, i_currGyroYPunt, BUF_SIZE/2, window, shift);
                                return i+40; // returns buffer ID
                            }
                            case 1: {
                                call BGyroY02.init(gyroY, i_currGyroYPunt, BUF_SIZE/2, window, shift);
                                return i+40; // returns buffer ID
                            }
                            case 2: {
                                call BGyroY03.init(gyroY, i_currGyroYPunt, BUF_SIZE/2, window, shift);
                                return i+40; // returns buffer ID
                            }
                            case 3: {
                                call BGyroY04.init(gyroY, i_currGyroYPunt, BUF_SIZE/2, window, shift);
                                return i+40; // returns buffer ID
                            }
                            case 4: {
                                call BGyroY05.init(gyroY, i_currGyroYPunt, BUF_SIZE/2, window, shift);
                                return i+40; // returns buffer ID
                            }
                            case 5: {
                                call BGyroY06.init(gyroY, i_currGyroYPunt, BUF_SIZE/2, window, shift);
                                return i+40; // returns buffer ID
                            }
                            case 6: {
                                call BGyroY07.init(gyroY, i_currGyroYPunt, BUF_SIZE/2, window, shift);
                                return i+40; // returns buffer ID
                            }
                            case 7: {
                                call BGyroY08.init(gyroY, i_currGyroYPunt, BUF_SIZE/2, window, shift);
                                return i+40; // returns buffer ID
                            }
                            case 8: {
                                call BGyroY09.init(gyroY, i_currGyroYPunt, BUF_SIZE/2, window, shift);
                                return i+40; // returns buffer ID
                            }
                            case 9: {
                                call BGyroY10.init(gyroY, i_currGyroYPunt, BUF_SIZE/2, window, shift);
                                return i+40; // returns buffer ID
                            }
                            default: break;
                         } // end switch Axis Code
                      } //else
                      break;
                  }//case AXIS_Y
                  default: return -INVALID_AXIS_CODE;
              } // end switch Axis Code
                break;
            }
            default: return -SENSOR_UNKNOWN;
        }  // end switch Sensor Code
        return 0;
     } // end bufferAllocReq

     /*
     * This command releases the buffer corresponding the given ID.
     *
     * @param id : the ID of the buffer to be released
     *
     * @return void
     */
     command void BuffersManager.bufferRelease(uint8_t id) {
        if (id<10)
          busyAccX[id] = FALSE;
        else if (id>=10 && id<20)
          busyAccY[id-10] = FALSE;
        else if (id>=20 && id<30)
          busyAccZ[id-20] = FALSE;
        else if (id>=30 && id<40)
          busyGyroX[id-30] = FALSE;
        else if (id>=40 && id<50)
          busyGyroY[id-40] = FALSE;
     }
     
     /*
     * This command returns the lenght of the window associated to the given buffer ID.
     *
     * @param id : the ID of the buffer
     *
     * @return 'uint16_t' : the window size
     */
     command uint16_t BuffersManager.getWindowSize(uint8_t ID) {
        switch(ID) {
           case 0: return call BAccX01.getWindowSize();
           case 1: return call BAccX02.getWindowSize();
           case 2: return call BAccX03.getWindowSize();
           case 3: return call BAccX04.getWindowSize();
           case 4: return call BAccX05.getWindowSize();
           case 5: return call BAccX06.getWindowSize();
           case 6: return call BAccX07.getWindowSize();
           case 7: return call BAccX08.getWindowSize();
           case 8: return call BAccX09.getWindowSize();
           case 9: return call BAccX10.getWindowSize();
           case 10: return call BAccY01.getWindowSize();
           case 11: return call BAccY02.getWindowSize();
           case 12: return call BAccY03.getWindowSize();
           case 13: return call BAccY04.getWindowSize();
           case 14: return call BAccY05.getWindowSize();
           case 15: return call BAccY06.getWindowSize();
           case 16: return call BAccY07.getWindowSize();
           case 17: return call BAccY08.getWindowSize();
           case 18: return call BAccY09.getWindowSize();
           case 19: return call BAccY10.getWindowSize();
           case 20: return call BAccZ01.getWindowSize();
           case 21: return call BAccZ02.getWindowSize();
           case 22: return call BAccZ03.getWindowSize();
           case 23: return call BAccZ04.getWindowSize();
           case 24: return call BAccZ05.getWindowSize();
           case 25: return call BAccZ06.getWindowSize();
           case 26: return call BAccZ07.getWindowSize();
           case 27: return call BAccZ08.getWindowSize();
           case 28: return call BAccZ09.getWindowSize();
           case 29: return call BAccZ10.getWindowSize();
           case 30: return call BGyroX01.getWindowSize();
           case 31: return call BGyroX02.getWindowSize();
           case 32: return call BGyroX03.getWindowSize();
           case 33: return call BGyroX04.getWindowSize();
           case 34: return call BGyroX05.getWindowSize();
           case 35: return call BGyroX06.getWindowSize();
           case 36: return call BGyroX07.getWindowSize();
           case 37: return call BGyroX08.getWindowSize();
           case 38: return call BGyroX09.getWindowSize();
           case 39: return call BGyroX10.getWindowSize();
           case 40: return call BGyroY01.getWindowSize();
           case 41: return call BGyroY02.getWindowSize();
           case 42: return call BGyroY03.getWindowSize();
           case 43: return call BGyroY04.getWindowSize();
           case 44: return call BGyroY05.getWindowSize();
           case 45: return call BGyroY06.getWindowSize();
           case 46: return call BGyroY07.getWindowSize();
           case 47: return call BGyroY08.getWindowSize();
           case 48: return call BGyroY09.getWindowSize();
           case 49: return call BGyroY10.getWindowSize();
        } // end switch

        return 0;
     }
     
     /*
     * This command sets the number of Motes in the buffer.
     *
     * @param nM : the number of Motes in the network
     *
     * @return void
     */
     command void BuffersManager.setNumMotes(uint8_t nM){
           call BAccX01.setNodeNumber(nM);
           call BAccX02.setNodeNumber(nM);
           call BAccX03.setNodeNumber(nM);
           call BAccX04.setNodeNumber(nM);
           call BAccX05.setNodeNumber(nM);
           call BAccX06.setNodeNumber(nM);
           call BAccX07.setNodeNumber(nM);
           call BAccX08.setNodeNumber(nM);
           call BAccX09.setNodeNumber(nM);
           call BAccX10.setNodeNumber(nM);
           call BAccY01.setNodeNumber(nM);
           call BAccY02.setNodeNumber(nM);
           call BAccY03.setNodeNumber(nM);
           call BAccY04.setNodeNumber(nM);
           call BAccY05.setNodeNumber(nM);
           call BAccY06.setNodeNumber(nM);
           call BAccY07.setNodeNumber(nM);
           call BAccY08.setNodeNumber(nM);
           call BAccY09.setNodeNumber(nM);
           call BAccY10.setNodeNumber(nM);
           call BAccZ01.setNodeNumber(nM);
           call BAccZ02.setNodeNumber(nM);
           call BAccZ03.setNodeNumber(nM);
           call BAccZ04.setNodeNumber(nM);
           call BAccZ05.setNodeNumber(nM);
           call BAccZ06.setNodeNumber(nM);
           call BAccZ07.setNodeNumber(nM);
           call BAccZ08.setNodeNumber(nM);
           call BAccZ09.setNodeNumber(nM);
           call BAccZ10.setNodeNumber(nM);
           call BGyroX01.setNodeNumber(nM);
           call BGyroX02.setNodeNumber(nM);
           call BGyroX03.setNodeNumber(nM);
           call BGyroX04.setNodeNumber(nM);
           call BGyroX05.setNodeNumber(nM);
           call BGyroX06.setNodeNumber(nM);
           call BGyroX07.setNodeNumber(nM);
           call BGyroX08.setNodeNumber(nM);
           call BGyroX09.setNodeNumber(nM);
           call BGyroX10.setNodeNumber(nM);
           call BGyroY01.setNodeNumber(nM);
           call BGyroY02.setNodeNumber(nM);
           call BGyroY03.setNodeNumber(nM);
           call BGyroY04.setNodeNumber(nM);
           call BGyroY05.setNodeNumber(nM);
           call BGyroY06.setNodeNumber(nM);
           call BGyroY07.setNodeNumber(nM);
           call BGyroY08.setNodeNumber(nM);
           call BGyroY09.setNodeNumber(nM);
           call BGyroY10.setNodeNumber(nM);
     }


}

