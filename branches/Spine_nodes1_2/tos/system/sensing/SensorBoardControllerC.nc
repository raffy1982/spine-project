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
 * Configuration component of the Sensor Board Controller. This component has been introduces to abstract
 * the access to the different sensors of the specific sensorboard and
 * to decouple the SPINE v1.2 core to the peripherals. 
 * This way, the only needed information to access a sensor, is to know its
 * code (as known by the Sensor Board Controller).
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */

configuration SensorBoardControllerC {
     provides interface SensorBoardController;

     uses {
        interface Sensor as SensorImpls[uint8_t sensorID];
        interface Timer<TMilli> as SamplingTimers[uint8_t sensorCode];
     }
}

implementation {
     components SensorBoardControllerP, SensorsRegistryC, PacketManagerC;
     components BufferPoolP;

     components VoltageSensorC;
     components AccSensorC;
     components GyroSensorC;
     components InternalTemperatureSensorC;

     components new TimerMilliC() as VoltageSensorTimer;
     components new TimerMilliC() as AccSensorTimer;
     components new TimerMilliC() as GyroSensorTimer;
     components new TimerMilliC() as InternalTemperatureSensorTimer;

     SensorBoardController = SensorBoardControllerP;

     SensorBoardControllerP.SensorsRegistry -> SensorsRegistryC;
     SensorBoardControllerP.BufferPool -> BufferPoolP;
     SensorBoardControllerP.PacketManager -> PacketManagerC;

     SensorBoardControllerP.SensorImpls = SensorImpls;
     SensorBoardControllerP.SensorImpls[VOLTAGE_SENSOR] -> VoltageSensorC;
     SensorBoardControllerP.SensorImpls[ACC_SENSOR] -> AccSensorC;
     SensorBoardControllerP.SensorImpls[GYRO_SENSOR] -> GyroSensorC;
     SensorBoardControllerP.SensorImpls[INTERNAL_TEMPERATURE_SENSOR] -> InternalTemperatureSensorC;

     SensorBoardControllerP.SamplingTimers = SamplingTimers;
     SensorBoardControllerP.SamplingTimers[VOLTAGE_SENSOR] -> VoltageSensorTimer;
     SensorBoardControllerP.SamplingTimers[ACC_SENSOR] -> AccSensorTimer;
     SensorBoardControllerP.SamplingTimers[GYRO_SENSOR] -> GyroSensorTimer;
     SensorBoardControllerP.SamplingTimers[INTERNAL_TEMPERATURE_SENSOR] -> InternalTemperatureSensorTimer;

}
