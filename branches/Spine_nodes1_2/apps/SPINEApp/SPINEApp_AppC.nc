/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that
allows dynamic configuration of feature extraction capabilities 
of WSN nodes via an OtA protocol

Copyright (C) 2007 Telecom Italia S.p.A. 
�
GNU Lesser General Public License
�
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
�
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.� See the GNU
Lesser General Public License for more details.
�
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA� 02111-1307, USA.
*****************************************************************/

/**
 * Configuration component of the SPINE Application.
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */
configuration SPINEApp_AppC {
}

implementation {
  components MainC, SPINEApp_C;
  
  components new TimerMilliC() as DebugTimer;  // DEGUB CODE TO BE REMOVED
  components RadioControllerC;

  components PacketManagerC;
  components SpineSetupSensorPktC;

  components SensorsRegistryC;
  components SensorBoardControllerC;

  components FunctionManagerC;

  SPINEApp_C.Boot -> MainC.Boot;
  
  SPINEApp_C.PacketManager -> PacketManagerC;
  SPINEApp_C.SpineSetupSensorPkt -> SpineSetupSensorPktC;

  SPINEApp_C.SensorsRegistry -> SensorsRegistryC;
  SPINEApp_C.SensorBoardController -> SensorBoardControllerC;

  SPINEApp_C.FunctionManager -> FunctionManagerC;
  
  SPINEApp_C.DebugTimer -> DebugTimer;  // DEGUB CODE TO BE REMOVED
  SPINEApp_C.RadioController -> RadioControllerC;  // DEGUB CODE TO BE REMOVED
}