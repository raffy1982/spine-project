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
 * The present 'enum' contains the sensor and the axis code standardized by AMP
 * (Activity Monitoring Features Selection Protocol)
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
 
#ifndef SENSORCODES_H
#define SENSORCODES_H

enum {

  GENERAL_PURPOSE_CODE = 0x0,

  ACCELEROMETER_CODE = 0x1,

  GYROSCOPE_CODE = 0x2,

  TEMPERATURE_CODE = 0x3,
  
  
  AXIS_X = 0,
  
  AXIS_Y = 1,

  AXIS_Z = 2

};

#endif

