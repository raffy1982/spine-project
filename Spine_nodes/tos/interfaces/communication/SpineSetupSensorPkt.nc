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
 *  Interface for the Setup-Sensor SPINE packet.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */ 
interface SpineSetupSensorPkt {

    /**
    * Returns the code of the sensor to setup
    *
    *
    * @return  the code of the sensor to setup
    */
    command uint8_t getSensorCode();

    /**
    * Returns the time scale by which multiply the sampling time.
    * Possible time scales are: millisec, sec, min. 
    * Moreover there is a special value: NOW (0) that is for immediate one-shot sensor sampling.
    *
    *
    * @return  the code of the sensor to setup
    */
    command uint16_t getTimeScale();

    /**
    * Returns the sampling time requested for the given sensor
    *
    *
    * @return  the sampling time requested for the given sensor
    */
    command uint16_t getSamplingTime();


}
