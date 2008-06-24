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
 * This file contains all constants needed by the Sensor Board Manager
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */

#ifndef SensorsConstants_H
#define SensorsConstants_H

enum SensorCode {

  ACC_SENSOR = 0x01,
  VOLTAGE_SENSOR = 0x02,
  GYRO_SENSOR = 0x03,
  INTERNAL_TEMPERATURE_SENSOR = 0x04

};

enum ValueTypes {
   CH_1 = 0x00,
   CH_2 = 0x01,
   CH_3 = 0x02,
   CH_4 = 0x03
};

enum AcquireTypes {
   CH_1_ONLY = 0x01,
   CH_2_ONLY = 0x02,
   CH_3_ONLY = 0x03,

   ALL = 0x0A
};

enum {
   MAX_VALUE_TYPES = 0x04
};

#endif


