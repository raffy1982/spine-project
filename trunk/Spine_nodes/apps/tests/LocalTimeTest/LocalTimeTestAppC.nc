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
 * Test component of the Local Time.
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */    
configuration LocalTimeTestAppC {
}

implementation {
  components MainC, LocalTimeTestC;
  components LedsC;   
  
  components ActiveMessageC;
  components new AMSenderC(0x99) as SenderC;
  
  //components RadioControllerC;

  components new TimerMilliC() as Timer;

  /*components SortC;
  components MathUtilsC;
  components PacketManagerC;
  components SpineDataPktC;
  components SpineServiceMessagePktC;
  components SpineServiceAdvertisementPktC;
  components SpineHeaderC;
  components SpineSetupSensorPktC;
  components SpineSetupFunctionPktC;
  components SpineFunctionReqPktC;
  components SpineStartPktC;
  components RangeC;
  components RmsC;
  components AmplitudeC;
  components StandardDeviationC;
  components VarianceC;
  components MeanC;
  components TotalEnergyC;
  components PitchRollC;
  components VectorMagnitudeC;
  components MaxC;
  components MinC;
  components ModeC;
  components MedianC;
  components RawDataC;
  components FeatureEngineC;
  components AccSensorC;
  components InternalTemperatureSensorC;  
  components SensorBoardControllerC;*/

  //components LocalTimeMilliC as LocalTime;                      // ***  FOR MILLI SEC ***
  components Counter32khz32C as LocalTime;                      // ***  FOR USING OSCILLATOR ***
  //components Msp430CounterMicroC as LocalTime;                    // ***  FOR MICRO SEC ***


  LocalTimeTestC.Boot -> MainC.Boot;
  LocalTimeTestC.Leds -> LedsC;

  LocalTimeTestC.Sender -> SenderC;
  LocalTimeTestC.Radio -> ActiveMessageC;

  //LocalTimeTestC.RadioController -> RadioControllerC;
  
  LocalTimeTestC.Timer -> Timer;

  /*LocalTimeTestC.Sort -> SortC;
  LocalTimeTestC.MathUtils -> MathUtilsC;
  LocalTimeTestC.PacketManager -> PacketManagerC;
  LocalTimeTestC.DataPkt -> SpineDataPktC;
  LocalTimeTestC.SvcMsgPkt -> SpineServiceMessagePktC;
  LocalTimeTestC.SvcAdvPkt -> SpineServiceAdvertisementPktC;
  LocalTimeTestC.SpineHeader -> SpineHeaderC;
  LocalTimeTestC.StpSensPkt -> SpineSetupSensorPktC;
  LocalTimeTestC.StpFuncPkt -> SpineSetupFunctionPktC;
  LocalTimeTestC.FunReqPkt -> SpineFunctionReqPktC;
  LocalTimeTestC.StartPkt -> SpineStartPktC;
  LocalTimeTestC.Range -> RangeC;
  LocalTimeTestC.Rms -> RmsC;
  LocalTimeTestC.Ampl -> AmplitudeC;
  LocalTimeTestC.StDev -> StandardDeviationC;
  LocalTimeTestC.Var -> VarianceC;
  LocalTimeTestC.Mean -> MeanC;
  LocalTimeTestC.TotEn -> TotalEnergyC;
  LocalTimeTestC.PitchRoll -> PitchRollC;
  LocalTimeTestC.VectMagn -> VectorMagnitudeC;
  LocalTimeTestC.Max -> MaxC;
  LocalTimeTestC.Min -> MinC;
  LocalTimeTestC.Mode -> ModeC;
  LocalTimeTestC.Median -> MedianC;
  LocalTimeTestC.Raw -> RawDataC;
  LocalTimeTestC.FeatureEng -> FeatureEngineC;
  LocalTimeTestC.SensorBoardController -> SensorBoardControllerC;
  LocalTimeTestC.Acc -> AccSensorC;
  LocalTimeTestC.Temp -> InternalTemperatureSensorC;   
  */

  LocalTimeTestC.LocalTime -> LocalTime;
}

