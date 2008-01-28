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
configuration JobEngineAppC {
     provides interface JobEngine;
}

implementation {
     components JobEngineC;

     components Mean4AMPC;
     components Range4AMPC;
     components CentralValue4AMPC;
     components Min4AMPC;
     components Max4AMPC;
     components Amplitude4AMPAppC;
     components Median4AMPAppC;
     components RmsAppC;
     components VarC;
     components StandardDeviationAppC;
     components TotalEnergyC;

     JobEngine = JobEngineC;
     
     JobEngineC.Mean4AMP -> Mean4AMPC;
     JobEngineC.Range4AMP -> Range4AMPC;
     JobEngineC.CentralValue4AMP -> CentralValue4AMPC;
     JobEngineC.Min4AMP -> Min4AMPC;
     JobEngineC.Max4AMP -> Max4AMPC;
     JobEngineC.Amplitude4AMP -> Amplitude4AMPAppC;
     JobEngineC.Median4AMP -> Median4AMPAppC;
     JobEngineC.Rms -> RmsAppC;
     JobEngineC.Var -> VarC;
     JobEngineC.StandardDeviation -> StandardDeviationAppC;
     JobEngineC.TotalEnergy -> TotalEnergyC;

}
