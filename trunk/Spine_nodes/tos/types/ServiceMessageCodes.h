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
 * The present 'enum' contains the codes associated to the errors eventually thrown by a mote
 * implementing AMP (Activity Monitoring Features Selection Protocol)
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
 
#ifndef ERRORCODES_H
#define ERRORCODES_H

enum {
  // types
  MESSAGE_CODE = 0x1,

  WARNING_CODE = 0x2,
  
  ALERT_CODE = 0x3,

  ERROR_CODE = 0x4,

  FATAL_ERROR_CODE = 0x5,
  
  EVENT_CODE = 0x6,
  
  ACTIVATE_FEATURE_ERROR_CODE = 0x7,
  
  // error detail codes
  WINDOW_SIZE_TOO_BIG_CODE = 0x1,

  TEMPORARY_OUT_OF_RESOURCES = 0x2,
  
  SENSOR_UNKNOWN = 0x3,
  
  INVALID_AXIS_CODE = 0x4,
  
  FALL_DETECTED_CODE = 0x5
};

#endif

