configuration HMMClassifierC {
  provides interface HMMClassifier;
}
implementation {
  components HMMClassifierP;

  components LedsC;

  HMMClassifierP.Leds -> LedsC;
  HMMClassifierP.HMMClassifier = HMMClassifier;

}

