/*****************************************************************
SPINE - Signal Processing In-Note Environment is a framework that 
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

#include <Timer.h>
#include "FeatureSelectionAgent.h"

/**
 * Implementation of the Feature Selection Agent (mote side).
 * It contains the AMP protocol support and the required business logic
 * in order to activate, disable features computation and send the results 
 * and to sense and send the battery level.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 **/
configuration FeatureSelectionAgentAppC {
}

implementation {
  components LedsC;
  components FeatureSelectionAgentC as App;

  components new TimerMilliC() as SamplingTimer;
  components new TimerMilliC() as SendTimer;
  components new TimerMilliC() as PostTimer;
  components new TimerMilliC() as BatteryInfoTimer;

  components new AMReceiverC(AM_SERVICEDISCOVERYPKT) as ServiceDiscoveryReceiver;
  components new AMReceiverC(AM_FEATUREACTIVATIONPKT) as FeatureActivationReceiver;
  components new AMReceiverC(AM_REMOVEFEATUREPKT) as RemoveFeatureReceiver;
  components new AMReceiverC(AM_BATTERYINFOREQPKT) as BatteryInfoReqReceiver;

  components new AMSenderC(AM_SERVICEADVERTISEMENTPKT) as ServiceAdvertisementSender;
  components new AMSenderC(AM_DATAPKT) as DataSender;
  components new AMSenderC(AM_SERVICEMESSAGEPKT) as ServiceMessageSender;
  components new AMSenderC(AM_BATTERYINFOPKT) as BatteryInfoSender;

  components AccSensorC as Acc;
  //components STAccelerometerC as Acc;

  //components new GyroXSensorC() as GyroX;
  //components new GyroYSensorC() as GyroY;

  components BuffersManagerAppC as BMAppC;

  components JobEngineAppC;
  
  components AmpHeaderC;
  components AmpServiceDiscoveryC;
  components AmpFeatActivationC;
  components AmpBatteryInfoC;
  components AmpBatteryInfoReqC;
  components AmpDataC;
  components AmpServiceMessageC;
  components AmpRemoveFeatureC;
  components AmpServiceAdvertisementC;
  
  //components new VoltageC() as Volt;

  App.Leds -> LedsC;

  App.SamplingTimer -> SamplingTimer;
  App.SendTimer -> SendTimer;
  App.PostTimer -> PostTimer;
  App.BatteryInfoTimer -> BatteryInfoTimer;

  App.PacketServiceAdvertisement -> ServiceAdvertisementSender;
  App.PacketData -> DataSender;
  App.PacketServiceMessage -> ServiceMessageSender;
  App.PacketBatteryInfo -> BatteryInfoSender;

  App.ServiceAdvertisementSender -> ServiceAdvertisementSender;
  App.DataSender -> DataSender;
  App.ServiceMessageSender -> ServiceMessageSender;
  App.BatteryInfoSender -> BatteryInfoSender;
  App.ServiceDiscoveryReceiver -> ServiceDiscoveryReceiver;
  App.FeatureActivationReceiver -> FeatureActivationReceiver;
  App.RemoveFeatureReceiver -> RemoveFeatureReceiver;
  App.BatteryInfoReqReceiver -> BatteryInfoReqReceiver;

  App.AccSensor -> Acc;
  //App.STAccelerometer -> Acc;
  //App.ReadGyroX -> GyroX;
  //App.ReadGyroY -> GyroY;
  
  App.BM -> BMAppC;

  App.JobEngine -> JobEngineAppC;

  App.AmpHeader -> AmpHeaderC;
  App.AmpServiceDiscovery -> AmpServiceDiscoveryC;
  App.AmpFeatActivation -> AmpFeatActivationC;
  App.AmpBatteryInfo -> AmpBatteryInfoC;
  App.AmpBatteryInfoReq -> AmpBatteryInfoReqC;
  App.AmpData -> AmpDataC;
  App.AmpServiceMessage -> AmpServiceMessageC;
  App.AmpRemoveFeature -> AmpRemoveFeatureC;
  App.AmpServiceAdvertisement -> AmpServiceAdvertisementC;
  
  //App.ReadVolt -> Volt;

}
