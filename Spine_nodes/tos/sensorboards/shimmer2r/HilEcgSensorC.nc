configuration HilEcgSensorC
{
  provides interface Sensor;
}

implementation
{
  components MainC,Msp430DmaC;
  
  components HilEcgSensorP;
  components SensorsRegistryC;
  components LedsC;
  
  HilEcgSensorP.Boot -> MainC;

  Sensor = HilEcgSensorP;
  HilEcgSensorP.SensorsRegistry -> SensorsRegistryC;
    
     components shimmerAnalogSetupC;
     MainC.SoftwareInit              -> shimmerAnalogSetupC.Init;
     HilEcgSensorP.shimmerAnalogSetup -> shimmerAnalogSetupC;
     HilEcgSensorP.DMA0               -> Msp430DmaC.Channel0;
     HilEcgSensorP.Leds               -> LedsC;

}