/****************************************************
 This file defines the behavior of the prefilter in the HMM Annotation project.

 **** Logic ****: 

 ( avg_5pts(signal(axes)) - avg_100pts(avg_5pts(signal(axes))) ) / stdev_100pts(avg_5pts(signal(axes)));

 - Collect A = 5 point average array of the signal (for each one of the accelerometer axes)
 - Collect B = 100 point average of the 5 point average array A
 - Collect C = 100 standard deviation of a 5 point average array A
 - Subtract 100 point average from the middle element( in this case 100/2 = 50) of the 5point average signal, ie D = A(50) - B
 - Divide the result of the previous operation by the value of the standard deviation, ie D / C
 - Overall: (A(50) - B)/C;
 
 **** Inputs ****
 
 The file is called every time data is sampled by the accelerometer. The input is three readings of the accelerometer at a given time.

 HMMPrefilter.computePrefilter(int16_t* data, uint8_t len)
 
 - data contains all channels of accelerometer sensor readings
 - len describes the number of channels in the data reading, which in our case is expected to be 3
 
 **** Output ****
 
 Produces filtered output in the same format as the received input. 
 
 - data contains the filtered accelerometer data for all of the input channels
 - len specifies the number of input channels
 
*****************************************************/
#define NUM_OUT_CHANNELS 3
#define FEATURES_PER_CHANNEL 1
#define READ_SIZE 105 /* should not be changed */

module HMMPrefilterP {
  
  provides interface HMMPrefilter;

  uses {
    interface MathUtils;
    interface Leds;
  }

}

