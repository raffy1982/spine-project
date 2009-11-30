configuration HMMPrefilterC {
  provides interface HMMPrefilter;
}
implementation {
  components HMMPrefilterP;
  
  HMMPrefilterP.HMMPrefilter = HMMPrefilter;

  components MathUtilsC;

  components LedsC;
  HMMPrefilterP.Leds -> LedsC;

  HMMPrefilterP.MathUtils -> MathUtilsC;

}

