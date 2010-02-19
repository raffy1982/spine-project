/****************************************************
 This file defines the behavior of the properties extractor in the HMM Annotation project.

 **** Logic ****: 

 Extract four properties from every input data point
   
  - The first derivative 	(A)
  - The second derivative	(B)
  - Pick detection value	(C)
  - Data value				(D)
 
 **** Inputs ****
 
 The file is called every time prefilter produces an output. At any time, the input is three readings of the filtered accelerometer data.

 HMMPropertiesExtractor.extractProperties(int16_t* data, uint8_t len)
 
 - data contains filtered accelerometer sensor readings
 - len describes the amount of readings stored in the data variable, which in our case is expected to be 3

 **** Output ****
 
 Produces feature output for all of the channels in the following format

 data = [Dx,Ax,Bx,Cx,Dy,Ay,By,Cy,Dz,Az,Bz,Cz] 
 where x,y,z are input channels, and A, B, C, and D are defined in the Logic section. 
 
 - len specifies the number of input channels multiplied by the number of features per channel, which in our case is 12
 
*****************************************************/
#define NUM_OUT_CHANNELS 3
#define FEATURES_PER_CHANNEL_PROP 4
#define READ_SIZE_PROP 5
#define WRITE_SIZE 15

module HMMPropertiesExtractorP {
  provides  interface HMMPropertiesExtractor;

  uses  interface Leds;
}

implementation {
  static int16_t buffered_input[READ_SIZE_PROP * NUM_OUT_CHANNELS];
  static int16_t writearray[WRITE_SIZE];

  static uint8_t counter = 0;

  /*
	A simple peak detection scheme
  */
  int16_t peak_detect (int16_t *data) {
    int16_t max_val, min_val;
    if (data[0] > data[1]) {
      max_val = data[0];
      min_val = data[1];
    }
    else {
      max_val = data[1];
      min_val = data[0];
    }
    if(max_val < data[3])
      max_val = data[3];
    else if(min_val > data[3])
      min_val = data[3];
    if(max_val < data[4])
      max_val = data[4];
    else if(min_val > data[4])
      min_val = data[4];
    if(max_val <= data[2])
      return 1;
    else if(min_val >= data[2])
      return -1;
    else
      return 0;
  }

  /*
	Compute all the required properties in the following way.
	Assume d(i) corresponds to ith data point of the input, v'(i) denotes the first derivative of ith point,
	v''(i) denotes the second derivative of the ith point, and p(i) corresponds to peak detection.
	
	v'(i) = (v(i+1) - v(i-1))/2
	v''(i) =  (v'(i+1) - v'(i-1))/2
	
	It is clear, that in order to find a second derivative of a point, a window of size 5 is required.
	
	d(0)
	d(1) v'(1)
	d(2) v'(2) v''(2)
	d(3) v'(3)
	d(4)
	
  */
  command void HMMPropertiesExtractor.extractProperties(int16_t* data, uint8_t len) {
    int16_t d0, d1, d2;
    int16_t dd0;
    int16_t pk;
    uint32_t n_writes = 0;
    uint32_t i, j;
    int16_t chanl[READ_SIZE_PROP];
    int16_t result[NUM_OUT_CHANNELS * FEATURES_PER_CHANNEL_PROP];

	/* Executed once. Starts when the propertes extractor is called for the first time.
	terminates when the writearray has 4 points of each channel available.*/
    if(counter < READ_SIZE_PROP - 1)   {
       for(i=0; i<NUM_OUT_CHANNELS; i++)
          writearray[(counter * NUM_OUT_CHANNELS) + i] = data[i];
       counter++;
    }
    else {
	/* Repeated execution. Add the new data reading as the last value to the list of 5 reading required to extract features.*/
      for(i=0; i<NUM_OUT_CHANNELS; i++)
          writearray[(counter * NUM_OUT_CHANNELS) + i] = data[i];

	  /* Extract Features*/
      for(i=0; i<NUM_OUT_CHANNELS; i++) {
        for(j=0; j < READ_SIZE_PROP; j++) {
          chanl[j] = writearray[j * NUM_OUT_CHANNELS + i];
        }
        /* do the feature extraction, write it into the writearray */
        /* using shift instead of divide by 2 */
        d0 = ((chanl[2] - chanl[0]) >> 1);
        d1 = ((chanl[3] - chanl[1]) >> 1);
        d2 = ((chanl[4] - chanl[2]) >> 1);
        dd0 = ((d2 - d0) >> 1);
        pk = peak_detect(chanl);
        result[n_writes] = chanl[2];
        n_writes++;
        result[n_writes] = d1;
        n_writes++;
        result[n_writes] = dd0;
        n_writes++;
        result[n_writes] = pk;
        n_writes++;
      }
      /* Shift all of the data readings one to the left, while removing the oldest one.*/
	  
      for(j=0; j<(READ_SIZE_PROP-1) * NUM_OUT_CHANNELS; j++) {
         writearray[j] = writearray[NUM_OUT_CHANNELS + j];
      }
      
	  /*signal DataReady for the next component to initiate computation*/
      signal HMMPropertiesExtractor.propertiesReady(result, NUM_OUT_CHANNELS * FEATURES_PER_CHANNEL_PROP * sizeof(int16_t));
    }
  
  
 }

}




