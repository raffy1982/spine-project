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
 * @version 1.3
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
     
     // if new sensors are added to one of the supported sensor boards, declare and wire PIL driver components and timers into the sensor board ifdef
     
   //SPINE sensor board support (acceleromenter, voltage, gyro, internal temp ...)
   #ifdef SPINE_SENSORBOARD
       /* For the ACC Sensor */
     components AccSensorC;
     components new TimerMilliC() as AccSensorTimer;
     SensorBoardControllerP.SensorImpls[ACC_SENSOR] -> AccSensorC;
     SensorBoardControllerP.SamplingTimers[ACC_SENSOR] -> AccSensorTimer;

       /* For the VOLTAGE Sensor */
     components HilLiIonVoltageSensorC;
     components new TimerMilliC() as VoltageSensorTimer;
     SensorBoardControllerP.SensorImpls[VOLTAGE_SENSOR] -> HilLiIonVoltageSensorC;
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

   //SHIMMER sensor board support (acceleromenter, gyro,...)
   #ifdef SHIMMER_SENSORBOARD
	 /* For the ACC Sensor */
     components AccSensorC;
     components new TimerMilliC() as AccSensorTimer;
     SensorBoardControllerP.SensorImpls[ACC_SENSOR] -> AccSensorC;
     SensorBoardControllerP.SamplingTimers[ACC_SENSOR] -> AccSensorTimer;
	 
	 /* For the GYRO Sensor */
     components GyroSensorC;
     components new TimerMilliC() as GyroSensorTimer;
     SensorBoardControllerP.SensorImpls[GYRO_SENSOR] -> GyroSensorC;
     SensorBoardControllerP.SamplingTimers[GYRO_SENSOR] -> GyroSensorTimer;
   #endif
   
   //SHIMMER2R sensor board support (acceleromenter...)
   #ifdef SHIMMER2R_SENSORBOARD
	 /* For the ACC Sensor */
     components AccSensorC;
     components new TimerMilliC() as AccSensorTimer;
     SensorBoardControllerP.SensorImpls[ACC_SENSOR] -> AccSensorC;
     SensorBoardControllerP.SamplingTimers[ACC_SENSOR] -> AccSensorTimer;
   #endif

   //MTS300 sensor board support (acceleromenter...)
   #ifdef MTS300_SENSORBOARD
	 /* For the ACC Sensor */
     components AccSensorC;
     components new TimerMilliC() as AccSensorTimer;
     SensorBoardControllerP.SensorImpls[ACC_SENSOR] -> AccSensorC;
     SensorBoardControllerP.SamplingTimers[ACC_SENSOR] -> AccSensorTimer;
   #endif
     
   //'Wisepla' BIOSENSOR sensor board (developed in Tampere University of Technology, Tampere, Finland) support (acceleromenter, ecg, eip)
   #ifdef WISEPLA_BIOSENSOR_SENSORBOARD
     /* For the EIP Sensor */
     components EipSensorC;
     components new TimerMilliC() as EipSensorTimer;
     SensorBoardControllerP.SensorImpls[EIP_SENSOR] -> EipSensorC;
     SensorBoardControllerP.SamplingTimers[EIP_SENSOR] -> EipSensorTimer;

     /* For the ECG Sensor */
     components EcgSensorC;
     components new TimerMilliC() as EcgSensorTimer;
     SensorBoardControllerP.SensorImpls[ECG_SENSOR] -> EcgSensorC;
     SensorBoardControllerP.SamplingTimers[ECG_SENSOR] -> EcgSensorTimer;
     
      /* For the ACC Sensor */
     components AccSensorC;
     components new TimerMilliC() as AccSensorTimer;
     SensorBoardControllerP.SensorImpls[ACC_SENSOR] -> AccSensorC;
     SensorBoardControllerP.SamplingTimers[ACC_SENSOR] -> AccSensorTimer;
   #endif
   
   //'Cardio-Shield' sensor board (by Alessandro Andreoli on the base of the spine motion board) support (acceleromenter, voltage, internal temp)
   #ifdef CARDIOSHIELD_SENSORBOARD
       /* For the ACC Sensor */
     components AccSensorC;
     components new TimerMilliC() as AccSensorTimer;
     SensorBoardControllerP.SensorImpls[ACC_SENSOR] -> AccSensorC;
     SensorBoardControllerP.SamplingTimers[ACC_SENSOR] -> AccSensorTimer;

       /* For the VOLTAGE Sensor */
     components HilLiIonVoltageSensorC;
     components new TimerMilliC() as VoltageSensorTimer;
     SensorBoardControllerP.SensorImpls[VOLTAGE_SENSOR] -> HilLiIonVoltageSensorC;
     SensorBoardControllerP.SamplingTimers[VOLTAGE_SENSOR] -> VoltageSensorTimer;

       /* For the INTERNAL_TEMP Sensor */
     components InternalTemperatureSensorC;
     components new TimerMilliC() as InternalTemperatureSensorTimer;
     SensorBoardControllerP.SensorImpls[INTERNAL_TEMPERATURE_SENSOR] -> InternalTemperatureSensorC;
     SensorBoardControllerP.SamplingTimers[INTERNAL_TEMPERATURE_SENSOR] -> InternalTemperatureSensorTimer;
   #endif

    //'Moteiv' Tmote Sky sensors kit
   #ifdef MOTEIV_KIT_SENSORBOARD
     /* For the Humidity Sensor */
     components HumiditySensorC;
     components new TimerMilliC() as HumiditySensorTimer;
     SensorBoardControllerP.SensorImpls[HUMIDITY_SENSOR] -> HumiditySensorC;
     SensorBoardControllerP.SamplingTimers[HUMIDITY_SENSOR] -> HumiditySensorTimer;

     /* For the Temperature Sensor */
     components TemperatureSensorC;
     components new TimerMilliC() as TemperatureSensorTimer;
     SensorBoardControllerP.SensorImpls[TEMPERATURE_SENSOR] -> TemperatureSensorC;
     SensorBoardControllerP.SamplingTimers[TEMPERATURE_SENSOR] -> TemperatureSensorTimer;
     
     /* For the Light Sensor */
     components LightSensorC;
     components new TimerMilliC() as LightSensorTimer;
     SensorBoardControllerP.SensorImpls[LIGHT_SENSOR] -> LightSensorC;
     SensorBoardControllerP.SamplingTimers[LIGHT_SENSOR] -> LightSensorTimer;
     
     /* For the VOLTAGE Sensor */
     components VoltageSensorC;
     components new TimerMilliC() as VoltageSensorTimer;
     SensorBoardControllerP.SensorImpls[VOLTAGE_SENSOR] -> VoltageSensorC;
     SensorBoardControllerP.SamplingTimers[VOLTAGE_SENSOR] -> VoltageSensorTimer;

     /* For the INTERNAL_TEMP Sensor */
     components InternalTemperatureSensorC;
     components new TimerMilliC() as InternalTemperatureSensorTimer;
     SensorBoardControllerP.SensorImpls[INTERNAL_TEMPERATURE_SENSOR] -> InternalTemperatureSensorC;
     SensorBoardControllerP.SamplingTimers[INTERNAL_TEMPERATURE_SENSOR] -> InternalTemperatureSensorTimer;
   #endif

     // if new sensor board are added, declare and wire PIL driver components and timers down here

}
