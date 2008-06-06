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
 * This file regards SPINE Packets
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */

#ifndef SpinePackets_H
#define SpinePackets_H

enum PacketTypes {

  AM_SPINE = 0x99,

  SERVICE_ADVERTISEMENT = 0x2,
  DATA = 0x4,
  VOLTAGE_NOTIFY = 0x7,
  SERVICE_MESSAGE = 0x8,

  SERVICE_DISCOVERY = 0x1,
  FUNCTION_ENABLE_REQ = 0x3,
  FUNCTION_DISABLE_REQ = 0x5,
  VOLTAGE_REQ = 0x6
};

#endif


