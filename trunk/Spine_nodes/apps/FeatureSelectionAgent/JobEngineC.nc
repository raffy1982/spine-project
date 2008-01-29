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
 * Implementation of the Job Engine. This component has been introduces to generalize
 * the execution of jobs and to make the resultant code in the end-user application smaller
 * and more readable. This way, the only needed information to compute a job, is to know its 
 * code (as known by the Job Engine).
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */      
 module JobEngineC {

       uses {
          interface Mean4AMP;
          interface Range4AMP;
          interface CentralValue4AMP;
          interface Min4AMP;
          interface Max4AMP;
          interface Amplitude4AMP;
          interface Median4AMP;
          interface Rms;
          interface Var;
          interface StandardDeviation;
          interface TotalEnergy;
       }

       provides interface JobEngine;
}

implementation {
      
      /*
      * This command is used to tell the Job Engine to execute a job, knowing its code, and giving
      * him the needed task parameters.
      *
      * @param jobCode : the code of the job to be executed
      * @param data : the pointer to the array containing the parameters that have to be passed 
      *               to the actual task executor
      * @param elemCount : the lenght of the parameters array
      *
      * @return 'int32_t' : the job result
      */
       command int32_t JobEngine.execute(uint8_t jobCode, int16_t* data, uint16_t elemCount) {
            switch (jobCode) {
               case ROW_DATA_CODE : // = 0x0
                   return *data;
               case MEAN_CODE : // = 0x1
                   return call Mean4AMP.calculate(data, elemCount);
               case MEDIAN_CODE : // = 0x2
                   return call Median4AMP.calculate(data, elemCount);
               case CENTRAL_VALUE_CODE : // = 0x3
                   return call CentralValue4AMP.calculate(data, elemCount);
               case AMPLITUDE_CODE : // = 0x4
                   return call Amplitude4AMP.calculate(data, elemCount);
               case RANGE_CODE : // = 0x5
                   return call Range4AMP.calculate(data, elemCount);
               case MIN_CODE :  // = 0x6
                   return call Min4AMP.calculate(data, elemCount);
               case MAX_CODE : // = 0x7
                   return call Max4AMP.calculate(data, elemCount);
               case RMS_CODE : // = 0x8
                   return call Rms.calculate(data, elemCount);
               case VAR_CODE :  // = 0x9
                   return call Var.calculate(data, elemCount);
               case ST_DEV_CODE : // = 0xa
                   return call StandardDeviation.calculate(data, elemCount);
               case TOTAL_ENERGY_CODE : // = 0xb
                   return call TotalEnergy.calculate(data, elemCount);
            }
            return 0;
       }

}




