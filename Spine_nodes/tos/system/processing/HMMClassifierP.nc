/****************************************************
This file defines the behavior of the classifier in the HMM Annotation project.

 **** Logic ****: 

 Selects the most probable state the system was 15 samples ago, based on the sequence of the features for 15 data points 
 
 **** Training File ****
 
 HMM training code generates a .h file, which includes probabilities that define HMM training.
 If you are retraining the system, be sure to include the proper file.
 
 **** Inputs ****
 
 The file is called every time the feature extractor produces and output. 
 
 HMMClassifier.classify(int16_t* data, uint8_t len)
 
 - data contains 4 features per data channel extracted during the previous step
 - len describes the number of channels in the data reading multiplied by the number of features per channel,
 which in our case is expected to be 12
 
 **** Output ****
 
 Index of the most probably state the system was in 15 observations ago.
 
*****************************************************/

#include "hmm_data.h"

#define NUM_OF_STATES 9
#define NUM_OF_FEATURES 12
#define NUM_OF_BINS 10
#define STWRITESIZE 1
#define HMM_NEG_INF -30000

module HMMClassifierP {
    provides interface HMMClassifier;

    uses interface Leds;
}

implementation {
  
  uint8_t hmmState;


  static int16_t feature_vector[NUM_OF_FEATURES]; /* stores the quantized feature vector */
  static uint16_t tbl_chains[NUM_OF_STATES][16];
  static uint16_t tbl_chains_old[NUM_OF_STATES][16];
  static int16_t tbl_chain_prob[NUM_OF_STATES];
  static int16_t tbl_chain_prob_old[NUM_OF_STATES];

  /*
	Initializing the state and probability chains.
	tbl_chain_prob is used for the current iteration of the algorithm
	tbl_chain_prob_old is used to preserve values from the previous iteration
  */
  void prob_vectors_init()
  {
  	uint16_t i,j;
  	for(i=0;i<NUM_OF_STATES;i++)
  	{
  	  for(j=0;j<16;j++)
  	  {
  	    tbl_chains[i][j]=-1;
  	    tbl_chains_old[i][j]=-1;
  	  }
  	  tbl_chain_prob[i]=0; 
  	  tbl_chain_prob_old[i]=0; 
  	}
  }

  /*
	Calculate new position based on the circular nature of the buffers
  */ 
  uint16_t newPosition(uint16_t pos)
  {
    return (1+pos)%NUM_OF_STATES;
  }

  /* 
	Find the chain with the max probability, update the probabilities 
  */
  uint16_t update_getmaxprobs(uint16_t cur_state) {
    int16_t max_prob = HMM_NEG_INF;
    uint16_t i;
    uint16_t max_index = 0;

    for(i=0;i<NUM_OF_STATES;i++)
    {
      /* If tbl_chain_prb[i] is greater, that becomes the max prob */
      if(max_prob < tbl_chain_prob[i])
      {
        max_prob = tbl_chain_prob[i];
        max_index = i;
      }
      /* If tbl_chain_prob[i] is same as max_prob, change state only if
         the state is same as the current state */
      else if (max_prob == tbl_chain_prob[i]){
        if(cur_state == tbl_chains[i][15])
          max_index = i;
      }
    }
    /* Subtract the max_prob from all to rescale the probability chain */
    for(i=0;i<NUM_OF_STATES;i++)
    {
        if(tbl_chain_prob[i] != HMM_NEG_INF)
          tbl_chain_prob[i] -= max_prob; 
    }
    return max_index;
  }

  /*
	Returns the most probable state after the execution of viterby path counting
  */
  uint8_t getHmmState() {
    int16_t max_prob = HMM_NEG_INF;
    uint16_t i;
    uint16_t max_index=0;
    for(i=0;i<NUM_OF_STATES;i++)
    {
      if(max_prob < tbl_chain_prob[i])
      {
        max_prob = tbl_chain_prob[i];
        max_index = i;
      }
    }
    hmmState = (uint8_t)(tbl_chains[max_index][15]+1);

    return hmmState;
  }

  /* 
	Get the emission probability corresponding to the feature vector defined in the .h file
  */
  int32_t get_pobs(int16_t m, int16_t *f_vect)
  {
  	int32_t result=0;
  	uint16_t i;
    /* Emission probability is calculated as the sum of observation probability
       corresponding to the input feature vector for the particular state */
  	for(i=0;i<NUM_OF_FEATURES;i++)
  		result += (int32_t)prob_obs[m][f_vect[i]][i];
  	
  	return result;
  }
 
  /*
	The system assumes that it can be described by a left->right HMM.
	Meaning that there is only one possible state to precede any other state.
	Returns the index of the previous states, based on the index of the current state.
  */
 
  uint16_t get_prev_state(uint16_t cur_state) {
    if (cur_state == 0)
      return NUM_OF_STATES-1;
    else
      return cur_state - 1;
  }
  
  /* 
	Does the quantization for all the features based on the data from the training .h file
  */
  void quantize(int16_t *f_vect) {
    uint16_t i;
    int16_t binpos;

    for(i=0;i < NUM_OF_FEATURES; i++)
    {
      binpos = (f_vect[i] + HMM_OFFSETS[i])/ HMM_INCREMENT[i];
      if(binpos < 0)
        binpos = 0;
      else if(binpos > 9)
        binpos = 9;
      f_vect[i] = binpos;
    }
  }

  /* 
	Given state1 and state2, returns the probability to have transitioned
    from state1 to state2 
  */
  uint16_t get_prev_trans (uint16_t state1, uint16_t state2) {
    uint16_t s2_prev;
    s2_prev = get_prev_state (state2);
    if(state1 == state2)
      return tbl_trns[state1][0];
    else if(state1 == s2_prev) 
      return tbl_trns[state1][1];
    else /* state1 != s2_prev */
      return HMM_NEG_INF; /* Largest negative number */
  }

  /* 
	Viterbi- Given an input feature vector, here is where all the chains and
    the probabilities get updated 
  */
  void Viterbi(int16_t *feature_vect) {
    int16_t m, i;
    uint16_t prev_state, state;
    int16_t prev_prob, cur_prob;
    int32_t prob;
    int32_t pobs;
    uint16_t max_indx;
    static uint16_t ps_val = 0;
    int16_t pt_prob, ct_prob;

    /* Quantization of the input feature vector */
    quantize (feature_vect);
    memcpy(tbl_chains_old, tbl_chains, sizeof(tbl_chains));
    memcpy(tbl_chain_prob_old, tbl_chain_prob, sizeof(tbl_chain_prob));
    /* iterate for each state */

    for(m=0; m < NUM_OF_STATES; m++) 
    {
      pobs = get_pobs (m, feature_vect);
      prev_state = get_prev_state(m);
      /* Calculate the probability for transition from previous state to current state */
      pt_prob = get_prev_trans(prev_state, m);
      prev_prob = tbl_chain_prob_old[prev_state] + pt_prob;

      /* Check for underflow */
      if((pt_prob < 0) && (tbl_chain_prob_old[prev_state] < 0) && (prev_prob > 0))
        prev_prob = HMM_NEG_INF+1;
      /* Calculate the probability to have remained in the current state */
      ct_prob = get_prev_trans(m,m);
      cur_prob = tbl_chain_prob_old[m] + ct_prob;
      /* Check for underflow */
      if((ct_prob < 0) && (tbl_chain_prob_old[m] < 0) && (cur_prob > 0)) 
        cur_prob = HMM_NEG_INF+1;

      if(prev_prob > cur_prob) {
        state = prev_state;
        prob = pobs + (int32_t)prev_prob;
      }
      else {
        state = m;
        prob = pobs + (int32_t)cur_prob;
      }
      if(prob < HMM_NEG_INF)
        tbl_chain_prob[m] = HMM_NEG_INF+1;
      else
        tbl_chain_prob[m] = prob;
      tbl_chains[m][0] = state;
      for(i=1; i < 16; i++) {
        tbl_chains[m][i] = tbl_chains_old[state][i-1];
      }
    }
    max_indx = update_getmaxprobs(ps_val);
    ps_val = tbl_chains[max_indx][15]; 
    /* fix the chains */
    for(m=0; m < NUM_OF_STATES; m++)
    {
      if(ps_val != tbl_chains[m][15])
        tbl_chain_prob[m] = HMM_NEG_INF;
    }
  }
  
  command void HMMClassifier.classify(int16_t* data, uint8_t len) {
     Viterbi(data);
	 /*signal DataReady for the system output*/
     signal HMMClassifier.classificationDone(getHmmState());
  }

}

