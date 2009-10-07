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
 * Configuration component of the SPINE Feature Engine.
 *
 *
 * @author Raffaele Gravina
 * @author Philip Kuryloski
 *
 * @version 1.2
 */

configuration FeatureEngineC {
	
        provides interface Function;
	provides interface FeatureEngine;

	uses interface Feature as Features[uint8_t featureID];

}

implementation {
	components MainC, FunctionManagerC, SensorsRegistryC, BufferPoolP, FeatureEngineP;
      
	// if new features are added, declare their component down here
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
        #ifndef MTS300_SENSOR_BOARD
        components PitchRollC;
        components EntropyC;
    	#endif
        components VectorMagnitudeC;
        components KcalC;


	components LedsC;

	FeatureEngineP.Function = Function;
	FeatureEngineP.FeatureEngine = FeatureEngine;

	FeatureEngineP.FunctionManager -> FunctionManagerC;
	FeatureEngineP.SensorsRegistry -> SensorsRegistryC;

	FeatureEngineP.BufferPool -> BufferPoolP;

	FeatureEngineP.Boot -> MainC.Boot;
	FeatureEngineP.Leds -> LedsC;

	FeatureEngineP.Features = Features;
	// if new features are added, wire the aforedeclared components down here
        FeatureEngineP.Features[MAX] -> MaxC;
        FeatureEngineP.Features[MIN] -> MinC;
        FeatureEngineP.Features[RANGE] -> RangeC;
        FeatureEngineP.Features[MEAN] -> MeanC;
        FeatureEngineP.Features[AMPLITUDE] -> AmplitudeC;
        FeatureEngineP.Features[RMS] -> RmsC;
        FeatureEngineP.Features[ST_DEV] -> StandardDeviationC;
        FeatureEngineP.Features[TOTAL_ENERGY] -> TotalEnergyC;
        FeatureEngineP.Features[VARIANCE] -> VarianceC;
        FeatureEngineP.Features[MODE] -> ModeC;
        FeatureEngineP.Features[MEDIAN] -> MedianC;
        FeatureEngineP.Features[RAW_DATA] -> RawDataC;
        #ifndef MTS300_SENSOR_BOARD
        FeatureEngineP.Features[PITCH_ROLL] -> PitchRollC;
        FeatureEngineP.Features[ENTROPY] -> EntropyC;
        #endif
	FeatureEngineP.Features[VECTOR_MAGNITUDE] -> VectorMagnitudeC;
	FeatureEngineP.Features[KCAL] -> KcalC;

}
