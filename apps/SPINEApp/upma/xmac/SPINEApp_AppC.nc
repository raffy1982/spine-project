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
 * Configuration component of the SPINE Application.
 *
 * @author Raffaele Gravina <raffale.gravina@gmail.com>
 *
 * @version 1.2
 */
configuration SPINEApp_AppC {
}

implementation {
  components MainC, SPINEApp_C as App;  
  App.Boot -> MainC.Boot;

  components SpinePacketizerC as PacketizerC;
  App.BufferedSend -> PacketizerC;
  App.Receive -> PacketizerC;

  components ActiveMessageC;
  components MacControlC;
  App.LowPowerListening -> MacControlC;
  App.AMControl -> ActiveMessageC;

  components SpineStartPktC;
  components SpineSetupSensorPktC;
  components SpineFunctionReqPktC;
  components SpineSetupFunctionPktC;
  App.SpineStartPkt -> SpineStartPktC;
  App.SpineSetupSensorPkt -> SpineSetupSensorPktC;
  App.SpineFunctionReqPkt -> SpineFunctionReqPktC;
  App.SpineSetupFunctionPkt -> SpineSetupFunctionPktC;
  
  components SensorsRegistryC;
  components SensorBoardControllerC;
  App.SensorsRegistry -> SensorsRegistryC;
  App.SensorBoardController -> SensorBoardControllerC;

  components FunctionManagerC;
  App.FunctionManager -> FunctionManagerC;
   
  components new TimerMilliC() as Annce_timer;
  App.Annce_timer -> Annce_timer; 

  components LedsC;
  App.Leds -> LedsC;
  
}
