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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.
*****************************************************************/

 /**
 *  Module component for the Setup-Sensor SPINE packet.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */ 
module SpineSetupSensorPktC {
  provides interface InPacket;
  provides interface SpineSetupSensorPkt;
}

implementation {
  spine_setup_sensor_t setup_sensor;

  command bool InPacket.parse(void* payload, uint8_t len) { 
    setup_sensor = *((spine_setup_sensor_t*)payload);
    return TRUE;
  }
    
  command uint8_t SpineSetupSensorPkt.getSensorCode() {
    return setup_sensor.sensCode;
  }
    
  command uint16_t SpineSetupSensorPkt.getTimeScale() {
    switch (setup_sensor.timeScale) {
      case 0 : return 0x0000; // NOW
      case 1 : return 0x0001; // 1ms
      case 2 : return 0x03E8; // 0x03E8 =  1000 (1000ms = 1sec)
      case 3 : return 0xEA60; // 0xEA60 = 60000 (1000ms x 60sec = 1min)
      default : return 0x0000;
     }
   }

   command uint16_t SpineSetupSensorPkt.getSamplingTime() {
     return setup_sensor.samplingTime;
   }
}
