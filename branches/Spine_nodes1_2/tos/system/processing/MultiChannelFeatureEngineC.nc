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
 * Configuration component of the SPINE Multi Channel Feature Engine.
 *
 *
 * @author Raffaele Gravina
 * @author Philip Kuryloski
 *
 * @version 1.0
 */

#include "Functions.h"

//#include "printf.h"

configuration MultiChannelFeatureEngineC {
	provides interface Function;
	provides interface MultiChannelFeatureEngine;

	uses {
		interface MultiChannelFeature as MultiChannelFeatures[uint8_t featureID];
        //interface Timer<TMilli> as ComputingTimers[uint8_t id];
	}
}

implementation {
	components MainC, FunctionManagerC, SensorsRegistryC, BufferPoolP, MultiChannelFeatureEngineP;
      
	components PitchRollC;
        components VectorMagnitudeC;
        components MaxC;
        components MinC;
        components RangeC;
        components MeanC;
        components AmplitudeC;
        components RmsC;
        components StandardDeviationC;
        components TotalEnergyC;
        components VarianceC;
        components ModeC;
        components MedianC;
        components RawDataC;
	
	components LedsC;

	MultiChannelFeatureEngineP.Function = Function;
	MultiChannelFeatureEngineP.MultiChannelFeatureEngine = MultiChannelFeatureEngine;

	MultiChannelFeatureEngineP.FunctionManager -> FunctionManagerC;
	MultiChannelFeatureEngineP.SensorsRegistry -> SensorsRegistryC;

	MultiChannelFeatureEngineP.BufferPool -> BufferPoolP;

	MultiChannelFeatureEngineP.Boot -> MainC.Boot;
	MultiChannelFeatureEngineP.Leds -> LedsC;

	MultiChannelFeatureEngineP.MultiChannelFeatures = MultiChannelFeatures;
	MultiChannelFeatureEngineP.MultiChannelFeatures[PITCH_ROLL] -> PitchRollC;
	MultiChannelFeatureEngineP.MultiChannelFeatures[VECTOR_MAGNITUDE] -> VectorMagnitudeC;
	MultiChannelFeatureEngineP.MultiChannelFeatures[MAX] -> MaxC;
        MultiChannelFeatureEngineP.MultiChannelFeatures[MIN] -> MinC;
        MultiChannelFeatureEngineP.MultiChannelFeatures[RANGE] -> RangeC;
        MultiChannelFeatureEngineP.MultiChannelFeatures[MEAN] -> MeanC;
        MultiChannelFeatureEngineP.MultiChannelFeatures[AMPLITUDE] -> AmplitudeC;
        MultiChannelFeatureEngineP.MultiChannelFeatures[RMS] -> RmsC;
        MultiChannelFeatureEngineP.MultiChannelFeatures[ST_DEV] -> StandardDeviationC;
        MultiChannelFeatureEngineP.MultiChannelFeatures[TOTAL_ENERGY] -> TotalEnergyC;
        MultiChannelFeatureEngineP.MultiChannelFeatures[VARIANCE] -> VarianceC;
        MultiChannelFeatureEngineP.MultiChannelFeatures[MODE] -> ModeC;
        MultiChannelFeatureEngineP.MultiChannelFeatures[MEDIAN] -> MedianC;
        MultiChannelFeatureEngineP.MultiChannelFeatures[RAW_DATA] -> RawDataC;
}
