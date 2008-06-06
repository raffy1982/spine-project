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
 * Configuration component of the Sensor Board Manager. This component has been introduces to abstract
 * the access to the different sensors of the specific sensorboard and
 * to decouple the SPINE v1.2 core to the peripherals. 
 * This way, the only needed information to access a sensor, is to know its
 * code (as known by the Sensor Board Manager).
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */
#include "SensorsConstants.h"
configuration SensorBoardManagerC {
     provides interface SensorBoardManager;
     
     uses interface Sensor as SensorImpls[uint8_t sensorID];
}

implementation {
     components SensorBoardManagerP;

     components AccSensorC;
     components VoltageSensorC;
     components GyroSensorC;
     components InternalTemperatureSensorC;

     SensorBoardManager = SensorBoardManagerP;

     SensorBoardManagerP.SensorImpls = SensorImpls;
     SensorBoardManagerP.SensorImpls[ACC_SENSOR] -> AccSensorC;
     SensorBoardManagerP.SensorImpls[VOLTAGE_SENSOR] -> VoltageSensorC;
     SensorBoardManagerP.SensorImpls[GYRO_SENSOR] -> GyroSensorC;
     SensorBoardManagerP.SensorImpls[INTERNAL_TEMPERATURE_SENSOR] -> InternalTemperatureSensorC;

}
