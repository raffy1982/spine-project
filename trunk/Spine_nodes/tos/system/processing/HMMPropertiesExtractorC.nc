configuration HMMPropertiesExtractorC {
  provides interface HMMPropertiesExtractor;
}
implementation {
  components HMMPropertiesExtractorP;

  components LedsC;

  HMMPropertiesExtractorP.Leds -> LedsC;

  HMMPropertiesExtractorP.HMMPropertiesExtractor = HMMPropertiesExtractor;

}