implementation {
  static int16_t buffered_input[READ_SIZE * NUM_OUT_CHANNELS];
  static int16_t writearray[FEATURES_PER_CHANNEL * NUM_OUT_CHANNELS];
  static uint32_t readpos = 0;
  static uint32_t writepos = 312;
  static int16_t _5ptarray[NUM_OUT_CHANNELS][5];
  static int16_t _5ptavgarray[NUM_OUT_CHANNELS][101];
  static int32_t _5ptsum[NUM_OUT_CHANNELS];
  static int32_t _101ptsum[NUM_OUT_CHANNELS];
  static int16_t offs1[NUM_OUT_CHANNELS];    
  
  static uint16_t counter = 0;

  /*
	Compute an n-point average of an array *data 
  */
  int16_t calc_npt_avg(int32_t n, int16_t *data) {
    int32_t i;
    int32_t sum = 0;
    int16_t avg;

    for(i = 0; i < n; i++) {
      sum += data[i];  
    }
    avg = sum / n;
    return avg;
  }

  /*
	Compute an approximation of an n-point standard deviation of an array *data 
    sqrt(sum(data(i)^2)/n - mean^2)
  */
  int32_t calc_std_dev(int16_t n, int16_t *data, int16_t mean) {
    int32_t i;
    int32_t sum, sum2;
    int32_t stdev;
    int32_t mean2;

	/*calculate mean*/
    mean = calc_npt_avg(n, data);
	/*calculate mean^2*/
    mean2 = (mean * mean);
    sum2 = 0;
	/*calculate sum(data(i)^2)*/
    for(i = 0; i < n; i++) {
      sum2 += (data[i] * data[i]);
    }
	/*calculate sum(data(i)^2)/n*/
    sum = sum2/n;
	/*calculate sum(data(i)^2)/n - mean^2*/
    sum = sum - mean2;
	/*calculate sqrt(sum(data(i)^2)/n - mean^2)*/
    stdev = call MathUtils.isqrt(sum);
    return stdev;
  }

  /* 
    Does the prefiltering for all the channel data. The input data contains all of the input channels for one data reading.
	The len input defines the number of channels in the data. 
   */
  command void HMMPrefilter.computePrefilter(int16_t* data, uint8_t len) {
    int32_t temp;
    int32_t i, j, k, p = 0;
    int16_t _101ptavg;
    int16_t idx2 = 0;
    int32_t _101ptstddev;

    int16_t tval = 0;
    
	/*init the 5point array; done only once*/
    for(i=0; i < NUM_OUT_CHANNELS; i++)
		if(counter == 0)
			_5ptarray[i][0] = 0;
	
	/* Populate the input buffer until it contains 104 values from each channel.
	No processing is done on the data.*/
    if(counter < 3*(READ_SIZE-1)) {
        for(i=0; i < NUM_OUT_CHANNELS; i++)
          buffered_input[counter++] = data[i];
    }
	/* Add the final 105th value of each channel to the input buffer 
	and start processing. */	
    else {
      for (i=0; i<NUM_OUT_CHANNELS; i++) {
         p = writepos + i;
         if (p >= 3*READ_SIZE)
            p -= 3*READ_SIZE;
         buffered_input[p] = data[i];
      }
      writepos = p+1;
      /* buffered_input contain values for all the channels in an interleaved
      fashion, each iteration corresponds to each channel */
      for(i=0; i < NUM_OUT_CHANNELS; i++) {
        /* Initialization phase, happens only in first case.
		Process the initial full buffer of data and prepare to compute a 100 point stats.
		This phase terminates when the system finishes construction of a 100 array of 5 point averages*/
        if(counter == 3*(READ_SIZE-1)) {
          readpos = 0;
          _101ptsum[i] = 0;
          offs1[i] = 0;
          idx2 = 0;
          for(j=0; j<(READ_SIZE-1); j++) {
		    /*tval helps select data reading from the proper channel*/
            tval = buffered_input[readpos * NUM_OUT_CHANNELS + i];
            _5ptsum[i] = _5ptsum[i] + tval;
            if(j < 4) {
			  /*initial populate the 5 point array*/
              _5ptarray[i][j+1] = tval;
            }
            else {
			  /*Remove the first element of the 5 point array, shift values, and add the last element*/
              _5ptsum[i] = _5ptsum[i] - _5ptarray[i][0];
              for(k=0; k < 4; k++)
                _5ptarray[i][k] = _5ptarray[i][k+1];
              _5ptarray[i][4] = tval;
			  /*Update the 5 point average array*/
              _5ptavgarray[i][idx2] = _5ptsum[i]/5; //_5ptavgarray here should start frm 1
			  /*Update the 100 point sum*/
              _101ptsum[i] += _5ptavgarray[i][idx2];
              idx2++;			  
            }
            readpos++;
          }
          if(i == (NUM_OUT_CHANNELS - 1))
             counter++;
        }

        /* General case, when only one input is read at a time 
		Proceeds when all of the buffers are full to process the filtering
		Produces an output for every input data set*/
		
		/*Remove the first element of the 5 point array, shift values, and add the last element*/
        _5ptsum[i] = _5ptsum[i] - _5ptarray[i][0];
        for(k=0; k < 4; k++)
          _5ptarray[i][k] = _5ptarray[i][k+1];
		/*Update the 5 point average array*/
        _5ptarray[i][4] = buffered_input[readpos * NUM_OUT_CHANNELS + i];
        _5ptsum[i] += _5ptarray[i][4];
        tval = (_5ptsum[i]/5);
        _101ptsum[i] = _101ptsum[i] - _5ptavgarray[i][offs1[i]] + tval;
		/*Update the 100 point average array*/
        _101ptavg = _101ptsum[i]/100;
		/*Update the 5 point average array*/
        _5ptavgarray[i][offs1[i]] = tval;

        /* Do the prefiltering, write to writearray */
		
		/*compute standard deviation*/		
        _101ptstddev = calc_std_dev(101, &(_5ptavgarray[i][0]), _101ptavg);
        /*update the offset by 49, to reach the midle element between 0 and 100 in a moving window*/
		idx2 = offs1[i] + 49;
        if(idx2 >= 100)
          idx2 -= 100;
        temp = (_5ptavgarray[i][idx2] - _101ptavg);
		/*normalize standard deviation*/
        _101ptstddev = _101ptstddev >> 12;
        if(_101ptstddev == 0)
          _101ptstddev = 1;
        writearray[i] = (temp/_101ptstddev);
        offs1[i]++;
        if(offs1[i] >= 100)
          offs1[i] = 0;

      }
      readpos++;
      if(readpos == READ_SIZE)
         readpos = 0;
	  
	  /*signal DataReady for the next component to initiate computation*/
      signal HMMPrefilter.prefilterDataReady(writearray, FEATURES_PER_CHANNEL * NUM_OUT_CHANNELS * sizeof(int16_t));

    }
  }

}

