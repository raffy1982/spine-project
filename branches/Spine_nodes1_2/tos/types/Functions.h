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
 * The present 'enum' contains the codes associated to the features expected and supported by AMP
 * (Activity Monitoring Features Selection Protocol)  
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */

#ifndef FUNCTIONS_H
#define FUNCTIONS_H

enum FunctionCodes {

  FEATURE = 0x01,
  ALARM = 0x02,
  SIGNAL_PROCESSING = 0x03,
  ONE_SHOT = 0x04
};

enum FeatureCodes {

  ROW_DATA = 0x00,
  MAX = 0x01,
  MIN = 0x02,
  RANGE = 0x03,
  MEAN = 0x04,
  AMPLITUDE = 0x05,
  RMS = 0x06,
  ST_DEV = 0x07,
  TOTAL_ENERGY = 0x08

};

#endif

