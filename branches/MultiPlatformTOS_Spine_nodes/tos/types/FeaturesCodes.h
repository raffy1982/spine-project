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

#ifndef FEATURESSCODES_H
#define FEATURESSCODES_H

enum {
  ROW_DATA_CODE = 0x0,

  MEAN_CODE = 0x1,

  MEDIAN_CODE = 0x2,

  CENTRAL_VALUE_CODE = 0x3,

  AMPLITUDE_CODE = 0x4,

  RANGE_CODE = 0x5,

  MIN_CODE = 0x6,

  MAX_CODE = 0x7,
  
  RMS_CODE = 0x8,

  VAR_CODE = 0x9,

  ST_DEV_CODE = 0xa,
  
  TOTAL_ENERGY_CODE = 0xb
};

#endif

