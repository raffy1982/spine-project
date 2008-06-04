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
 interface JobEngine {

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
       command int32_t execute(uint8_t jobCode, int16_t* data, uint16_t elemCount);
 }




