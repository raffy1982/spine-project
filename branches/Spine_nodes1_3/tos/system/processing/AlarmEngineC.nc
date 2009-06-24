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
 * Configuration component of the SPINE Alarm Engine.
 *
 *
 * @author Roberta Giannantonio
 *
 * @version 1.2
 */

 #include "Functions.h"

 configuration AlarmEngineC {
      provides interface Function;
      uses interface Feature as Features[uint8_t featureID];
 }

 implementation {
      components MainC, FunctionManagerC, SensorsRegistryC, BufferPoolP, AlarmEngineP;
      
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
	  components LedsC;

      AlarmEngineP.Function = Function;
      AlarmEngineP.FunctionManager -> FunctionManagerC;
      AlarmEngineP.SensorsRegistry -> SensorsRegistryC;

      AlarmEngineP.BufferPool -> BufferPoolP;

      AlarmEngineP.Boot -> MainC.Boot;
      AlarmEngineP.Leds -> LedsC;
 	AlarmEngineP.Features = Features;
	// if new features are added, wire the aforedeclared components down here
        AlarmEngineP.Features[MAX] -> MaxC;
        AlarmEngineP.Features[MIN] -> MinC;
        AlarmEngineP.Features[RANGE] -> RangeC;
        AlarmEngineP.Features[MEAN] -> MeanC;
        AlarmEngineP.Features[AMPLITUDE] -> AmplitudeC;
        AlarmEngineP.Features[RMS] -> RmsC;
        AlarmEngineP.Features[ST_DEV] -> StandardDeviationC;
        AlarmEngineP.Features[TOTAL_ENERGY] -> TotalEnergyC;
        AlarmEngineP.Features[VARIANCE] -> VarianceC;
        AlarmEngineP.Features[MODE] -> ModeC;
        AlarmEngineP.Features[MEDIAN] -> MedianC;
        AlarmEngineP.Features[RAW_DATA] -> RawDataC;
        #ifndef MTS300_SENSOR_BOARD
        AlarmEngineP.Features[PITCH_ROLL] -> PitchRollC;
        AlarmEngineP.Features[ENTROPY] -> EntropyC;
	#endif
        AlarmEngineP.Features[VECTOR_MAGNITUDE] -> VectorMagnitudeC;

 }
