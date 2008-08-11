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

     // if new sensors are added, declare their PIL driver components down here


     // if new sensors are added, also declare a new timer (each timer will be reserved for sampling one sensor)
     // for each new sensor down here


     SensorBoardController = SensorBoardControllerP;

     SensorBoardControllerP.SensorsRegistry -> SensorsRegistryC;
     SensorBoardControllerP.BufferPool -> BufferPoolP;
     SensorBoardControllerP.PacketManager -> PacketManagerC;

     SensorBoardControllerP.SensorImpls = SensorImpls;
     SensorBoardControllerP.SamplingTimers = SamplingTimers; 
     
     #ifdef SPINE_SENSOR_BOARD
       /* For the ACC Sensor */
     components AccSensorC;
     components new TimerMilliC() as AccSensorTimer;
     SensorBoardControllerP.SensorImpls[ACC_SENSOR] -> AccSensorC;
     SensorBoardControllerP.SamplingTimers[ACC_SENSOR] -> AccSensorTimer;
     
       /* For the VOLTAGE Sensor */
     components VoltageSensorC;
     components new TimerMilliC() as VoltageSensorTimer;
     SensorBoardControllerP.SensorImpls[VOLTAGE_SENSOR] -> VoltageSensorC;   
     SensorBoardControllerP.SamplingTimers[VOLTAGE_SENSOR] -> VoltageSensorTimer;
     
        /* For the GYRO Sensor */
     components GyroSensorC;
     components new TimerMilliC() as GyroSensorTimer;
     SensorBoardControllerP.SensorImpls[GYRO_SENSOR] -> GyroSensorC;  
     SensorBoardControllerP.SamplingTimers[GYRO_SENSOR] -> GyroSensorTimer;
     
      /* For the INTERNAL_TEMP Sensor */
     components InternalTemperatureSensorC;
     components new TimerMilliC() as InternalTemperatureSensorTimer;
     SensorBoardControllerP.SensorImpls[INTERNAL_TEMPERATURE_SENSOR] -> InternalTemperatureSensorC;
     SensorBoardControllerP.SamplingTimers[INTERNAL_TEMPERATURE_SENSOR] -> InternalTemperatureSensorTimer;
     #endif
	 
	 #ifdef SHIMMER_SENSOR_BOARD
	 /* For the ACC Sensor */
     components AccSensorC;
     components new TimerMilliC() as AccSensorTimer;
     SensorBoardControllerP.SensorImpls[ACC_SENSOR] -> AccSensorC;
     SensorBoardControllerP.SamplingTimers[ACC_SENSOR] -> AccSensorTimer; 
     #endif
	 
	 #ifdef MTS300_SENSOR_BOARD
	 /* For the ACC Sensor */
     components AccSensorC;
     components new TimerMilliC() as AccSensorTimer;
     SensorBoardControllerP.SensorImpls[ACC_SENSOR] -> AccSensorC;
     SensorBoardControllerP.SamplingTimers[ACC_SENSOR] -> AccSensorTimer; 
     #endif
	 
     // if new sensors are added, wire the aforedeclared PIL driver components down here


     // if new sensors are added, also wire the aforedeclared timers down here


}
