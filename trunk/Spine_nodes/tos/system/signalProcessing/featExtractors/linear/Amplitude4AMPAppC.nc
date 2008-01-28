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
 * This component calculate the Amplitude of a set of elements.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */

 configuration Amplitude4AMPAppC {
     provides interface Amplitude4AMP;
}

implementation {
     components Amplitude4AMPC;
     components Max4AMPC;
     components Mean4AMPC;

     Amplitude4AMP = Amplitude4AMPC;
     Amplitude4AMPC.Max4AMP -> Max4AMPC;
     Amplitude4AMPC.Mean4AMP -> Mean4AMPC;
}
