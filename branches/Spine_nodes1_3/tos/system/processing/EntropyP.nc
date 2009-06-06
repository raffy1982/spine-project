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

/**
 *  Entropy Feature
 *
 *
 * @author Stefano Galzarano <stefano.galzarano@libero.it>
 * @author Antonio Guerrieri <aguerrieri@deis.unical.it>
 *
 */
  
 module EntropyP {

	provides interface Feature;
	uses interface Boot;
	uses interface FeatureEngine;
}

implementation {
       
	bool registered = FALSE;

	event void Boot.booted() {
		if (!registered) {
			call FeatureEngine.registerFeature(ENTROPY);
			registered = TRUE;
		}
	}

	command uint8_t Feature.calculate(int16_t** data, uint8_t channelMask, uint16_t dataLen, int8_t* result) {
		uint8_t k;
		uint8_t mask = 0x08;
		uint8_t rChCount = 0;
		uint16_t i=0, j=0, diffCounter= 0;
		uint16_t diffValues[dataLen];
		float prob[dataLen];
		float entropy= 0;

		for (k = 0; k<MAX_VALUE_TYPES; k++)
			if ( (channelMask & (mask>>k)) == (mask>>k)){
				
				for(i=0; i<dataLen; i++){
					for(j=0; j<diffCounter; j++)
						if(data[k][i]==diffValues[j])
							break;
					
					if(j==diffCounter){
						diffCounter++;
						diffValues[j]= data[k][i];
						prob[j]= 1;
					}else{
						prob[j]++;
					}
				}
				
				for(i=0; i<diffCounter; i++){
					prob[i]= prob[i]/dataLen;
				}
				
				
				for(i=0; i<diffCounter; i++)
					entropy+= prob[i]*logf(prob[i]);
				
				
				((uint16_t *) result)[rChCount++] = (uint16_t)(entropy*-1000);
			
			}
		return channelMask;
	}

	command uint8_t Feature.getResultSize() {
		return 2;    // uint16_t = 2bytes
	}
}




